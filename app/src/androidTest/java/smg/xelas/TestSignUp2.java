package smg.xelas;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.contrib.PickerActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.rule.ActivityTestRule;
import android.util.Log;
import android.widget.DatePicker;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.*;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.assertion.ViewAssertions.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.*;


/**
 * Created by Sathya on 3/10/2017.
 */

public class TestSignUp2 {


    @Rule
    public IntentsTestRule<sign_up_2> intentsTestRule =
            new IntentsTestRule<sign_up_2>(sign_up_2.class) {
                @Override
                protected Intent getActivityIntent() {
                    Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
                    Intent result = new Intent(targetContext, sign_up_2.class);
                    result.putExtra("email", "example@email.com");
                    result.putExtra("firstName", "John");
                    result.putExtra("lastName", "Doe");
                    result.putExtra("password", "password");
                    return result;
                }
            };


    // All fields are invalid, and editTexts are empty.
    @Test
    public void allFieldsInvalid() {
        onView(withId(R.id.et_dobSignup)).perform(clearText(), typeText(""), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.et_height)).perform(clearText(), typeText(""), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.et_weight)).perform(clearText(), typeText(""), ViewActions.closeSoftKeyboard());

        assertTrue(intentsTestRule.getActivity().getViewBackground("dob")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("height")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("weight")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("gender")==R.drawable.spinnerbg);
        assertTrue(intentsTestRule.getActivity().getViewBackground("profession")==R.drawable.spinnerbg);
        assertTrue(intentsTestRule.getActivity().getViewBackground("exerciseFreq")==R.drawable.spinnerbg);

        onView(withId(R.id.btn_done)).perform(click());
        assertTrue(intentsTestRule.getActivity().getViewBackground("dob")==R.drawable.edittext_round_red);
        assertTrue(intentsTestRule.getActivity().getViewBackground("height")==R.drawable.edittext_round_red);
        assertTrue(intentsTestRule.getActivity().getViewBackground("weight")==R.drawable.edittext_round_red);
        assertTrue(intentsTestRule.getActivity().getViewBackground("gender")==R.drawable.spinnerbg_red);
        assertTrue(intentsTestRule.getActivity().getViewBackground("profession")==R.drawable.spinnerbg_red);
        assertTrue(intentsTestRule.getActivity().getViewBackground("exerciseFreq")==R.drawable.spinnerbg_red);
    }

    // Dob is empty, everything else is valid.
    @Test
    public void onlyDobIsEmpty() {
        onView(withId(R.id.et_dobSignup)).perform(clearText(), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.et_height)).perform(clearText(), typeText("100"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.et_weight)).perform(clearText(), typeText("100"), ViewActions.closeSoftKeyboard());

        onView(withId(R.id.sp_gender)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Male"))).perform(click());
        onView(withId(R.id.sp_profession)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Pilot"))).perform(click());
        onView(withId(R.id.sp_exerciseFreq)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("1-2 times a week"))).perform(click());

        assertTrue(intentsTestRule.getActivity().getViewBackground("dob")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("height")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("weight")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("gender")==R.drawable.spinnerbg);
        assertTrue(intentsTestRule.getActivity().getViewBackground("profession")==R.drawable.spinnerbg);
        assertTrue(intentsTestRule.getActivity().getViewBackground("exerciseFreq")==R.drawable.spinnerbg);

        onView(withId(R.id.btn_done)).perform(click());
        assertTrue(intentsTestRule.getActivity().getViewBackground("dob")==R.drawable.edittext_round_red);
        assertTrue(intentsTestRule.getActivity().getViewBackground("height")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("weight")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("gender")==R.drawable.spinnerbg);
        assertTrue(intentsTestRule.getActivity().getViewBackground("profession")==R.drawable.spinnerbg);
        assertTrue(intentsTestRule.getActivity().getViewBackground("exerciseFreq")==R.drawable.spinnerbg);
    }

    // Confirms datepicker works.
    @Test
    public void dobValidDate() {
        onView(withId(R.id.et_dobSignup)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(2017, 6, 30));
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.et_dobSignup)).check(matches(withText("30/6/2017")));
        assertTrue(intentsTestRule.getActivity().getViewBackground("dob")==R.drawable.edittext_round);
        onView(withId(R.id.btn_done)).perform(click());
        assertTrue(intentsTestRule.getActivity().getViewBackground("dob")==R.drawable.edittext_round);
    }


    // Height is empty, everything else is valid.
    @Test
    public void onlyHeightIsEmpty() {
        onView(withId(R.id.et_dobSignup)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(2017, 6, 30));
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.et_height)).perform(clearText(), typeText(""), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.et_weight)).perform(clearText(), typeText("100"), ViewActions.closeSoftKeyboard());

        onView(withId(R.id.sp_gender)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Male"))).perform(click());
        onView(withId(R.id.sp_profession)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Pilot"))).perform(click());
        onView(withId(R.id.sp_exerciseFreq)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("1-2 times a week"))).perform(click());

        assertTrue(intentsTestRule.getActivity().getViewBackground("dob")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("height")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("weight")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("gender")==R.drawable.spinnerbg);
        assertTrue(intentsTestRule.getActivity().getViewBackground("profession")==R.drawable.spinnerbg);
        assertTrue(intentsTestRule.getActivity().getViewBackground("exerciseFreq")==R.drawable.spinnerbg);
        onView(withId(R.id.btn_done)).perform(click());
        assertTrue(intentsTestRule.getActivity().getViewBackground("dob")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("height")==R.drawable.edittext_round_red);
        assertTrue(intentsTestRule.getActivity().getViewBackground("weight")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("gender")==R.drawable.spinnerbg);
        assertTrue(intentsTestRule.getActivity().getViewBackground("profession")==R.drawable.spinnerbg);
        assertTrue(intentsTestRule.getActivity().getViewBackground("exerciseFreq")==R.drawable.spinnerbg);
    }

    // Weight is empty, everything else is valid.
    @Test
    public void onlyWeightIsEmpty() {
        onView(withId(R.id.et_dobSignup)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(2017, 6, 30));
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.et_height)).perform(clearText(), typeText("100"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.et_weight)).perform(clearText(), typeText(""), ViewActions.closeSoftKeyboard());

        onView(withId(R.id.sp_gender)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Male"))).perform(click());
        onView(withId(R.id.sp_profession)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Pilot"))).perform(click());
        onView(withId(R.id.sp_exerciseFreq)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("1-2 times a week"))).perform(click());

        assertTrue(intentsTestRule.getActivity().getViewBackground("dob")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("height")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("weight")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("gender")==R.drawable.spinnerbg);
        assertTrue(intentsTestRule.getActivity().getViewBackground("profession")==R.drawable.spinnerbg);
        assertTrue(intentsTestRule.getActivity().getViewBackground("exerciseFreq")==R.drawable.spinnerbg);
        onView(withId(R.id.btn_done)).perform(click());
        assertTrue(intentsTestRule.getActivity().getViewBackground("dob")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("height")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("weight")==R.drawable.edittext_round_red);
        assertTrue(intentsTestRule.getActivity().getViewBackground("gender")==R.drawable.spinnerbg);
        assertTrue(intentsTestRule.getActivity().getViewBackground("profession")==R.drawable.spinnerbg);
        assertTrue(intentsTestRule.getActivity().getViewBackground("exerciseFreq")==R.drawable.spinnerbg);
    }

    // Gender is empty, everything else is valid.
    @Test
    public void onlyGenderIsEmpty() {
        onView(withId(R.id.et_dobSignup)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(2017, 6, 30));
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.et_height)).perform(clearText(), typeText("100"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.et_weight)).perform(clearText(), typeText("100"), ViewActions.closeSoftKeyboard());

        onView(withId(R.id.sp_gender)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("GENDER"))).perform(click());
        onView(withId(R.id.sp_profession)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Pilot"))).perform(click());
        onView(withId(R.id.sp_exerciseFreq)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("1-2 times a week"))).perform(click());

        assertTrue(intentsTestRule.getActivity().getViewBackground("dob")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("height")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("weight")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("gender")==R.drawable.spinnerbg);
        assertTrue(intentsTestRule.getActivity().getViewBackground("profession")==R.drawable.spinnerbg);
        assertTrue(intentsTestRule.getActivity().getViewBackground("exerciseFreq")==R.drawable.spinnerbg);
        onView(withId(R.id.btn_done)).perform(click());
        assertTrue(intentsTestRule.getActivity().getViewBackground("dob")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("height")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("weight")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("gender")==R.drawable.spinnerbg_red);
        assertTrue(intentsTestRule.getActivity().getViewBackground("profession")==R.drawable.spinnerbg);
        assertTrue(intentsTestRule.getActivity().getViewBackground("exerciseFreq")==R.drawable.spinnerbg);
    }

    // Gender is empty, everything else is valid.
    @Test
    public void onlyProfessionIsEmpty() {
        onView(withId(R.id.et_dobSignup)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(2017, 6, 30));
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.et_height)).perform(clearText(), typeText("100"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.et_weight)).perform(clearText(), typeText("100"), ViewActions.closeSoftKeyboard());

        onView(withId(R.id.sp_gender)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Male"))).perform(click());
        onView(withId(R.id.sp_profession)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("PROFESSION"))).perform(click());
        onView(withId(R.id.sp_exerciseFreq)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("1-2 times a week"))).perform(click());

        assertTrue(intentsTestRule.getActivity().getViewBackground("dob")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("height")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("weight")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("gender")==R.drawable.spinnerbg);
        assertTrue(intentsTestRule.getActivity().getViewBackground("profession")==R.drawable.spinnerbg);
        assertTrue(intentsTestRule.getActivity().getViewBackground("exerciseFreq")==R.drawable.spinnerbg);
        onView(withId(R.id.btn_done)).perform(click());
        assertTrue(intentsTestRule.getActivity().getViewBackground("dob")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("height")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("weight")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("gender")==R.drawable.spinnerbg);
        assertTrue(intentsTestRule.getActivity().getViewBackground("profession")==R.drawable.spinnerbg_red);
        assertTrue(intentsTestRule.getActivity().getViewBackground("exerciseFreq")==R.drawable.spinnerbg);
    }


    // Gender is empty, everything else is valid.
    @Test
    public void onlyExerciseFreqIsEmpty() {
        onView(withId(R.id.et_dobSignup)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(2017, 6, 30));
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.et_height)).perform(clearText(), typeText("100"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.et_weight)).perform(clearText(), typeText("100"), ViewActions.closeSoftKeyboard());

        onView(withId(R.id.sp_gender)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Male"))).perform(click());
        onView(withId(R.id.sp_profession)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Pilot"))).perform(click());
        onView(withId(R.id.sp_exerciseFreq)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("EXERCISE FREQUENCY"))).perform(click());

        assertTrue(intentsTestRule.getActivity().getViewBackground("dob")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("height")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("weight")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("gender")==R.drawable.spinnerbg);
        assertTrue(intentsTestRule.getActivity().getViewBackground("profession")==R.drawable.spinnerbg);
        assertTrue(intentsTestRule.getActivity().getViewBackground("exerciseFreq")==R.drawable.spinnerbg);
        onView(withId(R.id.btn_done)).perform(click());
        assertTrue(intentsTestRule.getActivity().getViewBackground("dob")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("height")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("weight")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("gender")==R.drawable.spinnerbg);
        assertTrue(intentsTestRule.getActivity().getViewBackground("profession")==R.drawable.spinnerbg);
        assertTrue(intentsTestRule.getActivity().getViewBackground("exerciseFreq")==R.drawable.spinnerbg_red);
    }

    // Gender is empty, everything else is valid.
    @Test
    public void allFieldsValid() {
        onView(withId(R.id.et_dobSignup)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(2017, 6, 30));
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.et_height)).perform(clearText(), typeText("100"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.et_weight)).perform(clearText(), typeText("100"), ViewActions.closeSoftKeyboard());

        onView(withId(R.id.sp_gender)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Male"))).perform(click());
        onView(withId(R.id.sp_profession)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Pilot"))).perform(click());
        onView(withId(R.id.sp_exerciseFreq)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("1-2 times a week"))).perform(click());

        onView(withId(R.id.btn_done)).perform(click());
        String activityFormBody = intentsTestRule.getActivity().connectionFormBody;
        Log.d("TestFormBody", "actualBody: " + activityFormBody);
        String expected = "{\"name\":\"John\",\"lastName\":\"Doe\",\"height\":\"100\",\"weight\":\"100\",\"email\":\"example@email.com\",\"profession\":\"Pilot\",\"exerciseFrequency\":2,\"DOB\":\"2017-6-30\",\"password\":\"password\"}";
        Log.d("TestFormBody", "expectedBody: " + expected);
        assertTrue(expected.equals(activityFormBody));
        intended(hasComponent("smg.xelas.SignUpActivity"));
    }






}
