package org.bdwallet.app.ui.wallet.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.bdwallet.app.R
import org.bdwallet.app.ui.wallet.BalanceUnits
import org.bdwallet.app.ui.wallet.WalletViewModel

class SettingsFragment : Fragment(), CoroutineScope by MainScope() {

    private val walletViewModel: WalletViewModel by viewModels()
    private lateinit var displayBtcAsSats: Switch

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        val root = inflater.inflate(R.layout.fragment_settings, container, false)
        displayBtcAsSats = root.findViewById(R.id.switch_display_sats)

        walletViewModel.convertToSats
            .observe(viewLifecycleOwner, { isSatbalanceUnits ->
                displayBtcAsSats.isChecked = isSatbalanceUnits
            })

        displayBtcAsSats.setOnClickListener { view ->
            val switch = view as Switch
            launch {
                if (switch.isChecked) {
                    walletViewModel.setBalanceUnits(BalanceUnits.SAT)
                } else {
                    walletViewModel.setBalanceUnits(BalanceUnits.BTC)
                }
            }
        }

        val walletActivity = activity as AppCompatActivity
        walletActivity.supportActionBar!!.show()
        walletActivity.window.statusBarColor = ContextCompat.getColor(
            requireContext(),
            R.color.darkBlue
        )

        return root
    }

    companion object {
        private const val TAG = "SettingsFragment"
    }
}







