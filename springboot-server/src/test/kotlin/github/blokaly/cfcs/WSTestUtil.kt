package github.blokaly.cfcs

import github.blokaly.cfcs.common.MainLogging
import github.blokaly.cfcs.common.logger
import org.springframework.messaging.converter.StringMessageConverter
import org.springframework.messaging.simp.stomp.*
import org.springframework.web.socket.client.WebSocketClient
import org.springframework.web.socket.client.standard.StandardWebSocketClient
import org.springframework.web.socket.messaging.WebSocketStompClient
import org.springframework.web.socket.sockjs.client.SockJsClient
import org.springframework.web.socket.sockjs.client.WebSocketTransport
import java.lang.reflect.Type
import java.util.function.Consumer


fun createWebSocketClient(): WebSocketStompClient? {
    val transport: WebSocketClient = SockJsClient(listOf(WebSocketTransport(StandardWebSocketClient())))
    return WebSocketStompClient(transport).apply { messageConverter= StringMessageConverter()}
}

class MyStompSessionHandler : StompSessionHandlerAdapter() {
    override fun afterConnected(session: StompSession, connectedHeaders: StompHeaders) {
        MainLogging.logger().info("Stomp client is connected")
        super.afterConnected(session, connectedHeaders)
    }

    override fun handleException(session: StompSession, command: StompCommand?, headers: StompHeaders, payload: ByteArray, exception: Throwable) {
        MainLogging.logger().info("Exception: $exception")
        super.handleException(session, command, headers, payload, exception)
    }
}

class MyStompFrameHandler(private val frameHandler: Consumer<String?>) : StompFrameHandler {

    override fun getPayloadType(headers: StompHeaders): Type {
        return String::class.java
    }

    override fun handleFrame(headers: StompHeaders, payload: Any?) {
        MainLogging.logger().info("received message: {} with headers: {}", payload, headers)
        frameHandler.accept(payload.toString())
    }
}