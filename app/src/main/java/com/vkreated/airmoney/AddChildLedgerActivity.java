package com.vkreated.airmoney;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import com.data.childledger;

public class AddChildLedgerActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    boolean menu_on = false;
    public static final String CURRENT_USER_ID ="user_id";
    private static final String TAG = "Add Child Ledger Act: ";
    public static final String USER_ID_LABEL = "userIDlabel";
    String currentUserId;
    String childId;
    String[] typesOfLedgers;
    String typeOfledgerSelected;
    private Uri mInvitationUrl;
    Toast toast;
    String currentChildID=null;
    TextInputEditText childNameforChange;
    Spinner spinnerForChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_child_ledger);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        typesOfLedgers=getResources().getStringArray(R.array.spinner_for_add_child_unit);
        typeOfledgerSelected=typesOfLedgers[0];
        if (savedInstanceState == null) {
            currentUserId = getIntent().getStringExtra(USER_ID_LABEL);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final FloatingActionButton floatingActionButtonMoreActions = (FloatingActionButton) findViewById(R.id.fabChildMoreActions);
        final FloatingActionButton floatingActionButtonDelete = (FloatingActionButton) findViewById(R.id.fabAddChildDelete);
        final FloatingActionButton floatingActionButtonShare = (FloatingActionButton) findViewById(R.id.fabAddChildShare);
        final FloatingActionButton floatingActionButtonUpload = (FloatingActionButton) findViewById(R.id.fabAddChildUpload);
        final TextView actionsLabel = (TextView) findViewById(R.id.actionsLabel);
        final TextView uploadChildLabel = (TextView) findViewById(R.id.uploadChild);
        final TextView clearChildLabel = (TextView) findViewById(R.id.clearChildLabel);
        final TextView shareChildLabel = (TextView) findViewById(R.id.shareChildLabel);
        final TextInputEditText childName = (TextInputEditText) findViewById(R.id.enterChildNameTextInput);
        final Spinner spinner = (Spinner) findViewById(R.id.chooseUnitAddChildSpinner);
        childNameforChange=childName;
        setUpSpinner(spinner);
        spinnerForChange=spinner;
        floatingActionButtonMoreActions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int distanceForMenu = 300;
                Animation anim = android.view.animation.AnimationUtils.loadAnimation(floatingActionButtonMoreActions.getContext(), R.anim.child_expantion_animation);
                floatingActionButtonMoreActions.startAnimation(anim);
                anim.setDuration(200L);
                //Expand Menu
                if (!menu_on) {
                    actionsLabel.setVisibility(View.INVISIBLE);
                    rotationFab(floatingActionButtonMoreActions, 0, 180);
                    int distance=getResources().getInteger(R.integer.base_distance_for_fab_but_menu);
                    animationFabOut(floatingActionButtonDelete, distanceForMenu + distance * 2);
                    animationFabOut(floatingActionButtonShare, distanceForMenu + distance);
                    animationFabOut(floatingActionButtonUpload, distanceForMenu);
                    animationFabOut(clearChildLabel, distanceForMenu + distance * 2);
                    animationFabOut(shareChildLabel, distanceForMenu + distance);
                    animationFabOut(uploadChildLabel, distanceForMenu);
                    menu_on = true;
                }
                //Collapse Menu
                else {
                    actionsLabel.setVisibility(View.VISIBLE);
                    animationFabIn(clearChildLabel);
                    animationFabIn(shareChildLabel);
                    animationFabIn(uploadChildLabel);
                    rotationFab(floatingActionButtonMoreActions, 180, 0);
                    animationFabIn(floatingActionButtonDelete);
                    animationFabIn(floatingActionButtonUpload);
                    menu_on = false;
                }
            }
        });
        //Click Listener for when an item needs to be added
        floatingActionButtonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Before Adding someone check if the user entered a name and ledgerType

                if(!childName.getText().toString().equals("")&&typeOfledgerSelected!=null&&
                        !typeOfledgerSelected.equals(typesOfLedgers[0])){
                addChildLedgerToOnlineDatabase(currentUserId, childName.getText().toString());
                }
                else{
                    toast = Toast.makeText(getApplication(), getResources().getString(R.string.please_add_name_and_ledger),Toast.LENGTH_LONG);
                    toast.show();
                }

            }
        });

        //button to share this child with another user
        floatingActionButtonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check that there is a name and an uploaded child before sharing
                if(currentChildID!=null&&!childName.getText().toString().equals("")&&childName.getText()!=null){
                    shareAchild(currentChildID);
                }
                else{
                    toast = Toast.makeText(getApplication(), getResources().getString(R.string.please_add_name_and_ledger_upload),Toast.LENGTH_LONG);
                    toast.show();
                }

            }
        });

        //This button will clear the child name
        floatingActionButtonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearEntry();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //

            this.navigateUpTo(new Intent(this, LoginActivity.class));

            Intent intent = new Intent(this, LoginActivity.class);

            intent.putExtra(CURRENT_USER_ID,currentUserId);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void clearEntry(){
        childNameforChange.setText("");
        currentChildID=null;
        int position=spinnerForChange.getSelectedItemPosition();
        //spinnerForChange.setSelection(0);
    }

    void animationFabOut(FloatingActionButton fab, int distance) {
        fab.setSize(FloatingActionButton.SIZE_MINI);
        ObjectAnimator animation = ObjectAnimator.ofFloat(fab, "translationY", -distance + 'f');
        animation.setDuration(500);
        animation.start();

    }

    void animationFabIn(FloatingActionButton fab) {
        final FloatingActionButton fabResize = fab;
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

    void rotationFab(FloatingActionButton fab, int i, int i1) {
        final FloatingActionButton fabResize = fab;
        ObjectAnimator animation = ObjectAnimator.ofFloat(fab, "rotation", i, i1);
        animation.setDuration(300);
        animation.start();
    }

    //text elevation Animation
    void animationFabOut(TextView textView, int distance) {
        textView.setVisibility(View.VISIBLE);
        ObjectAnimator animation = ObjectAnimator.ofFloat(textView, "translationY", -distance + 'f');
        animation.setDuration(500);
        animation.start();

    }

    void animationFabIn(final TextView textView) {
        final TextView fabResize = textView;
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

    //Setup the Spinner
    void setUpSpinner(Spinner spinner) {
        //Make Spinner always select money first

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_for_add_child_unit, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    void addChildLedgerToOnlineDatabase(final String currentUserId, final String childName) {
        // Write a message to the database
        final String userid = currentUserId;
        final String mchildName = childName;
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final int random=new Random().nextInt();
        //TODO: STRING
        final DatabaseReference myRef = database.getReference(getResources().getString(R.string.firebase_ref_user) + currentUserId + "/mchildren/"+currentUserId+random+childName+typeOfledgerSelected);
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                //It also removes the listener to avoid calling it a
                //second time once the value is written

                String doesThisLedgerExists = dataSnapshot.getValue(String.class);

                //Add Child if Child does not exist
                if(doesThisLedgerExists==null){
                    myRef.removeEventListener(this);
                    setUpChildLedger(String.valueOf(random), mchildName, currentUserId,childName,typeOfledgerSelected);
                    myRef.setValue(getResources().getString(R.string.mtrue));
                    //TODO:STRING
                    sendLongToast("Child added");
                    }
                else {
                    //TODO:STRING
                    sendLongToast("Child exists");
                }

                Log.d(TAG, "Value is: " + doesThisLedgerExists);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }

    void setUpChildLedger(String append, String childname, String currentUserId,String childName,String typeOfledgerSelected) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        Map<String, String> mparentowners= new HashMap<>();
        mparentowners.put(currentUserId,getResources().getString(R.string.mtrue));
        childledger childledgerToAdd = new childledger(Integer.valueOf(append),mparentowners,"", childname, 0,typeOfledgerSelected,null,null);
        DatabaseReference myRefChildLedger = database.getReference(getResources().getString(R.string.firebase_ref_child_ledger) + currentUserId + append+childName+typeOfledgerSelected);
        myRefChildLedger.setValue(childledgerToAdd);
        //Add this Parent to child owners
        DatabaseReference myRefChildLedgerParentOwners = database.getReference(getResources().getString(R.string.firebase_ref_child_ledger) + currentUserId + append+childName+typeOfledgerSelected+"/mparentowners/"+currentUserId);
        myRefChildLedgerParentOwners.setValue(getResources().getString(R.string.mtrue));
        currentChildID=myRefChildLedger.getKey();
    }


    //Methods needed to make spinner react to user input
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        typeOfledgerSelected=parent.getItemAtPosition(position).toString();
        Log.d(TAG, "onItemSelected: "+parent.getItemAtPosition(position));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    void shareAchild(String childLedgerId){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        //Generate randomKey string
        final String ranKey= UUID.randomUUID().toString();
        //TODO find A way to send just the shared link: +"
        //TODO: use resources for the link strings
        String link = "https://airmoney.page.link/?invitedby=" + uid+"&sharedledger="+childLedgerId+"&key="+ranKey;
        FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse(link))
                .setDomainUriPrefix("https://airmoney.page.link")
                .setAndroidParameters(
                        new DynamicLink.AndroidParameters.Builder().build())
                .buildShortDynamicLink()
                .addOnSuccessListener(new OnSuccessListener<ShortDynamicLink>() {
                    @Override
                    public void onSuccess(ShortDynamicLink shortDynamicLink) {
                        mInvitationUrl = shortDynamicLink.getShortLink();
                        sendInvitation();
                        // ...
                    }
                });
        addShareKeyToChildLedgerNode(childLedgerId, ranKey);
        //Release current child id.
        //TODO: check if releasing ID is the right choice
        currentChildID=null;
    }

    private void addShareKeyToChildLedgerNode(String childLedgerId, final String ranKey) {
        //Add KEY to childLedgerNode
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        //TODO: STRING
        //final DatabaseReference myRefChildLedger = database.getReference(getResources().getString(R.string.firebase_ref_child_ledger) +childLedgerId+"/mSharedKeys/"+ranKey);

        final DatabaseReference myRefChildLedger = database
                .getReference(getResources().getString(R.string.firebase_ref_child_ledger) +childLedgerId);

        //Get childledger Node
        childledger childToModify;
        myRefChildLedger.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                //It also removes the listener to avoid calling it a
                //second time once the value is written

                //ADD ShareKey
                childledger mchildledger=dataSnapshot.getValue(childledger.class);
                Map<String,String> mKeys=mchildledger.getmSharedKeys();
                if(mKeys==null){
                    mKeys=new HashMap<>();
                }
                mKeys.put(ranKey,getResources().getString(R.string.mtrue));
                mchildledger.setmSharedKeys(mKeys);
                myRefChildLedger.setValue(mchildledger).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        sendLongToast(getResources().getString(R.string.sentShared));
                        clearEntry();
                    }
                });
                myRefChildLedger.removeEventListener(this);

                //Replace Child
                Log.d(TAG, "Value is: ");
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    void sendInvitation(){
        //TODO: Enter Strings on string file
        String referrerName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        String subject = String.format("%s wants to share a ledger on AirMoney!", referrerName);
        String invitationLink = mInvitationUrl.toString();
        String msg = "Please click on the link on your Android device to add a child from: "+referrerName+" "
                + invitationLink;
        String msgHtml = String.format("I want to share a child ledger with you: "
                + "%s\"", invitationLink);

        //TODO: delete Email only intent
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, msgHtml.toString());
        //TODO: Add text to Strings
        intent.putExtra(Intent.EXTRA_SUBJECT, "Invitation From: "+referrerName);
        startActivity(Intent.createChooser(intent, "Share Link"));

        /*if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }*/
    }
    private void sendLongToast(String message) {
        Toast toast=Toast.makeText(getApplication(),message,Toast.LENGTH_LONG);
        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
        if( v != null) v.setGravity(Gravity.CENTER);
        toast.show();
    }

}
