# Machine_Stock

This is an example project that serves the sole purpose of advertising itself. It is capable of storing the email addresses of people who choose to subscribe for news updates, as well as returning a prediction of BTC price (or any other stock of your choice if you have right API) for the next 60 seconds (or any other desirable period of time).

![Application_Scheme drawio](https://github.com/Stee1yDan/Machine_Stock/assets/125751951/977dd227-9d22-43be-943c-e9c0b9090e05)

## Java module

#### Web Socket Distributor

Is used to retrive information from outer socket, process information and distribute it between Neural Network module and Frontend module. The module uses SockJS and Stomp Client for communicating with front end and java.net socket for communication with python model.

#### Email Service

It project provides RESTful API functionality to store email and confirmation information in a Postgres database using the Postgresql JDBC driver and Spring Data JPA for database connectivity. The frontend sends emails and confirmation information to the backend, which then stores them in the database.

## Frontend module

Simple html page that was build with Boostrap 5. Retrieves info from socket using Stomp Client and SockJS and displays it with ChartJS.

## Python module

#### Neural Network module

Basic LSTM model. Is used to return stock prediction.

#### Stock Data API

Simple API that is used to retrieve info from YFinance. Built with Flask.

![FireShot Capture 030 - Machine;Stock - localhost](https://github.com/Stee1yDan/Machine_Stock/assets/125751951/a737d6cf-53f8-4749-9986-abdbdb7ade8f)

