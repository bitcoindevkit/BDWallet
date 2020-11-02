/*
 * Copyright 2020 BDK Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.bdwallet.app.ui.wallet.balance

/*
 * Copyright 2020 BDK Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import android.app.Application
import android.nfc.Tag
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.preference.PreferenceManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.withContext
import org.bdwallet.app.BDWApplication
import org.bdwallet.app.ui.wallet.util.SharedPreferenceBooleanLiveData
import org.bdwallet.app.ui.wallet.util.bitstamp.Bitstamp
import java.math.BigDecimal
import java.math.MathContext.DECIMAL64
import java.math.RoundingMode.HALF_EVEN

private const val TAG = "BalanceViewModel"

class BalanceViewModel(application: Application) : AndroidViewModel(application),
    CoroutineScope by MainScope() {

    val app = application as BDWApplication
    val bitstamp = Bitstamp()
    val rounding = HALF_EVEN
    val btcScale = 8
    val fiatScale = 2

    val convertToSats = SharedPreferenceBooleanLiveData(
        PreferenceManager.getDefaultSharedPreferences(app),
        "sats_convert",
        false
    )

    private val _satBalance = MutableLiveData<Long>().apply {
        value = 0
    }
    val satBalance: LiveData<Long> = Transformations.distinctUntilChanged(_satBalance)

    val btcBalance: LiveData<BigDecimal> = Transformations.map(satBalance) { sats ->
        if (sats > 0) {
            satToBtc(sats)
        } else {
            BigDecimal.ZERO
        }
    }

    val walletBalance: LiveData<String> =
        Transformations.switchMap(convertToSats) { convertToSats ->
            if (convertToSats) {
                Transformations.map(satBalance) { sats ->
                    sats.toString()
                }
            } else {
                Transformations.map(btcBalance) { btc ->
                    btc.toPlainString()
                }
            }
        }

    private val _btcRefreshing = MutableLiveData<Boolean>().apply {
        value = false
    }
    val btcRefreshing: LiveData<Boolean> = _btcRefreshing

    private val _fiatPrice = MutableLiveData<BigDecimal>().apply {
        value = BigDecimal.ZERO
    }
    val fiatPrice: LiveData<BigDecimal> = Transformations.distinctUntilChanged(_fiatPrice)

    val fiatValue: LiveData<BigDecimal> = Transformations.map(fiatPrice) { price ->
        price.multiply(satToBtc(satBalance.value!!), DECIMAL64)
            .setScale(fiatScale, rounding)
    }

    private val _fiatRefreshing = MutableLiveData<Boolean>().apply {
        value = false
    }
    val fiatRefreshing: LiveData<Boolean> = _fiatRefreshing

    fun satToBtc(sats: Long): BigDecimal {
        return BigDecimal.valueOf(sats).divide(BigDecimal.valueOf(100000000))
            .setScale(btcScale, rounding)
            .stripTrailingZeros()
    }

    suspend fun refreshSatBalance() {
        withContext(Dispatchers.Main) {
            _btcRefreshing.value = true
        }
        withContext(Dispatchers.IO) {
            app.sync(100)
            val balance = app.getBalance()
            withContext(Dispatchers.Main) {
                _satBalance.value = balance
                _btcRefreshing.value = false
            }
        }
    }

    suspend fun refreshFiatPrice() {
        withContext(Dispatchers.Main) {
            _fiatRefreshing.value = true
        }
        withContext(Dispatchers.IO) {
            // TODO handle errors such as if user isn't connected to the internet
            val quote = bitstamp.getTickerService().getQuote()
            withContext(Dispatchers.Main) {
                _fiatPrice.value = BigDecimal(quote.last, DECIMAL64).setScale(fiatScale, rounding)
                _fiatRefreshing.value = false
            }
        }
    }
}