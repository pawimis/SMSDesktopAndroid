package com.example.pmisi.desktopsms;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;


public class MyGcmListenerService extends GcmListenerService {
    private static final String TAG = "MyGcmListenerService";

    @Override
    public void onMessageReceived(String from, Bundle data) {
        Log.d(TAG, "Message received " + data.toString() + " and " + from);
        //super.onMessageReceived(from, data);
        String message = data.getString("message");
        String number = data.getString("number");
        String topic = data.getString("topic");
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + message);
        Log.d(TAG, "Number: " + number);
        if(!topic.isEmpty()){
            if (topic.equals("sms")) {
                Log.d(TAG, "Topic global");
            }
        }
        sendSmsMessage(message, number);
    }
    private void sendSmsMessage(String msg, String phoneNumber){
        if (phoneNumber.length()>0 && msg.length()>0)
        {
            PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this, GcmListenerService.class), 0);
            SmsManager sms = SmsManager.getDefault();
            //sms.sendTextMessage(phoneNumber, null, msg, pi, null);
            Log.d(TAG, "SMS send");
        }
    }
}