package github.blokaly.cfcs.service

import com.fasterxml.jackson.databind.ObjectMapper
import github.blokaly.cfcs.common.Endpoints
import github.blokaly.cfcs.common.Endpoints.TRADE_SUMMARY_ENDPOINT
import github.blokaly.cfcs.common.LoggerDelegate
import github.blokaly.cfcs.model.TradeSummaryEvent
import github.blokaly.cfcs.model.TradeSummaryProcessor
import org.springframework.context.event.EventListener
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Component
import java.lang.Exception

@Component
class TradeSummaryPublisher(private val messageTemplate: SimpMessagingTemplate) : TradeSummaryProcessor {
    private val logger by LoggerDelegate()
    private val objectMapper = ObjectMapper()

    @EventListener
    override fun processTradeSummaryEvent(evt: TradeSummaryEvent) {
        try {
            messageTemplate.convertAndSend("${Endpoints.WS_MESSAGE_BROKER}/$TRADE_SUMMARY_ENDPOINT", objectMapper.writeValueAsString(evt))
        } catch (ex: Exception) {
            logger.error("Publishing trade summary error", ex)
        }
    }
}