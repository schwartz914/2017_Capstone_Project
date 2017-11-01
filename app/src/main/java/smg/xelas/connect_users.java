package smg.xelas;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


public class connect_users extends BaseNavDrawer implements View.OnClickListener {
    Button btn_getCode1, btn_verifyWatcherCode, btn_activateSharing;
    TextView tv_firstCode, tv_secondCode;
    EditText et_verifyCode, et_finalCode;
    ProgressBar mLoadingIndicator;
    JSONObject formBody;
    Toast toastMessage;
    HttpConnect httpConnect = new HttpConnect();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_connect_users, frameLayout);
        init();
    }

    private void init() {
        btn_getCode1 = (Button) findViewById(R.id.getCode1Button);
        tv_firstCode = (TextView) findViewById(R.id.tv_firstCode);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        btn_verifyWatcherCode = (Button) findViewById(R.id.getWatcherCodeButton);
        et_verifyCode = (EditText) findViewById(R.id.et_watcherEnteredCode);
        tv_secondCode = (TextView) findViewById(R.id.tv_secondCode);

        btn_activateSharing = (Button) findViewById(R.id.btn_activateSharing);
        et_finalCode = (EditText) findViewById(R.id.et_finalCode);


        btn_getCode1.setOnClickListener(this);
        btn_verifyWatcherCode.setOnClickListener(this);
        btn_activateSharing.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int buttonPressed = v.getId();
        switch (buttonPressed) {
            case R.id.getCode1Button:
                formBody = new JSONObject();
                try {
                    formBody.put("observeeEmail", userSession.getString("email", null));
                } catch(JSONException  e) {
                    e.printStackTrace();
                }
                new getCode1().execute(formBody);
                break;
            case R.id.getWatcherCodeButton:
                formBody = new JSONObject();
                try {
                    formBody.put("watcherEmail", userSession.getString("email", null));
                    formBody.put("enteredObserveeCode", et_verifyCode.getText() );
                } catch(JSONException  e) {
                    e.printStackTrace();
                }
                new verifyObserveeCode().execute(formBody);
                break;
            case R.id.btn_activateSharing:
                formBody = new JSONObject();
                try {
                    formBody.put("observeeEmail", userSession.getString("email", null));
                    formBody.put("enteredWatcherCode", et_finalCode.getText());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                new finalCodeVerify().execute(formBody);
                break;
        }
    }

    private class getCode1 extends AsyncTask<JSONObject, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONObject doInBackground(JSONObject... params) {
            JSONObject form = params[0];
            JSONObject responseData;
            responseData = httpConnect.connection("/public/api/shareData/getObserveeCode", form);

            return responseData;

        }

        @Override
        protected void onPostExecute(JSONObject result) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            try {
                if (result.getString("message").equals("Added Observee Code")) {
                    tv_firstCode.setText(result.getString("observeeCode"));
                    tv_firstCode.setVisibility(View.VISIBLE);
                } else {
                    toastMessage = Toast.makeText(getApplicationContext(), result.getString("message"), Toast.LENGTH_LONG);
                    toastMessage.show();
                }
            } catch(JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class verifyObserveeCode extends AsyncTask<JSONObject, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONObject doInBackground(JSONObject... params) {
            JSONObject form = params[0];
            JSONObject responseData = null;
            responseData = httpConnect.connection("/public/api/shareData/watcherConfirmCode", form);

            return responseData;

        }

        @Override
        protected void onPostExecute(JSONObject result) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            try {
                if (result.getString("message").equals("Added Watcher Code")) {
                    tv_secondCode.setText(result.getString("watcherCode"));
                    tv_secondCode.setVisibility(View.VISIBLE);
                } else {
                    toastMessage = Toast.makeText(getApplicationContext(), result.getString("message"), Toast.LENGTH_LONG);
                    toastMessage.show();
                }
            } catch(JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class finalCodeVerify extends AsyncTask<JSONObject, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONObject doInBackground(JSONObject... params) {
            JSONObject form = params[0];
            JSONObject responseData = null;
            responseData = httpConnect.connection("/public/api/shareData/finalConfirmCode", form);

            return responseData;

        }

        @Override
        protected void onPostExecute(JSONObject result) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            try {
                if (result.getString("message").equals("Details Shared")) {
                    toastMessage = Toast.makeText(getApplicationContext(), result.getString("message"), Toast.LENGTH_LONG);
                    toastMessage.show();
                } else {
                    toastMessage = Toast.makeText(getApplicationContext(), result.getString("message"), Toast.LENGTH_LONG);
                    toastMessage.show();
                }
            } catch(JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
