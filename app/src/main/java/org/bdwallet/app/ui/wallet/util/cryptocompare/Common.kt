package org.bdwallet.app.ui.wallet.util.cryptocompare

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Common {
    companion object {
        val API_URL = "https://min-api.cryptocompare.com"
        val BASE_URL = "https://cryptocompare.com"
        val ETH_IMAGE = StringBuilder(BASE_URL).append("media/20646/eth_logo.png").toString()
        val BTC_IMAGE = StringBuilder(BASE_URL).append("/media/19633/btc.png").toString()
        val ETC_IMAGE = StringBuilder(BASE_URL).append("/media/20275/etc2.png").toString()
        val LTC_IMAGE = StringBuilder(BASE_URL).append("/media/19782/litecoin-logo.png").toString()
        val XRP_IMAGE = StringBuilder(BASE_URL).append("/media/19972/ripple.png").toString()
        val XMR_IMAGE = StringBuilder(BASE_URL).append("/media/19969/xmr.png").toString()
        val DASH_IMAGE = StringBuilder(BASE_URL).append("/media/20026/dash.png").toString()
        val MAID_IMAGE = StringBuilder(BASE_URL).append("/media/352247/maid.png").toString()
        val AUR_IMAGE = StringBuilder(BASE_URL).append("/media/19608/aur.png").toString()
        val XEM_IMAGE = StringBuilder(BASE_URL).append("/media/20490/xem.png").toString()
        val USD_IMAGE = StringBuilder(BASE_URL).append("/media/351559/dlc.png").toString()
        val EUR_IMAGE = StringBuilder(BASE_URL).append("/media/1382471/euc.png").toString()
        val GBP_IMAGE = StringBuilder(BASE_URL).append("/media/350897/gbt.png").toString()

        fun getCoinService(): CoinService {
            return RetrofitClient().getClient(API_URL).create(CoinService::class.java)
        }

        fun <T> callback(fn: (Throwable?, Response<T>?) -> Unit): Callback<T> {
            return object : Callback<T> {
                override fun onResponse(call: Call<T>, response: retrofit2.Response<T>) = fn(null, response)
                override fun onFailure(call: Call<T>, t: Throwable) = fn(t, null)
            }
        }
    }


}