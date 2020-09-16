package org.bdwallet.app.ui.init

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import org.bdwallet.app.R
import org.bdwallet.app.ui.wallet.WalletActivity

class CreateWalletSeedActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_wallet_seed)
        addButtonListener()
        supportActionBar!!.setDisplayHomeAsUpEnabled(true) // enable back button on action bar
    }

    private fun addButtonListener() {
        val createButton = findViewById<Button>(R.id.create_btn)
        createButton.setOnClickListener {
            // TODO: reminder dialog
            startActivity(Intent(this, WalletActivity::class.java))
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        startActivity(Intent(this, InitActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT))
        return true
    }
}