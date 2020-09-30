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


import java.lang.reflect.InvocationTargetException


class RecoverWalletActivity : AppCompatActivity() {

    private lateinit var viewList : List<Int>
    private lateinit var keys: ExtendedKeys
    private lateinit var wordList : List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recover_wallet)
        this.addAutofill()
        this.addButtonListener()
        supportActionBar!!.setDisplayHomeAsUpEnabled(true) // enable back button on action bar
    }

    private fun addButtonListener() {
        val createButton = findViewById<Button>(R.id.recover_btn)
        createButton.setOnClickListener {

            if (!this.checkSeedWords()) { //if valid seed words
                this.showInvalidPhraseToast()
            } else {
                this.loadWallet()
                startActivity(Intent(this, WalletActivity::class.java))
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun checkSeedWords(): Boolean {
        //gather all text in autocompleteview and create mnemonic

        var mnemonicString = findViewById<AutoCompleteTextView>(R.id.seed_text_1).text //initialize Editable! so we can append to it

        if(this.wordList.contains(mnemonicString.toString())){
            this.showInvalidPhraseToast()
            return false
        }

        for (x in 1..11) {
            val seedWord = findViewById<AutoCompleteTextView>(this.viewList[x]).text

            if(this.wordList.contains(seedWord.toString())){
                return false
            }
            mnemonicString.append(seedWord)
        }

        try {

            this.keys = BDWApplication.instance.createExtendedKeys(mnemonicString.toString())

        } catch (e: InvocationTargetException) {

            return false
        }

        return true
    }

    private fun loadWallet() {
        val descriptor = BDWApplication.instance.createDescriptor(this.keys)
        BDWApplication.instance.createWallet(descriptor)
    }

    private fun addAutofill() {


        val filename = "BIP39/en.txt"
        val inputString = applicationContext.assets.open(filename).bufferedReader().use {
            it.readText()
        }
        wordList = inputString.split("\n")

        this.viewList = listOfNotNull<Int>(R.id.seed_text_1,R.id.seed_text_2, R.id.seed_text_3, R.id.seed_text_4, R.id.seed_text_5, //get AutoCompleteTextViews in list
            R.id.seed_text_6, R.id.seed_text_7, R.id.seed_text_8, R.id.seed_text_9, R.id.seed_text_10, R.id.seed_text_11, R.id.seed_text_12)



        for (x in 0..11){ //adding autofill to each AutoCompleteTextview
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