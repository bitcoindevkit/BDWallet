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
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import org.bdwallet.app.R

class WithdrawFragment : Fragment() {
    private lateinit var withdrawViewModel: WithdrawViewModel
    private lateinit var root: View
    private lateinit var reviewDialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        withdrawViewModel =
            ViewModelProviders.of(this).get(WithdrawViewModel::class.java)

        // Inflate the fragment and set up the review dialog
        this.root = inflater.inflate(R.layout.fragment_withdraw, container, false)
        this.reviewDialog = Dialog(requireContext())
        this.reviewDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.reviewDialog.setCancelable(false)
        this.reviewDialog.setContentView(R.layout.dialog_review)

        // Set onClickListener for the review, back, and send buttons
        this.root.findViewById<Button>(R.id.review_btn).setOnClickListener {
            this.reviewBtnOnClickListener()
        }
        this.reviewDialog.findViewById<TextView>(R.id.back_btn_text).setOnClickListener {
            this.reviewDialog.dismiss()
        }
        this.reviewDialog.findViewById<TextView>(R.id.send_btn_text).setOnClickListener {
            this.sendBtnOnClickListener()
        }

        // TODO fix the text watcher to show BTC amount (X.XXXXXXXX) rather than $USD
        val inputAmount: EditText = this.root.findViewById(R.id.input_amount)
        inputAmount.addTextChangedListener(CurrencyTextWatcher(inputAmount))
        this.withdrawViewModel.text.observe(viewLifecycleOwner, Observer {
            //textView.text = it
        })

        val walletActivity = activity as AppCompatActivity
        walletActivity.supportActionBar!!.show()
        walletActivity.window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.darkBlue)
        return this.root
    }

    // Check if the transaction inputs are valid:
        // if it's passes, set display values in the review dialog and show the review dialog
        // otherwise, show an error toast
    private fun reviewBtnOnClickListener() {
        // Get the recipientAddress and btcAmount from the text inputs
        val recipientAddress: String = this.root.findViewById<EditText>(R.id.input_recipient_address).text.toString()
        val btcAmount: String = this.root.findViewById<EditText>(R.id.input_amount).text.toString()

        // TODO: implement these functions which check if the recipientAddress / btcAmount are valid
        if (!validRecipientAddress(recipientAddress)) {
            this.showInvalidAddressToast()
        } else if (!haveSufficientBalance(btcAmount)) {
            this.showInsufficientBalanceToast()
        } else {
            // The transaction has been validated - set the display values before showing the reviewDialog
            this.reviewDialog.findViewById<TextView>(R.id.recipient_text).text = recipientAddress
            this.reviewDialog.findViewById<TextView>(R.id.amount_text).text = this.formatAmountText(btcAmount)

            // TODO set fee display in the reviewDialog
            // this.reviewDialog.findViewById<TextView>(R.id.fee_text).text = this.formatFeeText(...)
            // TODO calculate the total withdraw after fee, and set total withdraw display in the reviewDialog
            // val totalAmount = btcAmount - feeAmount
            // this.reviewDialog.findViewById<TextView>(R.id.total_text).text = this.formatAmountText(totalAmount)

            this.reviewDialog.show()
        }
    }

    // Create and broadcast a transaction after it's been verified and reviewed by the user (using BDK)
    private fun sendBtnOnClickListener() {
        // TODO create / send the transaction
        // var fee_rate: Float = 100 // TODO?
        // var addresses: List<Pair<String, String>> = null // TODO?
        // var utxos: List<String>? = null // TODO?
        // val createResp: CreateTxResponse = BDWApplication.instance.createTx(fee_rate, addresses, false, utxos, null, null)
        // val signResp: SignResponse = BDWApplication.instance.sign(createResp.psbt)
        // val rawTx: RawTransaction = BDWApplication.instance.extract_psbt(signResp.psbt)
        // val txid: Txid = BDWApplication.instance.broadcast(rawTx.transaction)
        // TODO save txid?
    }

    // Return true iff the recipientAddress is a valid Bitcoin address
    private fun validRecipientAddress(recipientAddress: String): Boolean {
        // TODO check if recipient address is valid
        return false
    }

    // Return true iff the wallet contains enough bitcoin to make the transaction (including the fee)
    private fun haveSufficientBalance(btcAmount: String): Boolean {
        // TODO check if wallet has sufficient balance
        return false
    }

    // Given a bitcoin amount, return a formatted string with USD conversion for display in reviewDialog
    private fun formatAmountText(btcAmount: String): String {
        // TODO convert btcAmount to the format: X.XXXXXXXX BTC ($XX,XXX.XX USD)
        return btcAmount
    }

    // When the recipient address is invalid, show this toast to signal a problem to the user
    private fun showInvalidAddressToast() {
        // TODO make the toast more visible and more obvious that it's an error (maybe make it red)
        val myToast = Toast.makeText(context,R.string.toast_invalid_address, Toast.LENGTH_SHORT)
        myToast.setGravity(Gravity.LEFT,200,200) // TODO this line causes a warning
        myToast.show()
    }

    // When the wallet does not have sufficient balance, show this toast to signal a problem to the user
    private fun showInsufficientBalanceToast() {
        // TODO make the toast more visible and more obvious that it's an error (maybe make it red)
        val myToast = Toast.makeText(context,R.string.toast_insufficient_balance, Toast.LENGTH_SHORT)
        myToast.setGravity(Gravity.LEFT,200,200) // TODO this line causes a warning
        myToast.show()
    }
}
