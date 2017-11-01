package smg.xelas;


import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.Arrays;


public class EditAccount extends BaseNavDrawer implements View.OnClickListener {
    EditText etfirstName, etLastName, etHeight, etWeight, etCurrentPassword, etPassword, etPasswordConfirm;
    Spinner spinProfession, spinWorkOut;
    Button btnUpdate, btnUpdatePassword;
    private String email;
    private ProgressBar mLoadingIndicator;
    Toast toastMessage;
    JSONObject userDetails;
    JSONObject formBody;
    HttpConnect httpConnect = new HttpConnect();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_edit_account, frameLayout);

        initScreen();
        new getDetails().execute();

    }

    private void initScreen() {
        etfirstName = (EditText) findViewById(R.id.eaFirstName);
        etLastName = (EditText) findViewById(R.id.eaLastName);
        etHeight = (EditText) findViewById(R.id.eaHeight);
        etWeight = (EditText) findViewById(R.id.eaWeight);
        spinProfession = (Spinner) findViewById(R.id.eaProfession);
        spinWorkOut = (Spinner) findViewById(R.id.eaWorkout);
        etCurrentPassword = (EditText) findViewById(R.id.eaCurrentPassword);
        etPassword = (EditText) findViewById(R.id.eaPassword);
        etPasswordConfirm = (EditText) findViewById(R.id.eaPasswordConfirm);
        btnUpdate = (Button) findViewById(R.id.btnUpdateDetails);
        btnUpdatePassword = (Button) findViewById(R.id.btnUpdatePassword);
        userSession = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        email = userSession.getString("email", null);

        btnUpdatePassword.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch(id) {
            case R.id.btnUpdateDetails:

                formBody = new JSONObject();
                try {
                    formBody.put("email", email);
                    formBody.put("name", etfirstName.getText().toString());
                    formBody.put("lastName", etLastName.getText().toString());
                    formBody.put("weight", Integer.parseInt(etWeight.getText().toString()));
                    formBody.put("height", Integer.parseInt(etHeight.getText().toString()));
                    formBody.put("profession", spinProfession.getSelectedItem().toString());
                    formBody.put("exerciseFrequency", spinWorkOut.getSelectedItem().toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                new updateDetails().execute(formBody);
                break;
            case R.id.btnUpdatePassword:
                if(etPassword.getText().toString().equals(etPasswordConfirm.getText().toString()) && (!etPassword.getText().toString().isEmpty())) {
                    formBody = new JSONObject();
                    try {
                        formBody.put("email", email);
                        formBody.put("password", etCurrentPassword.getText().toString());
                        formBody.put("newPassword", etPassword.getText().toString());
                        formBody.put("confirmPassword", etPasswordConfirm.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    new updatePassword().execute(formBody);
                    break;
                } else if (etPassword.getText().toString().isEmpty()) {
                    toastMessage = Toast.makeText(this, "Passwords cannot be Blank", Toast.LENGTH_LONG);
                    toastMessage.show();
                } else {
                    toastMessage = Toast.makeText(this, "New Passwords do not match!", Toast.LENGTH_LONG);
                    toastMessage.show();
                }
        }
    }

    private class getDetails extends AsyncTask<Object, Object, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONObject doInBackground(Object... params) {

            // Json string with email and password
            formBody = new JSONObject();
            try{
                formBody.put("email", email);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            userDetails = httpConnect.connection("/public/api/auth/getAccount", formBody);


            return userDetails;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            try {
                    if(result != null) {
                        if (result.getString("message").equals("Details gathered.")) {
                            String[] jobArray = getResources().getStringArray(R.array.jobs);
                            String[] workOutArray = getResources().getStringArray(R.array.workoutValues);

                            int professionInt = Arrays.asList(jobArray).indexOf(userDetails.getString("profession"));
                            int workOutInt = Arrays.asList(workOutArray).indexOf(userDetails.getString("exerciseFrequency"));
                            etfirstName.setText(userDetails.getString("user"));
                            etLastName.setText(userDetails.getString("lastName"));
                            etHeight.setText(userDetails.getString("height"));
                            etWeight.setText(userDetails.getString("weight"));
                            spinProfession.setSelection(professionInt);
                            spinWorkOut.setSelection(workOutInt);
                        } else {
                            toastMessage = Toast.makeText(getApplicationContext(), "An Error has occured. Please try again.", Toast.LENGTH_LONG);
                            toastMessage.show();
                        }
                    } else {
                        toastMessage = Toast.makeText(getApplicationContext(), "An error has occured. Please try again.", Toast.LENGTH_LONG);
                        toastMessage.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
        }
    }

    private class updateDetails extends AsyncTask<JSONObject, Void, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONObject doInBackground(JSONObject... params) {
            JSONObject formBody = params[0];

            JSONObject responseData;
            responseData = httpConnect.connection("/public/api/auth/updateAccount", formBody);

            return responseData;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            try {
                toastMessage = Toast.makeText(getApplicationContext(), result.getString("message"), Toast.LENGTH_LONG);
                toastMessage.show();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class updatePassword extends AsyncTask<JSONObject, Void, JSONObject> {
        @Override
        protected void onPreExecute() {
            mLoadingIndicator.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(JSONObject... params) {
            JSONObject responseData = null;

            JSONObject formBody = params[0];
            responseData = httpConnect.connection("/public/api/auth/updatePassword", formBody);

            return responseData;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            try {
                toastMessage = Toast.makeText(getApplicationContext(), result.getString("message"), Toast.LENGTH_LONG);
                toastMessage.show();

            } catch(JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
