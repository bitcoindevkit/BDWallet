package org.bdwallet.app.ui.wallet.util


import android.content.SharedPreferences
import androidx.lifecycle.LiveData

abstract class SharedPreferenceLiveData<T>(val sharedPrefs: SharedPreferences,
                                           val key: String,
                                           val defValue: T) : LiveData<T>() {

    private val preferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
        if (key == this.key) {
            value = getValueFromPreferences(key, defValue)
        }
    }

    abstract fun getValueFromPreferences(key: String, defValue: T): T

    override fun onActive() {
        super.onActive()
        value = getValueFromPreferences(key, defValue)
        sharedPrefs.registerOnSharedPreferenceChangeListener(preferenceChangeListener)
    }

    override fun onInactive() {
        sharedPrefs.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener)
        super.onInactive()
    }
}

class SharedPreferenceBooleanLiveData(sharedPrefs: SharedPreferences, key: String, defValue: Boolean) :
    SharedPreferenceLiveData<Boolean>(sharedPrefs, key, defValue) {
    override fun getValueFromPreferences(key: String, defValue: Boolean): Boolean = sharedPrefs.getBoolean(key, defValue)
}

fun SharedPreferences.booleanLiveData(key: String, defValue: Boolean): SharedPreferenceLiveData<Boolean> {
    return SharedPreferenceBooleanLiveData(this, key, defValue)
}