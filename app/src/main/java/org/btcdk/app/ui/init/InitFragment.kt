package org.btcdk.app.ui.init

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import org.btcdk.app.MainActivity
import org.btcdk.app.R

class InitFragment : Fragment() {

    private lateinit var initViewModel: InitViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Log.w("INIT", "Don't go back!")
            }
        }

        // This callback will only be called when MyFragment is at least Started.
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    fun handleBackPressed() {
        Log.w("INIT", "Don't go back!")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initViewModel = ViewModelProvider(this).get(InitViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_init, container, false)
        val textView: TextView = root.findViewById(R.id.text_seed_words)
        initViewModel.words.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

        val okButton: Button = root.findViewById(R.id.button_OK)
        okButton.setOnClickListener {
            findNavController().navigate(R.id.navigation_balance)
        }

        return root
    }

    override fun onResume() {
        super.onResume()

//        val mainActivity: MainActivity = activity as MainActivity
//        mainActivity.fullScreen()
//        mainActivity.hideNav()
//        mainActivity.hideActionBar()
    }
}


