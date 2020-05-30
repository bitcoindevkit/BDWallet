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

package org.bdk.app.ui.balance

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.bdk.app.ExampleApp
import java.nio.file.Path

class BalanceViewModel(application: Application) : AndroidViewModel(application) {

    private val _balance = MutableLiveData<String>().apply {
        val app = application as ExampleApp
        val workDir: Path = app.filesDir.toPath()
        val bdkApi = app.bdkApi
        val network = app.network
        val config = bdkApi.loadConfig(workDir, network)
        value = if (config.isPresent) {
            bdkApi.balance().balance.toString()
        } else {
            "UNKNOWN"
        }
    }
    val balance: LiveData<String> = _balance
}