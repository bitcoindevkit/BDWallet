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
import android.util.Log
import org.bitcoindevkit.bdkjni.Lib
import org.bitcoindevkit.bdkjni.Types.*


class BDWApplication : Application() {
    private lateinit var lib: Lib
    private lateinit var walletPtr: WalletPtr
    private lateinit var name: String
    private lateinit var network: String
    private lateinit var path: String
    private lateinit var descriptor: String
    private lateinit var changeDescriptor: String
    private lateinit var electrumUrl: String
    private var electrumProxy: String? = null

    override fun onCreate() {
        super.onCreate()
        Lib.load()
        this.lib = Lib()
        setDefaults()
    }

    override fun onTerminate() {
        super.onTerminate()
        lib.destructor(walletPtr)
    }

    // Set default wallet settings (strings located in R file)
    private fun setDefaults() {
        this.name = getString(R.string.app_name)
        this.network = getString(R.string.app_network)
        this.path = applicationContext.filesDir.toString()
        this.electrumUrl = getString(R.string.app_electrum_url)
        this.electrumProxy = getString(R.string.app_electrum_proxy).ifEmpty { null }
    }

    // Get mapping from String to Network enum
    private fun getNetworkMap(): Map<String, Network> {
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
        change_descriptor: String,
        electrum_url: String,
        electrum_proxy: String?,
    ) {
        this.name = name
        this.network = network
        this.path = path
        this.descriptor = descriptor
        this.changeDescriptor = change_descriptor
        this.electrumUrl = electrum_url
        this.electrumProxy = electrum_proxy

        walletPtr = lib.constructor(
            WalletConstructor(
                name,
                getNetworkMap().getValue(network),
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
    fun createWallet(descriptor: String, changeDescriptor: String) {
        setDefaults()
        initialize(
            name,
            network,
            path,
            descriptor,
            changeDescriptor,
            electrumUrl,
            electrumProxy
        )
        saveWalletPrefs()
    }

    // Save the constructor parameters in SharedPreferences so that wallet can be reloaded
    private fun saveWalletPrefs() {
        val editor: SharedPreferences.Editor = getSharedPreferences("saved_wallet", Context.MODE_PRIVATE).edit()
        editor.putBoolean("initialized", true)
        editor.putString("name", name)
        editor.putString("network", network)
        editor.putString("path", path)
        editor.putString("descriptor", descriptor)
        editor.putString("changeDescriptor", changeDescriptor)
        editor.putString("electrum_url", electrumUrl)
        editor.putString("electrum_proxy", electrumProxy)
        editor.commit()
    }

    // Returns a new public address for depositing into this wallet
    fun getNewAddress(): String {
        return lib.get_new_address(walletPtr)
    }

    // Sync with the blockchain to show balance & new incoming transactions
    fun sync(max_address: Int?=null) {
        lib.sync(walletPtr, max_address)
    }

    // Returns the list of UTXOs that are spendable by this wallet
    fun listUnspent(): List<UTXO> {
        return lib.list_unspent(walletPtr)
    }

    // Returns the total balance of this wallet in satoshis (the sum of UTXOs)
    fun getBalance(): Long {
        return lib.get_balance(walletPtr)
    }

    // Return the wallet's transaction history
    fun listTransactions(): List<TransactionDetails> {
        return lib.list_transactions(walletPtr)
    }

    // Create a transaction 'template' to be signed and broadcasted
    // Throws an exception if insufficient balance or invalid recipient address
    fun createTx(
        fee_rate: Float,
        addressees: List<Pair<String, String>>,
        send_all: Boolean?=false,
        utxos: List<String>?=null,
        unspendable: List<String>?=null,
        policy: Map<String, List<String>>?=null
    ): CreateTxResponse {
        return lib.create_tx(walletPtr, fee_rate, addressees, send_all, utxos, unspendable, policy)
    }

    fun sign(psbt: String, assume_height: Int?=null): SignResponse {
        return lib.sign(walletPtr, psbt, assume_height)
    }

    fun extract_psbt(psbt: String): RawTransaction {
        return lib.extract_psbt(walletPtr, psbt)
    }

    // Broadcasts a transaction to the bitcoin network
    fun broadcast(raw_tx: String): Txid {
        return lib.broadcast(walletPtr, raw_tx)
    }

    // TODO what is this function for?
    fun publicDescriptors(): PublicDescriptorsResponse {
        return lib.public_descriptors(walletPtr)
    }

    // Generate a new mnemonic and tpriv (for creating wallet)
    fun generateExtendedKey(mnemonicWordCount: Int): ExtendedKeys {
        return lib.generate_extended_key(getNetworkMap().getValue(network), mnemonicWordCount)
    }

    // Use a mnemonic to calculate the tpriv (for recovering wallet)
    fun createExtendedKeys(mnemonic: String): ExtendedKeys {
        return lib.create_extended_keys(getNetworkMap().getValue(network), mnemonic)
    }

    // Concatenate tpriv to create descriptor
    fun createDescriptor(keys: ExtendedKeys): String {
        return ("wpkh(" + keys.ext_priv_key + "/0/*)")
    }

    fun createChangeDescriptor(keys: ExtendedKeys): String {
        return ("wpkh(" + keys.ext_priv_key + "/1/*)")
    }
}