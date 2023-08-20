package com.trinitysmf.mysmf.ui.activities;

import android.content.Intent;
import androidx.core.app.NavUtils;
import androidx.core.app.TaskStackBuilder;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.trinitysmf.mysmf.R;
import com.trinitysmf.mysmf.models.User;

public class SectorEditActivity extends AppCompatActivity {

    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sector_edit);
        Toolbar t = findViewById(R.id.toolbar);
        setSupportActionBar(t);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final String sector = getIntent().getStringExtra("sector");

        findViewById(R.id.sector_add_member).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new IntentIntegrator(SectorEditActivity.this).initiateScan(); // `this` is the current Activity
            }
        });

        t.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               onBackPressed();
            }
        });
        findViewById(R.id.sector_edit_stocks).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sector!=null) {
                    startActivity(new Intent(SectorEditActivity.this, StockEditActivity.class).putExtra("sector", sector));
                }
            }
        });


        findViewById(R.id.sector_edit_meetings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sector!=null) {
                    startActivity(new Intent(SectorEditActivity.this, MeetingEditActivity.class).putExtra("sector", sector));
                }
            }
        });

        findViewById(R.id.sector_edit_sectors).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    startActivity(new Intent(SectorEditActivity.this, SectorListActivity.class));
            }
        });

        //get user info
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        if(firebaseUser.getUid() != null) {
            myRef.child("users").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    if(user.position.equals("CEO")){
                        findViewById(R.id.sector_edit_sectors).setVisibility(View.VISIBLE);

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    //TOOD:
                }
            });
        }



    }
    // Get the results:
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
