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
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import kotlinx.coroutines.*
import org.bdwallet.app.R
import org.bdwallet.app.ui.wallet.history.HistoryActivity
import org.bdwallet.app.ui.wallet.settings.SettingsActivity
import java.math.MathContext
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.*


class BalanceFragment : Fragment(), CoroutineScope by MainScope() {

    private val balanceViewModel: BalanceViewModel by activityViewModels()

    private lateinit var balanceCryptoLabel: TextView
    private lateinit var cryptoBalanceTextView: TextView
    private lateinit var localValueTextView: TextView
    private lateinit var btcPriceTextView: TextView
    private lateinit var cryptoBalanceProgressBar: ProgressBar
    private lateinit var localValueProgressBar: ProgressBar
    private lateinit var btcPriceProgressBar: ProgressBar
    private lateinit var priceGraph: WebView
    init {
        lifecycleScope.launch {
            whenStarted {
                while (isActive) { // cancellable computation loop
                    balanceViewModel.refreshSatBalance()
                    balanceViewModel.refreshFiatPrice()
                    delay(60000)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        val root = inflater.inflate(R.layout.fragment_balance, container, false)
        balanceCryptoLabel = root.findViewById(R.id.balance_crypto_label)
        localValueTextView = root.findViewById(R.id.balance_local)
        localValueProgressBar = root.findViewById(R.id.progress_bar_local_balance)
        cryptoBalanceTextView = root.findViewById(R.id.balance_crypto)
        cryptoBalanceProgressBar = root.findViewById(R.id.progress_bar_crypto_balance)
        btcPriceTextView = root.findViewById(R.id.price_crypto)
        btcPriceProgressBar = root.findViewById(R.id.progress_bar_price)
        priceGraph = root.findViewById(R.id.balance_graph)
        priceGraph.settings.javaScriptEnabled = true
        var webData = "<div id=\"bitcoinium-widget\" widget-coin=\"BTC\" widget-align=\"center\" widget-size=\"small\" widget-initial-pair=\"BTC_BITSTAMP_USD\"></div><script type=\"text/javascript\" src=\"https://bitcoinium.com/assets/js/bitcoinium-widget-min.js\"></script>"
        priceGraph.loadData(webData, "text/html", "UTF-8");

        val currencyFormatter = NumberFormat.getCurrencyInstance()
        currencyFormatter.currency = Currency.getInstance("USD")
        currencyFormatter.maximumFractionDigits = 2
        currencyFormatter.roundingMode = RoundingMode.HALF_EVEN
        val numberFormatter = NumberFormat.getNumberInstance(Locale.US)
        numberFormatter.maximumFractionDigits = 8

        balanceViewModel.convertToSats.observe(viewLifecycleOwner, { isSats ->
            balanceCryptoLabel.text = if (isSats) "SATS BALANCE" else "BTC BALANCE"
        })

        balanceViewModel.walletBalance.observe(viewLifecycleOwner, { walletBalance ->
            cryptoBalanceTextView.text = numberFormatter.format(walletBalance.toBigDecimal(MathContext.DECIMAL64))
        })

        balanceViewModel.fiatValue.observe(viewLifecycleOwner, { fiat ->
            localValueTextView.text = currencyFormatter.format(fiat)
        })

        balanceViewModel.fiatPrice.observe(viewLifecycleOwner, { price ->
            btcPriceTextView.text = currencyFormatter.format(price)
        })

        balanceViewModel.btcRefreshing.observe(viewLifecycleOwner, { refreshing ->
            if (refreshing) {
                cryptoBalanceProgressBar.visibility = View.VISIBLE
                cryptoBalanceTextView.visibility = View.INVISIBLE
                btcPriceProgressBar.visibility = View.VISIBLE
                localValueProgressBar.visibility = View.VISIBLE
                btcPriceTextView.visibility = View.INVISIBLE
                localValueTextView.visibility = View.INVISIBLE
            } else {
                cryptoBalanceProgressBar.visibility = View.INVISIBLE
                cryptoBalanceTextView.visibility = View.VISIBLE
            }
        })

        balanceViewModel.fiatRefreshing.observe(viewLifecycleOwner, { refreshing ->
            if (!refreshing) {
                btcPriceProgressBar.visibility = View.INVISIBLE
                localValueProgressBar.visibility = View.INVISIBLE
                btcPriceTextView.visibility = View.VISIBLE
                localValueTextView.visibility = View.VISIBLE
            }
        })

        var walletActivity = activity as AppCompatActivity
        walletActivity.supportActionBar?.setShowHideAnimationEnabled(false)
        walletActivity.supportActionBar?.hide()
        walletActivity.window.statusBarColor = Color.TRANSPARENT
        walletActivity.window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE

        addButtonListener(root.findViewById(R.id.settings_btn), root.findViewById(R.id.history_btn))
        return root
    }

    override fun onResume() {
        (activity as AppCompatActivity).supportActionBar!!.hide()
        super.onResume()
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
