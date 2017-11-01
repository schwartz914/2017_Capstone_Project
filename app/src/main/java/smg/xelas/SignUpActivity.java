package smg.xelas;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_signUp;
    private EditText et_email, et_firstName, et_lastName, et_password, et_passwordConfirm;
    private CheckBox cb_termsAndConditions, cb_privacyPolicy;

    private HashMap<String, Integer> ViewBackgrounds = new HashMap<>();

    private Toast myToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initButtons();
        setListeners();
        setEditTextsToDefault();
    }

    /**
     * Initalize and assign
     */
    private void initButtons() {
        btn_signUp = (Button) findViewById(R.id.btn_signUp);
        et_email = (EditText) findViewById(R.id.et_emailSignUp);
        et_firstName = (EditText) findViewById(R.id.et_firstNameSignUp);
        et_lastName = (EditText) findViewById(R.id.et_lastNameSignUp);
        et_password = (EditText) findViewById(R.id.et_passwordSignUp);
        et_passwordConfirm = (EditText) findViewById(R.id.et_passwordConfirmSignUp);
        cb_termsAndConditions = (CheckBox) findViewById(R.id.cb_termsAndConditions);
        cb_privacyPolicy = (CheckBox) findViewById(R.id.cb_privacyPolicy);

    }


    private void setListeners() {
        btn_signUp.setOnClickListener(this);
    }

    /**
     * Set all EditTexts to default setting
     */
    private void setEditTextsToDefault() {
        et_email.setBackgroundResource(R.drawable.edittext_round);
        ViewBackgrounds.put("email", R.drawable.edittext_round);
        et_firstName.setBackgroundResource(R.drawable.edittext_round);
        ViewBackgrounds.put("firstName", R.drawable.edittext_round);
        et_lastName.setBackgroundResource(R.drawable.edittext_round);
        ViewBackgrounds.put("lastName", R.drawable.edittext_round);
        et_password.setBackgroundResource(R.drawable.edittext_round);
        ViewBackgrounds.put("password", R.drawable.edittext_round);
        et_passwordConfirm.setBackgroundResource(R.drawable.edittext_round);
        ViewBackgrounds.put("passwordConfirm", R.drawable.edittext_round);
    }

    /**
     * Validates values in EditTexts
     *
     * @return boolean. false if anything is invalid, true if all is valid.
     */
    private boolean validCredentials(String emailText, String firstNameText, String lastNameText, String passwordText, String passwordConfirmText) {
        Boolean allValid = true;

        Boolean emailIsValid = Patterns.EMAIL_ADDRESS.matcher(emailText).matches();

        setEditTextsToDefault();

        if (emailText.length() <= 0 || !emailIsValid) {
            et_email.setBackgroundResource(R.drawable.edittext_round_red);
            ViewBackgrounds.put("email", R.drawable.edittext_round_red);
            allValid = false;
        }
        if (firstNameText.length() <= 0) {
            et_firstName.setBackgroundResource(R.drawable.edittext_round_red);
            ViewBackgrounds.put("firstName", R.drawable.edittext_round_red);
            allValid = false;
        }
        if (lastNameText.length() <= 0) {
            et_lastName.setBackgroundResource(R.drawable.edittext_round_red);
            ViewBackgrounds.put("lastName", R.drawable.edittext_round_red);
            allValid = false;
        }
        if (passwordText.length() <= 0) {
            et_password.setBackgroundResource(R.drawable.edittext_round_red);
            ViewBackgrounds.put("password", R.drawable.edittext_round_red);
            allValid = false;
        }
        if (passwordConfirmText.length() <= 0 || !(passwordConfirmText.equals(passwordText))) {
            et_passwordConfirm.setBackgroundResource(R.drawable.edittext_round_red);
            ViewBackgrounds.put("passwordConfirm", R.drawable.edittext_round_red);
            allValid = false;
        }

        return allValid;
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_signUp) {

            String emailText = et_email.getText().toString().trim();
            String firstNameText = et_firstName.getText().toString().trim();
            String lastNameText = et_lastName.getText().toString().trim();
            String passwordText = et_password.getText().toString().trim();
            String passwordConfirmText = et_passwordConfirm.getText().toString().trim();

            if (validCredentials(emailText, firstNameText, lastNameText, passwordText, passwordConfirmText)) {
                if (cb_termsAndConditions.isChecked() && cb_privacyPolicy.isChecked()) {

                    Intent intent = new Intent(SignUpActivity.this, sign_up_2.class);
                    intent.putExtra("email", emailText);
                    intent.putExtra("firstName", firstNameText);
                    intent.putExtra("lastName", lastNameText);
                    intent.putExtra("password", passwordText);

                    startActivity(intent);
                } else {
                    myToast = Toast.makeText(this, "Please Agree to Terms and Conditions and Read the Privacy Policy", Toast.LENGTH_SHORT);
                    myToast.show();
                }
            } else {
                myToast = Toast.makeText(this, "Your credentials are invalid", Toast.LENGTH_SHORT);
                myToast.show();

            }

            /* link to sign up 2 page */

        }
    }

    public int getViewBackground(String view){
        return ViewBackgrounds.get(view);
    }
}
