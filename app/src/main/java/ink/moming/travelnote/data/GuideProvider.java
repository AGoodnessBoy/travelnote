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
 * Created by Moming-Desgin on 2018/2/14.
 */

public class GuideProvider extends ContentProvider {


    public static final int CODE_GUIDE = 100;
    public static final int CODE_GUIDE_WITH_ID = 101;
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private GuideDbHelper mDbHelper;


    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        final String authority = GuideContract.CONTENT_AUTHORITY;

        matcher.addURI(authority,GuideContract.PATH_MOVIE,CODE_GUIDE);
        matcher.addURI(authority,GuideContract.PATH_MOVIE+"/#",CODE_GUIDE_WITH_ID);


        return matcher;
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new GuideDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri,
                        @Nullable String[] projection,
                        @Nullable String selection,
                        @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {


        Cursor cursor;
        switch (sUriMatcher.match(uri)){

            case CODE_GUIDE_WITH_ID:{
                String guideId = uri.getLastPathSegment();

                String[] selectionArguments =
                        new String[]{guideId};

                cursor = mDbHelper.getReadableDatabase().query(
                        GuideContract.GuideEntry.TABLE_NAME,
                        projection,
                        GuideContract.GuideEntry._ID+" = ? ",
                        selectionArguments,
                        null,
                        null,sortOrder);
                break;


            }

            case CODE_GUIDE:{
                cursor = mDbHelper.getReadableDatabase().query(
                        GuideContract.GuideEntry.TABLE_NAME,
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
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        return null;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri)){
            case CODE_GUIDE:
                db.beginTransaction();
                int rowsInserted = 0;
                try {
                    for (ContentValues value : values){

                        long _id =
                                db.insert(
                                        GuideContract.GuideEntry.TABLE_NAME,
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

                Log.v("bulk",Integer.toString(rowsInserted));

                if (rowsInserted > 0){
                    getContext().getContentResolver().notifyChange(uri,null);
                }
                return rowsInserted;

            default:
                return super.bulkInsert(uri,values);


        }
    }

    @Override
    public int delete(@NonNull Uri uri,
                      @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        int numRowsDeleted;

        if (null == selection) selection = "1";

        switch (sUriMatcher.match(uri)){

            case CODE_GUIDE:
                numRowsDeleted =mDbHelper.getWritableDatabase().delete(
                        GuideContract.GuideEntry.TABLE_NAME,
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
    public int update(@NonNull Uri uri,
                      @Nullable ContentValues values,
                      @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        int updateReturn;
        switch (sUriMatcher.match(uri)){
            case CODE_GUIDE_WITH_ID:
                updateReturn = mDbHelper.getWritableDatabase().update(
                       GuideContract.GuideEntry.TABLE_NAME,values,selection,selectionArgs);
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
