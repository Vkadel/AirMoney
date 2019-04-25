package com.data;

import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


    //Reference: https://firebase.googleblog.com/2017/12/using-android-architecture-components.html
    public class FirebaseLiveDataLedgers extends LiveData<DataSnapshot> {

    private static final String LOG_TAG = "FirebaseQueryLiveData";

    private final Query query;
    private final MyValueEventListener listener = new MyValueEventListener();

    public FirebaseLiveDataLedgers(Query query) {
        this.query = query;
    }

    @Override
    protected void onActive() {
        Log.d(LOG_TAG, "onActive");
        query.addChildEventListener((ChildEventListener) listener);
        super.onActive();
    }

    @Override
    protected void onInactive() {
        Log.d(LOG_TAG, "onInactive");
        query.removeEventListener(listener);
        super.onInactive();
    }

    private class MyValueEventListener implements ValueEventListener {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            setValue(dataSnapshot);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.e(LOG_TAG, "Can't listen to query " + query, databaseError.toException());
        }
    }
}
