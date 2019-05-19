package com.data;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.vkreated.airmoney.R;


//Reference: https://firebase.googleblog.com/2017/12/using-android-architecture-components.html
public class FirebaseLiveDataLedgersAll extends LiveData<DataSnapshot> {

private static final String LOG_TAG = "FirebaseQueryLiveData";
private final Query mRef;
private final MyValueEventListener listener = new MyValueEventListener();
private final Context mContext;


public FirebaseLiveDataLedgersAll(Query ref, Context context) {
    String currentUser= FirebaseAuth.getInstance().getCurrentUser().getEmail();
    this.mRef = ref;
    this.mContext=context;

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
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
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
    private void sendLongToast(String message) {
        Toast toast=Toast.makeText(mContext,message,Toast.LENGTH_LONG);
        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
        if( v != null) v.setGravity(Gravity.CENTER);
        toast.show();
    }

}
