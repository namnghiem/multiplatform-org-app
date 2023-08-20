package com.trinitysmf.mysmf.ui.fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.trinitysmf.mysmf.R;
import com.trinitysmf.mysmf.adapters.EventsAdapter;
import com.trinitysmf.mysmf.models.Event;
import com.trinitysmf.mysmf.models.providers.WordpressEventsProvider;
import com.trinitysmf.mysmf.ui.MySingleton;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EventsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventsFragment extends Fragment implements Response.ErrorListener, Response.Listener<ArrayList<Event>> {
    private View mRootView;
    private EventsAdapter recyclerAdapter;
    private ProgressBar mProgressBar;

    public EventsFragment() {
        // Required empty public constructor
    }

    public static EventsFragment newInstance() {
        EventsFragment fragment = new EventsFragment();
        Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         mRootView = inflater.inflate(R.layout.fragment_events, container, false);
        RecyclerView rv = (RecyclerView) mRootView.findViewById(R.id.articles_recycler);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerAdapter = new EventsAdapter(new ArrayList<Event>());
        rv.setAdapter(recyclerAdapter);
        //set up progress bar
        mProgressBar = mRootView.findViewById(R.id.event_progress);

        Request r = new WordpressEventsProvider(this, this, 50);
        //start data pull
        MySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(r);

        return mRootView;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        error.printStackTrace();
    }

    @Override
    public void onResponse(ArrayList<Event> response) {
        recyclerAdapter.setDataSet(response);
        recyclerAdapter.notifyDataSetChanged();
        mProgressBar.setVisibility(View.GONE);
    }

/*
    // Description AsyncTask
    public class FetchArticles extends AsyncTask<Void, Void, Void> {
        private final Activity context;
        String desc;
        ArrayList<Article> articles = new ArrayList<>();
        public FetchArticles(Activity context){
            this.context = context;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);

        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Document document = Jsoup.connect("http://www.trinitysmf.com/news").get();
                Elements elements = document.select("article");
                 articles = new ArrayList<>();
                for (int i = 0; i < elements.size(); i++) {
                    articles.add(Article.parseHtml(elements.get(i)));
                }
            } catch (IOException e) {
                e.printStackTrace();
                Snackbar.make(mRootView, "Please check your network connection", Snackbar.LENGTH_LONG).show();

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            recyclerAdapter.setDataSet(articles);
            recyclerAdapter.notifyDataSetChanged();
            mProgressBar.setVisibility(View.GONE);
        }
    }*/

}
