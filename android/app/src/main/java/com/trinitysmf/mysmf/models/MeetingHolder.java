package com.trinitysmf.mysmf.models;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.trinitysmf.mysmf.R;

/**
 * Created by namxn_000 on 17/11/2017.
 */
public class MeetingHolder extends RecyclerView.ViewHolder {
    public TextView meetingName;
    public TextView meetingVenue;
    public TextView meetingDay;
    public TextView meetingTime;
    public ImageButton meetingDelete;

    public MeetingHolder(View itemView) {
        super(itemView);
        meetingName = itemView.findViewById(R.id.item_meeting_name);
        meetingVenue= itemView.findViewById(R.id.item_meeting_venue);
        meetingDay= itemView.findViewById(R.id.item_meeting_day);
        meetingDelete = itemView.findViewById(R.id.item_meeting_delete);
        meetingTime = itemView.findViewById(R.id.item_meeting_time);
    }
}
