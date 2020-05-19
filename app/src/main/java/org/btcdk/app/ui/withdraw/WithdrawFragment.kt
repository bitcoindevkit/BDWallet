package org.btcdk.app.ui.withdraw

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import org.btcdk.app.R

class WithdrawFragment : Fragment() {

    private lateinit var withdrawViewModel: WithdrawViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        withdrawViewModel =
                ViewModelProviders.of(this).get(WithdrawViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_withdraw, container, false)
        val textView: TextView = root.findViewById(R.id.text_withdraw)
        withdrawViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}
