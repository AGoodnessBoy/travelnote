package ink.moming.travelnote.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Moming-Desgin on 2018/2/14.
 */

public class GuideDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "guide.db";

    private static final int DATABASE_VERSION = 1;


    public GuideDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_GUIDE_TABLE =
                "CREATE TABLE " + GuideContract.GuideEntry.TABLE_NAME +" ("+
                        GuideContract.GuideEntry._ID +
                        " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                        GuideContract.GuideEntry.COLUMN_CITY_NAME+
                        " TEXT, "+
                        GuideContract.GuideEntry.COLUMN_CITY_LINK+
                        " TEXT, "+
                        GuideContract.GuideEntry.COLUMN_CITY_AREA+
                        " TEXT, "+
                        GuideContract.GuideEntry.COLUMN_CITY_REGION+
                        " TEXT, "+
                        GuideContract.GuideEntry.COLUMN_CITY_INFO+
                        " TEXT, "+
                        GuideContract.GuideEntry.COLUMN_CITY_IMAGES+
                        " TEXT, "+
                        GuideContract.GuideEntry.COLUMN_CITY_ARTICLES+
                        " TEXT, "+
                        GuideContract.GuideEntry.COLUMN_UPDATE_TAG+
                        " TEXT NOT NULL , "+
                        " UNIQUE ("+ GuideContract.GuideEntry.COLUMN_CITY_NAME+
                        ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_GUIDE_TABLE);



    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "
                + GuideContract.GuideEntry.TABLE_NAME);

        onCreate(sqLiteDatabase);

    }
}
