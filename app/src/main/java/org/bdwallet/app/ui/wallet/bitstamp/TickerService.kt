package org.bdwallet.app.ui.wallet.bitstamp


import retrofit2.http.GET

interface TickerService {
    @GET("/api/v2/ticker/btcusd/")
    suspend fun getQuote(): Quote
}