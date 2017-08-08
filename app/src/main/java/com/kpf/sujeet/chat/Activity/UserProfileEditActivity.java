package com.kpf.sujeet.chat.Activity;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kpf.sujeet.chat.R;

import java.util.Iterator;

public class UserProfileEditActivity extends AppCompatActivity implements View.OnClickListener {
    EditText edt_profile_name;
    EditText edt_profile_contact;
    EditText edt_profile_country;
    EditText edt_profile_email;
    Button btn_profile_update;
    ProgressDialog progressDialog;
    private FirebaseAuth fbauth;
    private FirebaseAuth.AuthStateListener fbauthlistener;
    String name, email, phone, country;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_edit);

        fbauth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        edt_profile_name = (EditText) findViewById(R.id.edt_profile_name);
        edt_profile_email = (EditText) findViewById(R.id.edt_profile_email);
        edt_profile_country = (EditText) findViewById(R.id.edt_profile_country);
        edt_profile_contact = (EditText) findViewById(R.id.edt_profile_contact);
        btn_profile_update = (Button) findViewById(R.id.btn_profile_update);

        btn_profile_update.setOnClickListener(this);


    }

    @Override
    protected void onStart() {
        super.onStart();
        Query query = databaseReference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                Toast.makeText(UserProfileEditActivity.this, ""+dataSnapshot.getKey(), Toast.LENGTH_SHORT).show();
                Iterator iterator = dataSnapshot.getChildren().iterator();
                while (iterator.hasNext()) {
                    DataSnapshot dataSnapshot1 = (DataSnapshot) iterator.next();
                    if (dataSnapshot1.getKey().equals("name")) {
                        edt_profile_name.setText(dataSnapshot1.getValue().toString());
                    } else if (dataSnapshot1.getKey().equals("contact")) {
                        edt_profile_contact.setText(dataSnapshot1.getValue().toString());
                    } else if (dataSnapshot1.getKey().equals("country")) {
                        edt_profile_country.setText(dataSnapshot1.getValue().toString());
                    } else if (dataSnapshot1.getKey().equals("email")) {
                        edt_profile_email.setText(dataSnapshot1.getValue().toString());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        edt_profile_email.setEnabled(false);

    }

    @Override
    public void onClick(View view) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating");
        progressDialog.show();
        name = edt_profile_name.getText().toString().trim();
        phone = edt_profile_contact.getText().toString().trim();
        country = edt_profile_country.getText().toString().trim();
        switch (view.getId()) {
            case R.id.btn_profile_update:
                databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(fbauth.getCurrentUser().getUid());
                if (!name.equals("")) {
                    databaseReference.child("name").setValue(name);
                } if (!phone.equals("")) {
                    databaseReference.child("contact").setValue(phone);
                } if (!country.equals("")) {
                    databaseReference.child("country").setValue(country);
                }

                progressDialog.dismiss();
                Toast.makeText(this,R.string.update_completion_msg, Toast.LENGTH_SHORT).show();
                finish();
                break;
        }
    }
}
