package com.viewmodels;

import androidx.lifecycle.ViewModel;
import android.content.Context;

import com.data.FirebaseLiveDataLedgersLedgerItemAdd;
import com.data.ledgeritem;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.vkreated.airmoney.R;

public class ledgerItemsViewModel extends ViewModel {
    final static String TAG = "ledgersItemsViewModel";


    public FirebaseLiveDataLedgersLedgerItemAdd getLedgersItems(final Context context, String childLedgerID) {
        //No point on creating a paged List here since it will not be able to provide realtime updates
        final  String myChildrenLedgerItemRefString = context.getResources()
                .getString(R.string.firebase_ref_mchildren_mchilLedgerItem_add, childLedgerID);
        final DatabaseReference LEDGERS_REF =
                FirebaseDatabase.getInstance().getReference(myChildrenLedgerItemRefString);
        FirebaseLiveDataLedgersLedgerItemAdd childLedgersData=new FirebaseLiveDataLedgersLedgerItemAdd(LEDGERS_REF);
        return childLedgersData;
    }

    public boolean addItemToLedger(final Context context,ledgeritem mledgeritem,String childLedgerID){

        final  String myChildrenLedgerItemRefString = context.getResources()
                .getString(R.string.firebase_ref_mchildren_mchilLedgerItem_add, childLedgerID);
        final DatabaseReference LEDGERS_REF =
                FirebaseDatabase.getInstance().getReference(myChildrenLedgerItemRefString);
        FirebaseLiveDataLedgersLedgerItemAdd childLedgersData=new FirebaseLiveDataLedgersLedgerItemAdd(LEDGERS_REF);
        return true;
    }
}
