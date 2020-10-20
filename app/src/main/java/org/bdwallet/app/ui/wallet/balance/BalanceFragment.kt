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
import android.icu.text.DecimalFormat
import android.icu.text.NumberFormat
import android.icu.util.Currency
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import org.bdwallet.app.R
import org.bdwallet.app.ui.wallet.history.HistoryActivity
import org.bdwallet.app.ui.wallet.settings.SettingsActivity
import retrofit2.Call
import retrofit2.Response
//import java.util.logging.Handler
import android.os.Handler;
import android.widget.ProgressBar
import androidx.lifecycle.Observer
import org.bdwallet.app.ui.wallet.cryptocompare.Coin
import org.bdwallet.app.ui.wallet.cryptocompare.Common


class BalanceFragment : Fragment() {
    private lateinit var balanceViewModel: BalanceViewModel
    private lateinit var cryptoBalanceTextView: TextView
    private lateinit var localValueTextView: TextView
    private lateinit var btcPriceTextView: TextView
    private lateinit var cryptoBalanceProgressBar: ProgressBar
    private lateinit var localValueProgressBar: ProgressBar
    private lateinit var btcPriceProgressBar: ProgressBar
    private lateinit var mainHandler: Handler

    private val updateTextTask = object : Runnable {
        override fun run() {
            calculateValue(cryptoBalanceTextView.text.toString())
            mainHandler.postDelayed(this, 5000)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
        ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        mainHandler = Handler(Looper.getMainLooper())
        balanceViewModel = ViewModelProvider(this).get(BalanceViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_balance, container, false)
        localValueTextView = root.findViewById(R.id.balance_local)
        localValueProgressBar = root.findViewById(R.id.progress_bar_local_balance)
        cryptoBalanceTextView = root.findViewById(R.id.balance_crypto)
        cryptoBalanceProgressBar = root.findViewById(R.id.progress_bar_crypto_balance)
        balanceViewModel.balance.observe(viewLifecycleOwner, Observer {
            cryptoBalanceProgressBar.visibility = View.GONE
            cryptoBalanceTextView.text = it
        })
        btcPriceTextView = root.findViewById(R.id.price_crypto)
        btcPriceProgressBar = root.findViewById(R.id.progress_bar_price)
        var walletActivity = activity as AppCompatActivity
        walletActivity.supportActionBar?.hide()
        walletActivity.window.statusBarColor = Color.TRANSPARENT
        walletActivity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE

        mainHandler.post(updateTextTask)
        addButtonListener(root.findViewById(R.id.settings_btn), root.findViewById(R.id.history_btn))
        return root
    }

    override fun onPause() {
        mainHandler.removeCallbacks(updateTextTask)
        super.onPause()
    }


    override fun onResume() {
        (activity as AppCompatActivity).supportActionBar!!.hide()
        mainHandler.post(updateTextTask)
        super.onResume()

    }

    private fun calculateValue(userBalance: String) {
        val coinName = "USD"
        val fromCoin = "BTC"
        Common.getCoinService().calculateValue(fromCoin, coinName).enqueue(object : retrofit2.Callback<Coin> {
            override fun onFailure(call: Call<Coin>?, t: Throwable?) {

            }

            override fun onResponse(call: Call<Coin>?, response: Response<Coin>?) {
                showData(userBalance, response!!.body()!!.USD)
            }
        })
    }


    private fun showData(cryptoBalance: String, coinPrice: String) {
        var localValue: Double = cryptoBalance.toDouble() * coinPrice.toDouble()
        val priceDf = NumberFormat.getCurrencyInstance()
        priceDf.currency = Currency.getInstance("USD")
        priceDf.maximumFractionDigits = 2
        if (balanceViewModel._price.value.toString() != coinPrice) {
            localValueProgressBar.visibility = View.GONE
            btcPriceProgressBar.visibility = View.GONE
            balanceViewModel.setPrice(coinPrice)
            balanceViewModel.setCurValue(localValue.toString())
            btcPriceTextView.text = priceDf.format(coinPrice.toDouble()).toString()
            localValueTextView.text = if (cryptoBalance == "0") "$0" else priceDf.format(localValue).toString()
        }
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
