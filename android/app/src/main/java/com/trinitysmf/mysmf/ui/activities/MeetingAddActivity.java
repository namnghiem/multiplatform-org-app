package com.trinitysmf.mysmf.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.codetroopers.betterpickers.radialtimepicker.RadialTimePickerDialogFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.trinitysmf.mysmf.R;
import com.trinitysmf.mysmf.models.WeeklyMeeting;
import com.trinitysmf.mysmf.utils.MenuUtils;

public class MeetingAddActivity extends AppCompatActivity implements RadialTimePickerDialogFragment.OnTimeSetListener {

    private static final String FRAG_TAG_TIME_PICKER = "picker";
    private String sector;

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuUtils.tintAllIcons(menu, 0xFFFFFF);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_add);
        androidx.appcompat.widget.Toolbar t = findViewById(R.id.toolbar);
        setSupportActionBar(t);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Spinner spinner = (Spinner) findViewById(R.id.meeting_add_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.weekdays_array, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);


        sector = getIntent().getStringExtra("sector");

        //set up the time selector
        findViewById(R.id.meeting_add_time).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadialTimePickerDialogFragment rtpd = new RadialTimePickerDialogFragment()
                        .setOnTimeSetListener(MeetingAddActivity.this)
                        .setStartTime(10, 10)
                        .setDoneText("Set Time")
                        .setCancelText("Cancel")
                        .setThemeDark();
                rtpd.show(getSupportFragmentManager(), FRAG_TAG_TIME_PICKER);
            }
        });

        findViewById(R.id.meeting_add_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sector!=null) {
                    WeeklyMeeting meeting = new WeeklyMeeting();
                    meeting.day = ((Spinner) findViewById(R.id.meeting_add_spinner)).getSelectedItem().toString();
                    meeting.time = ((EditText) findViewById(R.id.meeting_add_time)).getText().toString();
                    meeting.venue = ((EditText) findViewById(R.id.meeting_add_venue)).getText().toString();
                    meeting.name = ((EditText) findViewById(R.id.meeting_add_name)).getText().toString();
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                    ref.child("sectors")
                            .child(sector)
                            .child("weekly_meetings")
                            .push()
                            .setValue(meeting)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            finish();
                        }
                    });
                }
        }});
    }


    @Override
    public void onTimeSet(RadialTimePickerDialogFragment dialog, int hourOfDay, int minute) {
        EditText e = findViewById(R.id.meeting_add_time);
        //if the number has one digit, we must add a 0
        if(hourOfDay<10){
            e.setText(getString(R.string.time_string_below10, hourOfDay, minute));

        }else {
            e.setText(getString(R.string.time_string, hourOfDay, minute));

        }
    }
}
