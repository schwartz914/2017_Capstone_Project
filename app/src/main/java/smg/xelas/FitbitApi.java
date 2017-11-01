package smg.xelas;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Ahmad-PC on 29/08/2017.
 */

public class FitbitApi {
    Context context;
    FitbitApi(Context context){
        this.context = context;
    }

    private IpDetails ipDetails = new IpDetails();

    private final int SUCCESS_CODE = 200;
    private final int TOKEN_EXPIRED_CODE = 511;

    public String spinnerDateToFitbitPeriod(String sp_date){
        String report = null;

        switch (sp_date){
            case "Last Week":
                report = "1w";  // fitbit requires this format
                break;
            case "Last Month":
                report = "1m";
                break;
            case "Last 3 Months":
                report = "3m";
                break;
            case "Last 6 Months":
                report = "6m";
                break;
            case "Last 12 Months":
                report = "1y";
                break;
        }
        return report;
    }

    public String formatDate(String dateString){

        DateFormat format = new SimpleDateFormat("yyyy-mm-dd", Locale.ENGLISH);
        try {
            Date date = format.parse(dateString);
            return new SimpleDateFormat("dd/mm/yy").format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dateString;
    }

    public String periodToSringDate(String sp_date){
        switch (sp_date){
            case "1w":
                return  "Last Week";
            case "1m":
                return "Last Month";
            case "3m":
                return "Last 3 Months";
            case "6m":
                return "Last 6 Months";
            case "1y":
                return "Last 12 Months";
        }
        return null;
    }


    public void checkFitbitDeviceConnected(){

        Thread thread = new Thread(new Runnable(){
            public void run() {
                OkHttpClient client = new OkHttpClient();
                SharedPreferences userSession = PreferenceManager.getDefaultSharedPreferences(context);
                String email = userSession.getString("email", null);
                MediaType mediaType = MediaType.parse("application/json");
                //TODO send access token with this..
                RequestBody body = RequestBody.create(mediaType, "{\n\t\"email\" : \"" + email + "\"\n}\n");
                Request request = new Request.Builder()
                        .url(ipDetails.getIpDetails() + "/public/api/device/deviceExistsMobile")
                        .post(body)
                        .addHeader("accept", "application/json")
                        .addHeader("content-type", "application/json")
                        .addHeader("cache-control", "no-cache")
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    if(response.code() == SUCCESS_CODE){
                        String json = response.body().string();
                        try {
                            JSONObject obj = new JSONObject(json);
                            userSession.edit().putString("fitbit_accessToken", obj.getString("access_token")).apply();
                            userSession.edit().putString("fitbit_refreshToken", obj.getString("refresh_token")).apply();
                            userSession.edit().putString("fitbit_userID", obj.getString("fitbitID")).apply();
                            userSession.edit().putBoolean("fitbitConnected", true).apply();
                            Log.d("fitbitCheckConnected", obj.getString("refresh_token"));
                            Log.d("fitbit", "fitbit is connected.....");

                        } catch (Throwable t) { Log.e("My App", " malformed JSON: \"" + json + "\""); }
                    } else if (response.code() == TOKEN_EXPIRED_CODE) {

                        Log.d("fitbit", "not connected");
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public JSONObject getActivityForToday(){
        final JSONObject[] toReturn = new JSONObject[1];
        final boolean[] tokenExpired = {false};

        Thread thread = new Thread(new Runnable() {
            public void run() {
            OkHttpClient client = new OkHttpClient();

            SharedPreferences userSession = PreferenceManager.getDefaultSharedPreferences(context);

            String fitbitID = userSession.getString("fitbit_userID", null);
            String accessToken = userSession.getString("fitbit_accessToken", null);

            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, "{\"fitbitID\" : \""+ fitbitID + "\", \"accessToken\" : \"" + accessToken + "\"}");

            Request request = new Request.Builder()
                    .url(ipDetails.getIpDetails() + "/public/api/device/fitbit/fitbitGetActivityToday")
                    .post(body)
                    .addHeader("content-type", "application/json")
                    .addHeader("cache-control", "no-cache")
                    .addHeader("postman-token", "274779cf-79ef-1289-cd19-cbf102ac015c")
                    .build();

                try {
                    Response response = client.newCall(request).execute();
                    String responseString = response.body().string();
                    if(response.code() == 200){
                        Log.d("fitbit", "got activity for today");
                        try {
                            JSONObject responseData = new JSONObject(responseString);
                            toReturn[0] = responseData;
                        } catch (Throwable t) {
                            Log.e("My App", "Could not parse malformed JSON: \"" + responseString + "\"");
                        }
                    } else if (response.code() == TOKEN_EXPIRED_CODE){ // fitbit token expired
                        tokenExpired[0] = true;
                        Log.d("fitbit", "token expried - activity today");
                    } else {
                        Log.d("fitbit", "unknown error code, this was the request: " + "{\"fitbitID\" : \""+ fitbitID + "\", \"accessToken\" : \"" + accessToken + "\"}");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

        try {
            thread.join();
            if (tokenExpired[0] == true){ // token has expired
                refreshToken();
                //redo request
                thread.start();
                thread.join();
            }

            return toReturn[0];
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return toReturn[0];
    }

    public JSONObject getReport(final String period, final String obID, final Boolean sendCopyToEmail){
        final JSONObject[] toReturn = new JSONObject[1];
        final boolean[] tokenExpired = {false};
        Log.d("fitbit", " fetching report...");

        Thread thread = new Thread(new Runnable() {
            public void run() {
                OkHttpClient client = new OkHttpClient();
                SharedPreferences userSession = PreferenceManager.getDefaultSharedPreferences(context);
                Log.d("fitbit", "making request...");
                String fitbitID = userSession.getString("fitbit_userID", null);
                String accessToken = userSession.getString("fitbit_accessToken", null);

                MediaType mediaType = MediaType.parse("application/json");
                String bodyString;

                if (obID == null) {// sendCopyToEmail
                    bodyString =
                            "{" +
                            "\"fitbitID\" : \"" + fitbitID +
                            "\", \"period\" : \"" + period +
                            "\", \"accessToken\" : \"" + accessToken + "\", " +
                            "\"sendCopyToEmail\" : \"" + sendCopyToEmail  + "\""
                            + "}";
                } else {
                    String userID = Integer.toString(userSession.getInt("id", -1));
                    bodyString = "{" +
                            "\"userID\" : \"" + userID + "\", " +
                            "\"period\" : \"" + period + "\", " +
                            "\"obID\" : \"" + obID + "\", " +
                            "\"sendCopyToEmail\" : \"" + sendCopyToEmail +
                            "\"}";
                }

                Log.d("fitbit", bodyString);

                RequestBody body = RequestBody.create(mediaType, bodyString);

                Request request = new Request.Builder()
                        .url(ipDetails.getIpDetails() + "/public/api/device/fitbit/fitbitGetReportTill")
                        .post(body)
                        .addHeader("content-type", "application/json")
                        .addHeader("cache-control", "no-cache")
                        .addHeader("postman-token", "e1733154-571b-42e9-6013-9f0e2643a2d8")
                        .build();

                try {
                    Response response = client.newCall(request).execute();
                    String responseString = response.body().string();
                    if(response.code() == 200){
                        Log.d("fitbit", " succesfully got report...");
                        //Log.d("fitbit", " response is :" + responseString);
                        try {
                            JSONObject responseData = new JSONObject(responseString);
                            toReturn[0] = responseData;
                        } catch (Throwable t) {
                            Log.e("My App", "Could not parse malformed JSON: \"" + responseString + "\"");
                        }
                    } else if (response.code() == TOKEN_EXPIRED_CODE){ // fitbit token expired
                        tokenExpired[0] = true;
                        Log.d("fitbit", "token expried - report");
                    } else {
                        try {
                            JSONObject responseData = new JSONObject(responseString);
                            toReturn[0] = responseData;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d("fitbit", "unknown error code: " + response.code());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

        try {
            thread.join();
            if (tokenExpired[0] == true){ // token has expired
                refreshToken();
                //redo request
                thread.start();
                thread.join();
            }

            return toReturn[0];
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return toReturn[0];
    }

    public void refreshToken() {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                SharedPreferences userSession = PreferenceManager.getDefaultSharedPreferences(context);
                String fitbitID = userSession.getString("fitbit_userID", null);
                String refreshToken = userSession.getString("fitbit_refreshToken", null);

                OkHttpClient client = new OkHttpClient();
                MediaType mediaType = MediaType.parse("application/json");

                String userID = Integer.toString(userSession.getInt("id", -1));
                RequestBody body = RequestBody.create(mediaType, "{\"userID\" : " + userID + ",\"refresh_token\" : \"" + refreshToken + "\"\n}");
                Request request = new Request.Builder()
                        .url(ipDetails.getIpDetails() + "/public/api/device/refreshFitbitToken")
                        .post(body)
                        .addHeader("content-type", "application/json")
                        .addHeader("cache-control", "no-cache")
                        .addHeader("postman-token", "faeaecdf-056f-9ed1-8977-0bd56302adca")
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    if(response.code() == 200){
                        String json = response.body().string();
                        try {
                            JSONObject obj = new JSONObject(json);
                            userSession.edit().putString("fitbit_accessToken", obj.getString("access_token")).apply();
                            userSession.edit().putString("fitbit_refreshToken", obj.getString("refresh_token")).apply();
                            userSession.edit().putString("fitbit_userID", obj.getString("resource_owner_id")).apply();
                            userSession.edit().putBoolean("fitbitConnected", true).apply();
                            Log.d("fitbitsucc", "successfully refreshed fitbit api tokens");
                        } catch (Throwable t) { Log.e("My App", " malformed JSON: \"" + json + "\""); }
                    } else {
                        Log.d("fitbit", "unable to refresh token");
                        userSession.edit().putBoolean("fitbitConnected", false).apply();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}