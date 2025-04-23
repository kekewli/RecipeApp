package com.example.recipeapp;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.IdlingRegistry.getInstance;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

import android.os.SystemClock;

import androidx.test.espresso.idling.CountingIdlingResource;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ExampleInstrumentedTest {
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);
    private final CountingIdlingResource idlingResource = new CountingIdlingResource("GalleryLoad");
    @Before
    public void setup() {
        getInstance().register(idlingResource);
    }
    @After
    public void teardown() {
        getInstance().unregister(idlingResource);
    }
    @Test
    public void CreateRecipe() {
        onView(withId(R.id.addRecipeButton)).check(matches(isDisplayed()));
        onView(withId(R.id.addRecipeButton)).perform(click());
        onView(withId(R.id.titleEditText)).check(matches(isDisplayed()));
        onView(withId(R.id.titleEditText)).perform(replaceText("Борщ"));
        onView(withId(R.id.categorySpinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Обед"))).perform(click());
        onView(withId(R.id.descriptionEditText)).perform(replaceText("Вкусный русский борщ"));
        onView(withId(R.id.ingredientsEditText)).perform(replaceText("Свекла, капуста, картофель, мясо"));
        onView(withId(R.id.addPhotoButton)).perform(click());
        idlingResource.increment();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        idlingResource.decrement();
        onView(withId(R.id.recipeImageView)).check(matches(isDisplayed()));
        onView(withId(R.id.saveButton)).perform(click());

        onView(withId(R.id.searchEditText)).perform(replaceText("Борщ"));
        SystemClock.sleep(1500);

        onData(allOf(is(instanceOf(String.class)), is("Борщ"))).perform(click());
        onView(withId(R.id.titleEditText)).perform(replaceText("Красный борщ"));
        onView(withId(R.id.saveButton)).perform(click());

        onData(allOf(is(instanceOf(String.class)), is("Красный борщ"))).perform(click());
        onView(withId(R.id.titleEditText)).check(matches(withText("Красный борщ")));
        onView(withId(R.id.deleteButton)).perform(click());
        onView(withText("Красный борщ")).check(doesNotExist());

        onView(withId(R.id.searchEditText)).perform(replaceText(""));
    }
}