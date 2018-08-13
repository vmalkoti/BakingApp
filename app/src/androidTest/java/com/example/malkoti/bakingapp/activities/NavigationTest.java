package com.example.malkoti.bakingapp.activities;


import android.support.test.espresso.ViewAction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.example.malkoti.bakingapp.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class NavigationTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void navigationTest() {
        /*
         * TO DEBUG
         *
         * On first few runs, the test works and passes.
         * After few runs, test starts failing.
         * If the user first clicks on emulator and then runs the test, it works and passes.
         *
         * If emulator is left open for some time after running test (with no user activity),
         * a translucent gray box appears behind date/time homescreen widget.
         * Whenever this occurs, test starts failing until user clicks on device screen.
         */

        // check if recipes are displayed
        onView(withId(R.id.recipes))
                .check(matches(isDisplayed()));

        // click on first recipe
        onView(withId(R.id.recipes))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0,click()));

        // check if header is displayed
        onView(withId(R.id.recipe_name_header)).check(matches(isDisplayed()));
        // check if ingredients are displayed
        onView(withId(R.id.recipe_ingredients)).check(matches(isDisplayed()));
        // check if steps are displayed
        onView(withId(R.id.recipe_steps)).check(matches(isDisplayed()));

    }

}
