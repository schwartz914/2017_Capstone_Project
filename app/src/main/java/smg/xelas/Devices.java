package smg.xelas;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

public class Devices extends BaseNavDrawer implements View.OnClickListener{
    private Button btn_connectFitbit;
    TextView tv_fitbitConnected;
    IpDetails ipDetails = new IpDetails();
    private ProgressBar pb_loading_indicator;
    FitbitApi fitbit = new FitbitApi(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_devices, frameLayout);
        userSession = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        init();

        new deviceConnectedAsync().execute();
    }

    private void init() {
        btn_connectFitbit = (Button) findViewById(R.id.btn_connectFitbit);
        btn_connectFitbit.setOnClickListener(this);

        tv_fitbitConnected = (TextView) findViewById(R.id.tv_fitbitConnected);
        pb_loading_indicator = (ProgressBar)findViewById(R.id.pb_loading_indicator);
    }

    private void hideFitbitConnectButton(){
        btn_connectFitbit.setText("Manage Device");
        tv_fitbitConnected.setVisibility(View.VISIBLE);
    }

    public class deviceConnectedAsync extends AsyncTask<JSONObject, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            pb_loading_indicator.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(JSONObject... params) {
            fitbit.checkFitbitDeviceConnected();

            return userSession.getBoolean("fitbitConnected", false);
        }

        @Override
        protected void onPostExecute(Boolean fitbitConnected) {
            pb_loading_indicator.setVisibility(View.GONE);
            btn_connectFitbit.setVisibility(View.VISIBLE);

            if (fitbitConnected) {
                hideFitbitConnectButton();
            }
        }
    }

    @Override
    public void onClick(View v) {
        int buttonPressed = v.getId();

        switch (buttonPressed) {
            case R.id.btn_connectFitbit:
                Toast.makeText(Devices.this, "fitbit", Toast.LENGTH_LONG).show();
                Uri webpage = Uri.parse(ipDetails.getIpDetails() + "/public/mlogin?email=" + userSession.getString("email", null));
                Intent intent = new Intent(Intent.ACTION_VIEW, webpage);

                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
                break;
        }

    }
}