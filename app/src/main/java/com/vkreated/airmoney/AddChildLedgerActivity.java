package com.vkreated.airmoney;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import data.childledger;
import data.user;

public class AddChildLedgerActivity extends AppCompatActivity {
boolean menu_on=false;
private static final String TAG="Add Child Ledger Act: ";
public static final String USER_ID_LABEL="userIDlabel";
String currentUserId;
String childId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_child_ledger);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(savedInstanceState==null){
        currentUserId=getIntent().getStringExtra(USER_ID_LABEL);}
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final FloatingActionButton floatingActionButtonMoreActions = (FloatingActionButton) findViewById(R.id.fabChildMoreActions);
        final FloatingActionButton floatingActionButtonDelete = (FloatingActionButton) findViewById(R.id.fabAddChildDelete);
        final FloatingActionButton floatingActionButtonShare=(FloatingActionButton) findViewById(R.id.fabAddChildShare);
        final FloatingActionButton floatingActionButtonUpload=(FloatingActionButton) findViewById(R.id.fabAddChildUpload);
        final TextView actionsLabel=(TextView) findViewById(R.id.actionsLabel);
        final TextView uploadChildLabel=(TextView) findViewById(R.id.uploadChild);
        final TextView clearChildLabel=(TextView) findViewById(R.id.clearChildLabel);
        final TextView shareChildLabel=(TextView) findViewById(R.id.shareChildLabel);
        final TextInputEditText childName=(TextInputEditText)findViewById(R.id.enterChildNameTextInput);

        floatingActionButtonMoreActions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int distanceForMenu=300;
                Animation anim = android.view.animation.AnimationUtils.loadAnimation(floatingActionButtonMoreActions.getContext(),  R.anim.child_expantion_animation);
                floatingActionButtonMoreActions.startAnimation(anim);
                anim.setDuration(200L);
                //Expand Menu
               if(!menu_on){
                   actionsLabel.setVisibility(View.INVISIBLE);
                   rotationFab(floatingActionButtonMoreActions,0,180);
                   ObjectAnimator animation = ObjectAnimator.ofFloat(floatingActionButtonDelete, "translationY", -350f);
                   animationFabOut(floatingActionButtonDelete,distanceForMenu+150*2);
                   animationFabOut(floatingActionButtonShare,distanceForMenu+150);
                   animationFabOut(floatingActionButtonUpload,distanceForMenu);
                   animationFabOut(clearChildLabel,distanceForMenu+150*2);
                   animationFabOut(shareChildLabel,distanceForMenu+150);
                   animationFabOut(uploadChildLabel,distanceForMenu);
                   menu_on=true;
               }
                    //Collapse Menu
                   else{
                   actionsLabel.setVisibility(View.VISIBLE);
                   animationFabIn(clearChildLabel);
                   animationFabIn(shareChildLabel);
                   animationFabIn(uploadChildLabel);
                   rotationFab(floatingActionButtonMoreActions,180,0);
                   animationFabIn(floatingActionButtonDelete);
                   animationFabIn(floatingActionButtonShare);
                   animationFabIn(floatingActionButtonUpload);
                   menu_on=false;
               }
            }
        });
        //Click Listener for when an item needs to be added
        floatingActionButtonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             addChildLedgerToOnlineDatabase(currentUserId,childName.getText().toString());
            }
        });

    }
    void animationFabOut(FloatingActionButton fab,int distance){
        fab.setSize(FloatingActionButton.SIZE_MINI);
        ObjectAnimator animation = ObjectAnimator.ofFloat(fab, "translationY", -distance+'f');
        animation.setDuration(500);
        animation.start();

    }

    void animationFabIn(FloatingActionButton fab){
        final FloatingActionButton fabResize=fab;
        ObjectAnimator animation = ObjectAnimator.ofFloat(fab, "translationY", 0f);
        animation.setDuration(300);
        animation.start();
        //Resizing buttons back to make it fit better under the fist button
        animation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                fabResize.setSize(FloatingActionButton.SIZE_NORMAL);
                super.onAnimationEnd(animation);
            }
        });
    }

    void rotationFab(FloatingActionButton fab, int i, int i1){
        final FloatingActionButton fabResize=fab;
        ObjectAnimator animation = ObjectAnimator.ofFloat(fab, "rotation", i,i1);
        animation.setDuration(300);
        animation.start();
    }

    //text elevation Animation
    void animationFabOut(TextView textView,int distance){
        textView.setVisibility(View.VISIBLE);
        ObjectAnimator animation = ObjectAnimator.ofFloat(textView, "translationY", -distance+'f');
        animation.setDuration(500);
        animation.start();

    }

    void animationFabIn(final TextView textView){
        final TextView fabResize=textView;
        ObjectAnimator animation = ObjectAnimator.ofFloat(textView, "translationY", 0f);
        animation.setDuration(300);
        animation.start();
        //Resizing buttons back to make it fit better under the fist button
        animation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                textView.setVisibility(View.INVISIBLE);
                super.onAnimationEnd(animation);
            }
        });
    }

    void addChildLedgerToOnlineDatabase(final String currentUserId, String childName){
        // Write a message to the database
        final String userid=currentUserId;
        final String mchildName=childName;
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference(getResources().getString(R.string.firebase_ref_user)+currentUserId+"/mchildren");
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Toast toast;
                String valueChildrenString = dataSnapshot.getValue(String.class);
                String []childrenMatrixConversionArray=valueChildrenString.split(",");
                //Add First Child
                if (valueChildrenString.equals("")||valueChildrenString==null||childrenMatrixConversionArray.length==0){
                    setUpChildLedger("0",mchildName,currentUserId);
                    myRef.setValue(userid+"0");
                    myRef.removeEventListener(this);
                    toast=Toast.makeText(getApplication(),"this is the first Child added",Toast.LENGTH_LONG);
                    toast.show();
                }
                //Add more children
                else{
                    setUpChildLedger(String.valueOf(childrenMatrixConversionArray.length),mchildName,currentUserId);
                    myRef.setValue(valueChildrenString+","+userid+String.valueOf(childrenMatrixConversionArray.length));
                    myRef.removeEventListener(this);
                    toast=Toast.makeText(getApplication(),"this is the number"+(childrenMatrixConversionArray.length+1)+"Child added",Toast.LENGTH_LONG);
                    toast.show();
                }
                Log.d(TAG, "Value is: " + valueChildrenString);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }

    void setUpChildLedger(String append,String chilname,String currentUserId){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        childledger childledgerToAdd=new childledger(Integer.valueOf(append),currentUserId,"",chilname,0);
        DatabaseReference myRefChildLedger = database.getReference(getResources().getString(R.string.firebase_ref_child_ledger)+currentUserId+append);
        myRefChildLedger.setValue(childledgerToAdd);
    }
}
