# currencyfair-challenge

Code repos of [Server](./springboot-server) side and [Frontend](./reactjs-web) side work of the CurrencyFair backend developer challenge.

Server
---

1. Technologies used

- Kotlin (Java)
- Spring Boot with REST and WebSocket
- Docker

2. How it works

Spring Boot application starts a web server and exposes a REST api endpoint to consume trade messages.
Then the endpoint controller publishes an internal event of the trade message, which will be handled by the event listener to process the trade message and in turn publish another internal event of the trade message processing summary report. Finally, the summary report listener will relay the message to all the websocket subscribers.

This loosely coupled implementation can be easily adapted to add more or swap to other message processors and publishers without breaking up the existing services.

3. How to build and run the server

- Command line: go into the _springboot-server_ folder and execute

```bash
$ ./gradlew clean bootRun
```

- Docker: go into the _springboot-server_ folder and execute

```bash
$ ./gradlew clean jibDockerBuild
$ cd docker
$ docker-compose up -d
```

Require Java 8 and port 8080 available on localhost. 
Docker with docker-compose required if build/run with docker.

4. How to access

After build and run successfully, open browser and goto http://localhost:8080, then you should be able to submit test trade message likes the following:

```json
{"userId": "134256", "currencyFrom": "EUR", "currencyTo": "GBP",
 "amountSell": 1000, "amountBuy": 747.10, "rate": 0.7471,
 "timePlaced": "24-JAN-18 10:27:44", "originatingCountry": "FR"}
```

Try to change the _currencyFrom_, _currencyTo_ and _originatingCountry_ values, and view the summary report updated realtime

Also, you could use Postman or other tools to post the above message to http://localhost:8080/api/trade endpoint.

Frontend
---

1. Technologies used

- ReactJS with SocketJS
- react-chartjs-2

2. How it works

This code is based on the template generated by create-react-app. Then SocketJS is added to open ws connection to the server side and receive summary updates from the websocket summary topic. Then bar graphs are generated using the react-chartjs-2 library.

3. How to build and run the frontend

Go into the _reactjs-web_ folder and execute

```bash
$ npm install
```

You can run it independently to the server side application

```bash
$ npm run start
```

Then goto http://localhost:3000

or you can build and copy the static files to the Spring Boot server to bundle them together

```bash
$ npm run package
```

Then follow the step 3 of the server side build/run. Please note, the current repo of the _springboot-server_ already contains the static files under `src/main/resources/static` folder.