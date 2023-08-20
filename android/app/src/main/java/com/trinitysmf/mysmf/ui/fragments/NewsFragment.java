package com.trinitysmf.mysmf.ui.fragments;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.trinitysmf.mysmf.R;
import com.trinitysmf.mysmf.adapters.ArticlesAdapter;
import com.trinitysmf.mysmf.models.Article;
import com.trinitysmf.mysmf.models.providers.NewsApiProvider;
import com.trinitysmf.mysmf.ui.MySingleton;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewsFragment extends Fragment implements Response.ErrorListener, Response.Listener<ArrayList<Article>> {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View mRootView;
    private ArticlesAdapter recyclerAdapter;
    private ProgressBar mProgressBar;


    public NewsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewsFragment newInstance(String param1, String param2) {
        NewsFragment fragment = new NewsFragment();
        Bundle args = new Bundle();/*
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);*/
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);/*
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_events, container, false);
        RecyclerView rv = (RecyclerView) mRootView.findViewById(R.id.articles_recycler);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerAdapter = new ArticlesAdapter(new ArrayList<Article>());
        rv.setAdapter(recyclerAdapter);
        //set up progress bar
        mProgressBar = (ProgressBar) mRootView.findViewById(R.id.event_progress);

        //start data pull
        MySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(new NewsApiProvider(this, this));

        return mRootView;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        error.printStackTrace();
    }

    @Override
    public void onResponse(ArrayList<Article> response) {
        recyclerAdapter.setDataSet(response);
        recyclerAdapter.notifyDataSetChanged();
        mProgressBar.setVisibility(View.GONE);
    }
}
