package com.trinitysmf.mysmf.ui.fragments;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.trinitysmf.mysmf.R;
import com.trinitysmf.mysmf.models.Event;
import com.trinitysmf.mysmf.models.Meeting;
import com.trinitysmf.mysmf.models.Share;
import com.trinitysmf.mysmf.models.ShareDetails;
import com.trinitysmf.mysmf.models.User;
import com.trinitysmf.mysmf.models.providers.AlphaVantageShareProvider;
import com.trinitysmf.mysmf.models.providers.WordpressEventsProvider;
import com.trinitysmf.mysmf.ui.MySingleton;
import com.trinitysmf.mysmf.ui.activities.CardActivity;
import com.trinitysmf.mysmf.ui.activities.MeetingListActivity;
import com.trinitysmf.mysmf.ui.activities.ProfileEditActivity;
import com.trinitysmf.mysmf.ui.activities.SectorActivity;
import com.trinitysmf.mysmf.utils.DateUtils;
import com.trinitysmf.mysmf.utils.DimensUtils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    /*// TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
*/
    private View mRootView;
    private LinearLayout mFeedView;
    private DatabaseReference myRef;
    private FirebaseUser firebaseUser;/*

    private FetchFirstEvent fetchFirstEvent;*/


    public HomeFragment() {
        // Required empty public constructor
        setRetainInstance(true);
    }

    @Override
    public void onStop() {
        super.onStop();/*
        fetchFirstEvent.cancel(false);*/
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();/*
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);*/
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {/*
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);*/

        }
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = null;
        if (connectivityManager != null) {
            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.fragment_home, container, false);
        mFeedView = mRootView.findViewById(R.id.linear_layout_feed);
        if(isNetworkAvailable()) {
            Glide.with(this)
                    .load("http://www.trinitysmf.com/wp-content/uploads/2018/05/landscapeSMF.jpg")
                    .into((ImageView) mRootView.findViewById(R.id.profile_header));
            // set up the login button
            mRootView.findViewById(R.id.button_login).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                /*startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(),
                        );*/
                }
            });

            //setup the events
            setUpView();

            //check login
            FirebaseAuth auth = FirebaseAuth.getInstance();

            if (auth.getCurrentUser() != null) {
                // already signed in
                firebaseUser = auth.getCurrentUser();
                setUp();
            } else {
                //hide the profile card
                mRootView.findViewById(R.id.profile_card).setVisibility(View.GONE);
                mRootView.findViewById(R.id.card_sector).setVisibility(View.GONE);
            }

            //setup the sector details button
            mRootView.findViewById(R.id.sector_button_more).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), SectorActivity.class));
                }
            });
        }else{
            mRootView.findViewById(R.id.home_error).setVisibility(View.VISIBLE);
            mRootView.findViewById(R.id.linear_layout_feed).setVisibility(View.GONE);
        }
        return mRootView;
    }

    private void setUp() {
        //remove the login nag
        mRootView.findViewById(R.id.home_login_nag).setVisibility(View.GONE);

        //get user info
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        if(firebaseUser.getUid() != null) {
            myRef.child("users").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final User user = dataSnapshot.getValue(User.class);

                    //set the name
                    if (user != null) {
                        setUpUser(user);
                    }else{
                        finishAccountSetup();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    databaseError.toException().printStackTrace();
                }
            });
        }
    }

    private void setUpUser(final User user){

        //open meeting when click upcoming
        mRootView.findViewById(R.id.profile_upcoming).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MeetingListActivity.class).putExtra("sector", user.sector));
            }
        });
        mRootView.findViewById(R.id.profile_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), CardActivity.class));
            }
        });
        ((TextView) mRootView.findViewById(R.id.profile_name)).setText(user.name);

        //set position
        ((TextView) mRootView.findViewById(R.id.profile_position)).setText(user.position);

        //load the image
        if(user.pictureUrl!=null && !user.pictureUrl.equals("")) {
            Glide.with(getActivity()).load(user.pictureUrl).into((ImageView) mRootView.findViewById(R.id.profile_image));
        }

        if(user.sector!= null && !user.equals("")){
            setUpAnalyst(user);

            mRootView.findViewById(R.id.profile_upcoming).setVisibility(View.VISIBLE);
            mRootView.findViewById(R.id.card_sector).setVisibility(View.VISIBLE);
        }else{
            mRootView.findViewById(R.id.profile_upcoming).setVisibility(View.GONE);
            mRootView.findViewById(R.id.card_sector).setVisibility(View.GONE);
        }


        //make the nag card invisible, make profile card visible
        mRootView.findViewById(R.id.profile_progress).setVisibility(View.INVISIBLE);
        mRootView.findViewById(R.id.profile_details).setVisibility(View.VISIBLE);
        mRootView.findViewById(R.id.profile_image).setVisibility(View.VISIBLE);

    }

    private void setUpAnalyst(User user){

        //make another query for sector details
        myRef.child("sectors").child(user.sector).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    ((TextView) mRootView.findViewById(R.id.profile_sector)).setText(dataSnapshot.child("name").getValue().toString());
                    ((TextView) mRootView.findViewById(R.id.sector_title)).setText(dataSnapshot.child("name").getValue().toString());
                    //fetch stock information
                    for (DataSnapshot stock : dataSnapshot.child("portfolio").getChildren()) {
                        ShareDetails details = stock.getValue(ShareDetails.class);
                        MySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(new AlphaVantageShareProvider(details, shareListener, errorListener));
                        Log.d(TAG, "onDataChange: " + stock);
                    }

                    //setup closest meeting card
                    final Meeting closestMeeting = DateUtils.getClosestMeeting(dataSnapshot);
                    if (closestMeeting != null) {
                        Log.d(TAG, "onDataChange: " + closestMeeting.toString());
                        TextView upcomingLabel = mRootView.findViewById(R.id.profile_upcoming_date);
                        SimpleDateFormat sdf = new SimpleDateFormat("EEEE dd/MM/yyyy HH:mm");
                        upcomingLabel.setText(sdf.format(closestMeeting.date));
                        ((TextView) mRootView.findViewById(R.id.profile_upcoming_title)).setText(closestMeeting.name);
                        ((TextView) mRootView.findViewById(R.id.profile_upcoming_venue)).setText(closestMeeting.venue);
                    } else {
                        ((TextView) mRootView.findViewById(R.id.profile_upcoming_title)).setText("No meetings available");
                        ((TextView) mRootView.findViewById(R.id.profile_upcoming_venue)).setText("Please try again later");
                    }


                    //set up the add to calendar button
                    mRootView.findViewById(R.id.profile_add_cal).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            long millis = 0;
                            if (closestMeeting != null) {
                                millis = closestMeeting.date.getTime();

                                Intent intent = new Intent(Intent.ACTION_EDIT);
                                intent.setType("vnd.android.cursor.item/event");
                                intent.putExtra(CalendarContract.Events.TITLE, "SMF Meeting");
                                intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, millis);
                                intent.putExtra(CalendarContract.Events.ALL_DAY, false);// periodicity
                                intent.putExtra(CalendarContract.Events.DESCRIPTION, "SMF Meeting");
                                startActivity(intent);
                            }
                        }
                    });


                    //check if the current user is actually the sector manager
                    if (firebaseUser.getUid().equals(dataSnapshot.child("sector_manager").getValue().toString())) {
                        mRootView.findViewById(R.id.sector_is_manager).setVisibility(View.VISIBLE);
                        ((ProgressBar) mRootView.findViewById(R.id.sector_progress)).setVisibility(View.GONE);
                    } else {

                        //if not, get the sector manager
                        myRef.child("users").child(dataSnapshot.child("sector_manager").getValue().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                User manager = dataSnapshot.getValue(User.class);
                                ((TextView) mRootView.findViewById(R.id.sector_manager)).setText(manager.name);
                                ((TextView) mRootView.findViewById(R.id.sector_manager_email)).setText(manager.username);
                                ((ProgressBar) mRootView.findViewById(R.id.sector_progress)).setVisibility(View.GONE);
                                ((TextView) mRootView.findViewById(R.id.sector_manager_role)).setText(manager.position);
                                Glide.with(getActivity()).load(manager.pictureUrl).into((ImageView) mRootView.findViewById(R.id.sector_manager_profile));
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }else{
                    //sector deleted, reset account
                    myRef.child("users")
                            .child(firebaseUser.getUid())
                            .removeValue();
                    getActivity().finish();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void finishAccountSetup() {
        //if it hasn't been set up before, tell activity to ask for code
        startActivity(new Intent(getActivity(), ProfileEditActivity.class).putExtra("code_prompt", true));
        getActivity().finish();
    }

    private void setUpView() {
        //show the progress bar
        //obtain user data from server

        //fetch event information

        Request firstEventRequest = new WordpressEventsProvider(new Response.Listener<ArrayList<Event>>() {
            @Override
            public void onResponse(ArrayList<Event> response) {
                final Event first = response.get(0);
                if(getActivity().getApplicationContext()!=null) {
                    if (first != null) {
                        ((TextView) mRootView.findViewById(R.id.event_title)).setText(Html.fromHtml(first.title));
                        Glide.with(getActivity()
                                .getApplicationContext())
                                .asBitmap()
                                .load(first.imgUrl)
                                .apply(RequestOptions.centerCropTransform())
                                .into((ImageView) mRootView.findViewById(R.id.event_image));
                        mRootView.findViewById(R.id.event_layout).setVisibility(View.VISIBLE);
                        mRootView.findViewById(R.id.event_progress).setVisibility(View.GONE);
                        //setup event button
                        mRootView.findViewById(R.id.event_button_more).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(first.url)));
                            }
                        });
                    } else {
                        Snackbar.make(mRootView, "Please check your internet connection", Snackbar.LENGTH_LONG).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        },1);
        MySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(firstEventRequest);


    }/*
    // Description AsyncTask
    public class FetchFirstEvent extends AsyncTask<Void, Void, Void> {
        String desc;
        private Article first;
        public FetchFirstEvent context;
        @Override
        protected Void doInBackground(Void... params) {
            try {
                Document document = Jsoup.connect("http://www.trinitysmf.com/news").get();
                Elements elements = document.select("article");
                first = Article.parseHtml(elements.first());
            } catch (IOException e) {
                e.printStackTrace();
                Snackbar.make(mRootView, "Please check your network connection", Snackbar.LENGTH_LONG).show();

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if(first!=null) {
                ((TextView) mRootView.findViewById(R.id.event_title)).setText(first.title);
                Glide.with(getActivity()
                        .getApplicationContext())
                        .asBitmap()
                        .load(first.imgUrl)
                        .apply(RequestOptions.centerCropTransform())
                        .into((ImageView) mRootView.findViewById(R.id.event_image));
                mRootView.findViewById(R.id.event_layout).setVisibility(View.VISIBLE);
                mRootView.findViewById(R.id.event_progress).setVisibility(View.GONE);
                //setup event button
                mRootView.findViewById(R.id.event_button_more).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(first.url)));
                    }
                });
            }else{
                Snackbar.make(mRootView, "Please check your internet connection", Snackbar.LENGTH_LONG).show();
        }
        }
    }*/

    Response.Listener<Share> shareListener = new Response.Listener<Share>() {
        @Override
        public void onResponse(final Share response) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());

            DecimalFormat decimalFormat = new DecimalFormat("+#,##0.00;-#");
            String changeFormatted = decimalFormat.format(((Double.parseDouble(response.quote) - response.buyPrice)/response.buyPrice)*100);

            View shareCard = inflater.inflate(R.layout.card_finance, null);
            ((TextView) shareCard.findViewById(R.id.tv_share_name)).setText(response.title);
            ((TextView) shareCard.findViewById(R.id.tv_share_change)).setText(response.change);
            ((TextView) shareCard.findViewById(R.id.tv_share_currency)).setText(response.currency);
            ((TextView) shareCard.findViewById(R.id.tv_share_quote)).setText(response.quote);
            ((TextView) shareCard.findViewById(R.id.tv_share_buyprice)).setText("Buy price: " + Double.toString(response.buyPrice) + " (" + changeFormatted +"%)");
            ((TextView) shareCard.findViewById(R.id.tv_share_number)).setText(String.valueOf(response.number) + " shares");
            ((TextView) shareCard.findViewById(R.id.tv_share_ticker)).setText(response.ticker.toUpperCase());
            shareCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://finance.yahoo.com/quote/"+response.ticker)));
                }
            });

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins((int)DimensUtils.convertDpToPixel(16, getContext()),
                    (int)DimensUtils.convertDpToPixel(4, getContext()),
                    (int)DimensUtils.convertDpToPixel(16, getContext()),
                    (int)DimensUtils.convertDpToPixel(4, getContext()));

            mFeedView.addView(shareCard,layoutParams);
        }
    };
    private Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            error.printStackTrace();
            Snackbar.make(mFeedView , "Please check your network connection", Snackbar.LENGTH_LONG).show();
        }
    };

}
