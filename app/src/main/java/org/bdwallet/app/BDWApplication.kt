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

package org.bdwallet.app

import android.app.Application
import org.bitcoindevkit.library.Lib
import org.bitcoindevkit.library.Types.WalletConstructor
import org.bitcoindevkit.library.Types.WalletPtr
import org.bitcoindevkit.library.Types.Network


class BDWApplication : Application() {
    private lateinit var lib: Lib
    private lateinit var walletPtr: WalletPtr
    private lateinit var walletConstructor: WalletConstructor

    override fun onCreate() {
        super.onCreate()
    }

    override fun onTerminate() {
        super.onTerminate()
    }

    companion object {
        init {
            Lib.load()
        }
    }

    fun startLib(): BDWApplication {
        // TODO: check if toml file is present
        lib = Lib()
        return this
    }

    fun createWallet(
        name: WalletConstructor,
        network: Network, // ex. Network.testnet or Network.regtest
        path: String,
        descriptor: String,
        change_descriptor: String?,
        electrum_url: String,
        electrum_proxy: String?,
    ) {
        this.walletConstructor = WalletConstructor(name, network, path, descriptor, change_descriptor, electrum_url, electrum_proxy)
        this.walletPtr = this.lib.constructor(this.walletConstructor)
    }



}