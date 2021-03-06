package ink.moming.travelnote.sync;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONException;

import ink.moming.travelnote.data.GuideContract;
import ink.moming.travelnote.data.GuidePerference;
import ink.moming.travelnote.data.NoteContract;

/**
 * Created by Moming-Desgin on 2018/2/14.
 */

public class GuideSyncUtils {

    private static boolean sInitialized;

    synchronized public static void initialize(@NonNull final Context context){

        if (sInitialized)return;

        sInitialized = true;

        Thread checkForEmpty = new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        Uri uri = GuideContract.GuideEntry.CONTENT_URI;
                        Log.d("url",uri.toString());
                        String[] projection = {GuideContract.GuideEntry._ID};
                        Cursor cursor = context.getContentResolver().query(
                                uri,projection,null,null,null
                        );

                        if (null == cursor || cursor.getCount() == 0){
                            startImmediateSync(context);
                        }
                        if (cursor!=null){
                            cursor.close();
                        }

                    }
                }
        );

        checkForEmpty.start();

    }

    public static void startImmediateSync(@NonNull final Context context){
        GuideSyncIntentService.startActionSyncGuideList(context);

    }
    public static Cursor updateCitySync(@NonNull final Context context,String cityname){
        Cursor cursor = null;
        try {
            cursor =  GuideSyncTask.upDateCityInfoValuesById(context,cityname);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return cursor;
    }

    synchronized public static void flashNoteList(@NonNull final Context context){


        if (!GuidePerference.getUserStatus(context))return;


        Thread checkForEmpty = new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        Uri uri = NoteContract.NoteEntry.CONTENT_URI;
                        Log.d("url",uri.toString());
                        String[] projection = {NoteContract.NoteEntry._ID};
                        Cursor cursor = context.getContentResolver().query(
                                uri,projection,null,null,null
                        );

                        if (null == cursor || cursor.getCount() == 0){
                            startUpdateNoteSync(context);
                        }
                        if (cursor!=null){
                            cursor.close();
                        }

                    }
                }
        );

        checkForEmpty.start();

    }

    public static void startUpdateNoteSync(@NonNull final Context context){
        GuideSyncIntentService.startActionSyncNoteList(context);

    }






}
