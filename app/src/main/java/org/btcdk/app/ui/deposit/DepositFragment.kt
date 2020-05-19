package org.btcdk.app.ui.deposit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import org.btcdk.app.R

class DepositFragment : Fragment() {

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
        return root
    }
}
