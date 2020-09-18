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

package org.bdwallet.app.ui.wallet.withdraw

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import org.bdwallet.app.R
import org.bdwallet.app.ui.init.CreateWalletPasswordActivity
import org.bdwallet.app.ui.init.RecoverWalletActivity

class WithdrawFragment : Fragment() {

    private lateinit var withdrawViewModel: WithdrawViewModelOld

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        withdrawViewModel =
            ViewModelProviders.of(this).get(WithdrawViewModelOld::class.java)
        val root = inflater.inflate(R.layout.fragment_withdraw, container, false)
        val textView: TextView = root.findViewById(R.id.input_amount)
        withdrawViewModel.text.observe(viewLifecycleOwner, Observer {
            //textView.text = it
        })
        addButtonListener(root.findViewById(R.id.review_btn))
        val walletActivity = activity as AppCompatActivity
        walletActivity.supportActionBar!!.show()
        walletActivity.window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.darkBlue)
        return root
    }

    private fun addButtonListener(button: Button) {
        button.setOnClickListener {
            showReviewDialog()
        }
    }

    private fun showReviewDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_review)
        val sendButton = dialog.findViewById<TextView>(R.id.send_btn_text)
        sendButton.setOnClickListener {
            // TODO: check balance and broadcast transaction
        }
        val backButton = dialog.findViewById<TextView>(R.id.back_btn_text)
        backButton.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun showInvalidPasswordToast() {
        val myToast = Toast.makeText(context,R.string.toast_invalid_password, Toast.LENGTH_SHORT)
        myToast.setGravity(Gravity.LEFT,200,200)
        myToast.show()
    }

    private fun showInvalidAddressToast() {
        val myToast = Toast.makeText(context,R.string.toast_invalid_address, Toast.LENGTH_SHORT)
        myToast.setGravity(Gravity.LEFT,200,200)
        myToast.show()
    }

    private fun showInsufficientBalanceToast() {
        val myToast = Toast.makeText(context,R.string.toast_insufficient_balance, Toast.LENGTH_SHORT)
        myToast.setGravity(Gravity.LEFT,200,200)
        myToast.show()
    }
}