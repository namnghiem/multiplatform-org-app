package com.trinitysmf.mysmf;

import android.app.Application;

import cat.ereza.customactivityoncrash.config.CaocConfig;

/**
 * Created by namxn_000 on 17/11/2017.
 */

public class SmfApplication extends Application {
    // Called when the application is starting, before any other application objects have been created.
    // Overriding this method is totally optional!
    @Override
    public void onCreate() {
        super.onCreate();
        // Required initialization logic here!/*
        CaocConfig.Builder.create()
                .backgroundMode(CaocConfig.BACKGROUND_MODE_SILENT)
                .errorDrawable(R.drawable.ic_network_check_black_24dp)
                .apply();
    }

}
