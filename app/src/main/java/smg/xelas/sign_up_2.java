package smg.xelas;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class sign_up_2 extends AppCompatActivity implements View.OnClickListener {


    private EditText et_height, et_weight, et_dob;
    private Spinner sp_gender, sp_profession, sp_exerciseFreq;
    private Button btn_done;
    private ProgressBar pb_indicator;

    private String email = null;
    private String firstName = null;
    private String lastName = null;
    private String password = null;
    JSONObject formBody;
    SharedPreferences userSession;
    HttpConnect httpConnect = new HttpConnect();

    private HashMap<String, Integer> ViewBackgrounds = new HashMap<>();
    public String connectionFormBody;

    private Toast myToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_2);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            email = extras.getString("email");
            firstName = extras.getString("firstName");
            lastName = extras.getString("lastName");
            password = extras.getString("password");
        } else {
            myToast = Toast.makeText(this, "An Error has Occurred.", Toast.LENGTH_SHORT);
            myToast.show();
            //rediect page to login just incase user somehow ends up on second login screen
            startActivity(new Intent(sign_up_2.this, Login.class));
        }

        initButtons();
        setListeners();
        setSpinnerAdapters();

        et_dob.setInputType(InputType.TYPE_NULL);
        et_dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
            }
        });
        et_dob.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showDatePickerDialog(v);
                }
            }
        });


    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }


    /**
     * Initalize and assign
     */
    private void initButtons() {
        btn_done = (Button) findViewById(R.id.btn_done);
        et_dob = (EditText) findViewById(R.id.et_dobSignup);
        et_height = (EditText) findViewById(R.id.et_height);
        et_weight = (EditText) findViewById(R.id.et_weight);

        sp_gender = (Spinner) findViewById(R.id.sp_gender);
        sp_profession = (Spinner) findViewById(R.id.sp_profession);
        sp_exerciseFreq = (Spinner) findViewById(R.id.sp_exerciseFreq);


        ViewBackgrounds.put("dob", R.drawable.edittext_round);
        ViewBackgrounds.put("height", R.drawable.edittext_round);
        ViewBackgrounds.put("weight", R.drawable.edittext_round);
        ViewBackgrounds.put("gender", R.drawable.spinnerbg);
        ViewBackgrounds.put("profession", R.drawable.spinnerbg);
        ViewBackgrounds.put("exerciseFreq", R.drawable.spinnerbg);


        //sets default text to speed up debugging
        et_dob.setText("01/01/2000");
        et_weight.setText("100");
        et_height.setText("180");

        pb_indicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);


    }


    private void setListeners() {
        btn_done.setOnClickListener(this);
    }





    /**
     * Validates values in EditTexts
     *
     * @return boolean. false if anything is invalid, true if all is valid.
     */
    private boolean validCredentials(String dobText, String heightText, String weightText, String genderText
            , String professionText, String exerciseFreqText) {
        Boolean allValid = true;

        final String dateRegex = "(0?[1-9]|[12][0-9]|3[01])[/.-](0?[1-9]|1[012])[/.-](19[0-9][0-9]|200[0-9]|201[0-6])";
        Pattern pattern = Pattern.compile(dateRegex);
        Matcher matcher = pattern.matcher(dobText);
        Boolean dobIsValid = matcher.matches();

        //padding values for spinner to restore padding values due to overwrite when setting bg resource
        int padLeft = sp_gender.getPaddingLeft();
        int padRight = sp_gender.getPaddingRight();
        int padTop = sp_gender.getPaddingTop();
        int padBottom = sp_gender.getPaddingBottom();

        if (dobText.length() == 0) {

            et_dob.setBackgroundResource(R.drawable.edittext_round_red);
            ViewBackgrounds.put("dob", R.drawable.edittext_round_red);
            allValid = false;
        } else {
            et_dob.setBackgroundResource(R.drawable.edittext_round);
            ViewBackgrounds.put("dob", R.drawable.edittext_round);
        }
        if(heightText.length() <= 0){

        }
        if (heightText.length() == 0) {

            et_height.setBackgroundResource(R.drawable.edittext_round_red);
            ViewBackgrounds.put("height", R.drawable.edittext_round_red);
            allValid = false;
        } else {
            et_height.setBackgroundResource(R.drawable.edittext_round);
            ViewBackgrounds.put("height", R.drawable.edittext_round);
        }
        if (weightText.length() == 0) {
            et_weight.setBackgroundResource(R.drawable.edittext_round_red);
            ViewBackgrounds.put("weight", R.drawable.edittext_round_red);
            allValid = false;
        } else {
            et_weight.setBackgroundResource(R.drawable.edittext_round);
            ViewBackgrounds.put("weight", R.drawable.edittext_round);
        }
        if (genderText.equals(getResources().getStringArray(R.array.gender_array)[0])) {
            sp_gender.setBackgroundResource(R.drawable.spinnerbg_red);
            ViewBackgrounds.put("gender", R.drawable.spinnerbg_red);
            sp_gender.setPadding(padLeft, padTop, padRight, padBottom);
            allValid = false;
        } else {
            sp_gender.setBackgroundResource(R.drawable.spinnerbg);
            ViewBackgrounds.put("gender", R.drawable.spinnerbg);
            sp_gender.setPadding(padLeft, padTop, padRight, padBottom);
        }
        if (professionText.equals(getResources().getStringArray(R.array.jobs)[0])) {
            sp_profession.setBackgroundResource(R.drawable.spinnerbg_red);
            ViewBackgrounds.put("profession", R.drawable.spinnerbg_red);
            sp_profession.setPadding(padLeft, padTop, padRight, padBottom);
            allValid = false;
        } else {
            sp_profession.setBackgroundResource(R.drawable.spinnerbg);
            ViewBackgrounds.put("profession", R.drawable.spinnerbg);
            sp_profession.setPadding(padLeft, padTop, padRight, padBottom);
        }

        if (exerciseFreqText.equals(getResources().getStringArray(R.array.workoutValues)[0])) {
            sp_exerciseFreq.setBackgroundResource(R.drawable.spinnerbg_red);
            ViewBackgrounds.put("exerciseFreq", R.drawable.spinnerbg_red);
            sp_exerciseFreq.setPadding(padLeft, padTop, padRight, padBottom);
            allValid = false;
        } else {
            sp_exerciseFreq.setBackgroundResource(R.drawable.spinnerbg);
            ViewBackgrounds.put("exerciseFreq", R.drawable.spinnerbg);
            sp_exerciseFreq.setPadding(padLeft, padTop, padRight, padBottom);
        }

        return allValid;
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_done) {

            String dobText = et_dob.getText().toString().trim();
            String heightText = et_height.getText().toString().trim();
            String weightText = et_weight.getText().toString().trim();
            String genderText = sp_gender.getSelectedItem().toString();
            String professionText = sp_profession.getSelectedItem().toString();
            String exerciseFreqText = sp_exerciseFreq.getSelectedItem().toString();
            Integer exerciseFreqNum = Arrays.asList(getResources().getStringArray(R.array.workoutStrings)).indexOf(sp_exerciseFreq.getSelectedItem().toString());
            if (validCredentials(dobText, heightText, weightText, genderText, professionText, exerciseFreqText)) {
                dobText = formatDob(dobText);
                formBody = new JSONObject();
                try {
                    formBody.put("name", firstName);
                    formBody.put("lastName", lastName);
                    formBody.put("height", heightText);
                    formBody.put("weight", weightText);
                    formBody.put("email", email);
                    formBody.put("profession", professionText);
                    formBody.put("exerciseFrequency", exerciseFreqNum);
                    formBody.put("DOB", dobText);
                    formBody.put("password", password);
                    new sign_up_2.authCredentials().execute(formBody);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                myToast = Toast.makeText(this, "Your credentials are invalid", Toast.LENGTH_SHORT);
                myToast.show();
            }
        }
    }

    private class authCredentials extends AsyncTask<JSONObject, Void, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pb_indicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONObject doInBackground(JSONObject... params) {
            JSONObject formBody = params[0];
            JSONObject responseData = null;
            //next line for testing of response string
            connectionFormBody = formBody.toString();
            Log.d("Signupformbody", connectionFormBody);
            responseData = httpConnect.connection("/public/api/auth/register", formBody);
            return responseData;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            pb_indicator.setVisibility(View.INVISIBLE);
            if (jsonObject != null) {
                Log.d("Signup", "Response String: " + jsonObject.toString());
                if(jsonObject.has("email") && !jsonObject.isNull("email")) {
                    Log.d("My App", "Email tag is shown");
                    myToast = Toast.makeText(getApplicationContext(), "The email has been used.", Toast.LENGTH_LONG);
                    myToast.show();
                    startActivity(new Intent(sign_up_2.this, SignUpActivity.class));
                } else if (!jsonObject.has("error")) {
                    // Store user session details
                    userSession = PreferenceManager.getDefaultSharedPreferences(sign_up_2.this);
                    userSession.edit().putBoolean("loggedIn", true).apply();
                    userSession.edit().putString("email", email).apply();
                    userSession.edit().putString("name", firstName).apply();
                    userSession.edit().putString("lastName", lastName).apply();
                    userSession.edit().putString("dob", et_dob.getText().toString().trim()).apply();
                    userSession.edit().putString("height", et_height.getText().toString().trim()).apply();
                    userSession.edit().putString("weight", et_weight.getText().toString().trim()).apply();
                    userSession.edit().putString("profession", sp_profession.getSelectedItem().toString()).apply();
                    userSession.edit().putInt("num_questions", 7).apply();
                    userSession.edit().putInt("exerciseFreq", (Arrays.asList(getResources().getStringArray(R.array.workoutStrings)).indexOf(sp_exerciseFreq.getSelectedItem().toString()))).apply();
                    userSession.edit().putInt("Steps", 0).apply();


                    try {
                        userSession.edit().putInt("id", jsonObject.getInt("userID")).apply();
                        userSession.edit().putString("access_token", jsonObject.getString("access_token")).apply();
                        userSession.edit().putString("refresh_token", jsonObject.getString("refresh_token")).apply();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    startActivity(new Intent(sign_up_2.this, HomePage.class));
                }
            } else {
                myToast = Toast.makeText(getApplicationContext(), "An Error occurred. Please check your internet connection and try again.", Toast.LENGTH_LONG);
                myToast.show();
            }
        }
    }

    private String formatDob(String dobText) {
        String[] dobArray = dobText.split("[.-/]");
        return dobArray[2] + "-" + dobArray[1] + "-" + dobArray[0];
    }

    private void setSpinnerAdapters() {

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter genderAdapter = ArrayAdapter.createFromResource(this,
                R.array.gender_array, R.layout.spinner_first_item);
        // Specify the layout to use when the list of choices appears
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        sp_gender.setAdapter(genderAdapter);

        sp_gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                parent.getItemAtPosition(pos);
                if (pos == 0) {
                    ((TextView) parent.getChildAt(0)).setTextColor(et_dob.getHintTextColors());
                } else {
                    ((TextView) parent.getChildAt(0)).setTextColor(Color.parseColor("#000000"));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        ArrayAdapter professionAdapter = ArrayAdapter.createFromResource(this, R.array.jobs, R.layout.spinner_first_item);
        professionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_profession.setAdapter(professionAdapter);

        sp_profession.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                parent.getItemAtPosition(pos);
                if (pos == 0) {
                    ((TextView) parent.getChildAt(0)).setTextColor(et_dob.getHintTextColors());
                } else {
                    ((TextView) parent.getChildAt(0)).setTextColor(Color.parseColor("#000000"));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        ArrayAdapter exerciseFreqAdapter = ArrayAdapter.createFromResource(this, R.array.workoutStrings, R.layout.spinner_first_item);
        exerciseFreqAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_exerciseFreq.setAdapter(exerciseFreqAdapter);

        sp_exerciseFreq.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                parent.getItemAtPosition(pos);
                if (pos == 0) {
                    ((TextView) parent.getChildAt(0)).setTextColor(et_dob.getHintTextColors());
                } else {
                    ((TextView) parent.getChildAt(0)).setTextColor(Color.parseColor("#000000"));
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView){}
        });

    }


    public int getViewBackground(String view){
        return ViewBackgrounds.get(view);
    }


}
