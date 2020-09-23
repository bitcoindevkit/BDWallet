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

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.jaredrummler.materialspinner.MaterialSpinner
import org.bdwallet.app.R
import org.bdwallet.app.ui.wallet.history.HistoryActivity
import org.bdwallet.app.ui.wallet.settings.SettingsActivity
import retrofit2.Call
import retrofit2.Response


class BalanceFragment : Fragment() {
    var coinService: CoinService? = null
    private lateinit var balanceViewModel: BalanceViewModel
    lateinit var convertedValueTxtView: TextView
    lateinit var beforeconvertedValueTxtView: TextView
    private val money = arrayOf("USD", "EUR", "GBP")
    private val coin = arrayOf("BTC", "ETH", "ETC", "XRP", "LTC", "XMR", "DASH", "MAID", "AUR", "XEM")

//    @SuppressLint("RestrictedApi")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_balance, container, false)
        super.onCreateView(inflater, container, savedInstanceState)
        balanceViewModel = ViewModelProvider(this).get(BalanceViewModel::class.java)

        ////
//        convertedValueTxtView = root.findViewById(R.id.balance_crypto)
        beforeconvertedValueTxtView = root.findViewById(R.id.balance_local)
        ////
        convertedValueTxtView = root.findViewById(R.id.balance_crypto)
        balanceViewModel.balance.observe(viewLifecycleOwner, Observer {
            convertedValueTxtView.text = it
        })
        calculateValue()
        var walletActivity = activity as AppCompatActivity
//        walletActivity.supportActionBar?.setShowHideAnimationEnabled(false)
        walletActivity.supportActionBar?.hide()
        walletActivity.window.statusBarColor = Color.TRANSPARENT
        walletActivity.window.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        addButtonListener(root.findViewById(R.id.settings_btn), root.findViewById(R.id.history_btn))
        return root
    }

    override fun onResume() {
        (activity as AppCompatActivity).supportActionBar!!.hide()
        super.onResume()
    }
    //////
    private fun calculateValue(){

//        val coinName = toSpinner.getItems<String>()[toSpinner.selectedIndex]
//        val fromCoin = fromSpinner.getItems<String>()[fromSpinner.selectedIndex]
        val coinName = "BTC"
        val fromCoin = "USD"
        coinService!!.calculateValue(fromCoin, coinName).enqueue(object : retrofit2.Callback<Coin> {
            override fun onFailure(call: Call<Coin>?, t: Throwable?) {

            }
            override fun onResponse(call: Call<Coin>?, response: Response<Coin>?) {
                //SUCCESS
                showData(response!!.body()!!.BTC)
            }
        })
    }
    /////

    private fun showData(coinName: String) {
        beforeconvertedValueTxtView.setText(coinName);
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
