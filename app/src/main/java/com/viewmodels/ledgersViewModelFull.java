package com.viewmodels;

import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.data.FirebaseLiveDataLedgers;
import com.data.FirebaseLiveDataLedgersFull;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.vkreated.airmoney.R;

//This ViewModel will be dedicated to download ledger Info and ledger Items
//This Viewmodel pulls items currently under the USER profile mchildren
//
public class ledgersViewModelFull extends ViewModel {
    final static String TAG = "ledgersViewModel";


    public FirebaseLiveDataLedgersFull getLedgers(final Context context, String ledgerid) {
        //No point on creating a paged List here since it will not be able to provide realtime updates
        final String userChildrenLedgerListPathFull =context.getString(R.string.firebase_ref_child_ledger)+ ledgerid;
        final DatabaseReference LEDGERS_REF =
                FirebaseDatabase.getInstance().getReference(userChildrenLedgerListPathFull);

        FirebaseLiveDataLedgersFull childLedgersData=new FirebaseLiveDataLedgersFull(LEDGERS_REF);
        Log.e(TAG, "ledgersViewModelFull: "+userChildrenLedgerListPathFull.toString() );
        return childLedgersData;
    }


    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}
