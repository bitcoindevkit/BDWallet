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
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.google.zxing.WriterException
import org.bdwallet.app.R
import org.bdwallet.app.ui.wallet.WalletViewModel

private const val TAG = "DepositFragment"

class DepositFragment : Fragment() {

    private val walletViewModel: WalletViewModel by activityViewModels()

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_deposit, container, false)
        val walletAddress: TextView = root.findViewById(R.id.wallet_address)
        val qrCode: ImageView = root.findViewById(R.id.qr_code)
        val shareBtn: Button = root.findViewById(R.id.share_btn)


        //Test Mode
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()

        StrictMode.setThreadPolicy(policy)

        walletViewModel.newAddress().observe(viewLifecycleOwner, Observer<String> { address ->
            // update UI
            walletAddress.text = address
            val qrgEncoder = QRGEncoder(address, null, QRGContents.Type.TEXT, 250)
            try {
                val bitmap = qrgEncoder.bitmap
                qrCode.setImageBitmap(bitmap)
            } catch (e: WriterException) {
                Log.v(TAG, e.toString())
            }
            addButtonListener(
                shareBtn,
                address
            )
            Log.d(TAG, "New deposit address: $address")
        })


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
