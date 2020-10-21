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
import org.bdwallet.app.ui.wallet.cryptocompare.Coin
import org.bdwallet.app.ui.wallet.cryptocompare.Common
import retrofit2.Call
import retrofit2.HttpException
import retrofit2.Response


class BalanceViewModel(application: Application) : AndroidViewModel(application) {

    var convertToSats = false

    val _balance = MutableLiveData<String>().apply {
        value = BDWApplication.instance.getBalance().toString()
        if (convertToSats && value != "0") {
            value = (value!!.toDouble() / 100000000).toString()
        }
    }

    val _curValue = MutableLiveData<String>()
    val _price = MutableLiveData<String>()

    fun setCurValue(curValue: String) {
        _curValue.value = curValue
    }

    fun setPrice(price: String) {
        _price.value = price
    }

    val balance: LiveData<String> = _balance
}