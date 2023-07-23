import json
import sys

import yfinance as yf
import datetime as dt
from flask import Flask, request
from flask_cors import CORS

app = Flask(__name__)
CORS(app)

print(sys.path)


class StockObject:
    def __init__(self, s1, f1, f2):
        self._s1 = s1
        self._f1 = f1
        self._d1 = f2

    @property
    def s1(self):
        return self._s1

    @property
    def f1(self):
        return self._f1

    @property
    def d1(self):
        return self._d1

    def to_dict(self):
        return {
            's': self._s1,
            'p': self._f1,
            't': self._d1
        }


mutex = 1;


@app.route('/getStockData', methods=['POST'])
def start():  # put application's code here

    global mutex

    while mutex != 1:
        pass

    mutex = 0

    req = json.loads(request.get_data())
    print(req)

    ticker = req['symbol']

    start = dt.datetime.now() - dt.timedelta(seconds=req['deltaTime'])
    end = dt.datetime.now()
    data = yf.download('BTC-USD', start, end, interval=req['interval'])
    data.reset_index(inplace=True)

    ticker = 'BTC-USD'

    stock_objects = []

    timestamps = data['Datetime']
    prices = data['Adj Close']

    for stock_price, stock_timestamp in zip(prices, timestamps):
        stock_objects.append(StockObject(ticker, float(stock_price), float(stock_timestamp.timestamp() * 1000)).to_dict())

    print(stock_objects)

    mutex = 1
    return json.dumps(stock_objects)


if __name__ == '__main__':
    app.run(debug=True)
