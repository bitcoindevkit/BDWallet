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
import android.content.Context
import android.content.SharedPreferences
import org.bitcoindevkit.bdkjni.Lib
import org.bitcoindevkit.bdkjni.Types.*


class BDWApplication : Application() {
    private lateinit var lib: Lib
    private lateinit var walletPtr: WalletPtr
    private lateinit var name: String
    private lateinit var network: String
    private lateinit var path: String
    private lateinit var descriptor: String
    private lateinit var electrumUrl: String
    private lateinit var denominationType : String

    companion object {
        lateinit var instance: BDWApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        Lib.load()
        this.lib = Lib()
        this.setDefaults()
        instance = this
    }

    override fun onTerminate() {
        super.onTerminate()
        this.lib.destructor(this.walletPtr)
    }

    // Set default wallet settings - TODO these will have to change eventually
    private fun setDefaults() {
        this.name = "testnet"
        this.network = "testnet"
        this.path = this.applicationContext.filesDir.toString()
        this.electrumUrl = "tcp://testnet.aranguren.org:51001"
        this.denominationType = "BTC"
    }

    // Get mapping from String to Network enum
    fun getNetworkMap(): Map<String, Network> {
        return mapOf(
            "testnet" to Network.testnet,
            "regtest" to Network.regtest,
            //"mainnet" to Network.mainnet,
        )
    }

    // To be called directly when auto-loading a saved wallet
    // Initializes data members and creates/opens a wallet
    fun initialize(
        name: String,
        network: String,
        path: String,
        descriptor: String,
        change_descriptor: String?,
        electrum_url: String,
        electrum_proxy: String?,
    ) {
        this.name = name
        this.network = network
        this.path = path
        this.descriptor = descriptor
        this.electrumUrl = electrum_url

        this.walletPtr = this.lib.constructor(
            WalletConstructor(
                name,
                this.getNetworkMap().getValue(network),
                path,
                descriptor,
                change_descriptor,
                electrum_url,
                electrum_proxy
            )
        )
    }

    // To be called by the CreateWallet and RecoverWallet viewmodels
    // Constructs a new wallet with default values
    // Saves the wallet constructor parameters in SharedPreferences
    fun createWallet(descriptor: String) {
        this.setDefaults()
        this.initialize(
            this.name,
            this.network,
            this.path,
            descriptor,
            null,
            this.electrumUrl,
            null
        )
        this.saveWalletPrefs()
    }

    // Save the constructor parameters so that wallet can be reloaded
    private fun saveWalletPrefs() {
        val editor: SharedPreferences.Editor = getSharedPreferences("saved_wallet", Context.MODE_PRIVATE).edit()
        editor.putBoolean("initialized", true)
        editor.putString("name", this.name)
        editor.putString("network", this.network)
        editor.putString("path", this.path)
        editor.putString("descriptor", this.descriptor)
        editor.putString("electrum_url", this.electrumUrl)
        editor.putString("denomType", this.denominationType)
        editor.commit()
    }

    // Returns a new public address for depositing into this wallet
    fun getNewAddress(): String {
        return this.lib.get_new_address(this.walletPtr)
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

    // Generate a new mnemonic and tpriv (for creating wallet)
    fun generateExtendedKey(mnemonicWordCount: Int): ExtendedKeys {
        return this.lib.generate_extended_key(this.getNetworkMap().getValue(this.network), mnemonicWordCount)
    }

    // Use a mnemonic to calculate the tpriv (for recovering wallet)
    fun createExtendedKeys(mnemonic: String): ExtendedKeys {
        return this.lib.create_extended_keys(this.getNetworkMap().getValue(this.network), mnemonic)
    }

    // Concatenate tpriv to create descriptor
    fun createDescriptor(keys: ExtendedKeys): String {
        return ("wpkh(" + keys.ext_priv_key + "/0/*)")
    }

    fun setDenomination(denom : String) {
        this.denominationType = denom
        this.saveWalletPrefs() //persistent memory
    }
}