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
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setup() {
    }
    @After
    public void teardown() {
    }
    @Test
    public void tc01_CreateRecipe() {
        onView(withId(R.id.addRecipeButton)).perform(click());
        onView(withId(R.id.titleEditText)).perform(replaceText("Борщ"));
        onView(withId(R.id.categorySpinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Обед"))).perform(click());
        onView(withId(R.id.descriptionEditText)).perform(replaceText("Desc"));
        onView(withId(R.id.ingredientsEditText)).perform(replaceText("Ing"));
        onView(withId(R.id.addPhotoButton)).perform(click());
        SystemClock.sleep(2000);
        onView(withId(R.id.recipeImageView)).check(matches(isDisplayed()));
        onView(withId(R.id.saveButton)).perform(click());
    }

    @Test
    public void tc02_Search() {
        onView(withId(R.id.searchEditText)).perform(replaceText("бор"));
        onData(allOf(is(instanceOf(String.class)), is("Борщ"))).inAdapterView(withId(R.id.listView))
                .check(matches(isDisplayed()));
    }

    @Test
    public void tc03_EditRecipe() {
        onData(allOf(is(instanceOf(String.class)), is("Борщ"))).perform(click());
        onView(withId(R.id.titleEditText)).perform(replaceText("Красный борщ"));
        onView(withId(R.id.saveButton)).perform(click());
        onView(withText("Красный борщ")).check(matches(isDisplayed()));
    }

    @Test
    public void tc04_DeleteRecipe() {
        onData(allOf(is(instanceOf(String.class)), is("Красный борщ"))).perform(click());
        onView(withId(R.id.deleteButton)).perform(click());
        onView(withText("Красный борщ")).check(doesNotExist());
    }
    @Test
    public void tc05_AddWithoutPhoto() {
        onView(withId(R.id.addRecipeButton)).perform(click());
        onView(withId(R.id.titleEditText)).perform(replaceText("Тест"));
        onView(withId(R.id.categorySpinner)).perform(click());
        onData(anything()).atPosition(0).perform(click());
        onView(withId(R.id.descriptionEditText)).perform(replaceText("D"));
        onView(withId(R.id.ingredientsEditText)).perform(replaceText("I"));
        onView(withId(R.id.saveButton)).perform(click());
        onView(withText("Выберите изображение!")).check(matches(isDisplayed()));
    }

    @Test
    public void tc06_AddEmptyFields() {
        onView(withId(R.id.addRecipeButton)).perform(click());
        onView(withId(R.id.saveButton)).perform(click());
        onView(withText("Заполните все поля!")).check(matches(isDisplayed()));
    }

    @Test
    public void tc07_AddInvalidImageFormat() {
        onView(withId(R.id.addRecipeButton)).perform(click());
        onView(withId(R.id.titleEditText)).perform(replaceText("T"));
        onView(withId(R.id.categorySpinner)).perform(click());
        onData(anything()).atPosition(0).perform(click());
        onView(withId(R.id.descriptionEditText)).perform(replaceText("D"));
        onView(withId(R.id.ingredientsEditText)).perform(replaceText("I"));
        onView(withId(R.id.addPhotoButton)).perform(click());
        SystemClock.sleep(2000);
        onView(withText("Неподдерживаемый формат. Выберите JPG или PNG!"))
                .check(matches(isDisplayed()));
    }

    @Test
    public void tc08_EditEmptyFields() {
        onView(withId(R.id.addRecipeButton)).perform(click());
        onView(withId(R.id.titleEditText)).perform(replaceText("Борщ"));
        onView(withId(R.id.categorySpinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Обед"))).perform(click());
        onView(withId(R.id.descriptionEditText)).perform(replaceText("Desc"));
        onView(withId(R.id.ingredientsEditText)).perform(replaceText("Ing"));

        onView(withId(R.id.addPhotoButton)).perform(click());
        SystemClock.sleep(2000);
        onView(withId(R.id.recipeImageView)).check(matches(isDisplayed()));
        onView(withId(R.id.saveButton)).perform(click());
        onView(withText("Борщ")).check(matches(isDisplayed()));

        onData(allOf(is(instanceOf(String.class)), is("Борщ"))).perform(click());
        onView(withId(R.id.titleEditText)).perform(replaceText(""));
        onView(withId(R.id.saveButton)).perform(click());
        onView(withText("Борщ")).check(matches(isDisplayed()));
    }

    @Test
    public void tc09_InputAboveLimit() {
        onView(withId(R.id.addRecipeButton)).perform(click());
        String longText = new String(new char[350]).replace('\0','A');
        onView(withId(R.id.titleEditText)).perform(replaceText(longText));
        onView(withId(R.id.titleEditText)).check((v, e) -> {
            String actual = ((EditText)v).getText().toString();
            assert(actual.length() <= 300);
        });
    }

    @Test
    public void tc10_ExitCreateDiscard() {
        onView(withId(R.id.addRecipeButton)).perform(click());
        onView(withId(R.id.titleEditText)).perform(replaceText("Tmp"));
        activityRule.getScenario().onActivity(ComponentActivity::onBackPressed);
        onView(withText("Tmp")).check(doesNotExist());
    }

    @Test
    public void tc11_ExitEditDiscard() {
        onView(withId(R.id.addRecipeButton)).perform(click());
        onView(withId(R.id.titleEditText)).perform(replaceText("Борщ"));
        onView(withId(R.id.categorySpinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Обед"))).perform(click());
        onView(withId(R.id.descriptionEditText)).perform(replaceText("Desc"));
        onView(withId(R.id.ingredientsEditText)).perform(replaceText("Ing"));
        onView(withId(R.id.addPhotoButton)).perform(click());
        SystemClock.sleep(2000);
        onView(withId(R.id.recipeImageView)).check(matches(isDisplayed()));
        onView(withId(R.id.saveButton)).perform(click());
        onView(withText("Борщ")).check(matches(isDisplayed()));

        onData(allOf(is(instanceOf(String.class)), is("Борщ"))).perform(click());
        onView(withId(R.id.titleEditText)).perform(replaceText("Tmp2"));
        activityRule.getScenario().onActivity(ComponentActivity::onBackPressed);
        onView(withText("Tmp2")).check(doesNotExist());
        onView(withText("Борщ")).check(matches(isDisplayed()));
    }

    @Test
    public void tc12_SearchCaseInsensitive() {
        onView(withId(R.id.searchEditText)).perform(replaceText("бОрЩ"));
        onData(allOf(is(instanceOf(String.class)), is("Борщ"))).check(matches(isDisplayed()));
    }

    @Test
    public void tc13_SearchNonexistent() {
        onView(withId(R.id.searchEditText)).perform(replaceText("XYZ"));
        onData(anything()).inAdapterView(withId(R.id.listView)).atPosition(0)
                .check(doesNotExist());
    }

    @Test
    public void tc14_LandscapeOrientation() {
        activityRule.getScenario().onActivity(a ->
                a.setRequestedOrientation(Configuration.ORIENTATION_LANDSCAPE));
        tc01_CreateRecipe();
    }

    @Test
    public void tc15_PortraitOrientation() {
        activityRule.getScenario().onActivity(a ->
                a.setRequestedOrientation(Configuration.ORIENTATION_PORTRAIT));
        tc01_CreateRecipe();
    }

    @Test
    public void tc16_CreateDuplicateRecipe() {
        onView(withId(R.id.addRecipeButton)).perform(click());
        onView(withId(R.id.titleEditText)).perform(replaceText("Борщ"));
        onView(withId(R.id.categorySpinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Обед"))).perform(click());
        onView(withId(R.id.descriptionEditText)).perform(replaceText("Desc"));
        onView(withId(R.id.ingredientsEditText)).perform(replaceText("Ing"));
        onView(withId(R.id.addPhotoButton)).perform(click());
        SystemClock.sleep(2000);
        onView(withId(R.id.recipeImageView)).check(matches(isDisplayed()));
        onView(withId(R.id.saveButton)).perform(click());

        onView(withId(R.id.addRecipeButton)).perform(click());
        onView(withId(R.id.titleEditText)).perform(replaceText("Борщ"));
        onView(withId(R.id.categorySpinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Обед"))).perform(click());
        onView(withId(R.id.descriptionEditText)).perform(replaceText("Desc"));
        onView(withId(R.id.ingredientsEditText)).perform(replaceText("Ing"));
        onView(withId(R.id.addPhotoButton)).perform(click());
        SystemClock.sleep(2000);
        onView(withId(R.id.recipeImageView)).check(matches(isDisplayed()));
        onView(withId(R.id.saveButton)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Борщ"))).inAdapterView(withId(R.id.listView))
                .atPosition(1).check(matches(isDisplayed()));
    }
}