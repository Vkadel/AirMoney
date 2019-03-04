package com.vkreated.airmoney;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.auth.api.credentials.CredentialRequest;
import com.google.android.gms.auth.api.credentials.CredentialRequestResponse;
import com.google.android.gms.auth.api.credentials.Credentials;
import com.google.android.gms.auth.api.credentials.CredentialsApi;
import com.google.android.gms.auth.api.credentials.CredentialsClient;
import com.google.android.gms.auth.api.credentials.CredentialsOptions;
import com.google.android.gms.auth.api.credentials.IdentityProviders;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

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
            mCredentialsClient.disableAutoSignIn();
            authenticationKickoff();}

        //Set up Log off and sign up with another account
        Button logOffandLogWithAnother=findViewById(R.id.log_out_with_another);
        logOffandLogWithAnother.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast=Toast.makeText(getApplication(),getResources().getString(R.string.logging_out),Toast.LENGTH_LONG);
                logWithAnotherAccount();
                authenticationKickoff();
            }
        });

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
        if(currentUser!=null){
            String fullName=currentUser.getDisplayName();
            String firstName[]=fullName.split(" ");
            String hi_messssage= getResources().getText(R.string.say_hi)+"\n"+firstName[0];
            sayHiTV.setText(hi_messssage);
        }
    }

    void logWithAnotherAccount(){
        currentUser=null;
        mAuth=null;
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
}
