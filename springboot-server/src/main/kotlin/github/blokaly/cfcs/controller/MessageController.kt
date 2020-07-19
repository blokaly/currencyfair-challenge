package github.blokaly.cfcs.controller

import github.blokaly.cfcs.common.Endpoints
import github.blokaly.cfcs.common.Endpoints.TRADE_MESSAGE_ENDPOINT
import github.blokaly.cfcs.common.LoggerDelegate
import github.blokaly.cfcs.model.Trade
import github.blokaly.cfcs.model.TradeEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(Endpoints.API_ROOT_PATH, produces = [MediaType.APPLICATION_JSON_VALUE])
class MessageController(private val applicationEventPublisher: ApplicationEventPublisher) {
    private val logger by LoggerDelegate()

    @PostMapping(TRADE_MESSAGE_ENDPOINT)
    fun handleTradeMessage(@RequestBody trade: Trade): ResponseEntity<Trade?> {
        return try {
            logger.info("received trade: ${trade}")
            applicationEventPublisher.publishEvent(TradeEvent(trade))
            ResponseEntity(trade, HttpStatus.CREATED)
        } catch (ex: Exception) {
            logger.error("Trade message processing error", ex)
            ResponseEntity(HttpStatus.EXPECTATION_FAILED)
        }
    }
}