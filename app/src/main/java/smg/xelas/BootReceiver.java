package smg.xelas;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

/**
 * Created by Peter on 4/10/2017.
 */

public class BootReceiver extends BroadcastReceiver {
    public void onReceive(final Context context, final Intent intent) {
        SharedPreferences prefs = context.getSharedPreferences("steps", Context.MODE_PRIVATE);
        int steps = prefs.getInt("steps", 0);
        prefs.edit().putInt("steps", steps).apply();

        context.startService(new Intent(context, HomePage.class));
    }

}
