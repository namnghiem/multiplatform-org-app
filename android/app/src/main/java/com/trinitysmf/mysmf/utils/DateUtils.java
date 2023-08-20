package com.trinitysmf.mysmf.utils;

import com.google.firebase.database.DataSnapshot;
import com.trinitysmf.mysmf.models.Meeting;

import org.threeten.bp.DateTimeUtils;
import org.threeten.bp.DayOfWeek;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.format.DateTimeParseException;
import org.threeten.bp.temporal.TemporalAdjusters;

import java.util.Date;
import java.util.Iterator;

/**
 * Created by namxn_000 on 27/10/2017.
 */

public class DateUtils {
    public static Meeting getClosestMeeting(DataSnapshot sector){
        try {
            Meeting closestMeeting = null;
            long shortestTime = 0;
            //make an arraylist with all the meeting dates, closest to today
            for (DataSnapshot meeting : sector.child("weekly_meetings").getChildren()) {

                //get the date of the next day with that weekday
                //TODO: bug when it is the same day
                LocalDate nextDate = LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.valueOf(meeting.child("day").getValue().toString().toUpperCase())));
                LocalTime time = LocalTime.parse(meeting.child("time").getValue().toString());

                // the conversion based on your system timezone

                Instant instant = nextDate.atTime(time).atZone(ZoneId.systemDefault()).toInstant();
                Date d = DateTimeUtils.toDate(instant);
                long timeFromNow = d.getTime() - System.currentTimeMillis();
                if (timeFromNow > 0) {
                    //if its the first one, store the value
                    if (shortestTime == 0) {
                        //get time to now
                        shortestTime = timeFromNow;
                        closestMeeting = new Meeting(meeting.child("name").getValue().toString(), meeting.child("venue").getValue().toString(), d);
                    } else if (timeFromNow < shortestTime) {
                        shortestTime = timeFromNow;
                        closestMeeting = new Meeting(meeting.child("name").getValue().toString(), meeting.child("venue").getValue().toString(), d);
                    }
                }
            }
            return closestMeeting;
        }catch (IllegalArgumentException e){
            e.printStackTrace();
            return null;
        }catch (DateTimeParseException e){
            e.printStackTrace();
            return null;
        }
    }
}
