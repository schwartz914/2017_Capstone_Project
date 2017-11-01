package smg.xelas;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;
import android.view.View;


import org.hamcrest.Matcher;
import org.junit.*;
import org.junit.runner.*;


import static android.support.test.espresso.Espresso.*;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.assertion.ViewAssertions.*;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static junit.framework.Assert.*;


/**
 * Created by Sathya on 23/09/2017.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class TestSignUp {

/*  @Rule
    public ActivityTestRule<SignUpActivity> mActivityRule =
            new ActivityTestRule(SignUpActivity.class);
*/
    @Rule
    public IntentsTestRule<SignUpActivity> intentsTestRule =
            new IntentsTestRule<>(SignUpActivity.class);

    // email is invalid. Everything else is valid
    @Test
    public void onlyEmailInvalid() {
        onView(withId(R.id.et_emailSignUp)).perform(clearText(), typeText("2"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.et_firstNameSignUp)).perform(clearText(), typeText("John"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.et_lastNameSignUp)).perform(clearText(), typeText("Doe"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.et_passwordSignUp)).perform(clearText(), typeText("password"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.et_passwordConfirmSignUp)).perform(clearText(), typeText("password"), ViewActions.closeSoftKeyboard());
        assertTrue(intentsTestRule.getActivity().getViewBackground("email")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("firstName")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("lastName")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("password")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("passwordConfirm")==R.drawable.edittext_round);

        onView(withId(R.id.btn_signUp)).perform(click());
        assertTrue(intentsTestRule.getActivity().getViewBackground("email")==R.drawable.edittext_round_red);
        assertTrue(intentsTestRule.getActivity().getViewBackground("firstName")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("lastName")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("password")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("passwordConfirm")==R.drawable.edittext_round);
    }

    // email is invalid. Everything else is valid
    @Test
    public void onlyEmailEmpty() {
        onView(withId(R.id.et_emailSignUp)).perform(clearText(), typeText(""), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.et_firstNameSignUp)).perform(clearText(), typeText("John"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.et_lastNameSignUp)).perform(clearText(), typeText("Doe"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.et_passwordSignUp)).perform(clearText(), typeText("password"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.et_passwordConfirmSignUp)).perform(clearText(), typeText("password"), ViewActions.closeSoftKeyboard());
        assertTrue(intentsTestRule.getActivity().getViewBackground("email")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("firstName")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("lastName")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("password")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("passwordConfirm")==R.drawable.edittext_round);

        onView(withId(R.id.btn_signUp)).perform(click());
        assertTrue(intentsTestRule.getActivity().getViewBackground("email")==R.drawable.edittext_round_red);
        assertTrue(intentsTestRule.getActivity().getViewBackground("firstName")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("lastName")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("password")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("passwordConfirm")==R.drawable.edittext_round);
    }

    // firstName is empty. Everything else is valid
    @Test
    public void onlyFirstNameEmpty() {
        onView(withId(R.id.et_emailSignUp)).perform(clearText(), typeText("example@email.com"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.et_firstNameSignUp)).perform(clearText(), typeText(""), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.et_lastNameSignUp)).perform(clearText(), typeText("Doe"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.et_passwordSignUp)).perform(clearText(), typeText("password"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.et_passwordConfirmSignUp)).perform(clearText(), typeText("password"), ViewActions.closeSoftKeyboard());
        assertTrue(intentsTestRule.getActivity().getViewBackground("email")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("firstName")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("lastName")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("password")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("passwordConfirm")==R.drawable.edittext_round);

        onView(withId(R.id.btn_signUp)).perform(click());
        assertTrue(intentsTestRule.getActivity().getViewBackground("email")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("firstName")==R.drawable.edittext_round_red);
        assertTrue(intentsTestRule.getActivity().getViewBackground("lastName")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("password")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("passwordConfirm")==R.drawable.edittext_round);
    }

    // lastName is empty. Everything else is valid
    @Test
    public void onlyLastNameEmpty() {
        onView(withId(R.id.et_emailSignUp)).perform(clearText(), typeText("example@email.com"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.et_firstNameSignUp)).perform(clearText(), typeText("John"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.et_lastNameSignUp)).perform(clearText(), typeText(""), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.et_passwordSignUp)).perform(clearText(), typeText("password"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.et_passwordConfirmSignUp)).perform(clearText(), typeText("password"), ViewActions.closeSoftKeyboard());
        assertTrue(intentsTestRule.getActivity().getViewBackground("email")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("firstName")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("lastName")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("password")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("passwordConfirm")==R.drawable.edittext_round);

        onView(withId(R.id.btn_signUp)).perform(click());
        assertTrue(intentsTestRule.getActivity().getViewBackground("email")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("firstName")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("lastName")==R.drawable.edittext_round_red);
        assertTrue(intentsTestRule.getActivity().getViewBackground("password")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("passwordConfirm")==R.drawable.edittext_round);
    }
    // password is empty. Everything else is valid
    @Test
    public void onlyPasswordEmpty() {
        onView(withId(R.id.et_emailSignUp)).perform(clearText(), typeText("example@email.com"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.et_firstNameSignUp)).perform(clearText(), typeText("John"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.et_lastNameSignUp)).perform(clearText(), typeText("Doe"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.et_passwordSignUp)).perform(clearText(), typeText(""), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.et_passwordConfirmSignUp)).perform(clearText(), typeText("password"), ViewActions.closeSoftKeyboard());
        assertTrue(intentsTestRule.getActivity().getViewBackground("email")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("firstName")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("lastName")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("password")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("passwordConfirm")==R.drawable.edittext_round);

        onView(withId(R.id.btn_signUp)).perform(click());
        assertTrue(intentsTestRule.getActivity().getViewBackground("email")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("firstName")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("lastName")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("password")==R.drawable.edittext_round_red);
        assertTrue(intentsTestRule.getActivity().getViewBackground("passwordConfirm")==R.drawable.edittext_round_red);
    }

    // passwordConfirm is empty and same as password. Everything else is valid
    @Test
    public void onlyPasswordConfirmEmpty() {
        onView(withId(R.id.et_emailSignUp)).perform(clearText(), typeText("example@email.com"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.et_firstNameSignUp)).perform(clearText(), typeText("John"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.et_lastNameSignUp)).perform(clearText(), typeText("Doe"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.et_passwordSignUp)).perform(clearText(), typeText(""), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.et_passwordConfirmSignUp)).perform(clearText(), typeText(""), ViewActions.closeSoftKeyboard());
        assertTrue(intentsTestRule.getActivity().getViewBackground("email")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("firstName")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("lastName")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("password")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("passwordConfirm")==R.drawable.edittext_round);

        onView(withId(R.id.btn_signUp)).perform(click());
        assertTrue(intentsTestRule.getActivity().getViewBackground("email")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("firstName")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("lastName")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("password")==R.drawable.edittext_round_red);
        assertTrue(intentsTestRule.getActivity().getViewBackground("passwordConfirm")==R.drawable.edittext_round_red);
    }

    // passwordConfirm isn't same as password. Everything else is valid
    @Test
    public void passwordConfirmNotSame() {
        onView(withId(R.id.et_emailSignUp)).perform(clearText(), typeText("example@email.com"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.et_firstNameSignUp)).perform(clearText(), typeText("John"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.et_lastNameSignUp)).perform(clearText(), typeText("Doe"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.et_passwordSignUp)).perform(clearText(), typeText("2"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.et_passwordConfirmSignUp)).perform(clearText(), typeText("3"), ViewActions.closeSoftKeyboard());
        assertTrue(intentsTestRule.getActivity().getViewBackground("email")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("firstName")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("lastName")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("password")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("passwordConfirm")==R.drawable.edittext_round);

        onView(withId(R.id.btn_signUp)).perform(click());
        assertTrue(intentsTestRule.getActivity().getViewBackground("email")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("firstName")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("lastName")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("password")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("passwordConfirm")==R.drawable.edittext_round_red);
    }

    // All invalid
    @Test
    public void allInvalid() {
        onView(withId(R.id.et_emailSignUp)).perform(clearText(), typeText("2"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.et_firstNameSignUp)).perform(clearText(), typeText(""), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.et_lastNameSignUp)).perform(clearText(), typeText(""), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.et_passwordSignUp)).perform(clearText(), typeText(""), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.et_passwordConfirmSignUp)).perform(clearText(), typeText("a"), ViewActions.closeSoftKeyboard());
        assertTrue(intentsTestRule.getActivity().getViewBackground("email")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("firstName")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("lastName")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("password")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("passwordConfirm")==R.drawable.edittext_round);

        onView(withId(R.id.btn_signUp)).perform(click());
        assertTrue(intentsTestRule.getActivity().getViewBackground("email")==R.drawable.edittext_round_red);
        assertTrue(intentsTestRule.getActivity().getViewBackground("firstName")==R.drawable.edittext_round_red);
        assertTrue(intentsTestRule.getActivity().getViewBackground("lastName")==R.drawable.edittext_round_red);
        assertTrue(intentsTestRule.getActivity().getViewBackground("password")==R.drawable.edittext_round_red);
        assertTrue(intentsTestRule.getActivity().getViewBackground("passwordConfirm")==R.drawable.edittext_round_red);
    }

    // All invalid
    @Test
    public void allValidCheckboxesUnchecked() {
        onView(withId(R.id.et_emailSignUp)).perform(clearText(), typeText("example@email.com"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.et_firstNameSignUp)).perform(clearText(), typeText("John"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.et_lastNameSignUp)).perform(clearText(), typeText("Doe"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.et_passwordSignUp)).perform(clearText(), typeText("password"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.et_passwordConfirmSignUp)).perform(clearText(), typeText("password"), ViewActions.closeSoftKeyboard());

        assertTrue(intentsTestRule.getActivity().getViewBackground("email")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("firstName")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("lastName")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("password")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("passwordConfirm")==R.drawable.edittext_round);

        onView(withId(R.id.btn_signUp)).perform(click());
        assertTrue(intentsTestRule.getActivity().getViewBackground("email")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("firstName")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("lastName")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("password")==R.drawable.edittext_round);
        assertTrue(intentsTestRule.getActivity().getViewBackground("passwordConfirm")==R.drawable.edittext_round);



    }

    // All invalid
    @Test
    public void allValidCheckboxesChecked() {
        onView(withId(R.id.et_emailSignUp)).perform(clearText(), typeText("example@email.com"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.et_firstNameSignUp)).perform(clearText(), typeText("John"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.et_lastNameSignUp)).perform(clearText(), typeText("Doe"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.et_passwordSignUp)).perform(clearText(), typeText("password"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.et_passwordConfirmSignUp)).perform(clearText(), typeText("password"), ViewActions.closeSoftKeyboard());

        onView(withId(R.id.cb_privacyPolicy)).perform(click());
        onView(withId(R.id.cb_termsAndConditions)).perform(click());


/*        Intent resultData = new Intent();
        String phoneNumber = "123-345-6789";
        resultData.putExtra("email", "example@email.com");
        resultData.putExtra("firstName", "John");
        resultData.putExtra("lastName", "Doe");
        resultData.putExtra("password", "password");
        Instrumentation.ActivityResult result =
                new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);
*/
        onView(withId(R.id.btn_signUp)).perform(click());
        intended(hasComponent("smg.xelas.sign_up_2"));

    }





}


