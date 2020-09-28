package org.bdwallet.app

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import org.bdwallet.app.ui.init.InitActivity
import org.bdwallet.app.ui.wallet.WalletActivity
import org.bitcoindevkit.bdkjni.Types.Network

class EntryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val savedWallet: SharedPreferences = getSharedPreferences("saved_wallet", Context.MODE_PRIVATE)

        val isWalletInitialized: Boolean = savedWallet.contains("initialized") && savedWallet.getBoolean("initialized", false)
        if (isWalletInitialized) {
            val name: String = savedWallet.getString("name", "testnet")!!
            val network: String = savedWallet.getString("network", "testnet")!!
            val path: String = savedWallet.getString("path", "")!!
            val descriptor: String = savedWallet.getString("descriptor", "")!!
            val electrumUrl: String = savedWallet.getString("electrum_url", "")!!

            val networkMap: Map<String, Network> = BDWApplication.instance.getNetworkMap()
            BDWApplication.instance.constructor(name, networkMap.getValue(network), path, descriptor, null, electrumUrl, null)
        }

        startActivity(Intent(this, Class.forName(getNextActivityName(isWalletInitialized))))
    }

    private fun getNextActivityName(walletInitialized: Boolean): String {
        return if (walletInitialized) WalletActivity::class.qualifiedName!! else InitActivity::class.qualifiedName!!
    }
}