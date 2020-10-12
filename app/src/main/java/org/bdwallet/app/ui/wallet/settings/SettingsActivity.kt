package org.bdwallet.app.ui.wallet.settings

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Switch
import androidx.preference.SwitchPreferenceCompat
import org.bdwallet.app.BDWApplication
import org.bdwallet.app.R
import org.bdwallet.app.ui.init.InitActivity
import org.jetbrains.anko.find

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true) // enable back button on action bar
        this.checkSwitch()
    }



    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun checkSwitch(){
        val sw1 = findViewById<Switch>(R.id.switch1)

        sw1.setOnCheckedChangeListener { _, isChecked ->
            // val message = if (isChecked) "Switch1:ON" else "Switch1:OFF"
            if(isChecked){
                BDWApplication.instance.setDenomination("SAT")
            } else {
                BDWApplication.instance.setDenomination("BTC")
            }
        }
    }

}