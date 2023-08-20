package com.trinitysmf.mysmf.models;

/**
 * Created by namxn_000 on 21/10/2017.
 */

public class User {
    public String username;
    public String gradYear;
    public String course;
    public String position;
    public String rankcode;
    public String sector;
    public String name;
    public String pictureUrl;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }


    public User(String name, String username, String gradYear, String course, String position, String rankcode, String sector, String pictureUrl) {
        this.name = name;
        this.username = username;
        this.gradYear = gradYear;
        this.course = course;
        this.position = position;
        this.rankcode = rankcode;
        this.sector = sector;
        this.pictureUrl = pictureUrl;
    }

    @Override
    public String toString() {
        return name;
    }
}
