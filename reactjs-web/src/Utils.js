import SockJS from "sockjs-client"

const http = require('http')

const SERVER_HOST = 'localhost'
const SERVER_PORT = 8080
const WS_ENDPOINT = '/ws'
const TRADE_ENDPOINT = '/api/trade'

export const prepareCountryData = (labels, values) => {
  return {
    labels: labels,
    datasets: [
      {
        label: 'No. of Trades',
        backgroundColor: '#36A2EB',
        borderColor: '#36A2EB',
        borderWidth: 1,
        hoverBackgroundColor: '#36A2EB',
        hoverBorderColor: '#36A2EB',
        data: values
      }
    ]
  }
}

export const prepareCcyPairData = (labels, values) => {
  return {
    labels: labels,
    datasets: [
      {
        label: 'No. of Trades',
        backgroundColor: '#FFCE56',
        borderColor: '#FFCE56',
        borderWidth: 1,
        hoverBackgroundColor: '#FFCE56',
        hoverBorderColor: '#FFCE56',
        data: values
      }
    ]
  }
}

export const initWebSocket = () => {
  let sock = new SockJS(`http://${SERVER_HOST}:${SERVER_PORT}${WS_ENDPOINT}`);

  sock.onopen = function () {
    sock.subscribe()
  }

  sock.onmessage = function (e) {
    sock.close();
  }

  sock.onclose = function () {}
  return sock
}

export const postTradeMessage = (message) => {
  const options = {
    hostname: SERVER_HOST,
    port: SERVER_PORT,
    path: TRADE_ENDPOINT,
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Content-Length': message.length
    }
  }

  const req = http.request(options, (res) => {
    res.on('data', (d) => {
    })
  })

  req.on('error', (error) => {
    alert("post trade error " + error)
  })

  req.write(message)
  req.end()

}
