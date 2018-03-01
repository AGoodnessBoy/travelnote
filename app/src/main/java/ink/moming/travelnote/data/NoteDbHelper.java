package ink.moming.travelnote.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by admin on 2018/3/1.
 */

public class NoteDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "note.db";

    private static final int DATABASE_VERSION = 1;

    public NoteDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_GUIDE_TABLE =
                "CREATE TABLE " + NoteContract.NoteEntry.TABLE_NAME +" ("+
                        NoteContract.NoteEntry._ID +
                        " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                        NoteContract.NoteEntry.COLUMN_NOTE_ID+
                        " INTEGER NOT NULL, "+
                        NoteContract.NoteEntry.COLUMN_NOTE_TEXT+
                        " TEXT, "+
                        NoteContract.NoteEntry.COLUMN_NOTE_IMAGE+
                        " TEXT, "+
                        NoteContract.NoteEntry.COLUMN_NOTE_TIME+
                        " TEXT, "+
                        NoteContract.NoteEntry.COLUMN_NOTE_USER+
                        " INTEGER NOT NULL, "+
                        " UNIQUE ("+ NoteContract.NoteEntry.COLUMN_NOTE_ID+
                        ") ON CONFLICT REPLACE);";

        db.execSQL(SQL_CREATE_GUIDE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS "
                + GuideContract.GuideEntry.TABLE_NAME);

        onCreate(db);


    }
}
