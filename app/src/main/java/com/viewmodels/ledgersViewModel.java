package com.viewmodels;

import androidx.lifecycle.ViewModel;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.data.FirebaseLiveDataLedgers;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.vkreated.airmoney.R;

//This ViewModel will be dedicated to download ledger Info and ledger Items
//This Viewmodel pulls items currently under the USER profile mchildren
//
public class ledgersViewModel extends ViewModel {
    final static String TAG = "ledgersViewModel";


    public FirebaseLiveDataLedgers getLedgers(final Context context) {
        //No point on creating a paged List here since it will not be able to provide realtime updates
        final  String userChildrenLedgerListPath =context.getString(R.string.firebase_ref_user)+ FirebaseAuth.getInstance().getCurrentUser()
                .getUid()+context.getString(R.string.firebase_ref_mchildren);
        final DatabaseReference LEDGERS_REF =
                FirebaseDatabase.getInstance().getReference(userChildrenLedgerListPath);

        FirebaseLiveDataLedgers childLedgersData=new FirebaseLiveDataLedgers(LEDGERS_REF,context);
        return childLedgersData;
    }


    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}
