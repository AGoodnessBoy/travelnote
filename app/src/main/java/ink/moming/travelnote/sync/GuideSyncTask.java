package ink.moming.travelnote.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ink.moming.travelnote.R;
import ink.moming.travelnote.data.ArticleContract;
import ink.moming.travelnote.data.GuideContract;
import ink.moming.travelnote.data.GuidePerference;
import ink.moming.travelnote.data.NoteContract;
import ink.moming.travelnote.unit.NetUnit;

/**
 * Created by Moming-Desgin on 2018/2/14.
 */

public class GuideSyncTask {


    public static final String  TAG  = GuideSyncTask.class.getSimpleName();

    /**
     *
     * @param context
     */
    synchronized public static void syncGuide(Context context){

        try {
            //保存为ContentValues[]
            ArrayList<ContentValues> initializedData = NetUnit.buildGuideCityListValuesFromFile(context);


            if (initializedData.size()!=0&&initializedData!=null){
                ContentResolver guideListResolver = context.getContentResolver();
                //先保存name 和 link
                guideListResolver.delete(GuideContract.GuideEntry.CONTENT_URI,null,null);

                ContentValues[] contentValues = new ContentValues[initializedData.size()];

                for (int i=0;i<initializedData.size();i++){
                    contentValues[i]=initializedData.get(i);
                }

                guideListResolver.bulkInsert(GuideContract.GuideEntry.CONTENT_URI,
                        contentValues);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    synchronized public static void syncNote(Context context){

        try {
            String notestr = NetUnit.getNoteList(GuidePerference.getUserId(context),context);
            JSONObject noteObj = new JSONObject(notestr);

            if (noteObj.getString("status").equals("400")){
                JSONArray noteArr = noteObj.getJSONArray("data");
                ContentValues[] contentValues = new ContentValues[noteArr.length()];
                for (int i=0;i<noteArr.length();i++){
                    ContentValues cv = new ContentValues();
                    cv.put(NoteContract.NoteEntry.COLUMN_NOTE_ID,noteArr.getJSONObject(i).getInt("note_id"));
                    cv.put(NoteContract.NoteEntry.COLUMN_NOTE_TEXT,noteArr.getJSONObject(i).getString("note_text"));
                    cv.put(NoteContract.NoteEntry.COLUMN_NOTE_IMAGE,noteArr.getJSONObject(i).getString("note_img"));
                    cv.put(NoteContract.NoteEntry.COLUMN_NOTE_TIME,noteArr.getJSONObject(i).getString("note_time"));
                    cv.put(NoteContract.NoteEntry.COLUMN_NOTE_USER,noteArr.getJSONObject(i).getInt("note_user"));
                    contentValues[i]=cv;
                }
                ContentResolver noteresolver = context.getContentResolver();

                noteresolver.delete(NoteContract.NoteEntry.CONTENT_URI,null,null);
                int status = noteresolver.bulkInsert(NoteContract.NoteEntry.CONTENT_URI,contentValues);

                if (status!=0){
                    Log.d(TAG,context.getString(R.string.note_update_success_str));
                }else {
                    Log.d(TAG,context.getString(R.string.note_update_failed_str));

                }

            }else {
                return;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public static Cursor upDateCityInfoValuesById(Context context,String cityname) throws JSONException {

        ContentResolver contentResolver = context.getContentResolver();

        Cursor cursor = contentResolver.query(GuideContract.GuideEntry.CONTENT_URI,null,
                GuideContract.GuideEntry.COLUMN_CITY_NAME+" = ?",
                new String[]{cityname},null);



        ContentValues cityInfoCV = new ContentValues();
        Cursor updated = null;

        if (cursor!=null){
            cursor.moveToFirst();
            int cityid = cursor.getInt(cursor.getColumnIndex(GuideContract.GuideEntry._ID));
            String citylink = cursor.getString(cursor.getColumnIndex(GuideContract.GuideEntry.COLUMN_CITY_LINK));
            String upDate_Tag = cursor.getString(cursor.getColumnIndex(GuideContract.GuideEntry.COLUMN_UPDATE_TAG));
            if (upDate_Tag.equals("1")){
                updated=cursor;
            }else {
                if (cursor!=null){
                    cursor.close();
                }
                Uri cityUri = GuideContract.GuideEntry.buildUriWithId(cityid);
                JSONObject cityInfo = NetUnit.getCityContentFromBaiduAPI(citylink,context);

                cityInfoCV.put(GuideContract.GuideEntry.COLUMN_CITY_INFO,
                        cityInfo.getString("info"));
                cityInfoCV.put(GuideContract.GuideEntry.COLUMN_CITY_IMAGES,
                        cityInfo.getString("image"));
                cityInfoCV.put(GuideContract.GuideEntry.COLUMN_CITY_ARTICLES,
                        cityInfo.getString("articles"));
                cityInfoCV.put(GuideContract.GuideEntry.COLUMN_UPDATE_TAG,"1");

                int updateStatus = contentResolver.update(cityUri,cityInfoCV, GuideContract.GuideEntry._ID+" = ?",
                        new String[]{Integer.toString(cityid)});

                if (updateStatus!=0){
                    Log.d(TAG,context.getString(R.string.data_input_success_str));
                    ContentValues contentValues[] = null;
                    JSONArray array = null;
                    array = new JSONArray(cityInfo.getString("articles"));
                    if (array.length()>0){
                        contentValues = new ContentValues[array.length()];
                        for (int i = 0;i<array.length();i++){
                            ContentValues item = new ContentValues();
                            item.put(ArticleContract.ArticleEntry.COLUMN_ARTICLE_ID,array.getJSONObject(i).getString("nid"));
                            item.put(ArticleContract.ArticleEntry.COLUMN_ARTICLE_IMAGE,array.getJSONObject(i)
                                    .getJSONArray("album_pic_list")
                                    .getJSONObject(0)
                                    .getString("pic_url"));
                            item.put(ArticleContract.ArticleEntry.COLUMN_ARTICLE_TITLE,array.getJSONObject(i).getString("title"));
                            item.put(ArticleContract.ArticleEntry.COLUMN_ARTICLE_CITY,cityname);
                            contentValues[i] = item;
                        }

                        int status = contentResolver.bulkInsert(ArticleContract.ArticleEntry.CONTENT_URI,contentValues);
                        if (status>0){
                            Log.d(TAG,context.getString(R.string.article_input_success_str));
                        }else {
                            Log.d(TAG,context.getString(R.string.article_input_failed_str));
                        }
                    }

                    updated = contentResolver.query(GuideContract.GuideEntry.CONTENT_URI,null,
                            GuideContract.GuideEntry.COLUMN_CITY_NAME+" = ?",
                            new String[]{cityname},null);
                }else {

                    Log.d(TAG,context.getString(R.string.date_input_failed_str));
                    updated = null;


                }
            }


        }

        return updated;
    }
}
