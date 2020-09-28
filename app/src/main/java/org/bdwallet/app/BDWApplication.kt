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
import android.util.Log
import org.bitcoindevkit.bdkjni.Lib
import org.bitcoindevkit.bdkjni.Types.*


class BDWApplication : Application() {
    private lateinit var lib: Lib
    private lateinit var walletPtr: WalletPtr
    private lateinit var keys: ExtendedKeys

    override fun onCreate() {
        super.onCreate()
        Lib.load()
        this.lib = Lib()
        instance = this
    }

    override fun onTerminate() {
        super.onTerminate()
        this.lib.destructor(this.walletPtr)
    }

    companion object {
        lateinit var instance: BDWApplication
            private set
    }

    fun createWallet(descriptor: String) {
        // TODO these hardcoded values may need to change eventually
        // TODO save the descriptor so that it can be loaded
        Log.d("DESCRIPTOR", "DESC: $descriptor")
        this.constructor(
            "testnet",
            Network.testnet,
            this.applicationContext.filesDir.toString(),
            descriptor,
            null,
            "tcp://testnet.aranguren.org:51001",
            null)
    }

    private fun constructor(
        name: String,
        network: Network, // ex. Network.testnet or Network.regtest
        path: String,
        descriptor: String,
        change_descriptor: String?,
        electrum_url: String,
        electrum_proxy: String?,
    ) {
        val walletConstructor: WalletConstructor = WalletConstructor(name, network, path, descriptor, change_descriptor, electrum_url, electrum_proxy)
        this.walletPtr = this.lib.constructor(walletConstructor)
    }

    // Returns a new public address for depositing into this wallet
    fun getNewAddress(): String {
        return this.lib.get_new_address(this.walletPtr)
        // TODO does toml file need to be manually updated?
    }

    fun sync(max_address: Int?=null) {
        // TODO what is this function for?
        this.lib.sync(this.walletPtr, max_address)
    }

    // Returns the list of UTXOs that are spendable by this wallet
    fun listUnspent(): List<UTXO> {
        return this.lib.list_unspent(this.walletPtr)
    }

    // Returns the total balance of this wallet (the sum of UTXOs)
    fun getBalance(): Long {
//        walletPtr = 100
        return this.lib.get_balance(this.walletPtr)
    }

    fun listTransactions(): List<TransactionDetails> {
        // TODO does this give the transaction history??
        return this.lib.list_transactions(this.walletPtr)
    }

    fun createTx(
        fee_rate: Float,
        addressees: List<Pair<String, String>>,
        send_all: Boolean?=false,
        utxos: List<String>?=null,
        unspendable: List<String>?=null,
        policy: Map<String, List<String>>?=null
    ): CreateTxResponse {
        // TODO how do we use this function?
        return this.lib.create_tx(this.walletPtr, fee_rate, addressees, send_all, utxos, unspendable, policy)
    }

    fun sign(psbt: String, assume_height: Int?=null): SignResponse {
        // TODO what is this function for?
        return this.lib.sign(this.walletPtr, psbt, assume_height)
    }

    fun extract_psbt(psbt: String): RawTransaction {
        // TODO what is this function for?
        return this.lib.extract_psbt(this.walletPtr, psbt)
    }

    // Broadcasts a transaction to the bitcoin network
    fun broadcast(raw_tx: String): Txid {
        return this.lib.broadcast(this.walletPtr, raw_tx)
    }

    fun publicDescriptors(): PublicDescriptorsResponse {
        // TODO what is this function for?
        return this.lib.public_descriptors(this.walletPtr)
    }

    // Generate a new mnemonic and tpriv, save the ExtendedKeys object (for creating wallet)
    fun generateExtendedKey(network: Network, mnemonicWordCount: Int): ExtendedKeys {
        this.keys = this.lib.generate_extended_key(network, mnemonicWordCount)
        return this.keys
    }

    // Use a mnemonic to calculate the tpriv, save the ExtendedKeys object (for recovering wallet)
    fun createExtendedKeys(network: Network, mnemonic: String): ExtendedKeys {
        this.keys = this.lib.create_extended_keys(network, mnemonic)
        return this.keys
    }

    // Concatenate tpriv to create descriptor, save the ExtendedKeys object (redundant but fine)
    fun createDescriptor(keys: ExtendedKeys): String {
        this.keys = keys
        return ("wpkh(" + this.keys.ext_priv_key + "/0/*)")
    }
}