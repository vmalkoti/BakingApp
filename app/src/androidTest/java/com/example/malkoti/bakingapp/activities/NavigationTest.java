package com.example.malkoti.bakingapp.activities;


import android.support.test.espresso.ViewInteraction;
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
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class NavigationTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void navigationTest() {
        String recipeName = "Nutella Pie";

        ViewInteraction appCompatTextView = onView(
                allOf(withId(R.id.recipe_name_item), withText(recipeName),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.recipes),
                                        0),
                                0),
                        isDisplayed()));
        appCompatTextView.perform(click());
        // check text of the header
        onView(withId(R.id.recipe_name_header)).check(matches(withText(recipeName)));

        String stepName = "Recipe Introduction";
        ViewInteraction appCompatTextView2 = onView(
                allOf(withId(R.id.step_name), withText(stepName),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.recipe_steps),
                                        0),
                                0),
                        isDisplayed()));
        appCompatTextView2.perform(click());

    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
