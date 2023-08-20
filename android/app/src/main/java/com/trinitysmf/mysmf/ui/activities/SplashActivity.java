package com.trinitysmf.mysmf.ui.activities;

import android.content.Intent;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.trinitysmf.mysmf.R;

public class SplashActivity extends AppCompatActivity {
    //this activity both acts a a splash screen and determines if the user is logged in or not
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Check login
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null || PreferenceManager.getDefaultSharedPreferences(this).getBoolean("login_skip", false)) {
            // already signed in
            startActivity(new Intent(this, MainActivity.class));
        } else {
            // not signed in
            startActivity(new Intent(this, LoginActivity.class));
        }
        finish();
    }
}
