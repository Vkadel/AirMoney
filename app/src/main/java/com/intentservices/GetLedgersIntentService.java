package com.intentservices;

import android.app.IntentService;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.JobIntentService;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import com.data.FirebaseLiveDataLedgers;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.viewmodels.ledgersViewModel;
import com.vkreated.airmoney.ChildrenLedgerListActivity;
import com.vkreated.airmoney.R;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class GetLedgersIntentService extends JobIntentService {

    private static final String TAG="getLedgersServ";
    public static final String GET_LEDGERS_ACTION="getLedgersAction";
    public static final String BROADCAST_RETURNED_DATA_LABEL ="returnDataLedgers";
    private static final String name="GetLedgersIntentService";


    @Override
    protected void onHandleWork(@NonNull Intent intent) {

        //android.os.Debug.waitForDebugger();

        String dataString = intent.getDataString();
        Log.e(TAG,"got Inside Service");
        //Actual action to perform

        /*
         * Creates a new Intent containing a Uri object
         * BROADCAST_ACTION is a custom Intent action
         */
        String status="mystatus";
        Intent localIntent = new Intent(Constants.BROADCAST_ACTION)
                        // Puts the status into the Intent
                        .putExtra(Constants.EXTENDED_DATA_STATUS, status);
        // Broadcasts the Intent to receivers in this app.
        intent.putExtra(Constants.mMESSAGE,"I love sending messages");
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
        Log.e(TAG,"Sent broadcast");
    }

}
