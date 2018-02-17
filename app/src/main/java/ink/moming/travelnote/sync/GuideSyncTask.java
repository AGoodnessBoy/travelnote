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

    /**
     *
     * @param context
     */
    synchronized public static void syncGuide(Context context){

        try {
            //保存为ContentValues[]
            ArrayList<ContentValues> initializedData = NetUnit.buildGuideCityListValues();


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
                //查询第一组数据 填充完整
                upDateFirstCityInfoValues(context);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * 获取首个城市的信息
     * @param context
     * @throws JSONException
     */
    private static void upDateFirstCityInfoValues(Context context) throws JSONException {

        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(GuideContract.GuideEntry.CONTENT_URI,null,
                null,null,null);


        ContentValues cityInfoCV = new ContentValues();

        if (cursor!=null){
            cursor.moveToFirst();
            int cityid = cursor.getInt(cursor.getColumnIndex(GuideContract.GuideEntry._ID));
            Uri cityUri = GuideContract.GuideEntry.buildUriWithId(cityid);
            String citylink = cursor.getString(cursor.getColumnIndex(GuideContract.GuideEntry.COLUMN_CITY_LINK));
            String cityName = cursor.getString(cursor.getColumnIndex(GuideContract.GuideEntry.COLUMN_CITY_NAME));
            GuidePerference.saveCityName(context,cityName);
            cursor.close();

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
                Log.d("first date","数据插入成功");
            }
        }

    }


    public static ContentValues upDateCityInfoValuesById(Context context,String cityname) throws JSONException {

        ContentResolver contentResolver = context.getContentResolver();

        Cursor cursor = contentResolver.query(GuideContract.GuideEntry.CONTENT_URI,null,
                GuideContract.GuideEntry.COLUMN_CITY_NAME+" = ?",
                new String[]{cityname},null);

        ContentValues cityInfoCV = new ContentValues();

        if (cursor!=null){
            cursor.moveToFirst();
            int cityid = cursor.getInt(cursor.getColumnIndex(GuideContract.GuideEntry._ID));
            Uri cityUri = GuideContract.GuideEntry.buildUriWithId(cityid);
            String citylink = cursor.getString(cursor.getColumnIndex(GuideContract.GuideEntry.COLUMN_CITY_LINK));
            cursor.close();

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
                Log.d("first date","数据插入成功");
            }
        }

        return cityInfoCV;
    }
}
