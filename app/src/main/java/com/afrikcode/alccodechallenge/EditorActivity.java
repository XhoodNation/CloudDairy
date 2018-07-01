package com.afrikcode.alccodechallenge;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afrikcode.alccodechallenge.data.NoteContract;
import com.afrikcode.alccodechallenge.data.Notes;
import com.afrikcode.alccodechallenge.data.RandomString;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    //Identifier for the note data loader
    private static final int SAVED_NOTE_LOADER = 0;
    //Firebase database
    FirebaseDatabase database;
    DatabaseReference notes;
    FirebaseAuth auth;
    String currentUID;
    Notes newNotes;
    private Uri mCurrentNoteUri;
    //Declaring Views Variables
    private EditText titleEditText, dateEditText;
    private TextView keyTextView;
    //Keeps track of whether the note has been edited or not
    private boolean mPetHasChanged = false;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mPetHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mPetHasChanged = true;
            return false;
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        //Initializing firebase
        database = FirebaseDatabase.getInstance();
        notes = database.getReference("Notes");
        auth = FirebaseAuth.getInstance();

        currentUID = auth.getCurrentUser().getUid();

        //Removing actionbar elvation
        ActionBar actionBar = getSupportActionBar();
        actionBar.setElevation(2);


        Intent intent = getIntent();
        mCurrentNoteUri = intent.getData();

        keyTextView = findViewById(R.id.tvKey);


        String aa = RandomString.generateString();

        titleEditText = findViewById(R.id.etTitle);
        dateEditText = findViewById(R.id.etBody);


        // If the intent DOES NOT contain note content URI, insert new note
        if (mCurrentNoteUri == null) {


            // Set title to add note
            setTitle("Add Note");
            keyTextView.setText(aa);
            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            //Hidinig option menu with delete when in add note mode
            invalidateOptionsMenu();
        } else {

            // If getIntent is not null meaning editing mode
            setTitle("Edit Note");
            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            // displaying current note in editor by initializing the loader
            getLoaderManager().initLoader(SAVED_NOTE_LOADER, null, this);
        }


        //Determining whether views have been touched
        titleEditText.setOnTouchListener(mTouchListener);
        dateEditText.setOnTouchListener(mTouchListener);

    }

    //Get Current date and return as a string
    private String date() {
        DateFormat dateFormat = new SimpleDateFormat("EEE, dd MM yyyy");
        try {
            dateFormat.parse("Fri, 13 June 00:00 CEST 2018");
        } catch (Exception e) {
        }
        return dateFormat.format(new Date());
    }

    //getting current time hour and min and returning as a string
    private String time() {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        try {
            dateFormat.parse("Fri, 13 June 00:00 CEST 2018");
        } catch (Exception e) {
        }
        return dateFormat.format(new Date());
    }

    private void saveNote() {
        //Converting input fields to string and removing spaces with the trim() method
        String nameString = titleEditText.getText().toString().trim();
        String breedString = dateEditText.getText().toString().trim();
        //Concatenating Date and time returned
        String weightString = "Last update on " + date() + " at " + time();
        String keyString = keyTextView.getText().toString().trim();

        // Checking if it is to be new note
        // and check if all the fields in the editor are blank
        if (mCurrentNoteUri == null &&
                TextUtils.isEmpty(nameString) && TextUtils.isEmpty(breedString)) {
            return;
        }

        //Creating content values
        ContentValues values = new ContentValues();
        values.put(NoteContract.NoteEntry.COLUMN_NOTE_TITLE, nameString);
        values.put(NoteContract.NoteEntry.COLUMN_NOTE_BODY, breedString);

        values.put(NoteContract.NoteEntry.COLUMN_NOTE_DATE, weightString);
        values.put(NoteContract.NoteEntry.COLUMN_NOTE_KEY, keyString);


        if (mCurrentNoteUri == null) {

            Uri newUri = getContentResolver().insert(NoteContract.NoteEntry.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, getString(R.string.editor_insert_note_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_insert_note_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentNoteUri, values, null, null);

            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.editor_update_note_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_update_note_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // Hiding if new note is to be added
        if (mCurrentNoteUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            MenuItem menuItem1 = menu.findItem(R.id.action_share);
            menuItem.setVisible(false);
            menuItem1.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveOptionDialog();
                return true;
            case R.id.action_delete:
                showDeleteDialog();
                return true;
            case R.id.action_share:
                shareNote();
                return true;
            case android.R.id.home:
                if (!mPetHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void shareNote() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, titleEditText.getText().toString());
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, dateEditText.getText().toString());
        startActivity(Intent.createChooser(sharingIntent, "alc Notes: Share to"));
    }

    @Override
    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
        if (!mPetHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                NoteContract.NoteEntry._ID,
                NoteContract.NoteEntry.COLUMN_NOTE_TITLE,
                NoteContract.NoteEntry.COLUMN_NOTE_BODY,
                NoteContract.NoteEntry.COLUMN_NOTE_DATE,
                NoteContract.NoteEntry.COLUMN_NOTE_KEY};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,
                mCurrentNoteUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }


        if (cursor.moveToFirst()) {
            int titleColumnIndex = cursor.getColumnIndex(NoteContract.NoteEntry.COLUMN_NOTE_TITLE);
            int dateColumnIndex = cursor.getColumnIndex(NoteContract.NoteEntry.COLUMN_NOTE_BODY);
            int keyColumnIndex = cursor.getColumnIndex(NoteContract.NoteEntry.COLUMN_NOTE_KEY);

            // Extract out the value from the Cursor for the given column index
            String title = cursor.getString(titleColumnIndex);
            String date = cursor.getString(dateColumnIndex);
            String key = cursor.getString(keyColumnIndex);

            // Update the views on the screen with the values from the database
            titleEditText.setText(title);
            dateEditText.setText(date);
            keyTextView.setText(key);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        titleEditText.setText("");
        dateEditText.setText("");
    }


    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.saving_changes_dialog_text);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void showDeleteDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteNoteEntry();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void saveOptionDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Save a copy to cloud and access it from other devices");
        builder.setPositiveButton("Sync to cloud", new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, int id) {

                dialog.dismiss();


                //Converting input fields to string and removing spaces with the trim() method
                String titleString = titleEditText.getText().toString().trim();
                String bodyString = dateEditText.getText().toString().trim();
                //Concatenating Date and time returned
                String dateString = "Last update on " + date() + " at " + time();

                String userID = auth.getCurrentUser().getUid();

                String noteKey = keyTextView.getText().toString().trim();


                newNotes = new Notes();
                newNotes.setBody(bodyString);
                newNotes.setDate(dateString);
                newNotes.setTitle(titleString);
                newNotes.setUid(userID);

                if (auth.getCurrentUser() != null) {
                    //Submit to cloud

                    notes.child(noteKey).setValue(newNotes).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                saveNote();
                                finish();
                            } else {
                                saveNote();
                                String error = task.getException().getMessage();
                                Toast.makeText(EditorActivity.this, "Error saving to cloud " + error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
        builder.setNegativeButton("Device only", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                saveNote();
                finish();
                dialog.dismiss();
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void deleteNoteEntry() {
        if (mCurrentNoteUri != null) {


            String noteKey = keyTextView.getText().toString().trim();
            notes.child(noteKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        int rowsDeleted = getContentResolver().delete(mCurrentNoteUri, null, null);
                        if (rowsDeleted == 0) {
                            Toast.makeText(EditorActivity.this, "Note failed to delete", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(EditorActivity.this, "Note deleted", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(EditorActivity.this, "Cloud note couldn't be deleted due to " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });

        }

        //finishing the activity
        finish();
    }
}
