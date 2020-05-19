package org.btcdk.app.ui.balance

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BalanceViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is balance Fragment"
    }
    val text: LiveData<String> = _text
}