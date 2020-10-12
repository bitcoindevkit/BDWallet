package org.bdwallet.app.ui.wallet.settings

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreferenceCompat
import org.bdwallet.app.BDWApplication
import org.bdwallet.app.R



class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

    }

//    private fun checkSwitch() {
//        var BTCtoSAT =  findPreference<SwitchPreferenceCompat>("sat_convert")
//       // val walletPreference: SharedPreferences = BDWApplication.instance.getSharedPreferences("saved_wallet", Context.MODE_PRIVATE)
//        var prefs : SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.context)
//        prefs.get
//        BTCtoSAT?.onPreferenceChangeListener?.onPreferenceChange(BTCtoSAT, true)
//
//        if (BTCtoSAT as Boolean){
//            BDWApplication.instance.setDenomination("SAT")
//        } else {
//            BDWApplication.instance.setDenomination("BTC")
//        }
//
//    }




}







