package com.trinitysmf.mysmf.models;

import java.util.Date;

/**
 * Created by namxn_000 on 27/10/2017.
 */

public class Meeting {
    public Date date;
    public String venue;
    public String name;

    public Meeting(String name, String venue, Date date){
        this.date = date;
        this.venue = venue;
        this.name = name;
    }
}
