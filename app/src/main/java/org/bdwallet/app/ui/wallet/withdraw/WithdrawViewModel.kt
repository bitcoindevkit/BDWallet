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
import android.util.Log
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


private const val TAG = "WithdrawViewModel"

class WithdrawViewModel(application: Application) : AndroidViewModel(application),
    CoroutineScope by MainScope()  {


    val app = application as BDWApplication
}