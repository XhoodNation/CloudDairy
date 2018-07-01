package com.afrikcode.alccodechallenge;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.afrikcode.alccodechallenge.data.Notes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class ReadActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    FirebaseDatabase database;
    DatabaseReference notes;

    TextView tvTitle, tvBody;

    Notes currentNote;

    String noteKey = "";

    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setElevation(2);

        tts = new TextToSpeech(ReadActivity.this, ReadActivity.this);

        //Init Firebase
        database = FirebaseDatabase.getInstance();
        notes = database.getReference("Notes");

        tvTitle = findViewById(R.id.titleRead);
        tvBody = findViewById(R.id.bodyRead);


        //Get note key from intent
        if (getIntent() != null)
            noteKey = getIntent().getStringExtra("NoteKey");
        if (!noteKey.isEmpty()) {
            getNoteContent(noteKey);
        }
    }

    private void getNoteContent(String noteKey) {
        notes.child(noteKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentNote = dataSnapshot.getValue(Notes.class);

                try {
                    //Load notes
                    tvBody.setText(currentNote.getBody());
                    tvTitle.setText(currentNote.getTitle());
                } catch (Exception e) {
                    Toast.makeText(ReadActivity.this, "Note deleted", Toast.LENGTH_SHORT).show();
                    finish();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                String error = databaseError.getMessage();
                Toast.makeText(ReadActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_reader, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_reader_share:
                shareNote();
                return true;
            case R.id.action_reader_delete:
                showDeleteDialog();
                return true;
            case R.id.action_play:
                readNote();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteNoteEntry(noteKey);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the reading note.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteNoteEntry(String noteKey) {
        notes.child(noteKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(ReadActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void readNote() {
        FirebaseAuth auth;
        auth = FirebaseAuth.getInstance();
        String username = auth.getCurrentUser().getDisplayName();

        tts.speak("Ok " + username + "\n" + "Note title: " + tvTitle.getText().toString() + "\n" + "Note Body: " + tvBody.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);

    }

    private void shareNote() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, tvTitle.getText().toString());
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, tvBody.getText().toString());
        startActivity(Intent.createChooser(sharingIntent, "ALC Notes: Share to"));
    }

    @Override
    protected void onDestroy() {
        // stop speach when activity is destroyed
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }
    }
}
