package com.trinitysmf.mysmf.ui.activities;

import android.content.DialogInterface;

import androidx.core.app.NavUtils;
import androidx.core.app.TaskStackBuilder;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.trinitysmf.mysmf.R;
import com.trinitysmf.mysmf.models.ShareDetails;
import com.trinitysmf.mysmf.utils.MenuUtils;

public class StockEditActivity extends AppCompatActivity {
    private DatabaseReference ref;
    RecyclerView r;
    String sector;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_share_add:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                LayoutInflater inflater = getLayoutInflater();
                builder.setTitle("Add to Portfolio");
                builder.setView(R.layout.dialog_add_share);/*
                //In case it gives you an error for setView(View) try
                builder.setView(inflater.inflate(R.layout.custom_view, null));*/
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText name = ((AlertDialog) dialog).findViewById(R.id.edit_share_name);
                        EditText ticker = ((AlertDialog) dialog).findViewById(R.id.edit_share_ticker);
                        EditText buyPrice = ((AlertDialog) dialog).findViewById(R.id.edit_share_buy_price);
                        EditText number = ((AlertDialog) dialog).findViewById(R.id.edit_share_number);
                        ShareDetails details = new ShareDetails();
                        details.ticker = ticker.getText().toString();
                        details.title = name.getText().toString();
                        details.buyPrice = Double.parseDouble(buyPrice.getText().toString());
                        details.number = Integer.parseInt(number.getText().toString());
                        ref.child("sectors")
                                .child(sector)
                                .child("portfolio")
                                .push()
                                .setValue(details);
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
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuUtils.tintAllIcons(menu, 0xFFFFFFFF);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sector);
        androidx.appcompat.widget.Toolbar t = findViewById(R.id.toolbar);
        setSupportActionBar(t);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Edit Portfolio");
        r = findViewById(R.id.sector_recycler);
        r.setLayoutManager(new LinearLayoutManager(this));


        t.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ref = FirebaseDatabase.getInstance()
                .getReference();


        sector = getIntent().getStringExtra("sector");
        if(sector!=null) {
            Query query = ref
                    .child("sectors")
                    .child(sector)
                    .child("portfolio");
            FirebaseRecyclerOptions<ShareDetails> options =
                    new FirebaseRecyclerOptions.Builder<ShareDetails>()
                            .setQuery(query, ShareDetails.class)
                            .build();

            final FirebaseRecyclerAdapter<ShareDetails, ShareHolder> adapter = new FirebaseRecyclerAdapter<ShareDetails, ShareHolder>(options) {
                @Override
                protected void onBindViewHolder(final ShareHolder holder, int position, ShareDetails model) {
                    holder.shareName.setText( model.title);
                    holder.shareTicker.setText(model.ticker);
                    holder.ref = getRef(position);
                    holder.deleteShare.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            holder.ref.removeValue();
                        }
                    });
                }


                @Override
                public ShareHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_share, parent, false);
                    return new ShareHolder(view);
                }
            };
            r.setAdapter(adapter);
            adapter.startListening();
        }
    }

    private class ShareHolder extends RecyclerView.ViewHolder {
        TextView shareName;
        TextView shareTicker;
        ImageButton deleteShare;
        public DatabaseReference ref;
        public ShareHolder(View itemView) {
            super(itemView);
            deleteShare = itemView.findViewById(R.id.share_button_delete);
            shareName = itemView.findViewById(R.id.share_name);
            shareTicker = itemView.findViewById(R.id.share_ticker);
        }
    }
}
