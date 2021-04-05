package com.example.protec;


import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class CarerDashboardTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void carerDashboardTest() {
        ViewInteraction materialButton = onView(
                allOf(withId(R.id.button_login_start), withText("Let's get started"),
                        childAtPosition(
                                allOf(withId(R.id.container),
                                        childAtPosition(
                                                withId(R.id.nav_host_fragment),
                                                0)),
                                1),
                        isDisplayed()));
        materialButton.perform(click());

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.login_email), withText("patient0@protec.com"),
                        childAtPosition(
                                allOf(withId(R.id.container),
                                        childAtPosition(
                                                withId(R.id.nav_host_fragment),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatEditText.perform(replaceText("carer0@protec.com"));

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.login_email), withText("carer0@protec.com"),
                        childAtPosition(
                                allOf(withId(R.id.container),
                                        childAtPosition(
                                                withId(R.id.nav_host_fragment),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatEditText2.perform(closeSoftKeyboard());

        ViewInteraction materialButton2 = onView(
                allOf(withId(R.id.button_login_signin), withText("Sign in"),
                        childAtPosition(
                                allOf(withId(R.id.container),
                                        childAtPosition(
                                                withId(R.id.nav_host_fragment),
                                                0)),
                                3),
                        isDisplayed()));
        materialButton2.perform(click());

        ViewInteraction button = onView(
                allOf(withId(R.id.open_map_btn), withText("SHOW PATIENTS ON MAP"),
                        withParent(allOf(withId(R.id.fragment_carer_dashboard),
                                withParent(withId(R.id.fragment_container)))),
                        isDisplayed()));
        button.check(matches(isDisplayed()));

        ViewInteraction frameLayout = onView(
                allOf(withId(R.id.action_logout), withContentDescription("Log out"),
                        withParent(withParent(withId(R.id.bottomNavigation))),
                        isDisplayed()));
        frameLayout.check(matches(isDisplayed()));

        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.recyclerView),
                        withParent(allOf(withId(R.id.fragment_carer_dashboard),
                                withParent(withId(R.id.fragment_container)))),
                        isDisplayed()));
        recyclerView.check(matches(isDisplayed()));

        ViewInteraction recyclerView2 = onView(
                allOf(withId(R.id.recyclerView),
                        withParent(allOf(withId(R.id.fragment_carer_dashboard),
                                withParent(withId(R.id.fragment_container)))),
                        isDisplayed()));
        recyclerView2.check(matches(isDisplayed()));
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
