package org.bdwallet.app.ui.wallet.util.bitstamp

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Bitstamp {
     val baseUrl = "https://bitstamp.net/"
     var retrofit: Retrofit? = null

     fun getClient(): Retrofit {
        if(retrofit == null){
            retrofit = Retrofit.Builder()
                        .baseUrl(baseUrl)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()

        }
        return retrofit!!
    }

     fun getTickerService(): TickerService {
         return getClient().create(TickerService::class.java)
     }
}