package smg.xelas;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;

/**
 * Created by 3lmo on 4/10/2017.
 */

public class XelasBroadcastReciever extends BroadcastReceiver {

    public void onReceive (Context context, Intent intent) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit().putInt("steps", 0).apply();
    }
}
