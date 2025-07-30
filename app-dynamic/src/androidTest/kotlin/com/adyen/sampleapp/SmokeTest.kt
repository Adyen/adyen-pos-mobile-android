package com.adyen.sampleapp

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.junit.Rule
import org.junit.Test

class SmokeTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun loadDynamicModule() {
        IdlingRegistry.getInstance().register(DynamicModuleCountingResource.resource)
        onView(withId(R.id.load_module_button))
            .perform(click())
        // checks if screen from dynamic_sdk has loaded.
        onView(withText("Start Payment")).check(matches(isDisplayed()))
    }
}
