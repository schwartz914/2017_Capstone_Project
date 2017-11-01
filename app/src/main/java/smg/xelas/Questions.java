package smg.xelas;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Questions extends BaseNavDrawer implements View.OnClickListener {

    private ArrayList<SeekBar> allAnswers = new ArrayList<SeekBar>();
    private final int MAX_SCALE_FOR_QUESTIONS = 100;
    private int numQuestions;

    private HashMap<String, Integer> userVars = new HashMap<>();
    private HashMap<String, Integer> varThresholds = new HashMap<>();

    IpDetails ipDetails = new IpDetails();
    private ProgressBar mLoadingIndicator;
    Toast toastMessage;
    LinearLayout eleven;
    Display display;
    String questions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_questions, frameLayout);
        numQuestions = userSession.getInt("num_questions", 8);
        Log.d("Questions", "number of Questions to show: " + numQuestions);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        eleven = (LinearLayout)findViewById(R.id.ll);
        display = ((WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        setDummyVarVals();
        setThresholds();

        new FetchQuestions().execute();

    }


    private void setDummyVarVals(){
        userVars.put("mentalHealth", 40);
        userVars.put("physicalHealth", 30);
        userVars.put("appetite", 30);
        userVars.put("fatigue", 40);
        userVars.put("mood", 40);
        userVars.put("motivation", 40);
        userVars.put("musclePain", 40);
        userVars.put("nutritionQuality", 40);
        userVars.put("readinessToTrain", 40);
        userVars.put("sleepQuality", 40);
        userVars.put("stress", 40);
        userVars.put("timeSlept", 40);
        //change AGE AND RECENTLY ACTIVE
        userVars.put("recentlyActive", 8);
        userVars.put("exerciseFreq", userSession.getInt("id", 1));
        userVars.put("age", 30);
    }
    private void setThresholds(){
        varThresholds.put("mentalHealthThresholdLo", 30);
        varThresholds.put("mentalHealthThresholdHi", 70);
        varThresholds.put("physicalHealthThresholdLo", 30);
        varThresholds.put("physicalHealthThresholdHi", 70);
        varThresholds.put("appetiteThresholdLo", 30);
        varThresholds.put("appetiteThresholdHi", 70);
        varThresholds.put("fatigueThresholdLo", 30);
        varThresholds.put("fatigueThresholdHi", 70);
        varThresholds.put("moodThresholdLo", 30);
        varThresholds.put("moodThresholdHi", 70);
        varThresholds.put("motivationThresholdLo", 30);
        varThresholds.put("motivationThresholdHi", 70);
        varThresholds.put("musclePainThresholdLo", 30);
        varThresholds.put("musclePainThresholdHi", 70);
        varThresholds.put("nutritionQualityThresholdLo", 30);
        varThresholds.put("nutritionQualityThresholdHi", 70);
        varThresholds.put("readinessToTrainThresholdLo", 30);
        varThresholds.put("readinessToTrainThresholdHi", 70);
        varThresholds.put("sleepQualityThresholdLo", 30);
        varThresholds.put("sleepQualityThresholdHi", 70);
        varThresholds.put("stressThresholdLo", 30);
        varThresholds.put("stressThresholdHi", 70);
        varThresholds.put("timeSleptThresholdLo", 30);
        varThresholds.put("timeSleptThresholdHi", 70);
        varThresholds.put("recentlyActiveThresholdLo", 3);
        varThresholds.put("recentlyActiveThresholdHi", 10);
        varThresholds.put("exerciseFreqThresholdLo", 2);
        varThresholds.put("exerciseFreqThresholdHi", 5);

        varThresholds.put("ageThresholdLo", 25);
        varThresholds.put("ageThresholdHi", 40);
    }

    private class FetchQuestions extends AsyncTask<Void, JSONObject, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            OkHttpClient client = new OkHttpClient();
            Boolean completed = false;

            Request request = new Request.Builder()
                    //.url("http://172.16.42.19/CAB939/public/api/questions/get")

                    .url(ipDetails.getIpDetails() + "/public/api/questions/get")
                    .get().addHeader("cache-control", "no-cache").build();

            try {
                Response response = client.newCall(request).execute();

                if(response.code() == 200){
                    questions = response.body().string();
                    completed = true;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return completed;



        }
        //TODO - Return a String instead of Bool and check if null for error.
        @Override
        protected void onPostExecute(Boolean result) {
            mLoadingIndicator.setVisibility(View.GONE);
            if(result == null) {
                toastMessage = Toast.makeText(getApplicationContext(), "An Error has occured. Please try again.", Toast.LENGTH_LONG);
                toastMessage.show();
            } else {
                Log.d("Questions", "Display Questions");
                DisplayQuestions(questions);
            }

        }
    }



    private void DisplayQuestions(String json){
        json = "{ \"questions\" :" + json + "}"; // added so jsonarray is possible
        Log.d("My App", "JSON: \"" + json + "\"");
        try {
            JSONObject obj = new JSONObject(json);
            JSONArray cast = shuffleJsonArray(obj.getJSONArray("questions"));
            Log.d("My App", "JSON shuffled ");
            int maxQuestions = cast.length();
            int questionCount = 0;

            for (int i=0; i < maxQuestions; i++) {
                if((questionCount<7) && (questionCount<maxQuestions)) {
                    Log.d("Questions", "In loop at: \"" + i + "\"");
                    JSONObject question = cast.getJSONObject(i);
                    Log.d("Questions", "Obtained question: \"" + i + "\"");
                    String name = question.getString("question");
                    Log.d("Questions", "String question: \"" + name + "\"");
                    int id = question.getInt("questionID");
                    Log.d("Questions", "int ID: \"" + i + "\"");
                    String tags = question.getString("tags");
                    Log.d("Questions", "String tags: \"" + i + "\"");
                    String[] tagArray = tags.split(",");
                    Boolean questionShown = false;
                    for (String tag : tagArray) {
                        Log.d("Questions", "For Tag: " + tag + "\"");
                        //Log.d("Questions", "userVar: " + userVars.get(tag) + "\"");
                        //Log.d("Questions", "varThreshold: " + varThresholds.get(tag + "Threshold") + "\"");

                        if (tag.equals("general")) {
                            Log.d("Questions", "Found question?: " + name + "\"");
                            CreateQuestionInputField(name, id, question.getString("type"));
                            questionShown = true;
                            questionCount++;
                            Log.d("Questions", "Question Count: " + questionCount + "\"");
                            break;
                        }
                        Log.d("Questions", "Will split: " + tag + "\"");
                        String[] tagParts = tag.split("\\.");
                        Log.d("Questions", "Tag split into: " + tagParts[0] + "\"");

                        if (tagParts[1].equals("Lo")) {
                            if (userVars.get(tagParts[0]) <= varThresholds.get(tagParts[0] + "ThresholdLo")) {
                                Log.d("Questions", "Found question? with Lo tag: " + name + "\"");
                                CreateQuestionInputField(name, id, question.getString("type"));
                                questionShown = true;
                                questionCount++;
                                Log.d("Questions", "Question Count: " + questionCount + "\"");
                                break;
                            }else{
                                Log.d("Questions", "Tag " + tagParts[0] + " not matched\"");
                            }
                        } else if (tagParts[1].equals("Hi")) {
                            if (userVars.get(tagParts[0]) >= varThresholds.get(tagParts[0] + "ThresholdHi")) {
                                Log.d("Questions", "Found question? with Hi tag: " + name + "\"");
                                CreateQuestionInputField(name, id, question.getString("type"));
                                questionShown = true;
                                questionCount++;
                                Log.d("Questions", "Question Count: " + questionCount + "\"");
                                break;
                            }else{
                                Log.d("Questions", "Tag " + tagParts[0] + " not matched\"");
                            }
                        }

                    /*
                    switch(tagParts[0]){
                        case "mentalHealth":
                        case "physicalHealth":
                        case "appetite":
                        case "mood":
                        case "readinessToTrain":
                        case "nutritionQuality":
                        case "motivation":
                        case "timeSlept":
                        case "sleepQuality":
                        case "fatigue":
                        case "stress":
                        case "musclePain":
                        case "exerciseFreq":
                        case "recentlyActive":
                        case "age":
                            if(userVars.get(tag)<= varThresholds.get(tag + "Threshold"))
                            {
                                Log.d("My App", "Found question?: " + name + "\"");
                                CreateQuestionInputField(name, id, question.getString("type"));
                                questionShown = true;
                            }
                            if(tagParts[1].equals("Lo")){
                                if(userVars.get(tagParts[0])<= varThresholds.get(tagParts[0] + "ThresholdLo"))
                                {
                                    Log.d("My App", "Found question? with Lo tag: " + name + "\"");
                                    CreateQuestionInputField(name, id, question.getString("type"));
                                    questionShown = true;
                                }
                            } else if(tagParts[1].equals("Hi")){
                                if(userVars.get(tagParts[0])<= varThresholds.get(tagParts[0] + "ThresholdHi"))
                                {
                                    Log.d("My App", "Found question? with Hi tag: " + name + "\"");
                                    CreateQuestionInputField(name, id, question.getString("type"));
                                    questionShown = true;
                                }
                            }

                            break;
                        case "fatigue":
                        case "stress":
                        case "musclePain":
                            if(userVars.get(tag)>= varThresholds.get(tag + "Threshold"))
                            {
                                Log.d("My App", "Found question?: " + name + "\"");
                                CreateQuestionInputField(name, id, question.getString("type"));
                                questionShown = true;
                            }
                            break;
                        case "recentlyActive":
                            if(id == 2 && userVars.get(tag)<= varThresholds.get(tag + "Threshold"))
                            {
                                Log.d("My App", "Found question?: " + name + "\"");
                                CreateQuestionInputField(name, id, question.getString("type"));
                                questionShown = true;
                            }
                            break;
                        case "exerciseFreq":
                            if(id == 2 && userVars.get(tag)>= varThresholds.get(tag + "Threshold"))
                            {
                                Log.d("My App", "Found question?: " + name + "\"");
                                CreateQuestionInputField(name, id, question.getString("type"));
                                questionShown = true;
                            }
                            break;
                        case "age":
                            if(id == 3 && userVars.get(tag)>= varThresholds.get(tag + "Threshold"))
                            {
                                Log.d("My App", "Found question?: " + name + "\"");
                                CreateQuestionInputField(name, id, question.getString("type"));
                                questionShown = true;
                            }
                            break;

                    }

                    if (questionShown) {
                        questionShown = false;
                        break;
                    }*/

                    /*if(userVars.get(tag)<= varThresholds.get(tag + "Threshold"))
                    {
                        Log.d("My App", "Found question?: " + name + "\"");
                        CreateQuestionInputField(name, id);
                        break;
                    }*/

                    }
                } else {break;}
                Log.d("Questions", "Out of for each, in for loop \"");
            }

            IncludeSubmitButton();
        } catch (Throwable t) {
            Log.e("My App", "Could not parse malformed JSON: \"" + json + "\"");
        }
    }

    public static JSONArray shuffleJsonArray (JSONArray array) throws JSONException {
        // Implementing Fisher–Yates shuffle
        Random rnd = new Random();
        for (int i = array.length() - 1; i >= 0; i--)
        {
            int j = rnd.nextInt(i + 1);
            Object object = array.get(j);
            array.put(j, array.get(i));
            array.put(i, object);
        }
        return array;
    }

    private boolean isBetween(int lower, int upper, int number){
        if(number >= lower && number <= upper){
            return true;
        }
        return false;
    }

    /* Creates a Seek bar for a given question.
     *
     */
    private void CreateQuestionInputField(String question, int questionID, String questionType){

        int width = display.getWidth();

        SeekBar answer = new SeekBar(this);
        answer.setMax(MAX_SCALE_FOR_QUESTIONS);
        answer.setProgress(MAX_SCALE_FOR_QUESTIONS/2);
        answer.setId(questionID);
        if(questionType.equals("emotional")){
            answer = emotionalQuestionSeekbar(answer);
        } else {
            answer = numericQuestionSeekbar(answer);
        }


        allAnswers.add(answer);

        TextView label = new TextView(this);
        label.setText(question);

        // Label
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.WRAP_CONTENT);
        l.addView(label, lp);

        // SeekBar
        LinearLayout l2 = new LinearLayout(this);
        l2.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.WRAP_CONTENT);
        l2.addView(answer, lp2);

        eleven.addView(l);
        eleven.addView(l2);
    }

    private SeekBar numericQuestionSeekbar(SeekBar answer){
        //answer.setProgressDrawable(new ColorDrawable(Color.TRANSPARENT));
        answer.setThumb(writeOnDrawable(R.drawable.seekbar_quan_thumb, ""));
        answer.getProgressDrawable().setColorFilter(Color.rgb(100, 100, 100), PorterDuff.Mode.MULTIPLY);

        answer.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if (isBetween(0, 49, progress)) {
                    seekBar.getProgressDrawable().setColorFilter(Color.rgb(155 + progress, 100, 100), PorterDuff.Mode.MULTIPLY);
                } else if(isBetween(50, 100, progress)) {
                    seekBar.getProgressDrawable().setColorFilter(Color.rgb(100, 255 - (progress), 100 + progress), PorterDuff.Mode.MULTIPLY);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        return answer;
    }

    private SeekBar emotionalQuestionSeekbar(SeekBar answer){

        //answer.setProgressDrawable(new ColorDrawable(Color.rgb(245, 245, 245)));
        answer.getProgressDrawable().setColorFilter(Color.rgb(100, 254, 255), PorterDuff.Mode.MULTIPLY);

        answer.setThumb(writeOnDrawable(R.drawable.emoji_neutrel, "text"));
        answer.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (isBetween(0, 20, progress)) {
                    seekBar.setThumb(writeOnDrawable(R.drawable.emoji_very_sad, ""));
                } else if(isBetween(21, 40, progress)) {
                    seekBar.setThumb(writeOnDrawable(R.drawable.emoji_sad, ""));
                } else if(isBetween(41, 60, progress)) {
                    seekBar.setThumb(writeOnDrawable(R.drawable.emoji_neutrel, ""));
                } else if(isBetween(61, 80, progress)) {
                    seekBar.setThumb(writeOnDrawable(R.drawable.emoji_happy, ""));
                } else if(isBetween(81, 100, progress)) {
                    seekBar.setThumb(writeOnDrawable(R.drawable.emoji_very_happy, ""));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        return answer;
    }

    public BitmapDrawable writeOnDrawable(int drawableId, String text){
        Bitmap bm = BitmapFactory.decodeResource(getResources(), drawableId).copy(Bitmap.Config.ARGB_8888, true);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);
        paint.setTextSize(20);
        Canvas canvas = new Canvas(bm);
        canvas.drawText(text, 0, bm.getHeight()/2, paint);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels / 4;

        return new BitmapDrawable(getResizedBitmap(bm, width, width));
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    private void IncludeSubmitButton(){
        LinearLayout ll = (LinearLayout)findViewById(R.id.ll);
        Display display = ((WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int width = display.getWidth();
        Button Submit = new Button(this);
        Submit.setText("Done for today!");
        Submit.setId(0);
        Submit.setOnClickListener(this);

        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.WRAP_CONTENT);
        l.addView(Submit, lp);
        ll.addView(l);
    }

    private void storeAnswers() {
        String json = "\"questions\" : {";

        int i = 0;
        for (SeekBar seekbar : allAnswers) {
            json += "\""+ i + "\" : { \"id\" : " + seekbar.getId() + ", \"response\": " + seekbar.getProgress() + "},";
            i++;
        }

        final String questionResponse = json.substring(0, json.length() - 1) + " }";

        Thread thread = new Thread(new Runnable() {
            public void run() {
                OkHttpClient client = new OkHttpClient();
                SharedPreferences userDetails = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                String email = userDetails.getString("email", null);

                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, "{\n\t\"email\" : \"" + email +"\",\n\t\"numQuestions\": " + numQuestions + "," + questionResponse +"}");
                Request request = new Request.Builder()
                        .url(ipDetails.getIpDetails() + "/public/api/questions/store")
                        .post(body)
                        .addHeader("accept", "application/json")
                        .addHeader("content-type", "application/json")
                        .addHeader("cache-control", "no-cache")
                        .addHeader("postman-token", "ece67b06-c470-7f7d-b0af-faa910a9742b")
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    Log.d("toSend", "{\n\t\"email\" : \"" + email +"\",\n\t\"numQuestions\": " + numQuestions + "," + questionResponse);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case 0: // submit button
                storeAnswers();
                Toast.makeText(Questions.this, "submitted", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Questions.this, HomePage.class));
                break;
        }
    }


    private String getAge(String dobStr){
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        String ageArr[] = dobStr.split("/");



        dob.set(Integer.parseInt(ageArr[2]), Integer.parseInt(ageArr[1]), Integer.parseInt(ageArr[0]));

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }

        Integer ageInt = new Integer(age);
        String ageStr = ageInt.toString();

        return ageStr;
    }



    /**
     * FOR TESTING PURPOSES ONLY
     * Public setter for user variables
     * @param key
     * @param val
     */
    public void setUserVariables(String key, Integer val) {
        userVars.put(key, val);
    }

    /**
     * FOR TESTING PURPOSES ONLY
     * public getter for user variables
     * @param key
     * @return
     */
    public int getUserVariables(String key){
        return userVars.get(key);
    }



}
