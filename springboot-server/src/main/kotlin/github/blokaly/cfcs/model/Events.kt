package github.blokaly.cfcs.model

import org.springframework.context.ApplicationEvent

data class TradeEvent(val trade: Trade): ApplicationEvent("trade")

interface TradeMessageProcessor {
    /**
     * Listener of trade messages relayed from the controller
     * @param evt the trade event
     */
    fun processTradeEvent(evt: TradeEvent)
}

data class TradeSummaryEvent(val total: Long, val tradesByCountry:Map<String, Long>, val tradesByCcyPair:Map<String, Long>): ApplicationEvent("summary")

interface TradeSummaryProcessor {
    /**
     * Listener of trade summary published by the message processor
     * @param evt the trade summary event
     */
    fun processTradeSummaryEvent(evt: TradeSummaryEvent)
}