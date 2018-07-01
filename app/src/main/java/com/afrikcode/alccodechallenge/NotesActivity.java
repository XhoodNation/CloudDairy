package com.afrikcode.alccodechallenge;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.afrikcode.alccodechallenge.data.NoteContract;
import com.google.firebase.auth.FirebaseAuth;

public class NotesActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    //Note Loader Identifier
    private static final int NOTE_LOADER = 0;

    //ListView Adaptor
    NoteCursorAdapter mCursorAdapter;

    //Firebase Auth
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);


        //Initiializing firebase Auth
        auth = FirebaseAuth.getInstance();

        //Removing actionbar elvation
        ActionBar actionBar = getSupportActionBar();
        actionBar.setElevation(2);

        // Setting onclick listener for fab to open EditorActivity
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent editorIntent = new Intent(NotesActivity.this, EditorActivity.class);
                startActivity(editorIntent);
            }
        });

        // Finding ListView which will be populated with the note info
        ListView noteItemListView = findViewById(R.id.list);

        View emptyView = findViewById(R.id.empty);
        noteItemListView.setEmptyView(emptyView);

        mCursorAdapter = new NoteCursorAdapter(this, null);
        noteItemListView.setAdapter(mCursorAdapter);

        // Setup the item click listener
        noteItemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Intent to go to EditorActivity with current note ID/URI
                Intent intent = new Intent(NotesActivity.this, EditorActivity.class);
                Uri clickedNoteUri = ContentUris.withAppendedId(NoteContract.NoteEntry.CONTENT_URI, id);
                intent.setData(clickedNoteUri);
                startActivity(intent);
            }
        });

        // Starts the loader
        getLoaderManager().initLoader(NOTE_LOADER, null, this);
    }


    private void deleteAllNotes() {
        int rowsDeleted = getContentResolver().delete(NoteContract.NoteEntry.CONTENT_URI, null, null);
        Toast.makeText(this, "All Notes Deleted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_notes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Delete all entries" menu option

            case R.id.action_cloud:
                Intent cloudIntent = new Intent(NotesActivity.this, CloudActivity.class);
                startActivity(cloudIntent);
                return true;
            case R.id.action_delete_all_entries:
                deleteAllNotes();
                return true;
            case R.id.action_logout:
                auth.signOut();
                Intent loginIntent = new Intent(NotesActivity.this, AuthActivity.class);
                startActivity(loginIntent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                NoteContract.NoteEntry._ID,
                NoteContract.NoteEntry.COLUMN_NOTE_TITLE,
                NoteContract.NoteEntry.COLUMN_NOTE_DATE};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                NoteContract.NoteEntry.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update {@link NoteCursorAdapter} with this new cursor containing updated pet data
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Checks if the current user is null then the user hasnt login
        //and will be directed to AuthActivity to login
        if (auth.getCurrentUser() == null) {
            Intent authIntent = new Intent(NotesActivity.this, AuthActivity.class);
            startActivity(authIntent);
            finish();
        }
    }
}
