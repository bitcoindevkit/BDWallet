package org.bdwallet.app.ui.login

import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.bdwallet.app.BDWApplication
import org.bdwallet.app.R
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URL


class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        addPasswordListeners()
    }

    private fun addPasswordListeners() {
        val passwordText = findViewById<TextView>(R.id.seed_text_1)
        passwordText.imeOptions = EditorInfo.IME_ACTION_DONE
        passwordText.setOnEditorActionListener(){ _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                checkPassword(passwordText.text)
                true
            }
            false
        }
        passwordText.setOnKeyListener { _, actionId, keyEvent ->
            if (actionId == KeyEvent.KEYCODE_ENTER && keyEvent.action == KeyEvent.ACTION_UP) {
                checkPassword(passwordText.text)
                true
            }
            false
        }
    }

    private fun checkPassword(password: CharSequence) {
        // TODO: check if password is correct
        // TODO: How to get the password from toml

        showWrongPasswordDialog()
    }



    private fun showWrongPasswordDialog() {
        val alertDialog = AlertDialog.Builder(this)
            .setMessage(R.string.login_failed)
            .setCancelable(false)
            .setPositiveButton(R.string.ok_btn) { _, _ -> }
            .create()
        alertDialog.show()
    }
}
