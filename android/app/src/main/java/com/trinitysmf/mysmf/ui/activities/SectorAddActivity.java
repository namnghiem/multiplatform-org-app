package com.trinitysmf.mysmf.ui.activities;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.trinitysmf.mysmf.R;
import com.trinitysmf.mysmf.models.Sector;
import com.trinitysmf.mysmf.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by namnghiem on 11/01/2018.
 */

public class SectorAddActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private String code;
    private ArrayAdapter<User> adapter;
    int position;
    private ArrayList<String> idList = new ArrayList();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sector_add);
        Toolbar t = findViewById(R.id.toolbar);
        setSupportActionBar(t);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        final Spinner spinner = findViewById(R.id.edit_sector_spinner);

        mDatabase.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<User> list = new ArrayList<>();

                for( DataSnapshot userSnapshot : dataSnapshot.getChildren()){
                    list.add(userSnapshot.getValue(User.class));
                    idList.add(userSnapshot.getKey());
                }
                adapter = new ArrayAdapter<>(SectorAddActivity.this, android.R.layout.simple_spinner_dropdown_item, list);
                spinner.setAdapter(adapter);

                //find unique code
                generateCodeAndContinue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TODO: warn
                finish();
            }

        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long id) {
                position = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //TODO: test this case
            }
        });

        findViewById(R.id.edit_sector_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Sector s = new Sector();
                s.name = ((TextView) findViewById(R.id.edit_sector_name)).getText().toString();
                s.sector_manager = idList.get(position);
                s.code = code;
                mDatabase.child("sectors")
                        .push()
                        .setValue(s);

                Map<String, Object> updateMap = new HashMap<>();
                updateMap.put("position", "Sector Manager");
                //also set position to sector manager
                mDatabase.child("users")
                        .child(idList.get(position))
                        .updateChildren(updateMap)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                finish();
                            }
                        });
            }
        });

    }

    private void generateCodeAndContinue() {
        code = String.valueOf((int)(Math.random()*9000)+1000);

        mDatabase.child("sectors")
                .orderByChild("code")
                .equalTo(code)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){

                            //try again
                            generateCodeAndContinue();
                        }else{

                            //show UI
                            findViewById(R.id.add_sector_content).setVisibility(View.VISIBLE);
                            findViewById(R.id.add_sector_progress).setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //TODO: warn
                        finish();
                    }
                });

    }
}
