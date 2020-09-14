package org.bdwallet.app

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import org.bdwallet.app.ui.init.InitActivity
import org.bdwallet.app.ui.login.LoginActivity

class EntryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val isWalletInitialized = getSharedPreferences("init", Context.MODE_PRIVATE)
            .getBoolean("walletInitialized", false)
        val intent = Intent(
            this,
            Class.forName(getNextActivityName(isWalletInitialized))
        )
        startActivity(intent)
    }

    private fun getNextActivityName(walletInitialized: Boolean): String {
        return if (walletInitialized) LoginActivity::class.qualifiedName!! else InitActivity::class.qualifiedName!!
    }
}