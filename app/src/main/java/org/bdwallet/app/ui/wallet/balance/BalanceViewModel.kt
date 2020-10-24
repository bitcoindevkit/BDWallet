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

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.bdwallet.app.BDWApplication


class BalanceViewModel(application: Application) : AndroidViewModel(application) {

    val app = application as BDWApplication

    var convertToSats = MutableLiveData<Boolean>().apply {
        value = false
    }

    val _balance = MutableLiveData<String>().apply {
        value = refresh_balance()
    }
    val balance: LiveData<String> = _balance

    val _curValue = MutableLiveData<String>()
    val _price = MutableLiveData<String>()

    fun setCurValue(curValue: String) {
        _curValue.value = curValue
    }

    fun setPrice(price: String) {
        _price.value = price
    }

    fun refresh_balance(): String {
        val app = getApplication() as BDWApplication
        app.sync(100)

        val sats = app.getBalance()
        if (sats > 0 && !convertToSats.value) {
            val btc = sats/100000000f
            return "%.8f".format(btc).trimEnd('0').trimEnd('.')
        } else {
            return sats.toString()
        }
    }
}