package com.data;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.paging.PagedList;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;


//Reference: https://firebase.googleblog.com/2017/12/using-android-architecture-components.html
    public class FirebaseLiveDataLedgers extends LiveData<DataSnapshot> {

    private static final String LOG_TAG = "FirebaseQueryLiveData";
    LiveData<PagedList<LedgerIdAtParent>> mypagedListOfData;
    private final DatabaseReference mRef;
    private final MyValueEventListener listener = new MyValueEventListener();


    public FirebaseLiveDataLedgers(DatabaseReference ref) {
        String currentUser= FirebaseAuth.getInstance().getCurrentUser().getEmail();
        this.mRef = ref;
    }

    public LiveData<PagedList<LedgerIdAtParent>> getPagedList(){

        return mypagedListOfData;
    }

    @Override
    protected void onActive() {
        Log.d(LOG_TAG, "onActive");
        mRef.addValueEventListener(listener);
        super.onActive();
    }

    @Override
    protected void onInactive() {
        Log.d(LOG_TAG, "onInactive");
        mRef.removeEventListener(listener);
        super.onInactive();
    }

    private class MyValueEventListener implements ValueEventListener {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if(dataSnapshot!=null){
            Log.d(LOG_TAG, "onDataChange");
            Log.d(LOG_TAG, "onDataChange this is the data: "+dataSnapshot.getValue().toString());
            setValue(dataSnapshot);
            }
            else{
                Log.e(LOG_TAG, "DataSnapshot was null");
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.e(LOG_TAG, "Could not Perform Read from Firebase " + mRef, databaseError.toException());
        }

    }


}
