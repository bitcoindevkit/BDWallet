package org.btcdk.app.ui.init

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.btcdk.app.ExampleApp
import java.nio.file.Path

private const val TAG = "INIT_MODEL"

class InitViewModel(application: Application) : AndroidViewModel(application) {

    private val _words = MutableLiveData<List<String>>().apply {
        val app = application as ExampleApp
        val workDir: Path = app.filesDir.toPath()
        val btcDkApi = app.btcDkApi
        val network = app.network
        val config = btcDkApi.loadConfig(workDir, network)
        if (config.isPresent) {
            value = listOf("ERROR","ERROR","ERROR","ERROR","ERROR","ERROR","ERROR","ERROR","ERROR","ERROR", "ERROR", "ERROR")
        } else {
            val initResult = btcDkApi.initConfig(
                workDir, network,
                "test passphrase",
                ""
            ).get()
            val words = initResult.mnemonicWords.joinToString()

            Log.d(TAG, "initResult.depositAddress: ${initResult.depositAddress}")
            Log.d(TAG, "initResult.mnemonicWords: $words")
            value = initResult.mnemonicWords.toList()
        }
    }

    val words: LiveData<List<String>> = _words
}
