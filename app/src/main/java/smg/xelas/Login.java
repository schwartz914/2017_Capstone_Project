package smg.xelas;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class Login extends AppCompatActivity implements View.OnClickListener {

    private TextView forgotPasswordTextView, tv_loginError;
    private EditText et_username, et_password;
    private Button btn_login, btn_signUp;
    private ProgressBar mLoadingIndicator;
    private Toast myToast;
    JSONObject formBody;
    SharedPreferences userSession;
    HttpConnect httpConnect = new HttpConnect();
    FitbitApi fitbit = new FitbitApi(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (userLoggedIn()) {
            startActivity(new Intent(Login.this, HomePage.class));
        } else {
            initButtons();
            setListeners();
        }
    }

    /* check if the user has already logged in or not */
    private boolean userLoggedIn() {
        userSession = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        return userSession.getBoolean("loggedIn", false);
    }

    /**
     * Initalize and assign
     */
    private void initButtons() {
        forgotPasswordTextView = (TextView) findViewById(R.id.forgotPassword);
        tv_loginError = (TextView) findViewById(R.id.tv_loginError);

        et_username = (EditText) findViewById(R.id.et_username);
        et_password = (EditText) findViewById(R.id.et_password);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_signUp = (Button) findViewById(R.id.btn_signUp);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

    }

    private void setListeners() {
        forgotPasswordTextView.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        btn_signUp.setOnClickListener(this);
    }

    /**
     * Returns true if user credentials are valid, false otherwise.
     */
    private boolean validCredentials() {
        // if both password and username empty
        if (et_username.getText().toString().trim().length() <= 0 && et_password.getText().toString().trim().length() <= 0) {
            et_username.setBackgroundResource(R.drawable.edittext_round_red);
            et_password.setBackgroundResource(R.drawable.edittext_round_red);
            return false;
        } else if (et_password.getText().toString().trim().length() <= 0) {
            et_password.setBackgroundResource(R.drawable.edittext_round_red);
            et_username.setBackgroundResource(R.drawable.edittext_round); // change colour back
            return false;
        } else if (et_username.getText().toString().trim().length() <= 0) {
            et_username.setBackgroundResource(R.drawable.edittext_round_red);
            et_password.setBackgroundResource(R.drawable.edittext_round); // Change colour back
            return false;
        }

        return true;
    }

    /**
     * Opens the website Forgot password page in the mobile browser.
     *
     * @param v - Object that was pressed.
     */
    @Override
    public void onClick(View v) {
        int buttonPressed = v.getId();

        switch (buttonPressed) {
            case R.id.forgotPassword:
                openForgotPassword("http://www.xelas.com.au/forgotpassword.php");
                break;
            case R.id.btn_login:

                if (validCredentials()) {
                    String username = et_username.getText().toString();
                    String password = et_password.getText().toString();
                    //Build the HTTP POST
                    formBody = new JSONObject();
                    try {
                        formBody.put("email", username);
                        formBody.put("password", password);
                        new authCredentials().execute(formBody);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                //new LoadUserDetails().execute();
                break;
            case R.id.btn_signUp:
                startActivity(new Intent(Login.this, SignUpActivity.class));
                break;
        }
    }

    public class authCredentials extends AsyncTask<JSONObject, Void, JSONObject> {
        @Override
        protected void onPreExecute() {
            mLoadingIndicator.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(JSONObject... params) {

            JSONObject formBody = params[0];
            JSONObject responseData = null;
            responseData = httpConnect.connection("/public/api/auth/login", formBody);

            return responseData;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if(jsonObject != null) {
                if (!jsonObject.has("error")) {
                    // Store user session details
                    userSession = PreferenceManager.getDefaultSharedPreferences(Login.this);

                    formBody = new JSONObject();
                    try {
                        formBody.put("email", et_username.getText().toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    userSession.edit().putBoolean("loggedIn", true).apply();
                    userSession.edit().putString("email", et_username.getText().toString()).apply();
                    userSession.edit().putInt("num_questions", 7).apply();
                    userSession.edit().putInt("Steps", 0).apply();
                    userSession.edit().putString("lastPull", "test").apply(); // needed
                    try {
                        userSession.edit().putInt("id", jsonObject.getInt("userID")).apply();
                        userSession.edit().putString("access_token", jsonObject.getString("access_token")).apply();
                        userSession.edit().putString("refresh_token", jsonObject.getString("refresh_token")).apply();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    new populateSharedPrefs().execute(formBody);
                    //startActivity(new Intent(Login.this, HomePage.class));
                } else {
                    myToast = Toast.makeText(getApplicationContext(), "Incorrect Email or Password. Please try again.", Toast.LENGTH_LONG);
                    myToast.show();
                }
            } else {
                myToast = Toast.makeText(getApplicationContext(), "An Error occured.", Toast.LENGTH_LONG);
            }
        }
    }

    public class populateSharedPrefs extends AsyncTask<JSONObject, Void, JSONObject> {
        @Override
        protected void onPreExecute() {
            mLoadingIndicator.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(JSONObject... params) {
            JSONObject formBody = params[0];
            JSONObject responseData = null;
            fitbit.checkFitbitDeviceConnected();
            responseData = httpConnect.connection("/public/api/auth/getAccount", formBody);
            return responseData;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if(jsonObject != null) {
                if (!jsonObject.has("error")) {
                    // Store user session details
                    try {
                        userSession.edit().putString("name", jsonObject.getString("user")).apply();
                        userSession.edit().putString("lastName", jsonObject.getString("lastName")).apply();
                        userSession.edit().putString("dob", jsonObject.getString("dob")).apply();
                        userSession.edit().putInt("weight", jsonObject.getInt("weight")).apply();
                        userSession.edit().putInt("height", jsonObject.getInt("height")).apply();
                        userSession.edit().putString("profession", jsonObject.getString("profession")).apply();
                        userSession.edit().putInt("exerciseFrequency", jsonObject.getInt("exerciseFrequency")).apply();
                        userSession.edit().putInt("initialStepCount", 0).apply();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    startActivity(new Intent(Login.this, HomePage.class));
                } else {
                    myToast = Toast.makeText(getApplicationContext(), "There has been an error. Please try again.", Toast.LENGTH_LONG);
                    myToast.show();
                }
            } else {
                myToast = Toast.makeText(getApplicationContext(), "An Error occured.", Toast.LENGTH_LONG);
            }
        }
    }


    public void openForgotPassword(String url) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

}
