const ctx = document.getElementById('myChart');
let predictionPrice = document.getElementById('predictionPrice');
let currentPrice = document.getElementById('currentPrice');
let lastPredictionResult = document.getElementById('lastResult');

const requestForMinutes = new XMLHttpRequest();
const requestForHours = new XMLHttpRequest();
const stockURL='http://localhost:5000/getStockData';

let pPrice;
let cPrice;


let hoursTimeInfo = [];
let minutesTimeInfo = [];
let secondsTimeInfo = [0,0,0];

let hoursPriceInfo = [];
let minutesPriceInfo = [];
let secondsPriceInfo = [0,0,0];

let minuteDelay = 1000 * 60 * 5;
let hourDelay = 1000 * 60 * 60;

let stockChart = buildChart(ctx)
let counter = 0


class RequestBody
{
    constructor(symbol, deltaTime, interval)
    {
        this.symbol = symbol;
        this.deltaTime = deltaTime;
        this.interval = interval;
    }
}

async function sendRequest(http, url, postObj, delay) {
    setInterval(function rec()
    {
        http.open("POST", url);
        http.send(JSON.stringify(postObj));
        return rec;
    }(), delay);
}

requestForMinutes.onreadystatechange = (e) => {
    if (requestForMinutes.readyState > 3)
    {
        minutesTimeInfo.length = 0;
        minutesPriceInfo.length = 0;

        let jsonArray = JSON.parse(requestForMinutes.response);

        for (i = 0; i < 3; i++) {
            minutesTimeInfo.push(jsonArray[jsonArray.length - 1 - i].t);
            minutesPriceInfo.push(jsonArray[jsonArray.length - 1 - i].p)
        }
        console.log(minutesTimeInfo);
        updateData(stockChart)
    }
}

requestForHours.onreadystatechange = (e) => {
    if (requestForHours.readyState > 3)
    {
        hoursTimeInfo.length = 0;
        hoursPriceInfo.length = 0;

        let jsonArray = JSON.parse(requestForHours.response);

        for (i = 0; i < 3; i++) {
            hoursTimeInfo.push(jsonArray[jsonArray.length - 1 - i].t);
            hoursPriceInfo.push(jsonArray[jsonArray.length - 1 - i].p)
        }
        console.log(hoursTimeInfo);
        updateData(stockChart)
    }
}

function getTimeFullArray() {
    return hoursTimeInfo.concat(minutesTimeInfo.concat(secondsTimeInfo));
}

function getPriceFullArray() {
    return hoursPriceInfo.concat(minutesPriceInfo.concat(secondsPriceInfo));
}

// API gives info with a delay, so more info has to be retrieved that is actually needed
sendRequest(requestForMinutes,stockURL,new RequestBody("BTC-USD",18000,"5m"),minuteDelay);
sendRequest(requestForHours,stockURL, new RequestBody("BTC-USD",100000,"1h"),hourDelay);

function updateData(chart) {

    let fullTimeArray = getTimeFullArray();
    let fullPriceArray = getPriceFullArray();

    fullTimeArray = fullTimeArray.map(element => formatDate(element))

    chart.options.scales.y.max = Math.round(Math.max(...fullPriceArray) / 100) * 100 * 1.2
    chart.options.scales.y.min = Math.round(Math.min(...fullPriceArray) / 100) * 100 * 0.9

    chart.data.labels = fullTimeArray;
    chart.data.datasets[0].data = fullPriceArray;
    chart.update();
}

function buildChart(current_ctx) {
    return new Chart(current_ctx, {
        type: 'line',
        data: {
            labels: [],
            datasets: [{
                data: [],
                borderWidth: 1
            }]
        },
        options: {
            scales: {
                y: {
                    max: 50,
                    min: 30,
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
function formatDate(timestamp) {
    let formattedDate = new Date(parseInt(timestamp)).toLocaleString('ru-RU');
    return formattedDate.substring(formattedDate.indexOf(",") + 1, formattedDate.length);
}
function connect(locales) {
    socket = new SockJS("http://localhost:8080/ws");
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (locales) {
        stompClient.subscribe('/topic/stock-info', function (data) {
            let response = JSON.parse(data.body);
            let formattedDate = formatDate(response.t)

            console.log(response.t + " " + formattedDate);

            if(response.s.includes("prediction"))
            {
                if (!predictionPrice.textContent.includes("Waiting"))
                {
                    lastPredictionResult.textContent = "Last prediction delta was: " + Math.abs(pPrice - cPrice) + "$";
                }
                pPrice = Math.round(response.p);
                predictionPrice.textContent = "For: " + formattedDate + ", Prediction is " + pPrice + "$";
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

                addData(stockChart, formattedDate, response.p)
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



