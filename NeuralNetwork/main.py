import json

import numpy as np
import matplotlib.pyplot as plt
import pandas as pd
import pandas_datareader as web
import datetime as dt
import tensorflow
import yfinance as yf
import socket

from sklearn.preprocessing import MinMaxScaler
from dateutil.tz import tzutc
from tensorflow import keras
from keras.models import Sequential
from keras.layers import Dense, Dropout, LSTM
from types import SimpleNamespace

# Socket configuration
server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
server_host = '0.0.0.0'  # Set the host to listen on all available network interfaces
server_port = 12345  # Set the port to listen on

server_socket.bind((server_host, server_port))
server_socket.listen(1)  # Limit the number of connections to 1

# Model config data

crypto_currency = 'BTC'
against_currency = 'USD'

prediction_units = 7
future_units = 7

last_prediction = 1
last_price = 1

# Get Data
start = dt.datetime.now() - dt.timedelta(days=prediction_units)
end = dt.datetime.now()

data = yf.download('BTC-USD', start, end, interval='1m')
test_data = data['Adj Close']
print(test_data)

# Prepare Data For Model Training
scaler = MinMaxScaler(feature_range=(0, 1))
scaled_data = scaler.fit_transform(data['Adj Close'].values.reshape(-1, 1))

x_train, y_train = [], []

for x in range(prediction_units, len(scaled_data) - future_units):
    x_train.append(scaled_data[x - prediction_units:x, 0])
    y_train.append(scaled_data[x + future_units, 0])

x_train, y_train = np.array(x_train), np.array(y_train)
x_train = x_train.reshape(x_train.shape[0], x_train.shape[1], 1)

# Create Neural Network

model = Sequential()
model.add(LSTM(units=250, return_sequences=True, input_shape=(x_train.shape[1], 1)))
model.add(Dropout(0.2))
model.add(LSTM(units=250, return_sequences=True))
model.add(Dropout(0.2))
model.add(LSTM(units=250))
model.add(Dropout(0.2))
model.add(Dense(units=1))

model.compile(optimizer='adam', loss='mean_squared_error')
model.fit(x_train, y_train, batch_size=32)

while True:
    client_socket, client_address = server_socket.accept()
    print("Accepted connection from {}:{}".format(client_address[0], client_address[1]))

    stock_info_str = client_socket.recv(1024).decode()
    stock_info = json.loads(stock_info_str)

    unix_timestamp = int(round(float(stock_info["t"])/1000))

    print(unix_timestamp)

    date_time = (dt.datetime.fromtimestamp(unix_timestamp) - dt.timedelta(hours=3)).strftime("%Y-%m-%d %H:%M:%S")


    socket_data = pd.Series(stock_info["p"], index=[date_time])
    current_price = stock_info["p"]

    last_prediction_accuracy = abs(current_price - last_prediction)
    last_price_accuracy = abs(current_price - last_price)

    prediction_coefficient = last_price_accuracy / (last_price_accuracy + last_prediction_accuracy)
    price_coefficient = last_prediction_accuracy / (last_price_accuracy + last_prediction_accuracy)

    # Adding the info for analysis`
    test_data = test_data._append(socket_data)
    actual_prices = test_data.values

    print(test_data)

    total_dataset = pd.concat((data['Adj Close'], test_data), axis=0)

    model_iputs = total_dataset[len(total_dataset) - len(test_data) - prediction_units:].values
    model_iputs = model_iputs.reshape(-1, 1)
    model_iputs = scaler.fit_transform(model_iputs)

    x_test = []

    for x in range(prediction_units, len(model_iputs)):
        x_test.append(model_iputs[x - prediction_units:x, 0])

    x_test = np.array(x_test)
    x_test = np.reshape(x_test, (x_test.shape[0], x_test.shape[1], 1))

    prediction_prices = model.predict(x_test)
    prediction_prices = scaler.inverse_transform(prediction_prices)

    # plt.plot(actual_prices, color='black', label='Actual Prices')
    # plt.plot(prediction_prices, color='red', label='Predicted Prices')
    # plt.title(f'{crypto_currency} price prediction')
    # plt.xlabel('time')
    # plt.ylabel('price')
    # plt.show()

    # Predict next day

    real_data = [model_iputs[len(model_iputs) + 1 - prediction_units:len(model_iputs) + 1, 0]]
    real_data = np.array(real_data)
    real_data = np.reshape(real_data, (real_data.shape[0], real_data.shape[1], 1))

    prediction = model.predict(real_data)
    prediction = scaler.inverse_transform(prediction)

    balanced_prediction = prediction[0][0] * prediction_coefficient + stock_info["p"] * price_coefficient

    print("prediction_coefficient: " + str(prediction_coefficient))
    print("price_coefficient: " + str(price_coefficient))

    print("Prediction is: " + str(prediction[0][0]))
    print("Current price is: " + str(stock_info["p"]))
    print("Balanced prediction is " + str(balanced_prediction))

    stock_info["s"] = str(stock_info["s"]) + "-prediction"
    stock_info["p"] = str(balanced_prediction)
    stock_info["t"] = float(str(stock_info["t"])) + 60000

    last_prediction = prediction[0][0]
    last_price = float(stock_info["p"])

    # Send the prediction back to the client
    client_socket.sendall(json.dumps(stock_info).encode())

    # Close the client socket
    client_socket.close()
