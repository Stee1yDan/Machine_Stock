import json

import numpy as np
import matplotlib.pyplot as plt
import pandas as pd
import pandas_datareader as web
import datetime as dt
import tensorflow
import yfinance as yf
import socket
from math import ceil

from sklearn.preprocessing import MinMaxScaler
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
future_units = 5

# Get Data
start = dt.datetime.now() - dt.timedelta(days=prediction_units)
end = dt.datetime.now()

data = yf.download('BTC-USD', start, end, interval='1m')
# btcusd['Open'].plot(figsize=(10,6))

# Prepare Data For Model Training
scaler = MinMaxScaler(feature_range=(0, 1))
scaled_data = scaler.fit_transform(data['Adj Close'].values.reshape(-1, 1))

x_train, y_train = [], []

for x in range(prediction_units, len(scaled_data)-future_units):
    x_train.append(scaled_data[x - prediction_units:x, 0])
    y_train.append(scaled_data[x + future_units, 0])

x_train, y_train = np.array(x_train), np.array(y_train)
x_train = x_train.reshape(x_train.shape[0], x_train.shape[1], 1)

# Create Neural Network

model = Sequential()
model.add(LSTM(units=50, return_sequences=True, input_shape=(x_train.shape[1], 1)))
model.add(Dropout(0.2))
model.add(LSTM(units=50, return_sequences=True))
model.add(Dropout(0.2))
model.add(LSTM(units=50))
model.add(Dropout(0.2))
model.add(Dense(units=1))

model.compile(optimizer='adam', loss='mean_squared_error')
model.fit(x_train, y_train, batch_size=32)

# Adding the info for analysis

while True:
    client_socket, client_address = server_socket.accept()
    print("Accepted connection from {}:{}".format(client_address[0], client_address[1]))

    stock_info_str = client_socket.recv(1024).decode()
    stock_info = json.loads(stock_info_str)

    test_start = dt.datetime.now() - dt.timedelta(days=1)
    test_end = dt.datetime.now()

    test_data = yf.download('BTC-USD', test_start, test_end, interval='1m')
    actual_prices = test_data['Adj Close'].values

    print(test_data)

    test_data.reset_index(inplace=True)

    total_dataset = pd.concat((data['Adj Close'], test_data['Adj Close']), axis=0)

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

    #Predict next day

    real_data = [model_iputs[len(model_iputs) + 1 - prediction_units:len(model_iputs) + 1, 0]]
    real_data = np.array(real_data)
    real_data = np.reshape(real_data, (real_data.shape[0], real_data.shape[1], 1))

    prediction = model.predict(real_data)
    prediction = scaler.inverse_transform(prediction)

    current_timestamp = test_data['Datetime'][len(test_data['Datetime']) - 1].timestamp()

    stock_info["s"] = str(stock_info["s"]) + "-prediction"
    stock_info["p"] = str(prediction[0][0])
    stock_info["t"] = int(current_timestamp + (60 * future_units))

    print(prediction[0])

    # Send the prediction back to the client
    client_socket.sendall(json.dumps(stock_info).encode())

    # Close the client socket
    client_socket.close()


