package smg.xelas;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Schwa on 31/08/2017.
 */

public class HttpConnect {

    protected IpDetails ipDetails = new IpDetails();

    public JSONObject connection(String Url, JSONObject fields) {
        OkHttpClient client = new OkHttpClient();

        /*client = new OkHttpClient.Builder()
                .connectTimeout(50, TimeUnit.SECONDS)
                .writeTimeout(50, TimeUnit.SECONDS)
                .readTimeout(50, TimeUnit.SECONDS)
                .build();*/

        JSONObject userDetails = null;
        Request request;

            String bodyString = fields.toString();
            Log.d("request", "creting request body...");
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, bodyString);
            Log.d("requestBody", bodyString);
            Log.d("requestHeader", ipDetails.getIpDetails() + Url);

            request = new Request.Builder()
                    // .url("http://192.168.1.100:8080/xelas-webapp/public/api/auth/getAccount")
                    .url(ipDetails.getIpDetails() + Url)
                    .post(body)
                    .addHeader("accept", "application/json")
                    .addHeader("content-type", "application/json")
                    .addHeader("cache-control", "no-cache")
                    .addHeader("postman-token", "aa26c59f-cf16-7856-fc37-7ae65c219936")
                    .build();
       /* }*/

        try {
            Log.d("responseData", "about to send request");
            Log.d("responseData", request.toString());
            Response response = client.newCall(request).execute();
            String responseData;

            Log.d("responseData", "about to check status code");
            Log.d("responseData", "here200");
            responseData = response.body().string();
            userDetails = new JSONObject(responseData);

            if(userDetails.toString().isEmpty()) {
                userDetails.put("message", "An error has occured. Please check your internet connection and try again.");
            }

        } catch (IOException e) {
            e.printStackTrace();
            Log.d("responseData", "caught error");
        } catch (JSONException e) {
            Log.d("responseData", "caught error 2");
            e.printStackTrace();
        }

        //Log.d("responseData", userDetails.toString());
        Log.d("responseData", "here.. finished");

        return userDetails;
    }

    public JSONArray connectionJSONArray(String Url, JSONObject fields) {
        OkHttpClient client = new OkHttpClient();

        JSONArray userDetails = null;
        Request request;


       /* if(Url.equals("/public/api/questions/get")) {
            request = new Request.Builder()
                    .url(ipDetails.getIpDetails() + "/public/api/questions/get")
                    .get().addHeader("cache-control", "no-cache").build();
        } else {*/
        String bodyString = fields.toString();

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, bodyString);
        Log.d("requestBody", bodyString);
        Log.d("requestHeader", ipDetails.getIpDetails() + Url);

        request = new Request.Builder()
                // .url("http://192.168.1.100:8080/xelas-webapp/public/api/auth/getAccount")
                .url(ipDetails.getIpDetails() + Url)
                .post(body)
                .addHeader("accept", "application/json")
                .addHeader("content-type", "application/json")
                .addHeader("cache-control", "no-cache")
                .addHeader("postman-token", "c3d60511-7e0f-5155-b5ad-66031ad76578")
                .build();
       /* }*/

        try {
            Response response = client.newCall(request).execute();
            String responseData;

            Log.d("responseData", "here..");

            if(response.code() == 200) {
                Log.d("responseData", "here200");
                responseData = response.body().string();
                Log.d("responseData", responseData);
                userDetails = new JSONArray(responseData);
                Log.d("responseData", "here200yeaaa");
            } else if (response.code() == 401) {
                Log.d("responseData", "here400..");
                responseData = response.body().string();
                userDetails = new JSONArray(responseData);
            } else {
                userDetails = new JSONArray();
                //userDetails.put("message", "An error has occured. Please try again.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Log.d("responseData", userDetails.toString());
        Log.d("responseData", "here.. return");

        return userDetails;
    }


}
