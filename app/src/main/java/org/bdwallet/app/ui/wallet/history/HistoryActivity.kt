package org.bdwallet.app.ui.wallet.history

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import org.bdwallet.app.R

class HistoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true) // enable back button on action bar
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}