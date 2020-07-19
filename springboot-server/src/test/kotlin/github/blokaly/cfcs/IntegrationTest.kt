package github.blokaly.cfcs

import github.blokaly.cfcs.common.Endpoints
import github.blokaly.cfcs.common.Endpoints.API_ROOT_PATH
import github.blokaly.cfcs.common.Endpoints.TRADE_MESSAGE_ENDPOINT
import github.blokaly.cfcs.common.Endpoints.TRADE_SUMMARY_ENDPOINT
import github.blokaly.cfcs.model.Trade
import github.blokaly.cfcs.service.TradeSummaryProcessor
import org.assertj.core.api.Assertions.assertThat
import org.json.JSONObject
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.messaging.simp.stomp.StompSession
import org.springframework.web.socket.messaging.WebSocketStompClient
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong
import java.util.function.Consumer
import kotlin.random.Random

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IntegrationTest(@Autowired val restTemplate: TestRestTemplate, @Autowired val processor: TradeSummaryProcessor) {

    @Value("\${local.server.port}")
    private val port = 0

    private var stompClient: WebSocketStompClient? = null
    private var stompSession: StompSession? = null

    @BeforeEach
    fun setup() {
        processor.reset()
        val wsUrl = "ws://127.0.0.1:$port${Endpoints.WEB_SOCKET_ENDPOINT}"
        stompClient = createWebSocketClient()
        stompSession = stompClient!!.connect(wsUrl, MyStompSessionHandler()).get()
    }

    @AfterEach
    fun teardown() {
        stompSession!!.disconnect()
        stompClient!!.stop()
    }

    @Test
    fun `Test Summary for Multiple Originating Trades`() {
        val rand = Random(System.currentTimeMillis())
        val locations = listOf("US", "GB", "JP", "CA", "HK")
        val total = rand.nextLong(5, 50)
        val count = mutableMapOf<String, Long>("US" to 0, "GB" to 0, "JP" to 0, "CA" to 0, "HK" to 0)

        val resultKeeper = CompletableFuture<String>()
        val msgReceived = AtomicLong()
        stompSession!!.subscribe("${Endpoints.WS_MESSAGE_BROKER}/$TRADE_SUMMARY_ENDPOINT",
            MyStompFrameHandler(Consumer { payload ->
                if (msgReceived.incrementAndGet() == total) {
                    resultKeeper.complete(payload.toString())
                }
            }))

        for (x in 1..total) {
            val loc = locations[rand.nextInt(5)]
            count[loc] = count[loc]!! + 1
            val trade = """{
                "userId": "134256",
                "currencyFrom": "EUR",
                "currencyTo": "GBP",
                "amountSell": 1000,
                "amountBuy": 747.10,
                "rate": 0.7471,
                "timePlaced": "24-JAN-18 10:27:44",
                "originatingCountry": "$loc"
                }"""

            val request = HttpEntity(trade, HttpHeaders().apply { contentType = MediaType.APPLICATION_JSON })
            val entity = restTemplate.postForEntity<Trade>("$API_ROOT_PATH$TRADE_MESSAGE_ENDPOINT", request, Trade::class.java)
            assertThat(entity.statusCode).isEqualTo(HttpStatus.CREATED)
        }

        val summary = JSONObject(resultKeeper[5, TimeUnit.SECONDS])
        assertThat(summary.getLong("total")).isEqualTo(total)
        assertThat(summary.getJSONObject("tradesByCcyPair").getLong("EURGBP")).isEqualTo(total)
        val tradesByCountry = summary.getJSONObject("tradesByCountry")
        locations.forEach {
            val expected = count[it]
            if (expected == 0L) {
                assertThat(tradesByCountry.has(it)).isFalse()
            } else {
                assertThat(tradesByCountry.getLong(it)).isEqualTo(expected)
            }
        }
    }

    @Test
    fun `Test Summary for Multiple CcyPair Trades`() {
        val rand = Random(System.currentTimeMillis())
        val pairs = listOf("EURUSD", "GBPUSD", "USDJPY", "AUDUSD", "USDCAD")
        val total = rand.nextLong(5, 50)
        val count = mutableMapOf<String, Long>("EURUSD" to 0, "GBPUSD" to 0, "USDJPY" to 0, "AUDUSD" to 0, "USDCAD" to 0)

        val resultKeeper = CompletableFuture<String>()
        val msgReceived = AtomicLong()
        stompSession!!.subscribe("${Endpoints.WS_MESSAGE_BROKER}/$TRADE_SUMMARY_ENDPOINT",
            MyStompFrameHandler(Consumer { payload ->
                if (msgReceived.incrementAndGet() == total) {
                    resultKeeper.complete(payload.toString())
                }
            }))

        for (x in 1..total) {
            val pair = pairs[rand.nextInt(5)]
            count[pair] = count[pair]!! + 1
            val trade = """{
                "userId": "134256",
                "currencyFrom": "${pair.substring(0, 3)}",
                "currencyTo": "${pair.substring(3)}",
                "amountSell": 1000,
                "amountBuy": 747.10,
                "rate": 0.7471,
                "timePlaced": "24-JAN-18 10:27:44",
                "originatingCountry": "US"
                }"""

            val request = HttpEntity(trade, HttpHeaders().apply { contentType = MediaType.APPLICATION_JSON })
            val entity = restTemplate.postForEntity<Trade>("$API_ROOT_PATH$TRADE_MESSAGE_ENDPOINT", request, Trade::class.java)
            assertThat(entity.statusCode).isEqualTo(HttpStatus.CREATED)
        }

        val summary = JSONObject(resultKeeper[5, TimeUnit.SECONDS])
        assertThat(summary.getLong("total")).isEqualTo(total)
        assertThat(summary.getJSONObject("tradesByCountry").getLong("US")).isEqualTo(total)
        val tradesByCcyPair = summary.getJSONObject("tradesByCcyPair")
        pairs.forEach {
            val expected = count[it]
            if (expected == 0L) {
                assertThat(tradesByCcyPair.has(it)).isFalse()
            } else {
                assertThat(tradesByCcyPair.getLong(it)).isEqualTo(expected)
            }
        }
    }
}