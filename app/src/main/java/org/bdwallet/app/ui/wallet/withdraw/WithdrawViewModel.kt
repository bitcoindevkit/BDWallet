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

package org.bdwallet.app.ui.wallet.withdraw

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.withContext
import org.bdwallet.app.BDWApplication
import org.bdwallet.app.ui.wallet.util.bitstamp.Bitstamp
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

class WithdrawViewModel(application: Application) : AndroidViewModel(application),
    CoroutineScope by MainScope()  {


    val app = application as BDWApplication
    val bitstamp = Bitstamp()
    val rounding = RoundingMode.HALF_EVEN
    val btcScale = 8
    val fiatScale = 2

    private val _fiatPrice = MutableLiveData<BigDecimal>().apply {
        value = BigDecimal.ZERO
    }
    val fiatPrice: LiveData<BigDecimal> = Transformations.distinctUntilChanged(_fiatPrice)

    private val _fiatRefreshing = MutableLiveData<Boolean>().apply {
        value = false
    }
    val fiatRefreshing: LiveData<Boolean> = _fiatRefreshing

    suspend fun refreshFiatPrice() {
        withContext(Dispatchers.Main) {
            _fiatRefreshing.value = true
        }
        withContext(Dispatchers.IO) {
            // TODO handle errors such as if user isn't connected to the internet
            val quote = bitstamp.getTickerService().getQuote()
            withContext(Dispatchers.Main) {
                _fiatPrice.value = BigDecimal(quote.last, MathContext.DECIMAL64).setScale(fiatScale, rounding)
                _fiatRefreshing.value = false
            }
        }
    }
}