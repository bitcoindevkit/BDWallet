/*
 * Copyright 2020 BDK Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.bdwallet.app.ui.wallet.balance

import android.content.Intent
import android.graphics.Color
import android.nfc.Tag
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import org.bdwallet.app.BDWApplication
import org.bdwallet.app.R
import org.bdwallet.app.ui.wallet.history.HistoryActivity
import org.bdwallet.app.ui.wallet.settings.SettingsActivity
import retrofit2.Call
import retrofit2.Response
import kotlinx.android.synthetic.main.fragment_balance.*
//import java.util.logging.Handler
import android.os.Handler;
import kotlin.random.Random


class BalanceFragment : Fragment() {
    var coinService: CoinService? = null
    private lateinit var balanceViewModel: BalanceViewModel
    lateinit var convertedValueTxtView: TextView
    lateinit var beforeconvertedValueTxtView: TextView
    lateinit var btcPrice: TextView
    lateinit var mainHandler: Handler
    private val money = arrayOf("USD", "EUR", "GBP")
    private val coin = arrayOf(
        "BTC",
        "ETH",
        "ETC",
        "XRP",
        "LTC",
        "XMR",
        "DASH",
        "MAID",
        "AUR",
        "XEM"
    )
    private val updateTextTask = object : Runnable {
        override fun run() {
//            Log.e("run","run")
            var amount = BDWApplication.instance.getBalance().toString()
            convertedValueTxtView.text = amount
            calculateValue(amount)
            mainHandler.postDelayed(this, 1000)
        }
    }
//    @SuppressLint("RestrictedApi")
    override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
): View? {


        coinService = Common.getCoinService()
        super.onCreateView(inflater, container, savedInstanceState)
        mainHandler = Handler(Looper.getMainLooper())
        balanceViewModel = ViewModelProvider(this).get(BalanceViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_balance, container, false)
        ////
//        convertedValueTxtView = root.findViewById(R.id.balance_crypto)
//        val textView: TextView = root.findViewById(R.id.text_balance)
        beforeconvertedValueTxtView = root.findViewById(R.id.balance_local)
        ////
        convertedValueTxtView = root.findViewById(R.id.balance_crypto)
        btcPrice = root.findViewById(R.id.price_crypto)
//        convertedValueTxtView.text = "1"
//        var amount:String = BDWApplication.instance.getBalance().toString()
//        convertedValueTxtView.text = amount


    ///


//        balanceViewModel.balance.observe(viewLifecycleOwner, Observer {
//            beforeconvertedValueTxtView.text = it
//        })
//        calculateValue(amount)
        var walletActivity = activity as AppCompatActivity
//        walletActivity.supportActionBar?.setShowHideAnimationEnabled(false)
        walletActivity.supportActionBar?.hide()
        walletActivity.window.statusBarColor = Color.TRANSPARENT
        walletActivity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        addButtonListener(root.findViewById(R.id.settings_btn), root.findViewById(R.id.history_btn))
        return root
    }

    override fun onPause() {
        Log.e("onPause","onPause")
        super.onPause()
        mainHandler.removeCallbacks(updateTextTask)
    }


    override fun onResume() {
        Log.e("ONRESUME","Resume")
        (activity as AppCompatActivity).supportActionBar!!.hide()
        super.onResume()
        mainHandler.post(updateTextTask)
    }
    //////
    private fun calculateValue(userBalance: String){

//        val coinName = toSpinner.getItems<String>()[toSpinner.selectedIndex]
//        val fromCoin = fromSpinner.getItems<String>()[fromSpinner.selectedIndex]
        val coinName = "USD"
        val fromCoin = "BTC"
//        coinService = Common.getCoinService() // Ethan had to add this line to prevent null pointer exception
        coinService!!.calculateValue(fromCoin, coinName).enqueue(object : retrofit2.Callback<Coin> {
            override fun onFailure(call: Call<Coin>?, t: Throwable?) {

            }

            override fun onResponse(call: Call<Coin>?, response: Response<Coin>?) {
                //SUCCESS
                println("SUCCESS")
                showData(userBalance, response!!.body()!!.USD)
            }
        })
    }
    /////

    private fun showData(userBalance: String, coinName: String) {
        Thread(Runnable {var i=0;
            while(i<Int.MAX_VALUE){
                i++
            }
            this.activity?.runOnUiThread(java.lang.Runnable{
                var result:Double = userBalance.toDouble() * coinName.toDouble()

                btcPrice.setText("$ " + coinName)
                beforeconvertedValueTxtView.setText("$ " + result.toString())
            })
        }).start()



    }




    private fun addButtonListener(settingsButton: ImageButton, historyButton: Button) {
        settingsButton.setOnClickListener {
            startActivity(Intent(requireContext(), SettingsActivity::class.java))
        }
        historyButton.setOnClickListener {
            startActivity(Intent(requireContext(), HistoryActivity::class.java))
        }
    }
}
