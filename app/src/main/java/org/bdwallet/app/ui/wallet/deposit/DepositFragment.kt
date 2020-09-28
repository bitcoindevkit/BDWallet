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

package org.bdwallet.app.ui.wallet.deposit

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import org.bdwallet.app.R
import java.net.URL


class DepositFragment : Fragment() {

    private lateinit var depositViewModel: DepositViewModel

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        depositViewModel =
            ViewModelProviders.of(this).get(DepositViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_deposit, container, false)
        val textView: TextView = root.findViewById(R.id.wallet_address)
        val qr_code: ImageView = root.findViewById(R.id.qr_code);


        //Test Mode
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()

        StrictMode.setThreadPolicy(policy)
        var address:String = "1M5m1DuGw4Wyq1Nf8sfoKRM6uA4oREzpCX"
        depositViewModel.text.observe(viewLifecycleOwner, Observer {
//            address = it
        })
        textView.text = address
        val url = URL("https://www.bitcoinqrcodemaker.com/api/?style=bitcoin&address=" + address)

        val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
        qr_code.setImageBitmap(bmp)
        addButtonListener(
            root.findViewById(R.id.share_btn),
            root.findViewById<TextView>(R.id.wallet_address).text.toString()
        )
        val walletActivity = activity as AppCompatActivity
        walletActivity.supportActionBar!!.show()
        walletActivity.window.statusBarColor = ContextCompat.getColor(
            requireContext(),
            R.color.darkBlue
        )
        return root
    }

    private fun addButtonListener(button: Button, address: String) {
        button.setOnClickListener {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, address)
                putExtra(Intent.EXTRA_TITLE, R.string.share_address_extra_title)
                putExtra(Intent.EXTRA_SUBJECT, R.string.share_address_extra)
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }
    }
}
