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
            val name: String = savedWallet.getString("name", null)!!
            val network: String = savedWallet.getString("network", null)!!
            val path: String = savedWallet.getString("path", null)!!
            val descriptor: String = savedWallet.getString("descriptor", null)!!
            val electrumUrl: String = savedWallet.getString("electrum_url", null)!!

            val networkMap: Map<String, Network> = BDWApplication.instance.getNetworkMap()
            BDWApplication.instance.initialize(
                name,
                network,
                path,
                descriptor,
                null,
                electrumUrl,
                null
            )
        }
        startActivity(Intent(this, Class.forName(getNextActivityName(isWalletInitialized))))
    }

    private fun getNextActivityName(walletInitialized: Boolean): String {
        return if (walletInitialized) WalletActivity::class.qualifiedName!! else InitActivity::class.qualifiedName!!
    }
}