package org.bdwallet.app

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class EntryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val pref = getSharedPreferences("init", Context.MODE_PRIVATE)
        val intent = Intent(
            this,
            //Class.forName(getNextActivityName(pref.getBoolean("walletInitialized", false)))
            InitActivity::class.java
        )
        startActivity(intent)
    }

    private fun getNextActivityName(walletInitialized: Boolean): String {
        if (walletInitialized) {
            return LoginActivity::class.simpleName!!
        }
        return InitActivity::class.simpleName!!
    }
}