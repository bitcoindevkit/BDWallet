package org.bdwallet.app.ui.init

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.TextView
import org.bdwallet.app.R

class InitActivity : AppCompatActivity() {
    private var dialogHeight: Int = 1000
    private var dialogWidth: Int = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_init)
        showDialog()
        addButtonListeners()
    }

    private fun showDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_init)
        val dismissButton = dialog.findViewById<TextView>(R.id.dismiss_btn_text)
        dismissButton.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun addButtonListeners() {
        val recoverButton = findViewById<Button>(R.id.recover_btn)
        val createButton = findViewById<Button>(R.id.create_btn)
        var intent: Intent
        recoverButton.setOnClickListener {
            intent = Intent(
                this,
                RecoverWalletActivity::class.java
            )
            startActivity(intent)
        }
        createButton.setOnClickListener {
            intent = Intent(
                this,
                CreateWalletActivity::class.java
            )
            startActivity(intent)
        }
    }
}