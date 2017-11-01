package smg.xelas;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Ahmad on 17/08/2017.
 */

public class TokenManager {
    Context context;
    TokenManager(Context context){
        this.context = context;
    }

    /* this is how you refresh a token if its expired
        TokenManager tokenManager = new TokenManager(this);
        SharedPreferences userDetails = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        tokenManager.RefreshToken(userDetails.getString("refresh_token", null));
     */

    // This code works, as of now it isn't called anywhere...
    IpDetails ipDetails = new IpDetails();
    public void RefreshToken(String refreshToken){
        final String token = refreshToken;
        Thread thread = new Thread(new Runnable(){
            public void run() {
                OkHttpClient client = new OkHttpClient();

                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, "{\n\t\"email\" : \"ahmad@email.com\",\n\t\"refresh_token\" : \" " + token + " \"\n}");
                Request request = new Request.Builder()
                        .url(ipDetails.getIpDetails() + "/public/api/refresh")
                        .post(body)
                        .addHeader("accept", "application/json")
                        .addHeader("content-type", "application/json")
                        .addHeader("cache-control", "no-cache")
                        .addHeader("postman-token", "002ec599-d7b4-9c9a-451e-93ce925779ee")
                        .build();

                try {
                    Response response = client.newCall(request).execute();
                    if (response.code() == 200){
                        try {
                            JSONObject obj = new JSONObject(response.body().string());

                            SharedPreferences userSession = PreferenceManager.getDefaultSharedPreferences(context);
                            userSession.edit().putString("access_token", obj.getString("access_token")).commit();
                            userSession.edit().putString("refresh_token", obj.getString("refresh_token")).commit();
                            Log.d("token", "token refreshed");
                        } catch (Throwable t) {
                            //Log.e("My App", "Could not parse malformed JSON: \"" + response.body().string() + "\"");
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
         });

        thread.start();
    }
}
