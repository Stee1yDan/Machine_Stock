# Machine_Stock

This project consist of 3 modules.

## Java module

Is used to retrive information from outer socket, process information and distribute it between 2 other modules. The module uses SockJS and Stomp Client for communicating with front end and java.net socket for communication with python model.

## Python module

Is used to return stock prediction for next 60 seconds. (The model is working, but not good).

## Frontend module

Simple html page that was build with boostrap 5. Retrieves info from socket using Stomp Client and SockJS. Displays retrieved info with ChartJS.

![FireShot Capture 023 - Machine;Stock - localhost](https://github.com/Stee1yDan/Machine_Stock/assets/125751951/0e4af496-b1d9-4b54-9912-19fe18a7f5e6)
