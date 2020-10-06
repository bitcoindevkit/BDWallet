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

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import org.bdwallet.app.BDWApplication
import org.bdwallet.app.R
import org.bdwallet.app.ui.wallet.history.HistoryActivity
import org.bdwallet.app.ui.wallet.settings.SettingsActivity
import retrofit2.Call
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URL


class BalanceFragment : Fragment() {
    var coinService: CoinService? = null
    private lateinit var balanceViewModel: BalanceViewModel
    lateinit var convertedValueTxtView: TextView
    lateinit var beforeconvertedValueTxtView: TextView
    lateinit var btcPrice: TextView
    private val money = arrayOf("USD", "EUR", "GBP")
    private val coin = arrayOf(
        "BTC",
        "ETH",
        "ETC",
        "XRP",
        "LTC",
        "XMR",
        "DASH",
        "MAID",
        "AUR",
        "XEM"
    )

//    @SuppressLint("RestrictedApi")
    override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
): View? {

        //CALL the QR generator
        generateQRcode()
        coinService = Common.getCoinService()
        super.onCreateView(inflater, container, savedInstanceState)
        balanceViewModel = ViewModelProvider(this).get(BalanceViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_balance, container, false)
        ////
//        convertedValueTxtView = root.findViewById(R.id.balance_crypto)
//        val textView: TextView = root.findViewById(R.id.text_balance)
        beforeconvertedValueTxtView = root.findViewById(R.id.balance_local)
        ////
        convertedValueTxtView = root.findViewById(R.id.balance_crypto)
        btcPrice = root.findViewById(R.id.price_crypto)
//        convertedValueTxtView.text = "1"
        var amount:String = BDWApplication.instance.getBalance().toString()
        convertedValueTxtView.text = amount

        //TODO: Unable to test it untile getbalace could use
//        beforeconvertedValueTxtView.text = "12345"
//        balanceViewModel.balance.observe(viewLifecycleOwner, Observer {
//            beforeconvertedValueTxtView.text = it
//        })
        calculateValue(amount)
        var walletActivity = activity as AppCompatActivity
//        walletActivity.supportActionBar?.setShowHideAnimationEnabled(false)
        walletActivity.supportActionBar?.hide()
        walletActivity.window.statusBarColor = Color.TRANSPARENT
        walletActivity.window.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        addButtonListener(root.findViewById(R.id.settings_btn), root.findViewById(R.id.history_btn))
        return root
    }

    ///////////////////////////////////
    private fun generateQRcode(){
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()

        StrictMode.setThreadPolicy(policy)
//        var address:String = "1M5m1DuGw4Wyq1Nf8sfoKRM6uA4oREzpCX"
        var address:String = BDWApplication.instance.getNewAddress()
        val url = URL("https://www.bitcoinqrcodemaker.com/api/?style=bitcoin&address=" + address)
        val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())

        //Write qrcode to file as png
        bitmapToFile(bmp, "QRCODE.png")
    }

//    private fun verifyStoragePermissions(activity: Activity) {
//        // Check if we have write permission
//        val REQUEST_EXTERNAL_STORAGE = 1
//        val PERMISSIONS_STORAGE = arrayOf(
//            Manifest.permission.READ_EXTERNAL_STORAGE,
//            Manifest.permission.WRITE_EXTERNAL_STORAGE
//        )
//        val permission: Int =
//            ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//        if (permission != PackageManager.PERMISSION_GRANTED) {
//            // We don't have permission so prompt the user
//            ActivityCompat.requestPermissions(
//                activity,
//                PERMISSIONS_STORAGE,
//                REQUEST_EXTERNAL_STORAGE
//            )
//        }
//    }
    private fun bitmapToFile(bitmap: Bitmap, fileNameToSave: String): File? { // File name like "image.png"
        //create a file to write bitmap data
//        verifyStoragePermissions(onResume());
        // Check whether this app has write external storage permission or not.
        val PERMISSIONS_STORAGE = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        // Check whether this app has write external storage permission or not.
        val writeExternalStoragePermission: Int = ContextCompat.checkSelfPermission(this.requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
// If do not grant write external storage permission.
// If do not grant write external storage permission.
        if (writeExternalStoragePermission != PackageManager.PERMISSION_GRANTED) {
            // Request user to grant write external storage permission.

            ActivityCompat.requestPermissions(this.requireActivity(),
                PERMISSIONS_STORAGE,
                1
            )
        }

        var file: File? = null
        return try {
            file = File(
                Environment.getExternalStorageDirectory()
                    .toString() + File.separator + fileNameToSave
            )
//            file.mkdir()

            //WHERE the permission denied happened
            file.createNewFile()

            //Convert bitmap to byte array
            val bos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos) // YOU can also save it in JPEG
            val bitmapdata = bos.toByteArray()

            //write the bytes in file
            val fos = FileOutputStream(file)
            println(file.absolutePath)
            fos.write(bitmapdata)
            fos.flush()
            fos.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            file // it will return null
        }
    }
    ///////////////////////////////////

    override fun onResume() {
        (activity as AppCompatActivity).supportActionBar!!.hide()
        super.onResume()
    }
    //////
    private fun calculateValue(userBalance: String){

//        val coinName = toSpinner.getItems<String>()[toSpinner.selectedIndex]
//        val fromCoin = fromSpinner.getItems<String>()[fromSpinner.selectedIndex]
        val coinName = "USD"
        val fromCoin = "BTC"
//        coinService = Common.getCoinService() // Ethan had to add this line to prevent null pointer exception
        coinService!!.calculateValue(fromCoin, coinName).enqueue(object : retrofit2.Callback<Coin> {
            override fun onFailure(call: Call<Coin>?, t: Throwable?) {

            }

            override fun onResponse(call: Call<Coin>?, response: Response<Coin>?) {
                //SUCCESS
                println("SUCCESS")
                showData(userBalance, response!!.body()!!.USD)
            }
        })
    }
    /////

    private fun showData(userBalance: String, coinName: String) {
        println(coinName)
        var result:Double = userBalance.toDouble() * coinName.toDouble()
        btcPrice.setText("$ " + coinName)
        beforeconvertedValueTxtView.setText("$ " + result.toString());
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
