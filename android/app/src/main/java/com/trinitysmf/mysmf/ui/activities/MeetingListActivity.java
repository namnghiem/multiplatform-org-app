package com.trinitysmf.mysmf.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.core.app.NavUtils;
import androidx.core.app.TaskStackBuilder;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
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

/**
 * Created by namxn_000 on 17/11/2017.
 */

public class MeetingListActivity extends AppCompatActivity{
    private DatabaseReference ref;
    RecyclerView r;
    String sector;


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuUtils.tintAllIcons(menu, 0xFFFFFFFF);
        return super.onPrepareOptionsMenu(menu);
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
                Intent upIntent = NavUtils.getParentActivityIntent(MeetingListActivity.this);
                if (NavUtils.shouldUpRecreateTask(MeetingListActivity.this, upIntent)) {
                    // This activity is NOT part of this app's task, so create a new task
                    // when navigating up, with a synthesized back stack.
                    TaskStackBuilder.create(MeetingListActivity.this)
                            // Add all of this activity's parents to the back stack
                            .addNextIntentWithParentStack(upIntent)
                            // Navigate up to the closest parent
                            .startActivities();
                } else {
                    // This activity is part of this app's task, so simply
                    // navigate up to the logical parent activity.
                    upIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    NavUtils.navigateUpTo(MeetingListActivity.this, upIntent);
                }
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Meetings");
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
                }


                @Override
                public MeetingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_meeting, parent, false);
                    view.findViewById(R.id.item_meeting_delete).setVisibility(View.GONE);
                    return new MeetingHolder(view);
                }
            };
            r.setAdapter(adapter);
            adapter.startListening();
        }
    }



}
