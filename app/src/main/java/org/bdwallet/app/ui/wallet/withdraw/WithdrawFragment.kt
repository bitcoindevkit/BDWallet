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
import android.util.Log
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
import org.bdwallet.app.BDWApplication
import org.bdwallet.app.R
import org.bitcoindevkit.bdkjni.Types.*

class WithdrawFragment : Fragment() {
    private lateinit var withdrawViewModel: WithdrawViewModel
    private lateinit var root: View
    private lateinit var reviewDialog: Dialog

    private lateinit var recipientAddress: String
    private lateinit var withdrawAmount: String
    private lateinit var createTxResp: CreateTxResponse

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
        // Get the recipientAddress and withdrawAmount from the text inputs, set the feeRate
        this.recipientAddress = this.root.findViewById<EditText>(R.id.input_recipient_address).text.toString().trim()
        this.withdrawAmount = this.btcToSatoshiString(
            this.root.findViewById<EditText>(R.id.input_amount).text.toString().trim()
        )
        val feeRate: Float = 1F // TODO change to be a dynamic value before moving to mainnnet
        val addresses: List<Pair<String, String>> = listOf(Pair(this.recipientAddress, this.withdrawAmount))

        // Check if the recipient address is valid
        if (!this.validRecipientAddress(this.recipientAddress)) {
            this.showInvalidAddressToast()
            return
        }

        // Attempt to create the transaction
        try {
            this.createTxResp = BDWApplication.instance.createTx(feeRate, addresses, false, null, null, null)
        } catch (e: Throwable) {
            // TODO more catch cases and errors to be added during testing
                // TODO specifically catch the exception that means insufficient balance
            Log.d("CREATE-TRANSACTION EXCEPTION", e.printStackTrace().toString())
            this.showInsufficientBalanceToast()
            return
        }

        // The transaction has been validated - set the display values before showing the reviewDialog
        this.reviewDialog.findViewById<TextView>(R.id.recipient_text).text = this.recipientAddress
        this.reviewDialog.findViewById<TextView>(R.id.amount_text).text = this.formatAmountText(this.withdrawAmount)
        this.reviewDialog.findViewById<TextView>(R.id.fee_text).text = this.formatFeeText()
        this.reviewDialog.findViewById<TextView>(R.id.total_text).text = this.formatAmountText(this.getTotalWithdraw())
        this.reviewDialog.show()
    }

    // Sign and broadcast a transaction after it's been verified, created, and reviewed by the user (using BDK)
    private fun sendBtnOnClickListener() {
        try {
            val signResp: SignResponse = BDWApplication.instance.sign(this.createTxResp.psbt)
            val rawTx: RawTransaction = BDWApplication.instance.extract_psbt(signResp.psbt)
            val txid: Txid = BDWApplication.instance.broadcast(rawTx.transaction)
            // TODO save or display txid?
        } catch (e: Throwable) {
            // TODO more catch cases to be added during testing
            Log.d("SEND-TRANSACTION EXCEPTION", e.printStackTrace().toString())
        }
        // TODO signal to the user whether the transaction was sent successfully or not
    }

    // Convert a BTC-formatted string (X.XXXXXXXX) to satoshi string
    private fun btcToSatoshiString(btcAmount: String): String {
        // TODO convert btcAmount to satoshiAmount
        return "0"
    }

    // Return the total withdraw amount String in satoshis (entered withdraw amount plus total fees)
    private fun getTotalWithdraw(): String {
        // TODO this.withdrawAmount + this.createTxResp.details.fees
        return "0"
    }

    // return BTC-formatted string with USD conversion for display in reviewDialog
    private fun formatAmountText(satoshiAmount: String): String {
        // TODO convert satoshiAmount to the format: X.XXXXXXXX BTC ($XX,XXX.XX USD)
        return ""
    }

    // return the total fee BTC formatted string with percentage of withdrawal amount for display in reviewDialog
    private fun formatFeeText(): String {
        // TODO convert this.createTxResp.details.fees to the format: X.XXXXXXXX BTC (X.XXX%)
        // TODO should it also display the USD value (maybe instead of the percentage)?
        return ""
    }

    // Return true iff the recipientAddress is a valid Bitcoin address
    private fun validRecipientAddress(recipientAddress: String): Boolean {
        // TODO check if recipient address is valid
        return false
    }

    // When the recipient address is invalid, show this toast to signal a problem to the user
    private fun showInvalidAddressToast() {
        // TODO make the toast more visible and more obvious that it's an error (maybe make it red)
        val myToast: Toast = Toast.makeText(context,R.string.toast_invalid_address, Toast.LENGTH_SHORT)
        myToast.setGravity(Gravity.LEFT,200,200) // TODO this line causes a warning
        myToast.show()
    }

    // When the wallet does not have sufficient balance, show this toast to signal a problem to the user
    private fun showInsufficientBalanceToast() {
        // TODO make the toast more visible and more obvious that it's an error (maybe make it red)
        val myToast: Toast = Toast.makeText(context,R.string.toast_insufficient_balance, Toast.LENGTH_SHORT)
        myToast.setGravity(Gravity.LEFT,200,200) // TODO this line causes a warning
        myToast.show()
    }
}
