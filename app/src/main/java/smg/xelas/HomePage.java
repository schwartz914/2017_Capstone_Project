package smg.xelas;


import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.ArcProgress;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class HomePage extends BaseNavDrawer implements View.OnClickListener, SensorEventListener {

    private TextView tv_welcomeMSG;
    private ArcProgress pb_steps;
    private Button btn_questions, btn_resetSteps;
    private Button btn_resync;
    private ProgressBar pb_loading_indicator;
    private ProgressBar pb_alerts_loading;
    IpDetails ipDetails = new IpDetails();
    private List<Recommendation> recommendations;
    RecyclerView rv_recommendations;

    //Pedometer Details
    protected SensorManager mSensorManager;
    protected Sensor mStepCounterSensor;
    protected int STEPS_PER_DAY = 10000;
    private final static long MICROSECONDS_IN_ONE_MINUTE = 60000000;
    private Boolean hardwareCapable = false;

    FitbitApi fitbit = new FitbitApi(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_home_page, frameLayout);
        userSession = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String userID = Integer.toString(userSession.getInt("id", -1));
        String name = userSession.getString("name", null);

        if(userID.equals("-1")) {
            userSession.edit().clear().apply();
            startActivity(new Intent(this, Login.class));
        }
        userSession.edit().putString("lastPull", getTodayDate()).apply();
        init();
        initRecommendations();

        tv_welcomeMSG.append(name);
        setTitle(mActivityTitle);

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] values = event.values;
        int value = -1;

        if(userSession.getInt("initialStepCount", 0) == 0) {
            userSession.edit().putInt("initialStepCount", (int)values[0]).apply();
        }

        if(values.length > 0) {
            value = (int) values[0] - userSession.getInt("initialStepCount", 0);
        }
        setSteps(value);
        userSession.edit().putInt("steps", value).apply();

    }


    @Override
    protected void onResume() {
        super.onResume();
        if(hardwareCapable) {
            mSensorManager.registerListener(this, mStepCounterSensor, SensorManager.SENSOR_DELAY_UI);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { //This is never called.

    }

    private void initRecommendations(){
        pb_alerts_loading.setVisibility(View.VISIBLE);
        new getRecommendations().execute();
    }

    @TargetApi(19)
    private void init() {
        tv_welcomeMSG = (TextView) findViewById(R.id.tv_welcomeMSG);
        btn_questions = (Button) findViewById(R.id.btn_questions);
        btn_resync = (Button) findViewById(R.id.btn_resync);

        btn_questions.setOnClickListener(this);
        btn_resync.setOnClickListener(this);
        pb_steps = (ArcProgress)findViewById(R.id.pb_steps);
        pb_loading_indicator = (ProgressBar)findViewById(R.id.pb_loading_indicator);
        pb_alerts_loading = (ProgressBar) findViewById(R.id.pb_alerts_loading);

        rv_recommendations = (RecyclerView)findViewById(R.id.rv_recommendations);
        rv_recommendations.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv_recommendations.setLayoutManager(llm);

        btn_resetSteps = (Button) findViewById(R.id.btn_resetSteps);
        btn_resetSteps.setOnClickListener(this);

        // Circular steps progress bar
        pb_steps.setMax(100);
        pb_steps.setFinishedStrokeColor(Color.CYAN);

        //if fitbit isn't connected, use mobile's step detector
        if(userSession.getBoolean("fitbitConnected", false)) {
            if (!userSession.getString("lastPull", null).equals(getTodayDate())){
                Log.d("testing", "getting new data");
                new fitbitDataAsync().execute();
            } else {
                Log.d("testing", "getting today data");
                updateDeviceData(userSession.getString("distance", null),
                        userSession.getString("heartRate", null),
                        userSession.getString("caloriesOut", null),
                        userSession.getInt("deviceSteps", 0));

                btn_resync.setVisibility(View.VISIBLE);
                btn_resetSteps.setVisibility(View.INVISIBLE);
            }

        } else if (!userSession.getBoolean("fitbitConnected", false) && isKitkatWithStepSensor()){  // otherwise
            startService(new Intent(this, HomePage.class));
            schedAlarm(this);
            pb_loading_indicator.setVisibility(View.INVISIBLE);
            hardwareCapable = true;

            mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            mStepCounterSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

            mSensorManager.registerListener(this, mStepCounterSensor, SensorManager.SENSOR_DELAY_UI);
        } else {
            pb_steps.setVisibility(View.INVISIBLE);
            btn_resetSteps.setVisibility(View.INVISIBLE);
        }
    }

    private void schedAlarm(Context context) {

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.DAY_OF_MONTH, 1);

        PendingIntent pi = PendingIntent.getBroadcast(context, 0, new Intent(context, XelasBroadcastReciever.class), PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 1000*60*60*24, pi);
    }

    public class fitbitDataAsync extends AsyncTask<JSONObject, Void, JSONObject> {
        @Override
        protected void onPreExecute() {
            btn_resync.setVisibility(View.GONE);
            pb_loading_indicator.setVisibility(View.VISIBLE);

            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(JSONObject... params) {
            //fitbit.checkFitbitDeviceConnected();
            if (userSession.getBoolean("fitbitConnected", false) == false){
                fitbit.checkFitbitDeviceConnected(); // recheck for new devices
            } else {
                fitbit.checkFitbitDeviceConnected(); // recheck for latest token...
                return fitbit.getActivityForToday();
            }

            return fitbit.getActivityForToday();
        }

        @Override
        protected void onPostExecute(JSONObject activityForToday) {
            pb_loading_indicator.setVisibility(View.GONE);
            btn_resync.setVisibility(View.VISIBLE);

            if (activityForToday != null) {
                try {
                    JSONObject summary = activityForToday.getJSONObject("summary");
                    Log.d("fitbit2", summary.toString());

                    JSONArray distances = summary.getJSONArray("distances");
                    JSONObject distance = distances.getJSONObject(0); // first element has total

                    //if checks incase device doesn't support certain things
                    String distanceVal = null, heartRate = null, caloriesOut = null;

                    if (summary.has("restingHeartRate")) {
                        heartRate = summary.getString("restingHeartRate");
                        userSession.edit().putString("heartRate", heartRate).apply();
                    }
                    if(summary.has("caloriesOut")){
                        caloriesOut = summary.getString("caloriesOut");
                        userSession.edit().putString("caloriesOut", caloriesOut).apply();
                    }

                    distanceVal = distance.getString("distance");
                    int fitBitSteps = Integer.parseInt(summary.getString("steps"));

                    userSession.edit().putString("distance", distanceVal).apply();
                    userSession.edit().putInt("deviceSteps", fitBitSteps).apply();

                    updateDeviceData(distanceVal, heartRate, caloriesOut, fitBitSteps);

                    userSession.edit().putString("lastPull", getTodayDate()).apply();
                    Toast.makeText(HomePage.this, "Data Synced", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    Log.e("fitbit2", "bad actvity");
                    e.printStackTrace();
                }
            } else {
                Log.d("fitbit2", "couldn't get activity for some reason or other...");
                Toast.makeText(HomePage.this, "There was an error syncing your data. Please try again.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateDeviceData(String distance, String heart, String calories, int steps){

        if (distance != null){
            TextView tv_distance = (TextView) findViewById(R.id.tv_distance);
            ImageView img_totalDistance = (ImageView) findViewById(R.id.img_totalDistance);
            img_totalDistance.setVisibility(View.VISIBLE);
            tv_distance.setText(distance + "km ");
        }

        if (heart != null){
            TextView tv_restingHeartRate = (TextView) findViewById(R.id.tv_restingHeartRate);
            ImageView img_restingHeartRate = (ImageView) findViewById(R.id.img_restingHeartRate);
            img_restingHeartRate.setVisibility(View.VISIBLE);
            tv_restingHeartRate.setText(heart + " ");
        }

        if (calories != null){
            TextView tv_caloriesOut = (TextView) findViewById(R.id.tv_caloriesOut);
            ImageView img_caloriesOut = (ImageView) findViewById(R.id.img_caloriesOut);
            img_caloriesOut.setVisibility(View.VISIBLE);
            tv_caloriesOut.setText(calories + "cal ");
        }

        //pb_steps.setProgress(Math.round((steps * 100.0f) / STEPS_PER_DAY));
        setSteps(steps);
    }

    private String getTodayDate(){
        Date today = new Date();
        today.setHours(0);

        SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/YYYY");
        return sdf.format(today);
    }

    private void setSteps(int steps){
        float percentage = Math.round((steps * 100.0f) / STEPS_PER_DAY);

        // set steps
        pb_steps.setBottomText("" + steps);
        pb_steps.setProgress(Math.round(percentage));

        //size depending on screen
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        pb_steps.setBottomTextSize(width / 16);

        //change to colour green when 100% reached
        if (percentage > 99){
            pb_steps.setProgress(100);
            pb_steps.setTextSize(width/12);
            pb_steps.setFinishedStrokeColor(Color.GREEN);
        }

    }

    @Override
    public void onClick(View v) {
        int buttonPressed = v.getId();

        switch (buttonPressed) {
            case R.id.btn_questions:
                startActivity(new Intent(HomePage.this, Questions.class));
                break;
            case R.id.btn_resync:
                btn_resync.setVisibility(View.GONE);
                pb_loading_indicator.setVisibility(View.VISIBLE);
                new fitbitDataAsync().execute();
                break;
            case R.id.btn_resetSteps:
                new SyncSteps().execute();
                break;
        }
    }

    @TargetApi(19)
    private boolean isKitkatWithStepSensor() {
        // BEGIN_INCLUDE(iskitkatsensor)
        // Require at least Android KitKat
        int currentApiVersion = android.os.Build.VERSION.SDK_INT;
        // Check that the device supports the step counter and detector sensors
        PackageManager packageManager = this.getPackageManager();
        return currentApiVersion >= android.os.Build.VERSION_CODES.KITKAT
                && packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_COUNTER)
                && packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_DETECTOR);
        // END_INCLUDE(iskitkatsensor)
    }


    public class SyncSteps extends AsyncTask<JSONObject, Void, JSONObject> {
        @Override
        protected void onPreExecute() {
            pb_loading_indicator.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(JSONObject... params) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            HttpConnect httpConnect = new HttpConnect();
            JSONObject formBody = new JSONObject();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDate = df.format(cal.getTime());

            try {

                formBody.put("userID", userSession.getInt("id", -1));
                formBody.put("date", formattedDate);
                formBody.put("steps", userSession.getInt("steps", 0));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.d("SyncSteps", formBody.toString());

            JSONObject stepsStored =  httpConnect.connection("/public/api/steps/storeSteps", formBody);

            return stepsStored;
        }

        @Override
        protected void onPostExecute(JSONObject stepsStored) {
            super.onPostExecute(stepsStored);
            pb_loading_indicator.setVisibility(View.INVISIBLE);
            if(!stepsStored.has("error")) {
                Toast.makeText(HomePage.this, "Steps Stored and Reset", Toast.LENGTH_LONG).show();
                pb_steps.setProgress(0);
                int steps = userSession.getInt("steps", 0);
                int initialSteps = userSession.getInt("initialStepCount",0) + steps;
                setSteps(0);
                userSession.edit().putInt("steps", 0).apply();
                userSession.edit().putInt("initialStepCount", initialSteps).apply();
            }
        }
    }

    // what it takes, ... , what it returns
    public class getRecommendations extends AsyncTask<JSONObject, Void, List> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List doInBackground(JSONObject... params) {

            JSONObject formbody = new JSONObject();
            JSONObject returnedData;
            JSONArray toReturn;
            List<Recommendation> recommendations = new ArrayList<>();

            try {
                formbody.put("userId", "2");
                formbody.put("email", "ahmad@email.com");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            HttpConnect httpConnect = new HttpConnect();
            returnedData = httpConnect.connection("/public/api/recommendations/getAll", formbody);
            //int id, String title, String date, int photoId
            try {
                toReturn = (returnedData.getJSONArray("recommendations"));
                for (int i=0; i < toReturn.length(); i++) {
                    JSONObject rec = toReturn.getJSONObject(i);
                    String description = rec.getString("description");
                    String date = formatDate(rec.getString("date"));

                    recommendations.add(new Recommendation(
                            0, description, date, imageRecType(rec.getString("type"))
                            ));
                }

            } catch (JSONException e) {
                Log.d("testingDataallThis", "cant cast!!");
                e.printStackTrace();
                return null;
            }

            Log.d("testingDataallThis", returnedData.toString());

            return recommendations;
        }

        @Override
        protected void onPostExecute(List recommendations) {
            pb_alerts_loading.setVisibility(View.GONE);
            if (recommendations != null) {
                RecommendationsAdapter adapter = new RecommendationsAdapter(recommendations);
                rv_recommendations.setAdapter(adapter);
            } else {
                Log.d("fitbit2", "couldn't get activity for some reason or other...");
            }
        }
    }

    private int imageRecType(String type){
        switch(type) {
            case "steps" :
                return R.drawable.steps;
            case "heart" :
                return R.drawable.resting_heart_rate;
            case "calories" :
                return R.drawable.calories_out;
            case "sleep" :
                return R.drawable.sleep;
            case "stress" :
                return R.drawable.mental;
            case "wellness" :
                return R.drawable.wellness;
            case "mental" :
                return R.drawable.mental;

        }
        return R.drawable.logo;
    }

    public String formatDate(String dateString){

        DateFormat format = new SimpleDateFormat("yyyy-mm-dd", Locale.ENGLISH);
        Date today = new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(0));
        Date lastWeek = new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7));

        try {
            Date date = format.parse(dateString);
            date.compareTo(today);
            if (date.after(lastWeek)){
                if (date.before(today)){
                    return "Last Week";
                }
                else {
                    return "Today";
                }
            }

            return new SimpleDateFormat("dd/mm/yyyy").format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dateString;
    }
    //TODO -- Add a value of starting steps counter and

}