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

package org.bdwallet.app.ui.wallet

import android.app.Application
import android.util.Log
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.emptyPreferences
import androidx.datastore.preferences.preferencesKey
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.channels.broadcast
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.flow.*
import org.bdwallet.app.BDWApplication
import org.bdwallet.app.ui.wallet.util.bitstamp.Bitstamp
import org.bdwallet.app.ui.wallet.util.bitstamp.Quote
import java.io.IOException
import java.math.BigDecimal
import java.math.MathContext.DECIMAL64
import java.math.RoundingMode.HALF_EVEN

class WalletViewModel(application: Application) : AndroidViewModel(application),
    CoroutineScope by MainScope() {

    val app = application as BDWApplication
    val bitstamp = Bitstamp()

    private val dataStore = app.createDataStore(name = "settings", scope = viewModelScope)

    val convertToSats: LiveData<Boolean> = newBalanceUnitsFlow()
        .onStart { Log.d(TAG, "start convertToSats") }
        .onCompletion { Log.d(TAG, "complete convertToSats") }
        .map { it == BalanceUnits.SAT }
        .flowOn(Dispatchers.IO)
        .asLiveData()

    private val sharedSatBalance = newSatBalanceFlow()
        .onStart { Log.d(TAG, "start sharedSatBalance") }
        .onCompletion { Log.d(TAG, "complete sharedSatBalance") }
        .shareIn(viewModelScope, SharingStarted.Lazily, 1)

    private val sharedBtcBalance = sharedSatBalance
        .onStart { Log.d(TAG, "start sharedBtcBalance") }
        .onCompletion { Log.d(TAG, "complete sharedBtcBalance") }
        .map { sats -> satToBtc(sats) }
        .shareIn(viewModelScope, SharingStarted.Lazily, 1)

    private val sharedFiatQuote = newFiatQuoteFlow()
        .shareIn(viewModelScope, SharingStarted.Lazily, 1)

    private val sharedFiatPrice: SharedFlow<BigDecimal> = sharedFiatQuote
        .map { quote -> BigDecimal(quote.last, DECIMAL64).setScale(FIAT_SCALE, ROUNDING) }
        .shareIn(viewModelScope, SharingStarted.Lazily, 1)

    val walletBalance: LiveData<Number> =
        newBalanceUnitsFlow()
            .onStart { Log.d(TAG, "start walletBalance") }
            .onCompletion { Log.d(TAG, "complete walletBalance") }
            .flatMapLatest { balanceUnits ->
                if (balanceUnits == BalanceUnits.SAT) {
                    sharedSatBalance
                        .map { sats -> sats }
                } else {
                    sharedBtcBalance
                        //.map { sat -> satToBtc(sat) }
                        .map { btc -> btc }
                }
            }
            .flowOn(Dispatchers.IO)
            .asLiveData()

    val fiatPrice: LiveData<BigDecimal> = sharedFiatPrice.asLiveData()

    val fiatValue: LiveData<BigDecimal> = sharedFiatPrice
        .combine(sharedBtcBalance) { price, btc ->
            price.multiply(btc, DECIMAL64).setScale(FIAT_SCALE, ROUNDING)
        }
        .asLiveData()

    fun newSatBalanceFlow(): Flow<Long> =
        ticker(20_000, 0).broadcast().asFlow()
            .onStart { Log.d(TAG, "start satBalance") }
            .onCompletion { Log.d(TAG, "complete satBalance") }
            .onEach { Log.d(TAG, "tick") }
            .map { app.sync(10) }
            .map { app.getBalance() }
            .onEach { Log.d(TAG, "balance $it") }
            .catch { e -> Log.e(TAG, "caught $e") }

    fun satToBtc(sats: Long): BigDecimal {
        return if (sats > 0) {
            BigDecimal.valueOf(sats).divide(BigDecimal.valueOf(100000000))
                .setScale(BTC_SCALE, ROUNDING)
                .stripTrailingZeros()
        } else {
            BigDecimal.ZERO
        }
    }

    fun newFiatQuoteFlow(): Flow<Quote> =
        ticker(10_000, 0).broadcast().asFlow()
            .onStart { Log.d(TAG, "start fiatQuote") }
            .onCompletion { Log.d(TAG, "complete fiatQuote") }
            .map { bitstamp.getTickerService().getQuote() }
            .catch { e -> Log.e(TAG, "caught $e") }
            .distinctUntilChanged()

    private fun newBalanceUnitsFlow(): Flow<BalanceUnits> = dataStore.data
        .onStart { Log.d(TAG, "start newBalanceUnitsFlow") }
        .onCompletion { Log.d(TAG, "complete newBalanceUnitsFlow") }
        .catch {
            if (it is IOException) {
                it.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preference ->
            val balanceUnits = preference[BALANCE_UNITS] ?: BalanceUnits.BTC.toString()
            BalanceUnits.valueOf(balanceUnits)
        }
        .onEach { balanceUnits -> Log.d(TAG, "new balanceUnits = $balanceUnits") }

    suspend fun setBalanceUnits(balanceUnits: BalanceUnits) {
        dataStore.edit { preferences ->
            preferences[BALANCE_UNITS] = balanceUnits.toString()
        }
    }

    fun newAddress(): LiveData<String> = flow<String> {
        emit(app.getNewAddress())
    }
        .onStart { Log.d(TAG, "start newAddress") }
        .onCompletion { Log.d(TAG, "complete newAddress") }
        .catch { e -> Log.e(TAG, "caught $e") }
        .flowOn(Dispatchers.IO)
        .asLiveData()

    companion object {
        private val BALANCE_UNITS = preferencesKey<String>("balance_units")
        private const val TAG = "WalletViewModel"

        private val ROUNDING = HALF_EVEN
        private const val BTC_SCALE = 8
        private const val FIAT_SCALE = 2
    }
}

enum class BalanceUnits {
    SAT, BTC
}