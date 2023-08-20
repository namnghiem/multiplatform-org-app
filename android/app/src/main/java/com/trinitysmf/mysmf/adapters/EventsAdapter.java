package com.trinitysmf.mysmf.adapters;

import android.content.Intent;
import android.net.Uri;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.drawee.view.SimpleDraweeView;
import com.trinitysmf.mysmf.R;
import com.trinitysmf.mysmf.models.Event;

import java.util.ArrayList;

/**
 * Created by namxn_000 on 20/10/2017.
 */

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {

    private ArrayList<Event> mArticles;

    public EventsAdapter(ArrayList<Event> articles){
        mArticles = articles;
    }
    @Override
    public EventsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_event, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final EventsAdapter.ViewHolder holder, final int position) {

        holder.authorTv.setText(mArticles.get(position).author);
        holder.dateTv.setText(mArticles.get(position).date);
        holder.summaryTv.setText(Html.fromHtml(mArticles.get(position).summary));
        holder.titleTv.setText(Html.fromHtml(mArticles.get(position).title));
        if(mArticles.get(position).facebookLink!=null &&mArticles.get(position).eventTime!=null && mArticles.get(position).venue!=null&& !mArticles.get(position).venue.equals("false")) {
            holder.facebookTv.setText(Html.fromHtml(mArticles.get(position).facebookLink));
            Linkify.addLinks(holder.facebookTv, Linkify.WEB_URLS);
            holder.facebookTv.setLinkTextColor(holder.facebookTv.getResources().getColor(R.color.cardview_light_background));
            holder.timeTv.setText(mArticles.get(position).eventTime);
            holder.venueTv.setText(mArticles.get(position).venue);
        }
        Glide.with(holder.imageView.getContext()).asBitmap().load(mArticles.get(position).imgUrl).apply(RequestOptions.centerCropTransform()).into(holder.imageView);
        holder.imageView.setImageURI(mArticles.get(position).imgUrl);
        holder.moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.moreButton.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mArticles.get(position).url)));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mArticles.size();
    }

    public void setDataSet(ArrayList<Event> articles){
        this.mArticles = articles;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public SimpleDraweeView imageView;
        public TextView summaryTv;
        public TextView dateTv;
        public TextView authorTv;
        public TextView titleTv;
        public TextView venueTv;
        public TextView timeTv;
        public TextView facebookTv;
        public Button moreButton;
        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (SimpleDraweeView) itemView.findViewById(R.id.sdvImage);
            summaryTv = (TextView) itemView.findViewById(R.id.event_summary);
            dateTv = (TextView) itemView.findViewById(R.id.event_date);
            authorTv= (TextView) itemView.findViewById(R.id.event_author);
            titleTv= (TextView) itemView.findViewById(R.id.event_title);
            venueTv=  itemView.findViewById(R.id.event_venue);
            timeTv=  itemView.findViewById(R.id.event_time);
            facebookTv=  itemView.findViewById(R.id.event_facebook);
            moreButton = (Button) itemView.findViewById(R.id.event_button_more);
        }
    }

}
