package com.trinitysmf.mysmf.ui.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

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
import com.trinitysmf.mysmf.models.Post;
import com.trinitysmf.mysmf.models.User;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by namxn_000 on 25/12/2017.
 */

public class FeedFragment extends Fragment {
    private View v;
    private RecyclerView mRecycler;
    private DatabaseReference myRef;
    private FirebaseRecyclerAdapter<Post, PostHolder> adapter;
    private User user;
    private LinearLayoutManager mLayoutManager;

    public FeedFragment(){};
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(adapter!=null){
            adapter.stopListening();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if(adapter!=null) adapter.startListening();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_feed, container, false);
        mRecycler = v.findViewById(R.id.articles_recycler);
        //temporarily attach to empty adapter
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
        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mLayoutManager);


        //to get sector

        //get the user
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        //get user info
        if (user != null) {


            if (firebaseUser.getUid() != null && user.sector != null) {
                myRef.child("users").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {

                        user = dataSnapshot.getValue(User.class);

                        if (user != null) {
                            //get posts
                            Query query = FirebaseDatabase.getInstance()
                                    .getReference()
                                    .child("sectors")
                                    .child(user.sector)
                                    .child("posts");

                            FirebaseRecyclerOptions<Post> options =
                                    new FirebaseRecyclerOptions.Builder<Post>()
                                            .setQuery(query, Post.class)
                                            .build();


                            adapter = new FirebaseRecyclerAdapter<Post, PostHolder>(options) {
                                @Override
                                protected void onBindViewHolder(PostHolder holder, final int position, Post model) {
                                    holder.mainText.setText(model.main);
                                    holder.titleText.setText(model.title);
                                    holder.timeText.setText(model.time);
                                    holder.authorText.setText(model.authorName);
                                    if (model.authorUrl != null && !model.authorUrl.equals("")) {
                                        Glide.with(holder.circleImageView)
                                                .load(model.authorUrl)
                                                .into(holder.circleImageView);
                                    } else {
                                        Glide.with(holder.circleImageView).clear(holder.circleImageView);
                                    }
                                    if (model.authorId.equals(firebaseUser.getUid())) {
                                        holder.moreButton.setVisibility(View.VISIBLE);
                                        holder.moreButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                PopupMenu popup = new PopupMenu(getActivity(), view);
                                                MenuInflater inflater = popup.getMenuInflater();
                                                inflater.inflate(R.menu.menu_feed, popup.getMenu());
                                                popup.show();
                                                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                                    @Override
                                                    public boolean onMenuItemClick(MenuItem menuItem) {
                                                        adapter.getRef(position).removeValue();

                                                        return true;
                                                    }
                                                });

                                            }
                                        });
                                    }

                                }

                                @Override
                                public void onDataChanged() {
                                    v.findViewById(R.id.feed_progress).setVisibility(View.INVISIBLE);
                                }

                                @Override
                                public PostHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                                    View itemView = LayoutInflater.from(getActivity()).inflate(R.layout.item_feed, parent, false);
                                    ((TextView) itemView.findViewById(R.id.main_text)).setMovementMethod(android.text.method.LinkMovementMethod.getInstance());

                                    return new PostHolder(itemView);
                                }
                            };
                            adapter.startListening();

                            mRecycler.setAdapter(adapter);
                        } else {
                            //should open activity to finish registration
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //TODO: finish
                    }
                });
            }
        }

        //action button
        v.findViewById(R.id.feed_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_add_feed, null);
                ((TextView) dialogView.findViewById(R.id.author_text)).setText(user.name);
                builder.setView(dialogView);

                String time = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date());
                ((TextView) dialogView.findViewById(R.id.time_text)).setText(time);

                Glide.with(getActivity())
                        .load(user.pictureUrl)
                        .into((ImageView) dialogView.findViewById(R.id.item_feed_profile));
                builder.setPositiveButton("Post", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText title = ((AlertDialog) dialog).findViewById(R.id.edit_title_text);
                        EditText text = ((AlertDialog) dialog).findViewById(R.id.edit_main_text);

                        Map<String, Object> update = new HashMap<>();
                        update.put("position", title.getText().toString());

                        Post post = new Post();
                        post.authorId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        post.authorName = user.name;
                        post.title = title.getText().toString();
                        if(user.pictureUrl != null && !user.pictureUrl.equals("")) {
                            post.authorUrl = user.pictureUrl;
                        }
                        post.main = text.getText().toString();
                        post.time = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date());

                        //getRef(position).updateChildren(update);
                        myRef.child("sectors")
                                .child(user.sector)
                                .child("posts")
                                .push()
                                .setValue(post);
                    }
                });
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog a = builder.create();
                a.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                a.show();
            }
        });

        return v;
    }

    private class PostHolder extends RecyclerView.ViewHolder{
        TextView titleText;
        TextView mainText;
        TextView authorText;
        TextView timeText;
        CircleImageView circleImageView;
        ImageButton moreButton;

        public PostHolder(View itemView) {
            super(itemView);
            this.timeText = itemView.findViewById(R.id.time_text);
            this.titleText = itemView.findViewById(R.id.title_text);
            this.mainText = itemView.findViewById(R.id.main_text);
            this.authorText = itemView.findViewById(R.id.author_text);
            this.circleImageView = itemView.findViewById(R.id.item_feed_profile);
            this.moreButton = itemView.findViewById(R.id.iv_more);
        }
    }
}
