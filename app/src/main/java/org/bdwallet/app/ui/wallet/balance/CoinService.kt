package org.bdwallet.app.ui.wallet.balance


import org.bdwallet.app.ui.wallet.balance.Coin
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface CoinService {
    @GET("data/price")
    fun calculateValue(@Query("fsym") from: String , @Query("tsyms") to: String): Call<Coin>
}