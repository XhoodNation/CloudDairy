package com.afrikcode.alccodechallenge.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class NoteContract {

    public static final String CONTENT_AUTHORITY = "com.afrikcode.alccodechallenge";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_NOTES = "notes";


    // An empty constructor to unaware instantiation of the contract class.
    private NoteContract() {
    }

    public static final class NoteEntry implements BaseColumns {


        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_NOTES);


        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_NOTES;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_NOTES;

        public final static String TABLE_NAME = "notes";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_NOTE_TITLE = "title";
        public final static String COLUMN_NOTE_BODY = "body";
        public final static String COLUMN_NOTE_DATE = "date";
        public final static String COLUMN_NOTE_KEY = "id";

    }

}
