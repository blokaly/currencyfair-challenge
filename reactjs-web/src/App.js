import React from 'react'
import {useEffect, useState} from 'react'
import './App.css'
import {HorizontalBar} from 'react-chartjs-2'
import Stomp from 'stompjs'
import {prepareCountryData, prepareCcyPairData, initWebSocket, postTradeMessage} from './Utils'

function App() {
  const [trade, setTrade] = useState("")
  const [total, setTotal] = useState(0)
  const [countryData, setCountryData] = useState({})
  const [ccyPairData, setCcyPairData] = useState({})

  const handleTotal = (value) => {
    setTotal(value)
  }

  const handleCountryData = (obj) => {
    let labels = [], values = []
    for (const [key, value] of Object.entries(obj)) {
      labels.push(key)
      values.push(value)
    }
    values.push(0)
    setCountryData(prepareCountryData(labels, values))
  }

  const handleCcyPairData = (obj) => {
    let labels = [], values = []
    for (const [key, value] of Object.entries(obj)) {
      labels.push(key)
      values.push(value)
    }
    values.push(0)
    setCcyPairData(prepareCcyPairData(labels, values))
  }

  const submitTrade = () => {
    try {
      let data = JSON.stringify(JSON.parse(trade))
      postTradeMessage(data)
    } catch (e) {
      alert("submit trade error " + e)
    }
  }

  useEffect(() => {
    let socket = initWebSocket()
    let stompClient = Stomp.over(socket)
    stompClient.connect({}, function (frame) {
      stompClient.subscribe('/topic/summary', function (summary) {
        let obj = JSON.parse(summary.body)
        handleTotal(obj['total'])
        handleCountryData(obj['tradesByCountry'])
        handleCcyPairData(obj['tradesByCcyPair'])
      });
    });

  }, [])

  return (
    <div className="App">

      <div className="container">

        <div className="trade-form">
          <textarea rows="6" cols="80" name="trade" form="tradeform" onChange={e => setTrade(e.target.value)}>
          </textarea>
          <button onClick={submitTrade}>Submit Trade Message</button>
        </div>

        <hr/>
        <h2>Total Number of Trades</h2>
        <label className="label">{total}</label>

        <hr/>
        <h2>Number of Trades by Originating Country</h2>
        {countryData &&
        <HorizontalBar data={countryData}/>
        }

        <hr/>
        <h2>Number of Trades by Currency Pair</h2>
        {ccyPairData &&
        <HorizontalBar data={ccyPairData}/>
        }

      </div>

    </div>
  );
}

export default App;
