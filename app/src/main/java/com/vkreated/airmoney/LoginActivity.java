package com.vkreated.airmoney;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.auth.api.credentials.CredentialRequest;
import com.google.android.gms.auth.api.credentials.CredentialRequestResponse;
import com.google.android.gms.auth.api.credentials.Credentials;
import com.google.android.gms.auth.api.credentials.CredentialsClient;
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
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.data.childledger;
import com.data.user;
import com.utils.SendALongToast;

public class LoginActivity extends AppCompatActivity {
    private static Boolean sharedChildissetup=false;
    private static int RC_SIGN_IN = 100;
    TextView sayHiTV;
    FirebaseUser currentUser;
    CredentialsClient mCredentialsClient;
    String currentUserAuthenticatedID;
    private FirebaseAuth mAuth;
    private String TAG = "Logging activity: ";
    private Uri mInvitationUrl;
    private Activity mActivity;
    private Button goToLedgersBut;
    private FirebaseDatabase myDatabase;
    private static boolean isPersisting = false;
    static Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Set up local storage to avoid downloading information that has not changed

        super.onCreate(savedInstanceState);
        Log.d(TAG, "OnCreate");
        checkForDynamicLink();
        setContentView(R.layout.activity_login);
        sayHiTV = findViewById(R.id.say_hi);
        goToLedgersBut= findViewById(R.id.gotoChildLedger);
        sayHiTV.setVisibility(View.INVISIBLE);
        mCredentialsClient = Credentials.getClient(this);
        mActivity=this;
        mContext=this;
        if(!isPersisting){
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            isPersisting=true;
        }

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        //Check if returning from another activity
        if(getIntent().hasExtra(AddChildLedgerActivity.CURRENT_USER_ID)){

        currentUserAuthenticatedID=getIntent().getStringExtra(AddChildLedgerActivity.CURRENT_USER_ID);}


        //Decide if new authentication is needed
        if ((currentUser == null && currentUserAuthenticatedID==null)||mAuth.getCurrentUser()==null) {
            authenticationKickoff();
        }
        else{
            //GetAuthenticated User
            currentUser=mAuth.getCurrentUser();
            setUpActionBar(currentUser);
        }

        goToLedgersBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Check if user has children Ledgers before sending and ask to create one
                //go to activity with list of ledgers will be listed
                Intent intent=new Intent(getApplicationContext(), ChildrenLedgerListActivity.class);
                intent.putExtra(getResources().getString(R.string.user_id_key),mAuth.getUid());
                startActivity(intent);
            }
        });



    }


    private void setUpActionBar(FirebaseUser currentUser) {
        if (currentUser != null) {
            getSupportActionBar().setSubtitle(currentUser.getEmail().toString());
            //TODO: may need to check if a userShared items with me.
        } else {
            getSupportActionBar().setSubtitle("no user logged");
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        Toast toast;
        switch (item.getItemId()) {
            case R.id.add_child:
                //startActivity(new Intent(this, About.class));
                toast = Toast.makeText(this, "I will add a child", Toast.LENGTH_SHORT);
                toast.show();
                Intent gotoChildLedgerSetup = new Intent(this, AddChildLedgerActivity.class);
                gotoChildLedgerSetup.putExtra(AddChildLedgerActivity.USER_ID_LABEL, currentUser.getUid());
                startActivity(gotoChildLedgerSetup);
                return true;
            case R.id.edit_child:
                //startActivity(new Intent(this, Help.class));
                toast = Toast.makeText(this, "I will edit this child", Toast.LENGTH_SHORT);
                toast.show();
                return true;
            case R.id.help_menu:
                //startActivity(new Intent(this, Help.class));
                toast = Toast.makeText(this, "We will help you", Toast.LENGTH_SHORT);
                toast.show();
                return true;
            case R.id.log_out:
                //startActivity(new Intent(this, Help.class));
                toast = Toast.makeText(getApplication(), getResources().getString(R.string.logging_out), Toast.LENGTH_LONG);
                toast.show();
                logWithAnotherAccount();
                authenticationKickoff();
                return true;
            case R.id.sign_in:
                //startActivity(new Intent(this, Help.class));
                toast = Toast.makeText(getApplication(), getResources().getString(R.string.logging), Toast.LENGTH_LONG);
                toast.show();
                authenticationKickoff();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        if (mAuth != null && mAuth.getCurrentUser()!=null) {
            currentUser = mAuth.getCurrentUser();
            updateUI(currentUser);
        }else{
            authenticationKickoff();
        }
    }

    //2. When the the sign-in flow is complete, you will receive the result:
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                new SendALongToast(this,getResources().getString(R.string.say_hi)+
                        FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).show();
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Log.e(TAG, "this user: " + user.getDisplayName());
                updateUI(user);
                // ...
            } else {
                new SendALongToast(this,getResources().getString(R.string.login_failed)).show();
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
        setUpActionBar(currentUser);
    }

    //1. Kick off the FirebaseUI sign in flow, create a sign in intent with your preferred sign-in methods:
    void authenticationKickoff() {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build());

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setLogo(R.drawable.place_holder_image_logo).setIsSmartLockEnabled(true)
                        .build(),
                RC_SIGN_IN);
    }

    private void updateUI(FirebaseUser currentUser) {
        Log.e(TAG, ": Need to update UI");
        sayHiTV = findViewById(R.id.say_hi);
        sayHiTV.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        sayHiTV.setVisibility(View.VISIBLE);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        if (currentUser != null) {
            String fullName = currentUser.getDisplayName();
            String firstName[] = fullName.split(" ");
            String hi_messssage = getResources().getText(R.string.say_hi) + "\n" + firstName[0];
            sayHiTV.setText(hi_messssage);
            //TODO: Add check if user already exist
            checkIfUserExistAndSetUp(database, currentUser);
        }
    }

    void logWithAnotherAccount() {
        currentUser = null;
        mAuth = null;
        sayHiTV.setText("");
        //Dissable autoCredentials to prevent the user from automatically get logged back in
        mCredentialsClient.disableAutoSignIn();
        CredentialRequest mCredentialRequest = new CredentialRequest.Builder()
                .setPasswordLoginSupported(true)
                .build();
        mCredentialsClient.request(mCredentialRequest).addOnCompleteListener(new OnCompleteListener<CredentialRequestResponse>() {
            @Override
            public void onComplete(@NonNull Task<CredentialRequestResponse> task) {
                Log.e(TAG, "got response");
                if (task.isSuccessful()) {
                    CredentialRequestResponse credentialRequestResponse = task.getResult();
                    mCredentialsClient.delete(credentialRequestResponse.getCredential());
                }
            }
        });
    }

    //This method Checks if the user exists before trying to add it.
    //Otherwise user information will get overwritten
    void checkIfUserExistAndSetUp(final FirebaseDatabase database, FirebaseUser currentUser) {
        final DatabaseReference myRef = database.getReference(getResources().getString(R.string.firebase_ref_user) + currentUser.getUid());
        final FirebaseUser mcurrentUser = currentUser;
        boolean userExist = false;
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                user mUser = dataSnapshot.getValue(user.class);

                //Only add the user if the user does not exist
                if (mUser == null) {
                    addUserOnDataBase(mcurrentUser);
                }
                myRef.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
                myRef.removeEventListener(this);
            }
        });
    }

    //Adds the authenticaded user to the database unless it exists

    void addUserOnDataBase(FirebaseUser currentUser) {
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(getResources().getString(R.string.firebase_ref_user) + currentUser.getUid());

        user muser = new user(currentUser.getUid(), user.PARENT, currentUser.getDisplayName(), currentUser.getUid().toString(), currentUser.getEmail(), "0216512", null);
        myRef.setValue(muser);
    }

    //Check if the user has a dynamic Link
    //TODO: Might want to make this a service
    void checkForDynamicLink() {
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                        }
                        //
                        // If the user isn't signed in and the pending Dynamic Link is
                        // an invitation, sign in the user anonymously, and record the
                        // referrer's UID.
                        //

                        //TODO: may want to log in as unauthenticaded user.. in which case the if will need: user == null
                        //                                &&
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (deepLink != null
                                && deepLink.getBooleanQueryParameter("invitedby", false)) {
                            String referrerUid = deepLink.getQueryParameter("invitedby");
                            String sharedledger = deepLink.getQueryParameter("sharedledger");
                            String key = deepLink.getQueryParameter("key");
                            Log.e(TAG, "I was referredby: " + referrerUid + "thechildID is: " + sharedledger + " the key is: "+key);
                            setUpSharedChild(referrerUid, sharedledger,key);
                        }
                    }
                });
    }

    private void setUpSharedChild(String referrerUid, String sharedledger,String key) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //TODO: Add Child to mchildren for logged user
        //TODO: Add current user to mparentowners on childrenledgers node.
        //Problem: How to edit the node if the parent is not there already
        //Perhaps use the original node owner id
        //Ensure the Child setup sequence is only called once
        if(!sharedChildissetup){
        addChildtoUserOwned(sharedledger,key);}

    }

    private void addChildtoUserOwned(final String sharedledger, final String key) {
        final String thisChildLedger=sharedledger;
        final String userid = currentUser.getUid();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        //Add key to shared to be able to read the childnode
        final DatabaseReference myRefKey = database.getReference(getResources().getString(R.string.firebase_ref_add_shared_child_to_user,userid,sharedledger));
        myRefKey.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Get current Owned Childrenledgers by this user
                String valueChildrenString = dataSnapshot.getValue(String.class);
                //Add First Childledger
                myRefKey.removeEventListener(this);
                myRefKey.setValue(key);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //TODO: Change /mchildren to Stringed ref
        //Add this ledger  to userLedgers
        final DatabaseReference myRef = database.getReference(getResources().getString(R.string.firebase_ref_user) + userid + "/mchildren/"+sharedledger);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Get current Owned Childrenledgers by this user
                String valueChildrenString = dataSnapshot.getValue(String.class);
                //Add First Childledger
                    myRef.removeEventListener(this);
                    myRef.setValue(getResources().getString(R.string.mtrue));
                //Add this child to parent mychildren
                final DatabaseReference mchildrenRef=database.getReference(getResources().getString(R.string.firebase_ref_mparentowners_add,sharedledger,userid));
                mchildrenRef.setValue(getResources().getString(R.string.mtrue));
                new SendALongToast(mContext,getResources().getString(R.string.child_added)).show();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Add this user to children Owners
        final DatabaseReference myRefChildLedgerParentOwners =
                database.getReference(getResources().getString(R.string.firebase_ref_child_ledger,sharedledger));

        myRefChildLedgerParentOwners.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myRefChildLedgerParentOwners.removeEventListener(this);
                Map<String ,String> myOwners;
                childledger thisChildLeger=dataSnapshot.getValue(childledger.class);
                myOwners=thisChildLeger.getMparentowners();
                myOwners.put(userid,getResources().getString(R.string.mtrue));
                //Update Value
                thisChildLeger.setMparentowners(myOwners);
                DatabaseReference setChild=database.getReference(getResources().getString(R.string.firebase_ref_child_ledger,sharedledger));
                sharedChildissetup=true;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void setUpConnectivityCheck(){
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        NetworkRequest.Builder builder=new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
        NetworkRequest networkRequest=builder.build();
        //Setting up networkcallback for system changes. In here the actions for when user is
        //connected and disconnedted online are listed
        ConnectivityManager.NetworkCallback mCallback=new ConnectivityManager.NetworkCallback(){
            @Override
            public void onAvailable(Network network) {
                new SendALongToast(getApplicationContext(),getResources().getString(R.string.have_internet)).show();
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //TODO: anything that you want to DO if there is a connection
                        new SendALongToast(getApplicationContext(),getResources().getString(R.string.have_internet)).show();
                    }
                });
                super.onAvailable(network);
            }

            @Override
            public void onLost(Network network) {
                new SendALongToast(getApplicationContext(),getResources().getString(R.string.lost_connection)).show();
                Log.e(TAG, "onAvailable: "+"sent connectionlost toast");
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //TODO: anything that you want to DONT if there is a connection
                        new SendALongToast(getApplicationContext(),getResources().getString(R.string.network_is_not_available)).show();
                    }
                });
                super.onLost(network);
            }
        };
        //Asking to be notified of connect/disconnects from internet
        connectivityManager.registerNetworkCallback(networkRequest,mCallback);
    }


}
