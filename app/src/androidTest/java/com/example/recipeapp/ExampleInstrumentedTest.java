package com.example.recipeapp;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

import android.content.res.Configuration;
import android.os.SystemClock;
import android.widget.EditText;

import androidx.activity.ComponentActivity;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.recipeapp.auth.LoginActivity;
import com.example.recipeapp.auth.RegisterActivity;
import com.example.recipeapp.recipes.MainActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule = new ActivityScenarioRule<>(LoginActivity.class);

    @Before
    public void setup() {
    }
    @After
    public void teardown() {
    }
    @Test
    public void tc01_RegisterUser() {
        onView(withId(R.id.registerLink)).perform(click());
        onView(withId(R.id.emailEditText)).perform(replaceText("TEST@mail.ru"));
        onView(withId(R.id.usernameEditText)).perform(replaceText("TEST"));
        onView(withId(R.id.passwordEditText)).perform(replaceText("TESTTEST"));
        onView(withId(R.id.confirmPasswordEditText)).perform(replaceText("TESTTEST"));
        onView(withId(R.id.registerButton)).perform(click());
        SystemClock.sleep(2000);
        onView(withId(R.id.loginButton)).check(matches(isDisplayed()));
    }
}