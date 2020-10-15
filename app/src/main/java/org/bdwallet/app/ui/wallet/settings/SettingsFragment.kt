package org.bdwallet.app.ui.wallet.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import org.bdwallet.app.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

    }
    //had to take out CompatPreferenceSwitch because it was unable to invoke BDWallet.Instance methods
}







