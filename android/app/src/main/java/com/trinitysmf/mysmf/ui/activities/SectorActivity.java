package com.trinitysmf.mysmf.ui.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

import androidx.core.app.NavUtils;
import androidx.core.app.TaskStackBuilder;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.trinitysmf.mysmf.R;
import com.trinitysmf.mysmf.models.User;
import com.trinitysmf.mysmf.utils.MenuUtils;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SectorActivity extends AppCompatActivity {
    private RecyclerView mRecycler;
    private DatabaseReference myRef;

    FirebaseRecyclerAdapter adapter;
    private Menu mMenu;
    private String sector;
    private boolean isManager;

    @Override
    protected void onStop() {
        super.onStop();
        if(adapter!=null){
            adapter.stopListening();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(adapter!=null){
            adapter.startListening();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        this.mMenu = menu;
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_sector_edit:
                Intent i = new Intent(this, SectorEditActivity.class);
                i.putExtra("sector", sector);
                startActivity(i);
                return true;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sector);
        androidx.appcompat.widget.Toolbar t = findViewById(R.id.toolbar);
        setSupportActionBar(t);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        t.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent upIntent = NavUtils.getParentActivityIntent(SectorActivity.this);
                if (NavUtils.shouldUpRecreateTask(SectorActivity.this, upIntent)) {
                    // This activity is NOT part of this app's task, so create a new task
                    // when navigating up, with a synthesized back stack.
                    TaskStackBuilder.create(SectorActivity.this)
                            // Add all of this activity's parents to the back stack
                            .addNextIntentWithParentStack(upIntent)
                            // Navigate up to the closest parent
                            .startActivities();
                } else {
                    // This activity is part of this app's task, so simply
                    // navigate up to the logical parent activity.
                    upIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    NavUtils.navigateUpTo(SectorActivity.this, upIntent);
                }
            }
        });
        setTitle("SMF Directory");
        //attach to recycler
        mRecycler = findViewById(R.id.sector_recycler);
        mRecycler.setAdapter(new RecyclerView.Adapter() {
                                 @Override
                                 public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                                     return null;
                                 }

                                 @Override
                                 public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

                                 }

                                 @Override
                                 public int getItemCount() {
                                     return 0;
                                 }
                             });
                mRecycler.setLayoutManager(new LinearLayoutManager(SectorActivity.this));


        //get the user
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        //get user info
        if(firebaseUser.getUid() != null) {
            myRef.child("users").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final User user = dataSnapshot.getValue(User.class);
                    //store sector
                    sector = user.sector;
                    //make another query for sector details
                    myRef.child("sectors").child(user.sector).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(final DataSnapshot dataSnapshot) {
                            //check if the current user is actually the sector manager
                            isManager = firebaseUser.getUid().equals(dataSnapshot.child("sector_manager").getValue().toString());

                            if (isManager){

                                //show edit option if sector manager

                                MenuInflater inflater = getMenuInflater();
                                inflater.inflate(R.menu.menu_sector, mMenu);

                                MenuUtils.tintAllIcons(mMenu, 0xFFFFFFFF);

                            }else {
                                //if not, get the sector manager
                                    /*myRef.child("users").child(dataSnapshot.child("sector_manager").getValue().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            User manager = dataSnapshot.getValue(User.class);

                                        }
                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });*/
                            }

                            //get other users belonging to the same sector

                            Query query = FirebaseDatabase.getInstance()
                                    .getReference()
                                    .child("users")
                                    .orderByChild("sector")
                                    .equalTo(user.sector);
                            FirebaseRecyclerOptions<User> options =
                                    new FirebaseRecyclerOptions.Builder<User>()
                                            .setQuery(query, User.class)
                                            .build();


                            //setup adapter
                            adapter = new FirebaseRecyclerAdapter<User, UserHolder>(options) {

                                @Override
                                public UserHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                                    // Create a new instance of the ViewHolder, in this case we are using a custom
                                    // layout called R.layout.message for each item
                                    View view = LayoutInflater.from(parent.getContext())
                                            .inflate(R.layout.card_user, parent, false);

                                    return new UserHolder(view);
                                }

                                @Override
                                protected void onBindViewHolder(UserHolder holder, final int position, final User model) {
                                    holder.userName.setText(model.name);
                                    holder.userContact.setText(model.username);
                                    holder.userPosition.setText(model.position);
                                    if(model.pictureUrl!=null && !model.pictureUrl.equals("")){
                                        Glide.with(holder.userPicture.getContext()).load(model.pictureUrl).into(holder.userPicture);
                                    }

                                    holder.buttonContact.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                                    "mailto", model.username, null));
                                            startActivity(Intent.createChooser(emailIntent, "Send email..."));
                                        }
                                    });

                                    //if is manager, allow edit titles
                                    if(isManager) {
                                        holder.buttonEdit.setVisibility(View.VISIBLE);
                                        holder.buttonEdit.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                AlertDialog.Builder builder = new AlertDialog.Builder(SectorActivity.this);
                                                builder.setTitle("Edit title");
                                                builder.setView(R.layout.dialog_edit_title);/*
                                        //In case it gives you an error for setView(View) try
                                        builder.setView(inflater.inflate(R.layout.custom_view, null));*/

                                                builder.setPositiveButton("Set Title", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                        EditText title = ((AlertDialog) dialog).findViewById(R.id.edit_member_title);

                                                        if(!title.getText().toString().toLowerCase().equals("ceo")) {
                                                            Map<String, Object> update = new HashMap<>();
                                                            update.put("position", title.getText().toString());

                                                            getRef(position).updateChildren(update);
                                                        }else{
                                                            Toast.makeText(SectorActivity.this, "Action failed", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.cancel();
                                                    }
                                                });
                                                builder.show();
                                            }
                                        });
                                    }
                                }
                            };

                            adapter.startListening();
                            mRecycler.setAdapter(adapter);

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }
    private class UserHolder extends RecyclerView.ViewHolder {
        ImageButton buttonEdit;
        TextView userName;
        TextView userContact;
        TextView userPosition;
        ImageButton buttonContact;
        CircleImageView userPicture;
        public UserHolder(View itemView) {
            super(itemView);
            userName  = itemView.findViewById(R.id.user_name);
            userContact = itemView.findViewById(R.id.user_email);
            userPosition = itemView.findViewById(R.id.user_position);
            buttonContact = itemView.findViewById(R.id.user_button_contact);
            userPicture = itemView.findViewById(R.id.user_picture);
            buttonEdit = itemView.findViewById(R.id.user_edit);
        }
    }
}

