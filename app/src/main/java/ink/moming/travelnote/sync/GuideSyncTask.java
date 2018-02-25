package ink.moming.travelnote.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import ink.moming.travelnote.data.GuideContract;
import ink.moming.travelnote.data.GuidePerference;
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
                JSONObject cityInfo = NetUnit.getCityContentFromBaiduAPI(citylink);

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
                    Log.d(TAG,"数据插入成功");
                    updated = contentResolver.query(GuideContract.GuideEntry.CONTENT_URI,null,
                            GuideContract.GuideEntry.COLUMN_CITY_NAME+" = ?",
                            new String[]{cityname},null);
                }else {
                    Log.d(TAG,"数据插入失败");
                    updated = null;

                }
            }


        }

        return updated;
    }
}
