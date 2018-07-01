package com.afrikcode.alccodechallenge;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.afrikcode.alccodechallenge.data.NoteContract;

public class NoteCursorAdapter extends CursorAdapter {


    public NoteCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        //inflating custom list item look xml file
        return LayoutInflater.from(context).inflate(R.layout.note_list_item, parent, false);
    }

    //Binding Note
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Declaring and initializing note items attributes for the list
        TextView titleTextView = view.findViewById(R.id.title);
        TextView dateTextView = view.findViewById(R.id.date);

        // Find the columns of pet attributes that we're interested in
        int titleColumnIndex = cursor.getColumnIndex(NoteContract.NoteEntry.COLUMN_NOTE_TITLE);
        int dateColumnIndex = cursor.getColumnIndex(NoteContract.NoteEntry.COLUMN_NOTE_DATE);

        // Read the pet attributes from the Cursor for the current pet
        String noteTitle = cursor.getString(titleColumnIndex);
        String noteDate = cursor.getString(dateColumnIndex);

        //If the title is empty then we set the title to this
        if (TextUtils.isEmpty(noteTitle)) {
            noteTitle = "Untitled Note";
        }


        titleTextView.setText(noteTitle);
        dateTextView.setText(noteDate);
    }
}
