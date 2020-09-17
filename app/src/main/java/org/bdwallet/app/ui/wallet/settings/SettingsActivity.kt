package org.bdwallet.app.ui.wallet.settings

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import org.bdwallet.app.R
import org.bdwallet.app.ui.init.InitActivity

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true) // enable back button on action bar
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

}