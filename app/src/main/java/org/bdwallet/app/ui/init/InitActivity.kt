package org.bdwallet.app.ui.init

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import kotlinx.android.synthetic.main.dialog_init.*
import org.bdwallet.app.R

class InitActivity : AppCompatActivity() {
    var dialogHeight: Int = 1000
    var dialogWidth: Int = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_init)
        showDialog()
    }

    private fun showDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_init)
        val continueText = dialog.findViewById(R.id.dismiss) as TextView
        continueText.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
        dialog.window!!.setLayout(dialogWidth, dialogHeight)
    }
}