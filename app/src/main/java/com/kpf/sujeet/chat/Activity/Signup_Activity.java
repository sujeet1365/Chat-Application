package com.kpf.sujeet.chat.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.kpf.sujeet.chat.Models.User;
import com.kpf.sujeet.chat.R;

public class Signup_Activity extends AppCompatActivity implements View.OnClickListener
{
    EditText edt_signup_name;
    EditText edt_signup_phone;
    EditText edt_signup_country;
    EditText edt_signup_email;
    EditText edt_signup_password;
    EditText edt_signup_cnfrm_password;
    Button btn_submit;
    ProgressDialog progressDialog;
    private FirebaseAuth fbauth;
    private FirebaseAuth.AuthStateListener fbauthlistener;
   // ProgressDialog progressDialog;

    String name,email,password,cnfrm_password,phone,country;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_);

        fbauth = FirebaseAuth.getInstance();

        edt_signup_name = (EditText)findViewById(R.id.edt_signup_name);
        edt_signup_email = (EditText)findViewById(R.id.edt_signup_email);
        edt_signup_country = (EditText)findViewById(R.id.edt_signup_country);
        edt_signup_phone = (EditText)findViewById(R.id.edt_signup_contact);
        edt_signup_password = (EditText)findViewById(R.id.edt_signup_password);
        edt_signup_cnfrm_password = (EditText)findViewById(R.id.edt_signup_cnfirm_password);
        btn_submit = (Button)findViewById(R.id.btn_submit);

        btn_submit.setOnClickListener(this);

        fbauthlistener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d("", "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };


    }

    @Override
    protected void onStart() {
        super.onStart();
        fbauth.addAuthStateListener(fbauthlistener);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        if(fbauthlistener !=null)
        {
            fbauth.removeAuthStateListener(fbauthlistener);
        }
    }

    @Override
    public void onClick(View view)
    {
        name = edt_signup_name.getText().toString().trim();
        phone = edt_signup_phone.getText().toString().trim();
        country = edt_signup_country.getText().toString().trim();
        email = edt_signup_email.getText().toString().trim();
        password = edt_signup_password.getText().toString().trim();
        cnfrm_password = edt_signup_cnfrm_password.getText().toString().trim();
        switch (view.getId())
        {
            case R.id.btn_submit:
                if(name.equals(""))
                    edt_signup_name.setError("Empty Field");
                if(phone.equals(""))
                    edt_signup_phone.setError("Empty Field");
                if(country.equals(""))
                    edt_signup_country.setError("Empty Field");
                else if(email.equals(""))
                    edt_signup_email.setError("Empty Field");
                else if(edt_signup_password.equals(""))
                    edt_signup_password.setError("Empty Field");
                else if(cnfrm_password.equals(""))
                    edt_signup_cnfrm_password.setError("Empty Field");
                else if(! password.equals(cnfrm_password))
                    edt_signup_cnfrm_password.setError("No match found");
                else {
                    createUser(email,password);
                    ProgressDialog progressDialog = new ProgressDialog(Signup_Activity.this);
                    progressDialog.setMessage("Registering......");
                    progressDialog.show();
                }

                break;
        }
    }

    private void createUser(final String email, String password)
    {
        fbauth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if(task.isSuccessful())
                {
//                    Toast.makeText(Signup_Activity.this, "Signup Successful", Toast.LENGTH_SHORT).show();
                    saveUserDataToDatabase(task.getResult().getUser(),name,phone,country,email);
                }
                else
                {
                    progressDialog.dismiss();
                    Toast.makeText(Signup_Activity.this, "Signup Unsuccessful", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void saveUserDataToDatabase(FirebaseUser firebaseUser, String name, final String phone, final String country, final String email)
    {
        String uid = firebaseUser.getUid();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = firebaseDatabase.getReference().child("users").child(uid);

        databaseReference.child("name").setValue(name).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    databaseReference.child("contact").setValue(phone);
                    databaseReference.child("country").setValue(country);
                    databaseReference.child("email").setValue(email);
                    Toast.makeText(Signup_Activity.this,"Successfully Registered", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Signup_Activity.this,Login_Activity.class));
                    finish();
                }
                else {
                    Toast.makeText(Signup_Activity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

//        User user = new User(name,email);
//
//        databaseReference.child(name).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task)
//            {
//                if(task.isSuccessful())
//                    Toast.makeText(Signup_Activity.this, "Data Inserted", Toast.LENGTH_SHORT).show();
//                else
//                    Toast.makeText(Signup_Activity.this, "Data Failed", Toast.LENGTH_SHORT).show();
//
//            }
//        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
