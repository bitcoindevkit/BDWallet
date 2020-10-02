package org.bdwallet.app.ui.wallet.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import org.bdwallet.app.BDWApplication
import org.bdwallet.app.R


class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        this.checkSwitch()
    }

    private fun checkSwitch() {
        var BTCtoSAT =  findPreference<SwitchPreferenceCompat>("sat_convert")
        if (BTCtoSAT != null) {
            BTCtoSAT.onPreferenceChangeListener
        }
        {
            if (BTCtoSAT != null) {
                if (BTCtoSAT.isEnabled){ //switch from BTC to SAT

                } else { //from SAT to BTC

                }
            }
        }


    }

}