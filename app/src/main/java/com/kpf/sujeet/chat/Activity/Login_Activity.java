package com.kpf.sujeet.chat.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.CallbackManager;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kpf.sujeet.chat.R;

import java.util.Arrays;
import java.util.Iterator;

public class Login_Activity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
    private static final int RC_SIGN_IN =9001 ;
    private static final int FB_SIGN_IN =64206 ;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseAuth fbauth;
    EditText edt_username;
    EditText edt_password;
    Button btn_login;
    Button btn_signup;
    TextView txt_btn_forgotpswd;
    Button btn_fb_login;
    SignInButton signInButton;
    GoogleSignInOptions gso;
    ProgressDialog progressDialog;
    private CallbackManager mCallbackManager;
    FirebaseAuth.AuthStateListener mAuthListener;
    static GoogleApiClient mGoogleApiClient;
    String login_user,login_password;
    static int f=0;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        mCallbackManager = CallbackManager.Factory.create();
        if (AccessToken.getCurrentAccessToken() != null){
            LoginManager.getInstance().logOut();
        }

        setContentView(R.layout.activity_login_);

        fbauth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                //dialog.dismiss();
                //user = firebaseAuth.getCurrentUser();
            }
        };

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        edt_username = (EditText)findViewById(R.id.edt_username);
        edt_password = (EditText)findViewById(R.id.edt_password);
        btn_login = (Button)findViewById(R.id.btn_login);
        btn_signup = (Button)findViewById(R.id.btn_signup);
        txt_btn_forgotpswd = (TextView)findViewById(R.id.txt_btn_forgotpswd);
        btn_fb_login = (Button) findViewById(R.id.btn_fb_login);
        signInButton = (SignInButton) findViewById(R.id.sign_in_button);
       // signInButton.setSize(SignInButton.SIZE_STANDARD);


        btn_login.setOnClickListener(this);
        btn_signup.setOnClickListener(this);
        txt_btn_forgotpswd.setOnClickListener(this);
        btn_fb_login.setOnClickListener(this);
        signInButton.setOnClickListener(this);





        //Google Signin Integration..............
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        //For shutting keyboard
        Login_Activity.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }

    @Override
    protected void onStart() {
        super.onStart();
        fbauth.addAuthStateListener(mAuthListener);
        progressDialog = new ProgressDialog(Login_Activity.this);
        progressDialog.setMessage("Processing......");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            fbauth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onClick(View view)
    {
        login_user = edt_username.getText().toString().trim();
        login_password = edt_password.getText().toString().trim();
        switch (view.getId())
        {
            case R.id.btn_login:
                progressDialog.show();
                if(login_user.equals(""))
                    edt_username.setError("Empty");
                else if(edt_password.equals(""))
                    edt_password.setError("Empty");
                else {
                fbauth.signInWithEmailAndPassword(login_user, login_password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d("", "signInWithEmail:onComplete:" + task.isSuccessful());

                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Log.w("", "signInWithEmail:failed", task.getException());
                                    Toast.makeText(Login_Activity.this, "Wrong Credential",
                                            Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
                                else {
                                    startActivity(new Intent(Login_Activity.this,HomeActivity.class));
                                    progressDialog.dismiss();
                                    finish();

                                }

                            }
                        });
            }
                break;
            case R.id.btn_signup:
                progressDialog.show();
                startActivity(new Intent(this,Signup_Activity.class));
                break;
            case R.id.txt_btn_forgotpswd:
                Toast.makeText(this, "forgot Password", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_fb_login:
                progressDialog.show();
                facebook_login();
                break;
            case R.id.sign_in_button:
                progressDialog.show();
                    signIn();
                break;
        }
    }



    public void facebook_login()
    {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends", "email"));
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                progressDialog.dismiss();
                Toast.makeText(Login_Activity.this, "User Cancel", Toast.LENGTH_LONG).show();
//                        rlayout_parent.setVisibility(View.VISIBLE);

            }

            @Override
            public void onError(FacebookException error) {
                progressDialog.dismiss();
                Log.d("error:", error.toString());
                Toast.makeText(Login_Activity.this, "Something Wrong", Toast.LENGTH_LONG).show();

            }
        });
    }

    // [START auth_with_facebook]
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("fbbbbb", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        fbauth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("fbbbbb", "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w("fbbbbb", "signInWithCredential", task.getException());
                            Toast.makeText(Login_Activity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                        else{
                            progressDialog.show();
                            final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                            databaseReference = firebaseDatabase.getReference().child("users").child(uid);
                                            databaseReference.child("name").setValue(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()) {
                                                        databaseReference.child("email").setValue(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                                                        Toast.makeText(Login_Activity.this,"Login Successful", Toast.LENGTH_SHORT).show();
                                                        startActivity(new Intent(Login_Activity.this,HomeActivity.class));
                                                        progressDialog.dismiss();
                                                        finish();
                                                    }
                                                    else {
                                                        progressDialog.dismiss();
                                                        Toast.makeText(Login_Activity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });

                        }


                    }
                });
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
        else if(requestCode==FB_SIGN_IN)
        {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    //Google Signin......................................

    private void signIn() {
        f=1;
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("Google", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            firebaseAuthWithGoogle(acct);

//            updateUI(true);
        } else {
            progressDialog.dismiss();
            // Signed out, show unauthenticated UI.
//            updateUI(false);
        }
    }

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("Google", "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        fbauth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        Log.d("Google", "signInWithCredential:onComplete:" + task.isSuccessful());
                        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                        final DatabaseReference databaseReference = firebaseDatabase.getReference().child("users").child(uid);



                        databaseReference.child("name").setValue(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()) {

                                    databaseReference.child("email").setValue(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                                    task.getResult();
                                    Toast.makeText(Login_Activity.this,"Login Successful", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(Login_Activity.this,HomeActivity.class));
                                    progressDialog.dismiss();
                                    finish();
                                }
                                else {
                                    progressDialog.dismiss();
                                    Toast.makeText(Login_Activity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w("Google", "signInWithCredential", task.getException());
                            Toast.makeText(Login_Activity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // [START_EXCLUDE]
//                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END auth_with_google]


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

}
