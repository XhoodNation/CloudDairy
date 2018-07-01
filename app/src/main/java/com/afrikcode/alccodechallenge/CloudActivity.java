package com.afrikcode.alccodechallenge;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.afrikcode.alccodechallenge.Interface.ItemClickListener;
import com.afrikcode.alccodechallenge.ViewHolder.CloudViewHolder;
import com.afrikcode.alccodechallenge.data.Notes;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CloudActivity extends AppCompatActivity {

    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;

    FirebaseRecyclerAdapter<Notes, CloudViewHolder> adapter;

    DatabaseReference notes;
    FirebaseAuth auth;

    String currentEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud);

        auth = FirebaseAuth.getInstance();
        currentEmail = auth.getCurrentUser().getUid();

        //Removing actionbar elvation
        ActionBar actionBar = getSupportActionBar();
        actionBar.setElevation(2);

        //Firebase
        database = FirebaseDatabase.getInstance();
        notes = database.getReference("Notes");
        notes.keepSynced(true);


        recyclerView = findViewById(R.id.cloudRecyclerView);
        recyclerView.setHasFixedSize(false);
        //layoutManager = new LinearLayoutManager(this);
        //recyclerView.setLayoutManager(layoutManager);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, 1));


        noteList(currentEmail);



    }

    private void noteList(String currentEmail) {
        adapter = new FirebaseRecyclerAdapter<Notes, CloudViewHolder>(
                Notes.class,
                R.layout.cloud_item_layout,
                CloudViewHolder.class,
                notes.orderByChild("uid")
                        .equalTo(currentEmail)
        ) {
            @Override
            protected void populateViewHolder(CloudViewHolder viewHolder, Notes model, int position) {
                viewHolder.tvKey.setText(adapter.getRef(position).getKey());
                viewHolder.tvTitle.setText(model.getTitle());
                viewHolder.tvBody.setText(model.getBody());
                viewHolder.tvDate.setText(model.getDate());

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        Intent CloudIntent = new Intent(CloudActivity.this, ReadActivity.class);
                        CloudIntent.putExtra("NoteKey", adapter.getRef(position).getKey());
                        startActivity(CloudIntent);
                    }
                });
            }
        };

        recyclerView.setAdapter(adapter);
    }

}
