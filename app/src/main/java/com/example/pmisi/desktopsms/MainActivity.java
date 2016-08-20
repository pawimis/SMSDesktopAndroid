package com.example.pmisi.desktopsms;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

public class MainActivity extends Activity {

    private TCPClient mTcpClient;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    EditText portText;
    EditText ipText;
    Button connect_button;
    Button send_button;
    String gcmRegID = "";
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(checkPlayServices()) {
            new GCMRegistrationTask().execute();
        }else {
            Toast.makeText(getApplicationContext(),"No google services :(",Toast.LENGTH_LONG).show();
            connect_button.setEnabled(false);
        }
        portText = (EditText) findViewById(R.id.EditText_portNumber);
        ipText = (EditText) findViewById(R.id.EditText_ipAddress);
        connect_button = (Button)findViewById(R.id.Button_connect);
        send_button = (Button)findViewById(R.id.Button_send_id);
        send_button.setEnabled(false);

        //relate the listView from java to the one created in xml

        // connect to the server


        connect_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String port = portText.getText().toString();
                String ip_addr = ipText.getText().toString();

                if(!port.isEmpty() && !ip_addr.isEmpty()){
                    if(isInteger(port)){
                        if(isRange(port)){
                            proceed(ip_addr,port);
                            if(!gcmRegID.isEmpty()){
                                send_button.setEnabled(true);
                            }

                        }
                    }
                }
                //sends the message to the server

            }
        });
        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTcpClient != null) {
                    mTcpClient.sendMessage(gcmRegID);
                }else {
                    Toast.makeText(getApplicationContext(),"No connection",Toast.LENGTH_LONG).show();
                }
            }
        });

    }
    private void proceed(String ipAddres , String portNum) {
        class connectTask extends AsyncTask<String, String, TCPClient> {

            @Override
            protected TCPClient doInBackground(String... params) {

                //we create a TCPClient object and
                mTcpClient = new TCPClient(new TCPClient.OnMessageReceived() {
                    @Override
                    //here the messageReceived method is implemented
                    public void messageReceived(String message) {
                        //this method calls the onProgressUpdate
                        publishProgress(message);
                    }
                },params[0],params[1]);
                mTcpClient.run();

                return null;
            }
        }
        connectTask cT = new connectTask();
        cT.execute(ipAddres,portNum);
    }
    private class GCMRegistrationTask extends AsyncTask<Void,Void,String> {

        @Override
        protected String doInBackground(Void... params) {
            Log.d("Registration token", "Executing ");
            try{
                InstanceID instanceID = InstanceID.getInstance(getApplicationContext());
                gcmRegID = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                        GoogleCloudMessaging.INSTANCE_ID_SCOPE,null);
                Log.d("Registration token", "GCM registration Token " + gcmRegID);
            }catch (IOException e){
                e.printStackTrace();
            }
            return gcmRegID;
        }
    }
    private boolean checkPlayServices(){
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if(resultCode != ConnectionResult.SUCCESS){
            if(apiAvailability.isUserResolvableError(resultCode)){
                apiAvailability.getErrorDialog(this,resultCode,PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }else{
                Log.i("Play services", "not supported");
                finish();
            }
            return false;
        }
        return true;
    }
    public  boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch(NumberFormatException e) {
            return false;
        } catch(NullPointerException e) {
            return false;
        }

        return true;
    }
    public boolean isRange(String s){
        int conf = Integer.parseInt(s);
        if((conf >= 1024) && (conf <= 65535))
            return true;
        else
            return false;
    }
}
