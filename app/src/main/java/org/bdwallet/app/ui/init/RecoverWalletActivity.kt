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

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recover_wallet)
        this.initializeWordList()
        this.addAutofill()
        this.addButtonListener()
        supportActionBar!!.setDisplayHomeAsUpEnabled(true) // enable back button on action bar
    }

    // Read the BIP39 file to initialize the list of valid words. Also initialize the viewList
    private fun initializeWordList() {
        val filename = "BIP39/en.txt"
        val inputString = applicationContext.assets.open(filename).bufferedReader().use {
            it.readText()
        }
        this.wordList = inputString.split("\n")
        this.viewList = listOfNotNull<Int>(R.id.seed_text_1,R.id.seed_text_2, R.id.seed_text_3, R.id.seed_text_4, R.id.seed_text_5,
            R.id.seed_text_6, R.id.seed_text_7, R.id.seed_text_8, R.id.seed_text_9, R.id.seed_text_10, R.id.seed_text_11, R.id.seed_text_12)
    }

    // Add autofill adapter to each AutoCompleteTextView seed word input
    private fun addAutofill() {
        for (x in 0..11) {
            var currView = findViewById<AutoCompleteTextView>(this.viewList[x])
            val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                this.wordList
            )
            currView.setAdapter(adapter)
        }
    }

    // When recover button is clicked, check to make sure seed word inputs are valid:
        // If they are valid, load the wallet
        // Otherwise, show the user an error toast
    private fun addButtonListener() {
        val createButton = findViewById<Button>(R.id.recover_btn)
        createButton.setOnClickListener {
            if (!this.checkSeedWords()) {
                this.showInvalidPhraseToast()
            } else {
                this.loadWallet()
                finish()
                startActivity(Intent(this, WalletActivity::class.java))
            }
        }
    }

    // make sure all seed words are valid, create mnemonic, use mnemonic to calculate & save private key (this.keys)
    // return true if valid & successful, false otherwise
    private fun checkSeedWords(): Boolean {
        var mnemonicWordList: MutableList<String> = mutableListOf()
        for (x in 0..11) {
            val seedWord = findViewById<AutoCompleteTextView>(this.viewList[x]).text.toString().trim()
            if (seedWord.isEmpty() || !this.wordList.contains(seedWord)) {
                return false
            }
            mnemonicWordList.add(seedWord)
        }
        val mnemonicString: String = mnemonicWordList.joinToString(separator = " ")
        try {
            this.keys = BDWApplication.instance.createExtendedKeys(mnemonicString)
        } catch (e: InvocationTargetException) {
            return false
        }
        return true
    }

    // Call BDK library to load the wallet using the recovered private key
    private fun loadWallet() {
        val descriptor = BDWApplication.instance.createDescriptor(this.keys)
        BDWApplication.instance.createWallet(descriptor)
    }

    // Notify the user that the entered seed phrase is invalid
    private fun showInvalidPhraseToast() {
        val myToast = Toast.makeText(applicationContext,R.string.toast_invalid_seed_phrase, Toast.LENGTH_SHORT)
        myToast.setGravity(Gravity.LEFT,200,200)
        myToast.show()
    }
}