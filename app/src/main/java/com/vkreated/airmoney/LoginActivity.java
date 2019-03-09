package com.vkreated.airmoney;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.auth.api.credentials.CredentialRequest;
import com.google.android.gms.auth.api.credentials.CredentialRequestResponse;
import com.google.android.gms.auth.api.credentials.Credentials;
import com.google.android.gms.auth.api.credentials.CredentialsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;

import data.user;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private String TAG="This is the logging activity";
    private static int RC_SIGN_IN = 100;
    TextView sayHiTV;
    FirebaseUser currentUser;
    CredentialsClient mCredentialsClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sayHiTV=findViewById(R.id.say_hi);
        sayHiTV.setVisibility(View.INVISIBLE);
        mCredentialsClient = Credentials.getClient(this);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        //Decide if new authentication is needed
        if(currentUser==null){
            authenticationKickoff();}
        //setting menu enabled
        //TODO set This subtitle to Child name
        getSupportActionBar().setSubtitle("subtitle");
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
                toast=Toast.makeText(this,"I will add a child",Toast.LENGTH_SHORT);
                toast.show();
                Intent gotoChildLedgerSetup=new Intent(this,AddChildLedgerActivity.class);
                gotoChildLedgerSetup.putExtra(AddChildLedgerActivity.USER_ID_LABEL,currentUser.getUid());
                startActivity(gotoChildLedgerSetup);
                return true;
            case R.id.edit_child:
                //startActivity(new Intent(this, Help.class));
                toast=Toast.makeText(this,"I will edit this child",Toast.LENGTH_SHORT);
                toast.show();
                return true;
            case R.id.help_menu:
                //startActivity(new Intent(this, Help.class));
                toast=Toast.makeText(this,"We will help you",Toast.LENGTH_SHORT);
                toast.show();
                return true;
            case R.id.log_out:
                //startActivity(new Intent(this, Help.class));
                toast=Toast.makeText(getApplication(),getResources().getString(R.string.logging_out),Toast.LENGTH_LONG);
                toast.show();
                logWithAnotherAccount();
                authenticationKickoff();
                return true;
            case R.id.sign_in:
                //startActivity(new Intent(this, Help.class));
                toast=Toast.makeText(getApplication(),getResources().getString(R.string.logging),Toast.LENGTH_LONG);
                toast.show();
                authenticationKickoff();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //1. Kick off the FirebaseUI sign in flow, create a sign in intent with your preferred sign-in methods:
    void authenticationKickoff(){
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
                        .setLogo(R.drawable.place_holder_image)
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        if(mAuth!=null){
        currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);}
    }

    //2. When the the sign-in flow is complete, you will receive the result:
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Log.e(TAG,"this user: "+ user.getDisplayName());
                updateUI(user);
                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }

    private void updateUI(FirebaseUser currentUser) {
        Log.e(TAG,": Need to update UI");
        sayHiTV=findViewById(R.id.say_hi);
        sayHiTV.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        sayHiTV.setVisibility(View.VISIBLE);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        if(currentUser!=null){
            String fullName=currentUser.getDisplayName();
            String firstName[]=fullName.split(" ");
            String hi_messssage= getResources().getText(R.string.say_hi)+"\n"+firstName[0];
            sayHiTV.setText(hi_messssage);
            //TODO: Add check if user already exist
            checkIfUserExistAndSetUp(database,currentUser);
        }
    }

    void logWithAnotherAccount(){
        currentUser=null;
        mAuth=null;
        sayHiTV.setText("");
        //Dissable autoCredentials to prevent the user from automatically get logged back in
        mCredentialsClient.disableAutoSignIn();
        CredentialRequest mCredentialRequest = new CredentialRequest.Builder()
                .setPasswordLoginSupported(true)
                .build();
        mCredentialsClient.request(mCredentialRequest).addOnCompleteListener(new OnCompleteListener<CredentialRequestResponse>() {
            @Override
            public void onComplete(@NonNull Task<CredentialRequestResponse> task) {
                Log.e(TAG,"got response");
                if(task.isSuccessful()){
                    CredentialRequestResponse credentialRequestResponse=task.getResult();
                    mCredentialsClient.delete(credentialRequestResponse.getCredential());
                }
            }
        });
    }

   //This method Checks if the user exists before trying to add it.
    //Otherwise user information will get overwritten
    void checkIfUserExistAndSetUp(FirebaseDatabase database, FirebaseUser currentUser){
        final DatabaseReference myRef = database.getReference(getResources().getString(R.string.firebase_ref_user)+currentUser.getUid());
        final FirebaseUser mcurrentUser=currentUser;
        boolean userExist=false;
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                user mUser = dataSnapshot.getValue(user.class);

                //Only add the user if the user does not exist
                if(mUser==null){
                addUserOnDataBase(mcurrentUser);}
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

    void addUserOnDataBase(FirebaseUser currentUser){
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(getResources().getString(R.string.firebase_ref_user)+currentUser.getUid());

        user muser=new user(currentUser.getUid(),user.PARENT,currentUser.getDisplayName(),currentUser.getUid().toString(), currentUser.getEmail(),"0216512","");
        myRef.setValue(muser);
    }
}
