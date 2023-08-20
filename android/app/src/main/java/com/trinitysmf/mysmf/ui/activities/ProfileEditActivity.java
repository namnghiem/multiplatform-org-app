package com.trinitysmf.mysmf.ui.activities;

import android.content.Intent;
import androidx.annotation.NonNull;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.trinitysmf.mysmf.R;
import com.trinitysmf.mysmf.models.User;

import java.util.HashMap;
import java.util.Map;

public class ProfileEditActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private User currentUser;
    private FirebaseUser currentFirebaseUser;
    private boolean isNewUser = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);
        Toolbar t = findViewById(R.id.toolbar);
        setSupportActionBar(t);

        final EditText courseEdit = findViewById(R.id.edit_course);
        final EditText yearEdit = findViewById(R.id.edit_gradyear);
        //boolean codePrompt = getIntent().getBooleanExtra("code_prompt", false);
        //if ask for code, show it
        //findViewById(R.id.edit_code).setVisibility(View.VISIBLE);

        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        findViewById(R.id.edit_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!courseEdit.getText().toString().equals("") && !yearEdit.getText().toString().equals("")){
                final FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
                if (fbUser != null) {
                    User user = new User(
                            fbUser.getDisplayName(),
                            fbUser.getEmail(),
                            ((EditText) findViewById(R.id.edit_gradyear)).getText().toString(),
                            ((EditText) findViewById(R.id.edit_course)).getText().toString(),
                            "User",
                            "0",
                            null,
                            ""
                    );
                    mDatabase.child("users").child(fbUser.getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                                                    /*//also add user to the sector itself - not necessary
                                                    mDatabase.child("sectors").child(sectorKey).child("members").child(fbUser.getUid()).setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            // all done

                                                        }
                                                    });*/
                            startActivity(new Intent(ProfileEditActivity.this, SplashActivity.class));
                            finish();
                        }
                    });
                }else{
                    courseEdit.setError("Please enter your course details");
                    yearEdit.setError("Please enter your graduation year");
                }
        }}});

        //get current user details if it exists. If it does, update new fields only.
        //if it doesnt, push the entire object
        /*
        mDatabase.child("users").child(currentFirebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                isNewUser = dataSnapshot.getValue(User.class) == null;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
*/
            //
/*        findViewById(R.id.edit_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNewUser) {*/

           /* mDatabase.child("sectors")
                    .orderByChild("code")
                    .equalTo(((EditText) findViewById(R.id.edit_code)).getText().toString())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                final String sectorKey = dataSnapshot.getChildren().iterator().next().getKey();



                            } else {
                                TextInputLayout til = (TextInputLayout) findViewById(R.id.edit_code_layout);
                                til.setError("The code was not found. Please try again.");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            //TODO: error message
                        }
                    });*/
                    /*//get sector based on code
                    mDatabase.child("codes").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            String code = ((EditText) findViewById(R.id.edit_code)).getText().toString();
                            //if exists, finish creating account
                            if(dataSnapshot.child(code).exists() && !code.equals("")){
                                final String sectorName = dataSnapshot.child(code).getValue().toString();
                                final FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
                                if (fbUser != null) {
                                    User user = new User(
                                            fbUser.getDisplayName(),
                                            fbUser.getEmail(),
                                            ((EditText) findViewById(R.id.edit_gradyear)).getText().toString(),
                                            ((EditText) findViewById(R.id.edit_course)).getText().toString(),
                                            "Analyst",
                                            "0",
                                            sectorName,
                                            ""
                                            );
                                    mDatabase.child("users").child(fbUser.getUid()).setValue(user);*//*.addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            //also add user to the sector itself
                                            mDatabase.child("sectors").child(sectorName).child("members").child(fbUser.getUid()).setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    // all done
                                                    startActivity(new Intent(ProfileEditActivity.this, SplashActivity.class));
                                                    finish();
                                                }
                                            });

                                        }
                                    });*//*
                                }


                            }else {
                                TextInputLayout til = (TextInputLayout) findViewById(R.id.edit_code_layout);
                                til.setError("The code was not found. Please try again.");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });*/

         /*       }else{
                    //just update the two values
                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put("/users/" + currentFirebaseUser.getUid() + "/gradYear",((EditText) findViewById(R.id.edit_gradyear)).getText().toString());
                    childUpdates.put("/users/" + currentFirebaseUser.getUid() + "/course",((EditText) findViewById(R.id.edit_course)).getText().toString());
                    mDatabase.updateChildren(childUpdates);

                }*/


    }
}
