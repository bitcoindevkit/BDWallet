package org.btcdk.app.ui.balance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import org.btcdk.app.MainActivity
import org.btcdk.app.R

class BalanceFragment : Fragment() {

    private lateinit var balanceViewModel: BalanceViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        balanceViewModel = ViewModelProvider(this).get(BalanceViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_balance, container, false)
        val textView: TextView = root.findViewById(R.id.text_balance)
        balanceViewModel.balance.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

        return root
    }

    override fun onResume() {
        super.onResume()

        val mainActivity: MainActivity = activity as MainActivity
        mainActivity.showNav()
    }
}
