package com.trinitysmf.mysmf.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import android.widget.ArrayAdapter;

import com.trinitysmf.mysmf.models.User;

/**
 * Created by namnghiem on 11/01/2018.
 */

public class UserAdapter extends ArrayAdapter<User> {
    public UserAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }
}
