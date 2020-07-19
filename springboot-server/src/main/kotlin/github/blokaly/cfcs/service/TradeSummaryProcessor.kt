package github.blokaly.cfcs.service

import github.blokaly.cfcs.common.LoggerDelegate
import github.blokaly.cfcs.model.TradeEvent
import github.blokaly.cfcs.model.TradeMessageProcessor
import github.blokaly.cfcs.model.TradeSummaryEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

@Component
class TradeSummaryProcessor(private val applicationEventPublisher: ApplicationEventPublisher) : TradeMessageProcessor {
    private val logger by LoggerDelegate()

    // Total number of trades
    private val total = AtomicLong()

    // Map of country code to no. of trades
    private val tradesByCountry = ConcurrentHashMap<String, AtomicLong>()

    // Map of currency pair to no. of trades
    private val tradesByCcyPair = ConcurrentHashMap<String, AtomicLong>()

    @EventListener
    override fun processTradeEvent(evt: TradeEvent) {
        logger.info("processing trade $evt")

        val country = evt.trade.originatingCountry.toUpperCase()
        val baseCcy = evt.trade.currencyFrom.toUpperCase()
        val termsCcy = evt.trade.currencyTo.toUpperCase()

        tradesByCountry.putIfAbsent(country, AtomicLong(1))?.incrementAndGet()
        tradesByCcyPair.putIfAbsent("$baseCcy$termsCcy", AtomicLong(1))?.incrementAndGet()

        val noOfTrades = total.incrementAndGet()
        val byCountry = tradesByCountry.entries.associate { it.key to it.value.get() }
        val byCcyPair = tradesByCcyPair.entries.associate { it.key to it.value.get() }

        applicationEventPublisher.publishEvent(TradeSummaryEvent(noOfTrades, byCountry, byCcyPair))
    }

    /**
     * For testing purpose, to clear the states during tear down call
     */
    fun reset() {
        total.set(0)
        tradesByCountry.clear()
        tradesByCcyPair.clear()
    }
}