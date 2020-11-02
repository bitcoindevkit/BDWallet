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

package org.bdwallet.app.ui.wallet.deposit

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.bdwallet.app.BDWApplication

private const val TAG = "DepositViewModel"

class DepositViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as BDWApplication


    private val _address = MutableLiveData<String>().apply {
        value = app.getNewAddress()
    }
    val address: LiveData<String> = _address

    fun refresh() {
        _address.apply { value = app.getNewAddress() }
    }
}