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
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void loginActivityTest() {
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

        ViewInteraction button = onView(
                allOf(withId(R.id.button_login_signin), withText("SIGN IN"),
                        withParent(allOf(withId(R.id.container),
                                withParent(withId(R.id.nav_host_fragment)))),
                        isDisplayed()));
        button.check(matches(isDisplayed()));

        ViewInteraction button2 = onView(
                allOf(withId(R.id.button_login_facebook), withText("FACEBOOK"),
                        withParent(allOf(withId(R.id.container),
                                withParent(withId(R.id.nav_host_fragment)))),
                        isDisplayed()));
        button2.check(matches(isDisplayed()));

        ViewInteraction button3 = onView(
                allOf(withId(R.id.button_login_google), withText("GOOGLE"),
                        withParent(allOf(withId(R.id.container),
                                withParent(withId(R.id.nav_host_fragment)))),
                        isDisplayed()));
        button3.check(matches(isDisplayed()));

        ViewInteraction textView = onView(
                allOf(withId(R.id.register_link), withText("Don't have an account? Register here"),
                        withParent(allOf(withId(R.id.container),
                                withParent(withId(R.id.nav_host_fragment)))),
                        isDisplayed()));
        textView.check(matches(withText("Don't have an account? Register here")));

        ViewInteraction textView2 = onView(
                allOf(withText("Login"),
                        withParent(allOf(withId(R.id.container),
                                withParent(withId(R.id.nav_host_fragment)))),
                        isDisplayed()));
        textView2.check(matches(withText("Login")));

        ViewInteraction editText = onView(
                allOf(withId(R.id.login_email), withText("patient0@protec.com"),
                        withParent(allOf(withId(R.id.container),
                                withParent(withId(R.id.nav_host_fragment)))),
                        isDisplayed()));
        editText.check(matches(withText("patient0@protec.com")));

        ViewInteraction editText2 = onView(
                allOf(withId(R.id.login_password), withText("••••••"),
                        withParent(allOf(withId(R.id.container),
                                withParent(withId(R.id.nav_host_fragment)))),
                        isDisplayed()));
        editText2.check(matches(isDisplayed()));

        ViewInteraction editText3 = onView(
                allOf(withId(R.id.login_email), withText("patient0@protec.com"),
                        withParent(allOf(withId(R.id.container),
                                withParent(withId(R.id.nav_host_fragment)))),
                        isDisplayed()));
        editText3.check(matches(isDisplayed()));

        ViewInteraction editText4 = onView(
                allOf(withId(R.id.login_email), withText("patient0@protec.com"),
                        withParent(allOf(withId(R.id.container),
                                withParent(withId(R.id.nav_host_fragment)))),
                        isDisplayed()));
        editText4.check(matches(isDisplayed()));
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
