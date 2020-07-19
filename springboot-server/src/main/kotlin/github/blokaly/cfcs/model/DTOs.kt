package github.blokaly.cfcs.model

import java.math.BigDecimal

data class Trade(val userId: String,
                 val currencyFrom: String,
                 val currencyTo: String,
                 val amountSell: BigDecimal,
                 val amountBuy: BigDecimal,
                 val rate: BigDecimal,
                 val timePlaced: String,
                 val originatingCountry: String)
