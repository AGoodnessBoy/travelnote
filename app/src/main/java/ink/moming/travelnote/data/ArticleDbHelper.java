package ink.moming.travelnote.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by admin on 2018/3/1.
 */

public class ArticleDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "article.db";

    private static final int DATABASE_VERSION = 1;

    public ArticleDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_GUIDE_TABLE =
                "CREATE TABLE " + ArticleContract.ArticleEntry.TABLE_NAME +" ("+
                        ArticleContract.ArticleEntry._ID +
                        " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                        ArticleContract.ArticleEntry.COLUMN_ARTICLE_ID+
                        " TEXT NOT NULL, "+
                        ArticleContract.ArticleEntry.COLUMN_ARTICLE_TITLE+
                        " TEXT NOT NULL, "+
                        ArticleContract.ArticleEntry.COLUMN_ARTICLE_IMAGE+
                        " TEXT NOT NULL, "+
                        ArticleContract.ArticleEntry.COLUMN_ARTICLE_CITY+
                        " TEXT NOT NULL, "+
                        " UNIQUE ("+ ArticleContract.ArticleEntry.COLUMN_ARTICLE_ID+
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
