package org.bdwallet.app.ui.init

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import org.bdwallet.app.BDWApplication
import org.bdwallet.app.R
import org.bdwallet.app.ui.wallet.WalletActivity
import org.bitcoindevkit.bdkjni.Types.*

class CreateWalletSeedActivity : AppCompatActivity() {
    private lateinit var keys: ExtendedKeys

    override fun onStart() {
        super.onStart()
        setContentView(R.layout.activity_create_wallet_seed)
        this.fillSeedWords()
        this.addButtonListener()
        this.showBackupDialog()
        supportActionBar!!.setDisplayHomeAsUpEnabled(true) // enable back button on action bar
    }

    private fun fillSeedWords() {
        this.keys = BDWApplication.instance.generateExtendedKey(12)
        val words: List<String> = this.keys.mnemonic.split(' ')
        val seedViews: List<Int> = listOfNotNull<Int>(R.id.seed_text_1, R.id.seed_text_2, R.id.seed_text_3, R.id.seed_text_4,
            R.id.seed_text_5, R.id.seed_text_6, R.id.seed_text_7, R.id.seed_text_8, R.id.seed_text_9, R.id.seed_text_10,
            R.id.seed_text_11, R.id.seed_text_12)
        for (x in 0..11)
            findViewById<TextView>(seedViews[x]).text = words[x]
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
                val descriptor: String = BDWApplication.instance.createDescriptor(this.keys)
                BDWApplication.instance.createWallet(descriptor)
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