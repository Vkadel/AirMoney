package com.viewmodels;

import androidx.lifecycle.ViewModel;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.data.FirebaseLiveDataLedgersAll;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.vkreated.airmoney.R;

//This ViewModel will be dedicated to download ledger Info and ledger Items
//This Viewmodel pulls items currently under the USER profile mchildren
//
public class ledgersViewModelAllLedgers extends ViewModel {
    final static String TAG = "ledgersViewModel";


    public FirebaseLiveDataLedgersAll getLedgers(final Context context, String ledgerid) {
        //No point on creating a paged List here since it will not be able to provide realtime updates
        final String userChildrenLedgerListPathFull =context.getString(R.string.firebase_ref_mchildren_mchilLedgerItems);
        final DatabaseReference LEDGERS_REF =
                FirebaseDatabase.getInstance().getReference(userChildrenLedgerListPathFull);
        Query myquery=LEDGERS_REF.orderByChild(context.getString(R.string.firebase_key_mchildrenledgerid_sort))
                .equalTo(ledgerid);
        FirebaseLiveDataLedgersAll childLedgersData=new FirebaseLiveDataLedgersAll(myquery,context);
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
