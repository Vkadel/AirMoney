package com.vkreated.airmoney;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.data.FirebaseLiveDataLedgers;
import com.data.childledger;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.NotNull;

import java.util.List;
//This ViewModel will be dedicated to download ledger Info and ledger Items

public class ledgersViewModel extends ViewModel {
    //TODO: change your reference for the user
    private static final DatabaseReference LEDGERS_REF =
            FirebaseDatabase.getInstance().getReference("/hotstock")
    private final FirebaseLiveDataLedgers childLedgersData = new FirebaseLiveDataLedgers(LEDGERS_REF);

    @NotNull
    public LiveData<DataSnapshot> getLedgers() {
        return childLedgersData;
    }
}
