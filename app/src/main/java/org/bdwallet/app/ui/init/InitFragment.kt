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

package org.bdwallet.app.ui.init

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
import org.bdwallet.app.*

private const val TAG = "InitFragment"

class InitFragment : Fragment() {

    private lateinit var initViewModel: InitViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel = ViewModelProvider(this).get(InitViewModel::class.java)

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Log.w("INIT", "Back button disabled!")
            }
        }

        // This callback will only be called when MyFragment is at least Started.
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        val root = inflater.inflate(R.layout.fragment_init, container, false)
        val seedWord1: TextView = root.findViewById(R.id.text_seed_word1)
        val seedWord2: TextView = root.findViewById(R.id.text_seed_word2)
        val seedWord3: TextView = root.findViewById(R.id.text_seed_word3)
        val seedWord4: TextView = root.findViewById(R.id.text_seed_word4)
        val seedWord5: TextView = root.findViewById(R.id.text_seed_word5)
        val seedWord6: TextView = root.findViewById(R.id.text_seed_word6)
        val seedWord7: TextView = root.findViewById(R.id.text_seed_word7)
        val seedWord8: TextView = root.findViewById(R.id.text_seed_word8)
        val seedWord9: TextView = root.findViewById(R.id.text_seed_word9)
        val seedWord10: TextView = root.findViewById(R.id.text_seed_word10)
        val seedWord11: TextView = root.findViewById(R.id.text_seed_word11)
        val seedWord12: TextView = root.findViewById(R.id.text_seed_word12)

        initViewModel.words.observe(viewLifecycleOwner, Observer {
            seedWord1.text = it[0]
            seedWord2.text = it[1]
            seedWord3.text = it[2]
            seedWord4.text = it[3]
            seedWord5.text = it[4]
            seedWord6.text = it[5]
            seedWord7.text = it[6]
            seedWord8.text = it[7]
            seedWord9.text = it[8]
            seedWord10.text = it[9]
            seedWord11.text = it[10]
            seedWord12.text = it[11]
        })

        val okButton: Button = root.findViewById(R.id.button_OK)
        okButton.setOnClickListener {
            // clear seed words
            seedWord1.text = ""
            seedWord2.text = ""
            seedWord3.text = ""
            seedWord4.text = ""
            seedWord5.text = ""
            seedWord6.text = ""
            seedWord7.text = ""
            seedWord8.text = ""
            seedWord9.text = ""
            seedWord10.text = ""
            seedWord11.text = ""
            seedWord12.text = ""

            //val app = activity?.application as ExampleApp
            //app.startBdk()
            // show balance fragment
            findNavController().navigate(R.id.navigation_balance)
        }

        return root
    }

    override fun onResume() {
        super.onResume()

        //val app = activity?.application as ExampleApp
    }
}


