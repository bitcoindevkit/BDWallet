package org.bdwallet.app.ui.init

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import org.bdwallet.app.R

class CreateWalletPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_wallet_password)
        addButtonListeners()
    }

    private fun addButtonListeners() {
        val setPasswordButton = findViewById<Button>(R.id.set_password_btn)
        val backButton = findViewById<Button>(R.id.back_btn)
        setPasswordButton.setOnClickListener {
            // TODO: check if passwords match and store to toml file
            startActivity(Intent(this, CreateWalletSeedActivity::class.java))
        }
        backButton.setOnClickListener {
            onBackPressed()
        }
    }
}