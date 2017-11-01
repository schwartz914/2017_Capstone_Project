package smg.xelas;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class ShareData extends BaseNavDrawer implements View.OnClickListener{

    IpDetails ipDetails = new IpDetails();

    FitbitApi fitbit = new FitbitApi(this);
    HttpConnect httpConnect = new HttpConnect();
    SharedPreferences userDetails;
    ProgressBar pb_loading_indicator;
    Button btn_connectUsers;

    JSONObject formBody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_share_data, frameLayout);

        init();
        userDetails = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        setTitle(mActivityTitle);
        pb_loading_indicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        JSONObject formBody = new JSONObject();
        try {
            formBody.put("userID", "2"); // change to ID!!
            new getUserObserveeAsync().execute(formBody);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void init(){
        btn_connectUsers = (Button) findViewById(R.id.btn_connectUsers);
        btn_connectUsers.setOnClickListener(this);
    }

    private View.OnClickListener addButtonOnClickListener(final Button button, final String userID)  {
        return new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ShareData.this, Reports.class);
                Log.d("fitbit", userID);
                intent.putExtra("obID", userID);
                Spinner sp_date = (Spinner) findViewById(Integer.parseInt(userID));
                Log.d("fitbit", "from sp" + fitbit.spinnerDateToFitbitPeriod(sp_date.getSelectedItem().toString()));
                intent.putExtra("period", fitbit.spinnerDateToFitbitPeriod(sp_date.getSelectedItem().toString()));
                startActivity(intent);
            }
        };
    }

    private void displayObservee(String name, String weight, String height, String id){
        LinearLayout ll_listUsers = (LinearLayout) findViewById(R.id.ll_listUsers);

        //report for label
        TextView reportForLabel = new TextView(this);
        reportForLabel.setText("\nFor when do you want a report? select below:");

        //spinner
        Spinner sp_date = new Spinner(this);
        ArrayAdapter reportAdapter = ArrayAdapter.createFromResource(this, R.array.reports_array, R.layout.spinner_first_item);
        reportAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_date.setAdapter(reportAdapter);
        sp_date.setId(Integer.parseInt(id)); // set user id as sp id

        // Label
        TextView label = new TextView(this);
        label.setText(" \n" + name);
        label.setTextSize(30);
        LinearLayout l = new LinearLayout(this);
        //l.setOrientation(LinearLayout.HORIZONTAL);
        l.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        l.addView(label, lp);

        // sub Label
        TextView subLabel = new TextView(this);
        subLabel.setText("Weight: " + weight + "kgs Height: " + height + "cms");
        subLabel.setTextSize(20);

        LinearLayout l2 = new LinearLayout(this);
        //l2.setOrientation(LinearLayout.HORIZONTAL);
        l2.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        l2.addView(subLabel, lp2);

        // button
        Button viewUser = new Button(this);
        viewUser.setText("Generate Report");
        viewUser.setOnClickListener(addButtonOnClickListener(viewUser, id));
        LinearLayout l3 = new LinearLayout(this);
        l3.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams lp3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        l3.addView(viewUser, lp3);

        ll_listUsers.addView(l);
        ll_listUsers.addView(l2);
        ll_listUsers.addView(reportForLabel);
        ll_listUsers.addView(sp_date);
        ll_listUsers.addView(l3);
    }

    public class getUserObserveeAsync extends AsyncTask<JSONObject, Void, JSONArray> {
        @Override
        protected void onPreExecute() {
            pb_loading_indicator.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected JSONArray doInBackground(JSONObject... params) {
            return getUserObservees();
        }

        @Override
        protected void onPostExecute(JSONArray observees) {
            pb_loading_indicator.setVisibility(View.INVISIBLE);
            try {
                //JSONArray observees = getUserObservees();
                int len = observees.length();
                if (len > 0){
                    for (int i=0; i < len; i++) {
                        JSONObject thisUser = observees.getJSONObject(i);
                        String id = thisUser.getString("observeeID");
                        //userDetails.edit().
                        String name = thisUser.getString("name") + " " + thisUser.get("lastName");
                        String weight = thisUser.getString("weight");
                        String height = thisUser.getString("height");

                        displayObservee(name, weight, height,  id);
                    }
                } else {

                }
            } catch (Throwable t) { Log.e("sharingData", "error parsing json array"); }
        }
    }

    private JSONArray getUserObservees(){
        final JSONArray[] toReturn = new JSONArray[1];

        Thread thread = new Thread(new Runnable() {
            public void run() {
            OkHttpClient client = new OkHttpClient();


            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, "{\n\t\"userID\" : \"2\"\n}\n");
            Request request = new Request.Builder()
                    .url(ipDetails.getIpDetails() + "/public/api/shareData/getUserObservees")
                    .post(body)
                    .addHeader("accept", "application/json")
                    .addHeader("content-type", "application/json")
                    .addHeader("cache-control", "no-cache")
                    .addHeader("postman-token", "5369dfda-2ff2-f725-2ea2-397fadd4fb04")
                    .build();

                try {
                    Log.d("sharingData", "about to exe");
                    Response response = client.newCall(request).execute();
                    String res = response.body().string();

                    Log.d("sharingData", res);
                    if(response.code() == 200){
                        res = "{ \"results\" :" + res + "}"; //added so jsonarray is possible

                        try {
                            JSONObject obj = new JSONObject(res);
                            JSONArray cast = obj.getJSONArray("results");
                            toReturn[0] = cast;

                            Log.d("sharingData", "user");
                        } catch (Throwable t) { Log.e("sharingData", " malformed JSON: \"" + res + "\""); }

                    } else if (response.code() == 500) {
                        Log.d("sharingData", "no one found ");
                    } else {
                        Log.d("sharingData", "error ");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

        try {
            thread.join();
            return toReturn[0];
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return toReturn[0];
    }

    @Override
    public void onClick(View v) {
        int buttonPressed = v.getId();

        switch (buttonPressed) {
            case R.id.btn_connectUsers:
                startActivity(new Intent(ShareData.this, connect_users.class));
                break;
        }

    }


}