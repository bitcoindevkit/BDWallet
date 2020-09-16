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
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import org.bdwallet.app.R

class DepositFragmentOld : Fragment() {

    private lateinit var depositViewModel: DepositViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        depositViewModel =
            ViewModelProviders.of(this).get(DepositViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_deposit, container, false)
        val textView: TextView = root.findViewById(R.id.text_deposit)
        depositViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

        val address = depositViewModel.text.value

        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Deposit Address")
        sharingIntent.putExtra(Intent.EXTRA_TEXT, address)

        val shareButton: Button = root.findViewById(R.id.share_button)
        shareButton.setOnClickListener(View.OnClickListener {
            startActivity(
                Intent.createChooser(
                    sharingIntent,
                    "Share via"
                )
            )
        })

        return root
    }
}
