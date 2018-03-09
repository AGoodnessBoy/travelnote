package ink.moming.travelnote.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by admin on 2018/3/1.
 */

public class ArticleProvider extends ContentProvider {


    public static final String TAG = ArticleProvider.class.getSimpleName();
    public static final int CODE_ARTICLE = 600;
    public static final int CODE_ARTICLE_WITH_ID = 601;
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private ArticleDbHelper mDbHelper;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        final String authority = ArticleContract.CONTENT_AUTHORITY;

        matcher.addURI(authority,ArticleContract.PATH_NOTE,CODE_ARTICLE);
        matcher.addURI(authority,ArticleContract.PATH_NOTE+"/#",CODE_ARTICLE_WITH_ID);


        return matcher;
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new ArticleDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;
        switch (sUriMatcher.match(uri)){

            case CODE_ARTICLE_WITH_ID:{
                String noteId = uri.getLastPathSegment();

                String[] selectionArguments =
                        new String[]{noteId};

                cursor = mDbHelper.getReadableDatabase().query(
                        ArticleContract.ArticleEntry.TABLE_NAME,
                        projection,
                        ArticleContract.ArticleEntry.COLUMN_ARTICLE_ID+" = ? ",
                        selectionArguments,
                        null,
                        null,sortOrder);
                break;


            }

            case CODE_ARTICLE:{
                cursor = mDbHelper.getReadableDatabase().query(
                        ArticleContract.ArticleEntry.TABLE_NAME,
                        projection,selection,selectionArgs, null,null,sortOrder);

                break;
            }

            default:
                throw new UnsupportedOperationException();

        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri)){
            case CODE_ARTICLE:
                db.beginTransaction();
                int rowsInserted = 0;
                try {
                    for (ContentValues value : values){

                        long _id =
                                db.insert(
                                        ArticleContract.ArticleEntry.TABLE_NAME,
                                        null,
                                        value
                                );
                        if (_id != -1){
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                }finally {
                    db.endTransaction();
                }

                Log.v(TAG,"article:"+Integer.toString(rowsInserted));

                if (rowsInserted > 0){
                    getContext().getContentResolver().notifyChange(uri,null);
                }
                return rowsInserted;

            default:
                return super.bulkInsert(uri,values);


        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int numRowsDeleted;

        if (null == selection) selection = "1";

        switch (sUriMatcher.match(uri)){

            case CODE_ARTICLE:
                numRowsDeleted =mDbHelper.getWritableDatabase().delete(
                       ArticleContract.ArticleEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);


        }
        if (numRowsDeleted !=0 ){
            getContext().getContentResolver().notifyChange(uri,null);
        }

        return numRowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int updateReturn;
        switch (sUriMatcher.match(uri)){
            case CODE_ARTICLE_WITH_ID:
                updateReturn = mDbHelper.getWritableDatabase().update(
                        ArticleContract.ArticleEntry.TABLE_NAME,values,selection,selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (updateReturn!=0){
            getContext().getContentResolver().notifyChange(uri,null);
        }

        return updateReturn;
    }
}
