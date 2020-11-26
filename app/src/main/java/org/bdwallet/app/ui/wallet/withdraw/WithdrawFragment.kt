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
import android.icu.text.NumberFormat
import android.icu.util.Currency
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.bdwallet.app.BDWApplication
import org.bdwallet.app.R
import org.bdwallet.app.ui.wallet.WalletViewModel
import org.bdwallet.app.ui.wallet.util.bitstamp.Bitstamp
import org.bitcoindevkit.bdkjni.Types.CreateTxResponse
import org.bitcoindevkit.bdkjni.Types.RawTransaction
import org.bitcoindevkit.bdkjni.Types.SignResponse
import org.bitcoindevkit.bdkjni.Types.Txid
import java.math.BigDecimal
import java.math.MathContext.DECIMAL64
import java.math.RoundingMode.HALF_EVEN
import java.util.*

private const val TAG = "WithdrawFragment"

class WithdrawFragment : Fragment(), CoroutineScope by MainScope() {
    private val walletViewModel: WalletViewModel by activityViewModels()

    private lateinit var recipientAddressTextView: TextView
    private lateinit var inputAmountTextView: TextView
    private lateinit var reviewButton: Button
    private lateinit var reviewDialog: Dialog

    private lateinit var recipientAddress: String
    private lateinit var withdrawSatAmount: String
    private lateinit var withdrawBtcAmount: String
    private lateinit var createTxResp: CreateTxResponse
    private lateinit var btcPriceUsd: BigDecimal

    private val currencyFormatter = NumberFormat.getCurrencyInstance()
    private val numberFormatter = NumberFormat.getNumberInstance(Locale.US)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the fragment and set up the review dialog
        val root = inflater.inflate(R.layout.fragment_withdraw, container, false)
        recipientAddressTextView = root.findViewById(R.id.input_recipient_address)
        inputAmountTextView = root.findViewById(R.id.input_amount)
        reviewButton = root.findViewById(R.id.review_btn)

        reviewDialog = Dialog(requireContext())
        reviewDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        reviewDialog.setCancelable(false)
        reviewDialog.setContentView(R.layout.dialog_review)
        reviewDialog.findViewById<TextView>(R.id.back_btn_text).setOnClickListener {
            reviewDialog.dismiss()
        }
        reviewDialog.findViewById<TextView>(R.id.send_btn_text).setOnClickListener {
            broadcastTransaction()
        }

        // Set onClickListener for the review, back, and send buttons
        reviewButton.setOnClickListener {
            recipientAddress = recipientAddressTextView.text.toString().trim()
            withdrawBtcAmount = inputAmountTextView.text.toString().trim()
            if (withdrawBtcAmount.isNotEmpty() && recipientAddress.isNotEmpty()) {
                withdrawSatAmount = withdrawBtcAmount.toBigDecimal(DECIMAL64)
                    .multiply(BigDecimal.valueOf(100000000))
                    .longValueExact()
                    .toString()
                if (withdrawSatAmount != "0") {
                    verifyTransaction()
                }
            }
        }

        launch {
            val bitstamp = Bitstamp()
            btcPriceUsd = bitstamp.getTickerService().getQuote().bid.toBigDecimal()
        }

        currencyFormatter.currency = Currency.getInstance("USD")
        currencyFormatter.maximumFractionDigits = 2

