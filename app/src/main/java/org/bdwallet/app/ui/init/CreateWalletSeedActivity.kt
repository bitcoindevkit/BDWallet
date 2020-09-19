package org.bdwallet.app.ui.init

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import org.bdwallet.app.BDWApplication
import org.bdwallet.app.R
import org.bdwallet.app.ui.wallet.WalletActivity
import org.bitcoindevkit.library.Types.WalletConstructor

class CreateWalletSeedActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_wallet_seed)
        addButtonListener()
        showBackupDialog()
        supportActionBar!!.setDisplayHomeAsUpEnabled(true) // enable back button on action bar
    }

    private fun addButtonListener() {
        val createButton = findViewById<Button>(R.id.create_btn)
        createButton.setOnClickListener {
            showReminderDialog()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        startActivity(Intent(this, InitActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT))
        return true
    }

    private fun showBackupDialog() {
        val alertDialog = AlertDialog.Builder(this)
            .setTitle(R.string.backup_dialog_title)
            .setMessage(R.string.backup_dialog_body)
            .setCancelable(false)
            .setPositiveButton(R.string.continue_btn) { _, _ -> }
            .create()
        alertDialog.show()
    }

    private fun showReminderDialog() {
        val alertDialog = AlertDialog.Builder(this)
            .setTitle(R.string.reminder_dialog_title)
            .setMessage(R.string.reminder_dialog_body)
            .setCancelable(false)
            .setNegativeButton(R.string.back_btn) { _, _ -> }
            .setPositiveButton(R.string.reminder_dialog_btn) { _, _ ->
                // TODO: create wallet in bdk
                val walletConstructor = WalletConstructor("",
                    org.bitcoindevkit.library.Types.Network.testnet,
                    "",
                    "",
                    "",
                    "",
                    null
                )

                val app = application as BDWApplication
                app.startLib()
                    .createWallet(walletConstructor)
                finish()
                startActivity(Intent(this, WalletActivity::class.java))
            }
            .create()
        alertDialog.setOnShowListener {
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(resources.getColor(android.R.color.darker_gray))
        }
        alertDialog.show()
    }
}