/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.tiptime

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.data.PreferencesDataStore
import com.example.data.PreferencesDataStore.Companion.IS_ROUND_UP
import com.example.data.PreferencesDataStore.Companion.TIP_OPTION
import com.example.tiptime.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.NumberFormat

/**
 * Activity that displays a tip calculator.
 */
class MainActivity : AppCompatActivity() {

    // Binding object instance with access to the views in the activity_main.xml layout
    private lateinit var binding: ActivityMainBinding
    private lateinit var PreferencesDataStore: PreferencesDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout XML file and return a binding object instance
        binding = ActivityMainBinding.inflate(layoutInflater)

        // Set the content view of the Activity to be the root view of the layout
        setContentView(binding.root)

        PreferencesDataStore = PreferencesDataStore(this)

        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                PreferencesDataStore.preferencesFlow
                    .stateIn(lifecycleScope)
                    .map { it[TIP_OPTION] }
                    .collectLatest { tipPercentage ->
                        binding.apply {
                            when (tipPercentage) {
                                20 -> tipOptions.check(R.id.option_twenty_percent)
                                18 -> tipOptions.check(R.id.option_eighteen_percent)
                                15 -> tipOptions.check(R.id.option_fifteen_percent)
                            }
                            Log.d("PreferenceDataStore", "collected tip option: $tipPercentage")
                        }
                    }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                PreferencesDataStore.preferencesFlow
                    .stateIn(lifecycleScope)
                    .map { it[IS_ROUND_UP] }
                    .collectLatest { isRoundUp ->
                        binding.roundUpSwitch.isChecked = isRoundUp ?: true
                        Log.d("PreferenceDataStore", "collected isRoundUp: $isRoundUp")
                    }
            }
        }

        // Setup a click listener on the calculate button to calculate the tip
        binding.calculateButton.setOnClickListener { calculateTip() }

        // Set up a key listener on the EditText field to listen for "enter" button presses
        binding.costOfServiceEditText.setOnKeyListener { view, keyCode, _ ->
            handleKeyEvent(
                view,
                keyCode
            )
        }
    }

    /**
     * Calculates the tip based on the user input.
     */
    private fun calculateTip() {
        // Get the decimal value from the cost of service EditText field
        val stringInTextField = binding.costOfServiceEditText.text.toString()
        val cost = stringInTextField.toDoubleOrNull()

        // If the cost is null or 0, then display 0 tip and exit this function early.
        if (cost == null || cost == 0.0) {
            displayTip(0.0)
            return
        }

        // Get the tip percentage based on which radio button is selected
        val tipPercentage = when (binding.tipOptions.checkedRadioButtonId) {
            R.id.option_twenty_percent -> {
                lifecycleScope.launch {
                    PreferencesDataStore.saveInputPreferences(20,
                        binding.roundUpSwitch.isChecked,
                        this@MainActivity)
                    Log.d("PreferenceDataStore",
                        "Saved successful \n tipOption = 20, isRoundUp = ${binding.roundUpSwitch.isChecked}")
                }
                0.20
            }
            R.id.option_eighteen_percent -> {
                lifecycleScope.launch {
                    PreferencesDataStore.saveInputPreferences(18,
                        binding.roundUpSwitch.isChecked,
                        this@MainActivity)
                    Log.d("PreferenceDataStore",
                        "Saved successful \n tipOption = 18, isRoundUp = ${binding.roundUpSwitch.isChecked}")
                }
                0.18
            }
            else -> {
                lifecycleScope.launch {
                    PreferencesDataStore.saveInputPreferences(15,
                        binding.roundUpSwitch.isChecked,
                        this@MainActivity)
                    Log.d("PreferenceDataStore",
                        "Saved successful \n tipOption = 15, isRoundUp = ${binding.roundUpSwitch.isChecked}")
                }
                0.15
            }
        }

        // Calculate the tip
        var tip = tipPercentage * cost

        // If the switch for rounding up the tip toggled on (isChecked is true), then round up the
        // tip. Otherwise do not change the tip value.
        val roundUp = binding.roundUpSwitch.isChecked
        if (roundUp) {
            // Take the ceiling of the current tip, which rounds up to the next integer, and store
            // the new value in the tip variable.
            tip = kotlin.math.ceil(tip)
        }

        // Display the formatted tip value onscreen
        displayTip(tip)
    }

    /**
     * Format the tip amount according to the local currency and display it onscreen.
     * Example would be "Tip Amount: $10.00".
     */
    private fun displayTip(tip: Double) {
        val formattedTip = NumberFormat.getCurrencyInstance().format(tip)
        binding.tipResult.text = getString(R.string.tip_amount, formattedTip)
    }

    /**
     * Key listener for hiding the keyboard when the "Enter" button is tapped.
     */
    private fun handleKeyEvent(view: View, keyCode: Int): Boolean {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            // Hide the keyboard
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
            return true
        }
        return false
    }
}