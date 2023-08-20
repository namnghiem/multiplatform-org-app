package com.trinitysmf.mysmf.ui.activities;

import android.content.Intent;
import androidx.core.app.NavUtils;
import androidx.core.app.TaskStackBuilder;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.trinitysmf.mysmf.R;
import com.trinitysmf.mysmf.models.Sector;
import com.trinitysmf.mysmf.utils.MenuUtils;

public class SectorListActivity extends AppCompatActivity {
    private DatabaseReference ref;
    RecyclerView r;
    String sector;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_share_add:
                startActivity(new Intent(this, SectorAddActivity.class));
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
        setTitle("Manage Sectors");
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


            Query query = ref.child("sectors");
            FirebaseRecyclerOptions<Sector> options =
                    new FirebaseRecyclerOptions.Builder<Sector>()
                            .setQuery(query, Sector.class)
                            .build();

            final FirebaseRecyclerAdapter<Sector, SectorHolder> adapter = new FirebaseRecyclerAdapter<Sector, SectorHolder>(options) {
                @Override
                protected void onBindViewHolder(SectorHolder holder, final int position, Sector model) {
                    holder.sectorName.setText(model.name);
                    holder.codeText.setText(model.code);
                    holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getRef(position).removeValue();
                        }
                    });/*

                    holder.shareTicker.setText(model.ticker);
                    holder.ref = getRef(position);
                    holder.deleteShare.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            holder.ref.removeValue();
                        }
                    });*/
                }


                @Override
                public SectorHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_sector, parent, false);
                    return new SectorHolder(view);
                }
            };
            r.setAdapter(adapter);
            adapter.startListening();


    }


    private class SectorHolder extends ViewHolder {
        TextView sectorName;
        ImageButton deleteButton;
        TextView codeText;
        public SectorHolder(View itemView) {
            super(itemView);
            sectorName = itemView.findViewById(R.id.item_sector_name);
            codeText = itemView.findViewById(R.id.item_sector_code);
            deleteButton=itemView.findViewById(R.id.item_sector_clear);
        }

    }
}
