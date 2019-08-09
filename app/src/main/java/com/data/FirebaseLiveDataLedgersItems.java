package com.data;

import androidx.lifecycle.LiveData;
import android.content.Context;
import androidx.annotation.NonNull;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.vkreated.airmoney.R;


//Reference: https://firebase.googleblog.com/2017/12/using-android-architecture-components.html
public class FirebaseLiveDataLedgersItems extends LiveData<DataSnapshot> {

private static final String LOG_TAG = "FirebaseQueryLiveData";
private final DatabaseReference mRef;
private final MyValueEventListener listener = new MyValueEventListener();
private final Context mContext;
private String mid;


public FirebaseLiveDataLedgersItems(DatabaseReference ref, Context context,String id) {
    this.mRef = ref;
    this.mContext=context;
    this.mid=id;

}

@Override
protected void onActive() {
    //TODO:Clean up the code
    Log.d(LOG_TAG, "onActive");
    mRef.orderByChild("mchildrenledgerid").equalTo(mid)
            .limitToFirst(mContext.getResources().getInteger(R.integer.count_of_ledger_items_on_list)).
            addValueEventListener(listener);
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
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        Log.e("onDataChange", "onDataChange: "+dataSnapshot.toString());
        try {
            if(dataSnapshot!=null){
                Log.d(LOG_TAG, "onDataChange");
                Log.d(LOG_TAG, "onDataChange this is the data: "+dataSnapshot.getValue().toString());
                setValue(dataSnapshot);
            }
            else{
                Log.e(LOG_TAG, "DataSnapshot was null");
            }
        }catch (Exception io){
            sendLongToast(mContext.getResources().getString(R.string.add_items_to_this_ledger));
            return;
        }

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.e(LOG_TAG, "Could not Perform Read from Firebase " + mRef, databaseError.toException());
    }

}
//TODO: Create a class sendlongtoasts
    private void sendLongToast(String message) {
        Toast toast=Toast.makeText(mContext,message,Toast.LENGTH_LONG);
        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
        if( v != null) v.setGravity(Gravity.CENTER);
        toast.show();
    }

}
