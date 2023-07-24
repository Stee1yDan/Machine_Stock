# Machine_Stock

This project consist of 4 modules.

## Java module

Is used to retrive information from outer socket, process information and distribute it between 2 other modules. The module uses SockJS and Stomp Client for communicating with front end and java.net socket for communication with python model.

## Neural Network module

Basic LSTM model. Is used to return stock prediction for next 60 seconds. 

## Frontend module

Simple html page that was build with boostrap 5. Retrieves info from socket using Stomp Client and SockJS. Retrieves info with SockJS and StompClient and displays it with ChartJS.

## Python module

Simple API that is used to retrive info through POST request. Build with Flask.

![FireShot Capture 030 - Machine;Stock - localhost](https://github.com/Stee1yDan/Machine_Stock/assets/125751951/a737d6cf-53f8-4749-9986-abdbdb7ade8f)

