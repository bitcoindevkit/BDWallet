package org.btcdk.app.ui.withdraw

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class WithdrawViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is withdraw Fragment"
    }
    val text: LiveData<String> = _text
}