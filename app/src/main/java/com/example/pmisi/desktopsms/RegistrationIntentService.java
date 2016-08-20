package com.example.pmisi.desktopsms;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

/**
 * Created by pmisi on 11.08.2016.
 */

public class RegistrationIntentService extends IntentService {
    private static final String TAG = "RegIntentService";
    String token;
    public RegistrationIntentService() {
        super(TAG);
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        Context mContext = getApplicationContext();
        SharedPreferences sharedPref = mContext.getSharedPreferences("GCM_Preferences", Context.MODE_PRIVATE);

        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            token = instanceID.getToken(getString(R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
        }catch (Exception e){
            e.printStackTrace();
        }
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("Phone_GCM_id", token);
    }
}