        val walletActivity = activity as AppCompatActivity
        walletActivity.supportActionBar!!.show()
        walletActivity.window.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.darkBlue)
        return root
    }

    // Check if the transaction inputs are valid:
    // if it's passes, set display values in the review dialog and show the review dialog
    // otherwise, show an error toast and return
    private fun verifyTransaction() {
        val feeRate = 1F // TODO change to be a dynamic value before moving to mainnnet
        val addresses: List<Pair<String, String>> =
            listOf(Pair(recipientAddress, withdrawSatAmount))
        val app = requireActivity().application as BDWApplication
        Log.d(TAG, "Recipient: $recipientAddress")
        Log.d(TAG, "Withdraw (SAT): $withdrawSatAmount")
        Log.d(TAG, "Withdraw (BTC): $withdrawBtcAmount")
        // Attempt to create the transaction
        try {
            createTxResp = app.createTx(feeRate, addresses, false, null, null, null)
        } catch (e: Throwable) {
            Log.d("CREATE-TRANSACTION EXCEPTION", "MSG: ".plus(e.message))
            if (e.message == "WalletError(InsufficientFunds)") {
                showInsufficientBalanceToast()
            } else if (e.message!!.contains("ParseIntError")) {
                showInvalidAmountToast()
            } else if (e.message!!.contains("Parsing(")) {
                showInvalidAddressToast()
            }
            return
        }

        // The transaction has been validated - set the dialog display values before showing the reviewDialog
        reviewDialog.findViewById<TextView>(R.id.recipient_text).text = recipientAddress
        reviewDialog.findViewById<TextView>(R.id.amount_text).text = formatAmountText()
        reviewDialog.findViewById<TextView>(R.id.fee_text).text = formatFeeText()
        reviewDialog.findViewById<TextView>(R.id.total_text).text = formatTotalWithdraw()
        reviewDialog.show()
    }

    // Sign and broadcast a transaction after it's been verified, created, and reviewed by the user (using BDK)
    private fun broadcastTransaction() {
        val app = requireActivity().application as BDWApplication
        try {
            val signResp: SignResponse = app.sign(createTxResp.psbt)
            val rawTx: RawTransaction = app.extract_psbt(signResp.psbt)
            val txid: Txid = app.broadcast(rawTx.transaction)
            // TODO save or display txid?
        } catch (e: Throwable) {
            Log.d("SEND-TRANSACTION EXCEPTION", "MSG: ".plus(e.message))
            e.printStackTrace()
        }
        showTransactionSuccessToast()
        reviewDialog.dismiss()
    }

    // Return the total withdraw amount String in satoshis (entered withdraw amount plus total fees)
    private fun formatTotalWithdraw(): String {
        val feeInBtc =
            createTxResp.details.fees.toBigDecimal(DECIMAL64).divide(BigDecimal.valueOf(100000000))
                .setScale(8, HALF_EVEN)
                .stripTrailingZeros()
        val totalInBtc = withdrawBtcAmount.toBigDecimal(DECIMAL64) + feeInBtc
        val totalInUsd = totalInBtc.multiply(btcPriceUsd, DECIMAL64).setScale(2, HALF_EVEN)
        return "$totalInBtc BTC (${currencyFormatter.format(totalInUsd)} USD)"
    }

    // return BTC-formatted string with USD conversion for display in reviewDialog
    private fun formatAmountText(): String {
        val formattedValue = withdrawBtcAmount.toBigDecimal(DECIMAL64)
            .multiply(btcPriceUsd, DECIMAL64)
            .setScale(3, HALF_EVEN)
        return "${withdrawBtcAmount.toBigDecimal(DECIMAL64)} BTC (${
            currencyFormatter.format(
                formattedValue
            )
        } USD)"
    }

    // return the total fee BTC formatted string with percentage of withdrawal amount for display in reviewDialog
    private fun formatFeeText(): String {
        val feeInSats = createTxResp.details.fees.toString()
        val feeInBtc = feeInSats.toBigDecimal(DECIMAL64).divide(BigDecimal.valueOf(100000000))
            .setScale(8, HALF_EVEN)
            .stripTrailingZeros()
        val feeInUsd = feeInBtc.multiply(btcPriceUsd, DECIMAL64).setScale(8, HALF_EVEN)
        return "$feeInBtc BTC (${currencyFormatter.format(feeInUsd)} USD)"
    }

    // When the recipient address is invalid, show this toast to signal a problem to the user
    private fun showInvalidAddressToast() {
        val myToast: Toast =
            Toast.makeText(context, R.string.toast_invalid_address, Toast.LENGTH_SHORT)
        myToast.show()
    }

    // When the amount is invalid, show this toast to signal a problem to the user
    private fun showInvalidAmountToast() {
        val myToast: Toast =
            Toast.makeText(context, R.string.toast_invalid_amount, Toast.LENGTH_SHORT)
        myToast.show()
    }

    // When the wallet does not have sufficient balance, show this toast to signal a problem to the user
    private fun showInsufficientBalanceToast() {
        val myToast: Toast =
            Toast.makeText(context, R.string.toast_insufficient_balance, Toast.LENGTH_SHORT)
        myToast.show()
    }

    // When the transaction was sent successfully, show this toast to confirm to user
    private fun showTransactionSuccessToast() {
        val myToast: Toast = Toast.makeText(context, "Transaction successful", Toast.LENGTH_SHORT)
        myToast.show()
    }
}
