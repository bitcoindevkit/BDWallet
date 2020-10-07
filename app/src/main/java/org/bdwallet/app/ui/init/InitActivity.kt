package org.bdwallet.app.ui.init

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.bdwallet.app.BDWApplication
import org.bdwallet.app.R
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class InitActivity : AppCompatActivity() {

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
        val dismissButton = dialog.findViewById<TextView>(R.id.continue_btn_text)
        dismissButton.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun addButtonListeners() {
        val recoverButton = findViewById<Button>(R.id.back_btn)
        val createButton = findViewById<Button>(R.id.set_password_btn)
        recoverButton.setOnClickListener {
            startActivity(Intent(this, RecoverWalletActivity::class.java))
        }
        createButton.setOnClickListener {
            startActivity(Intent(this, CreateWalletSeedActivity::class.java))
        }
    }




}