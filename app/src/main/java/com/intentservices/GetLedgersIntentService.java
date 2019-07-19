package com.intentservices;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;

import com.google.gson.Gson;

import java.util.HashMap;


public class GetLedgersIntentService extends JobIntentService {

    private static final String TAG="getLedgersServ";
    public static final String GET_LEDGERS_ACTION="getLedgersAction";
    public static final String BROADCAST_RETURNED_DATA_LABEL ="returnDataLedgers";
    private static final String name="GetLedgersIntentService";


    @Override
    protected void onHandleWork(@NonNull Intent intent) {

        //android.os.Debug.waitForDebugger();

        String dataString = intent.getDataString();
        String myvariableLedger=intent.getStringExtra(Constants.mAction_GET_LEDGER_DATA);

        Gson mjson=new Gson();

        HashMap<String,String> map;

        Log.e(TAG,"got Inside Service");
        //Actual action to perform

        /*
         * Creates a new Intent containing a Uri object
         * BROADCAST_ACTION is a custom Intent action
         */
        String result="mystatus";
        switch (intent.getAction()){
            case Constants.mAction_GET_LEDGER_ACTION:
                //Send Request to get
                Log.d(TAG,"got Action to download data");
        }


        Intent localIntent = new Intent(Constants.BROADCAST_ACTION)
                        // Puts the result into the Intent
                        .putExtra(Constants.RESULT_OF_SERVICE, dataString);

        // Broadcasts the Intent to receivers in this app.
        intent.putExtra(Constants.mMESSAGE,"I love sending messages");
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
        Log.e(TAG,"Sent broadcast");

    }

}
