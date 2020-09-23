package org.bdwallet.app.ui.wallet.balance

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

 class RetrofitClient{
     var retrofit: Retrofit? = null

     fun getClient(baseUrl: String): Retrofit {
        if(retrofit == null){
            retrofit = Retrofit.Builder()
                        .baseUrl(baseUrl)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()

        }
        return retrofit!!
    }



}