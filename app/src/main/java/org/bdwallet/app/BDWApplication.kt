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
import org.magicalbitcoin.wallet.Lib
import org.magicalbitcoin.wallet.Types.WalletConstructor
import org.magicalbitcoin.wallet.Types.WalletPtr


class BDWApplication : Application() {
    private lateinit var lib: Lib
    private lateinit var walletPtr: WalletPtr
    private lateinit var walletConstructor: WalletConstructor

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

    fun createWallet(walletConstructor: WalletConstructor) {
        this.walletConstructor = walletConstructor
        // walletPtr = lib.constructor(walletConstructor)
    }



}