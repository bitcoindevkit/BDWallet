package org.btcdk.app.ui.balance

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.btcdk.app.ExampleApp
import java.nio.file.Path

class BalanceViewModel(application: Application) : AndroidViewModel(application) {

    private val _balance = MutableLiveData<String>().apply {
        val app = application as ExampleApp
        val workDir: Path = app.filesDir.toPath()
        val btcDkApi = app.btcDkApi
        val network = app.network
        val config = btcDkApi.loadConfig(workDir, network)
//        value = if (config.isPresent) {
//            btcDkApi.balance().balance.toString()
//        } else {
//            "UNKNOWN"
//        }
        value = "UNKNOWN"
    }
    val balance: LiveData<String> = _balance
}