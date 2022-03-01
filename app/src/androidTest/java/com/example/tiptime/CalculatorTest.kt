package com.example.tiptime

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Matchers.containsString
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CalculatorTest {

    @get:Rule()
    val activity = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun calculate_20_percent_tip() {
        // Type in cost of service
        onView(withId(R.id.cost_of_service_edit_text))
            .perform(typeText("50.00"))

        // Select the 18% options
        onView(withId(R.id.option_twenty_percent))
            .perform(click())

        // Uncheck the 'Round up' Switch
        onView(withId(R.id.round_up_switch))
            .perform(click())

        // Click the calculate button
        onView(withId(R.id.calculate_button))
            .perform(click())

        // Check if the tip result displayed is correct
        onView(withId(R.id.tip_result))
            .check(matches(withText(containsString("10.00"))))
    }

    @Test
    fun calculate_18_percent_tip() {
        // Action to type in the cost of service
        onView(withId(R.id.cost_of_service_edit_text))
            .perform(typeText("50.00"))

        // Select the 18% options
        onView(withId(R.id.option_eighteen_percent))
            .perform(click())

        // Uncheck the 'Round up' Switch
        onView(withId(R.id.round_up_switch))
            .perform(click())

        // Action to click the calculate button
        onView(withId(R.id.calculate_button))
            .perform(click())

        // Assert if the tip result displays the expected value
        onView(withId(R.id.tip_result))
            .check(matches(withText(containsString("9.00"))))
    }

    @Test
    fun calculate_15_percent_tip() {
        // Action to type in the cost of service
        onView(withId(R.id.cost_of_service_edit_text))
            .perform(typeText("50.00"))

        // Action to select the 15% tip
        onView(withId(R.id.option_fifteen_percent))
            .perform(click())

        // Uncheck the 'Round up' Switch
        onView(withId(R.id.round_up_switch))
            .perform(click())

        // Action to click the calculate button
        onView(withId(R.id.calculate_button))
            .perform(click())

        // Assert if the tip result displays the expected value
        onView(withId(R.id.tip_result))
            .check(matches(withText(containsString("7.50"))))
    }

}