package com.trinitysmf.mysmf.ui.activities;

import android.content.Intent;
import androidx.core.app.NavUtils;
import androidx.core.app.TaskStackBuilder;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.trinitysmf.mysmf.R;
import com.trinitysmf.mysmf.models.MeetingHolder;
import com.trinitysmf.mysmf.models.WeeklyMeeting;
import com.trinitysmf.mysmf.utils.MenuUtils;

public class MeetingEditActivity extends AppCompatActivity {

    private RecyclerView r;
    private DatabaseReference ref;
    private String sector;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuUtils.tintAllIcons(menu, 0xFFFFFFFF);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_share_add:
                startActivity(new Intent(this, MeetingAddActivity.class).putExtra("sector",sector));
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sector);
        androidx.appcompat.widget.Toolbar t = findViewById(R.id.toolbar);
        setSupportActionBar(t);

        t.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Edit Meetings");
        r = findViewById(R.id.sector_recycler);
        r.setLayoutManager(new LinearLayoutManager(this));

        ref = FirebaseDatabase.getInstance()
                .getReference();


        sector = getIntent().getStringExtra("sector");
        if(sector!=null) {
            Query query = ref
                    .child("sectors")
                    .child(sector)
                    .child("weekly_meetings");
            FirebaseRecyclerOptions<WeeklyMeeting> options =
                    new FirebaseRecyclerOptions.Builder<WeeklyMeeting>()
                            .setQuery(query, WeeklyMeeting.class)
                            .build();

            FirebaseRecyclerAdapter<WeeklyMeeting, MeetingHolder> adapter = new FirebaseRecyclerAdapter<WeeklyMeeting,MeetingHolder>(options) {
                @Override
                protected void onBindViewHolder(MeetingHolder holder, final int position, WeeklyMeeting model) {
                    holder.meetingName.setText( model.name);
                    holder.meetingDay.setText(model.day);
                    holder.meetingTime.setText(model.time);
                    holder.meetingVenue.setText(model.venue);
                    holder.meetingDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getRef(position).removeValue();
                        }
                    });
                }


                @Override
                public MeetingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_meeting, parent, false);
                    return new MeetingHolder(view);
                }
            };
            r.setAdapter(adapter);
            adapter.startListening();
        }
    }


}
