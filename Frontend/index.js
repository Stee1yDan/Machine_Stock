// Import the necessary libraries
// let SockJS = require('sockjs-client');
// let Stomp = require('webstomp-client');

const ctx = document.getElementById('myChart');
let predictionPrice = document.getElementById('predictionPrice');
let currentPrice = document.getElementById('currentPrice');
let lastPredictionResult = document.getElementById('lastResult');

let pPrice;
let cPrice;

let stockChart = buildChart(ctx)
let counter = 0

connect()


function buildChart(current_ctx) {
    return new Chart(current_ctx, {
        type: 'line',
        data: {
            labels: [],
            datasets: [{
                label: '',
                data: [],
                borderWidth: 1
            }]
        },
        options: {
            scales: {
                y: {
                    max: 1,
                    min: 0,
                    ticks: {
                        stepSize: 10
                    }
                }

            },
            plugins: {
                legend: {
                    display: false
                },
            }
        }
    });
}
function connect() {
    socket = new SockJS("http://localhost:8080/ws");
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function () {
        stompClient.subscribe('/topic/stock-info', function (data) {
            let response = JSON.parse(data.body);
            console.log(response.t)

            if(response.s.includes("prediction"))
            {
                if (!predictionPrice.textContent.includes("Waiting"))
                {
                    lastPredictionResult.textContent = "Last prediction delta was: " + (pPrice - cPrice) + "$";
                }
                pPrice = Math.round(response.p);
                predictionPrice.textContent = "For: " + response.t + ", Prediction is " + pPrice + "$";
            }
            else
            {
                counter++;

                if (counter > 10)
                {
                    removeData(stockChart)
                }

                stockChart.options.scales.y.max = Math.round(response.p/10) * 10 + 10;
                stockChart.options.scales.y.min = Math.round(response.p/10) * 10 - 10;


                cPrice = Math.round(response.p);
                currentPrice.textContent = "Current price is: " + cPrice + "$";

                // if (stockChart.options.scales.y.max < response.p)
                // {
                //     stockChart.options.scales.y.max = Math.round(response.p/10) * 10 + 10;
                // }
                //
                // if (stockChart.options.scales.y.min > response.p)
                // {
                //     stockChart.options.scales.y.min = Math.round(response.p/10) * 10 - 10;
                // }

                addData(stockChart, response.t, response.p)
            }
        });
    });
}

function disconnect() {
    if (stompClient != null) {
        stompClient.disconnect();
    }
    console.log("Disconnected");
}



function addData(chart, label, data) {
    chart.data.labels.push(label);
    chart.data.datasets.forEach((dataset) => {
        dataset.data.push(data);
    });
    chart.update();
}

function removeData(chart) {
    chart.data.labels.shift();
    chart.data.datasets.forEach((dataset) => {
        dataset.data.shift();
    });
    chart.update();
}


