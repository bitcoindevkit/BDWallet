package org.btcdk.app

import android.app.Application
import org.btcdk.jni.BtcDkApi
import org.btcdk.jni.Network

class ExampleApp : Application() {

    val btcDkApi = BtcDkApi()
    val network = Network.Testnet
}