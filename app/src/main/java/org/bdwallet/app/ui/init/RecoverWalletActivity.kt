package org.bdwallet.app.ui.init

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.bdwallet.app.BDWApplication
import org.bdwallet.app.R
import org.bdwallet.app.ui.wallet.WalletActivity
import org.bitcoindevkit.bdkjni.Types.ExtendedKeys
import org.bitcoindevkit.bdkjni.Types.Network
import java.io.File
import java.lang.reflect.InvocationTargetException


class RecoverWalletActivity : AppCompatActivity() {

    private lateinit var viewList : List<Int>
    private lateinit var keys: ExtendedKeys

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recover_wallet)
        addAutofill()
        addButtonListener()
        supportActionBar!!.setDisplayHomeAsUpEnabled(true) // enable back button on action bar
    }

    private fun addButtonListener() {
        val createButton = findViewById<Button>(R.id.recover_btn)
        createButton.setOnClickListener {
            startActivity(Intent(this, WalletActivity::class.java))

            if (!this.checkSeedWords()) { //if valid seed words

            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun checkSeedWords(): Boolean {


        //gather all text in autocompleteview and create mnemonic
//        val word1 = findViewById<AutoCompleteTextView>(R.id.seed_text_1).text
//        val word2 = findViewById<AutoCompleteTextView>(R.id.seed_text_2).text
//        val word3 = findViewById<AutoCompleteTextView>(R.id.seed_text_3).text
//        val word4 = findViewById<AutoCompleteTextView>(R.id.seed_text_4).text
//        val word5 = findViewById<AutoCompleteTextView>(R.id.seed_text_5).text
//        val word6 = findViewById<AutoCompleteTextView>(R.id.seed_text_6).text
//        val word7 = findViewById<AutoCompleteTextView>(R.id.seed_text_7).text
//        val word8 = findViewById<AutoCompleteTextView>(R.id.seed_text_8).text
//        val word9 = findViewById<AutoCompleteTextView>(R.id.seed_text_9).text
//        val word10 = findViewById<AutoCompleteTextView>(R.id.seed_text_10).text
//        val word11 = findViewById<AutoCompleteTextView>(R.id.seed_text_11).text
//        val word12 = findViewById<AutoCompleteTextView>(R.id.seed_text_12).text
        //var mnemonicString = "$word1 $word2 $word3 $word4 $word5 $word6 $word7 $word8 $word9 $word10 $word11 $word12"

        var mnemonicString = findViewById<AutoCompleteTextView>(R.id.seed_text_1).text
        for (x in 1..11){
            var seedWord =  findViewById<AutoCompleteTextView>(this.viewList[x]).text
            mnemonicString.append(seedWord)
        }

        try{
            this.keys = BDWApplication.instance.createExtendedKeys(Network.testnet, mnemonicString.toString())
        } catch (e: InvocationTargetException){
            showInvalidPhraseToast()
            return false
        }

        return true
    }

    private fun loadWallet(){

    }

    private fun addAutofill(){

        val fileName = "/BIP39/en.txt"
        val inputString = application.assets.open(fileName).bufferedReader().use { it.readText() }
        val wordList: List<String> = inputString.split("\n")

        this.viewList = listOfNotNull<Int>(R.id.seed_text_1,R.id.seed_text_2, R.id.seed_text_3, R.id.seed_text_4, R.id.seed_text_5, //get AutoCompleteTextViews in list
            R.id.seed_text_6, R.id.seed_text_7, R.id.seed_text_8, R.id.seed_text_9, R.id.seed_text_10, R.id.seed_text_11, R.id.seed_text_12)

        for (x in 0..11){ //adding autofill to each Textview
            var currView = findViewById<AutoCompleteTextView>(this.viewList[x])
            val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
                this,
                android.R.layout.simple_dropdown_item_1line, wordList
            )
            currView.setAdapter(adapter)
        }

    }

    private fun showInvalidPhraseToast() {
        val myToast = Toast.makeText(applicationContext,R.string.toast_invalid_seed_phrase, Toast.LENGTH_SHORT)
        myToast.setGravity(Gravity.LEFT,200,200)
        myToast.show()
    }
}