package org.bdwallet.app.ui.init

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.Toast
import org.bdwallet.app.R
import org.bdwallet.app.ui.wallet.WalletActivity

class RecoverWalletActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recover_wallet)
        addButtonListener()
        supportActionBar!!.setDisplayHomeAsUpEnabled(true) // enable back button on action bar
    }

    private fun addButtonListener() {
        val createButton = findViewById<Button>(R.id.recover_btn)
        createButton.setOnClickListener {
            startActivity(Intent(this, WalletActivity::class.java))
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun showInvalidPhraseToast() {
        val myToast = Toast.makeText(applicationContext,R.string.toast_invalid_seed_phrase, Toast.LENGTH_SHORT)
        myToast.setGravity(Gravity.LEFT,200,200)
        myToast.show()
    }
}