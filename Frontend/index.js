const ctx = document.getElementById('myChart');
let predictionPrice = document.getElementById('predictionPrice');
let currentPrice = document.getElementById('currentPrice');
let lastPredictionResult = document.getElementById('lastResult');

const requestForMinutes = new XMLHttpRequest();
const requestForHours = new XMLHttpRequest();
const stockURL='http://localhost:5000/getStockData';

let pPrice;
let cPrice;


let hoursTimeInfo = [0,0,0];
let minutesTimeInfo = [0,0,0];
let secondsTimeInfo = [0,0,0];

let hoursPriceInfo = [0,0,0];
let minutesPriceInfo = [0,0,0];
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

        let jsonArray1 = JSON.parse(requestForMinutes.response);

        for (i = 3; i > 0; i--) {
            minutesTimeInfo.push(jsonArray1[jsonArray1.length - 1 - i].t);
            minutesPriceInfo.push(jsonArray1[jsonArray1.length - 1 - i].p)
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

        for (i = 3; i > 0; i--) {
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
sendRequest(requestForHours,stockURL, new RequestBody("BTC-USD",100000,"1h"),hourDelay);
sendRequest(requestForMinutes,stockURL,new RequestBody("BTC-USD",18000,"5m"),minuteDelay);
connect();

function updateData(chart) {

    let fullTimeArray = getTimeFullArray();
    let fullPriceArray = getPriceFullArray();

    let dates = [];

    for (i = 0; i < fullTimeArray.length; i++)
    {
        fullTimeArray[i] = formatDate(fullTimeArray[i])
    }

    console.log(dates)

    chart.options.scales.y.max = Math.round(Math.max(...fullPriceArray) / 100) * 100 + 50
    chart.options.scales.y.min = Math.round(Math.min(...fullPriceArray) / 100) * 100 - 50

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

            console.log(response)

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

                if (secondsPriceInfo.length === 3)
                {
                    secondsPriceInfo.shift();
                    secondsTimeInfo.shift();
                }
                while (secondsPriceInfo.length < 3)
                {
                    secondsPriceInfo.push(response.p);
                    secondsTimeInfo.push(response.t); //TODO: Make
                }
                updateData(stockChart);

                cPrice = Math.round(response.p);
                currentPrice.textContent = "Current price is: " + cPrice + "$";
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



