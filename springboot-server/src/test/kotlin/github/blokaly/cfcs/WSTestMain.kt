package github.blokaly.cfcs

import github.blokaly.cfcs.common.Endpoints
import github.blokaly.cfcs.common.MainLogging
import github.blokaly.cfcs.common.logger
import org.springframework.messaging.simp.stomp.StompFrameHandler
import org.springframework.messaging.simp.stomp.StompHeaders
import java.lang.reflect.Type

object WSTestMain {

    @JvmStatic
    fun main(args: Array<String>) {
        println("running wsclient")
        val wsUrl = "ws://127.0.0.1:8080${Endpoints.WEB_SOCKET_ENDPOINT}"
        val stompClient = createWebSocketClient()
        val stompSession = stompClient!!.connect(wsUrl, MyStompSessionHandler()).get()
        stompSession!!.subscribe("${Endpoints.WS_MESSAGE_BROKER}/summary", object: StompFrameHandler {
            override fun handleFrame(headers: StompHeaders, payload: Any?) {
                MainLogging.logger().info("received message: {} with headers: {}", payload, headers)
            }

            override fun getPayloadType(headers: StompHeaders): Type {
                return String::class.java
            }

        })

        while (stompSession!!.isConnected) {}
    }
}