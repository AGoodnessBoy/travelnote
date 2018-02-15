package ink.moming.travelnote.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import ink.moming.travelnote.data.GuideContract;
import ink.moming.travelnote.unit.NetUnit;

/**
 * Created by Moming-Desgin on 2018/2/14.
 */

public class GuideSyncTask {

    synchronized public static void syncGuide(Context context){

        try {
            //保存为ContentValues[]
            ContentValues[] initializedData = NetUnit.buildGuideCityListValues();
            //先保存name 和 link

            if (initializedData.length!=0&&initializedData!=null){
                ContentResolver guideListResolver = context.getContentResolver();
                guideListResolver.delete(GuideContract.GuideEntry.CONTENT_URI,null,null);
                guideListResolver.bulkInsert(GuideContract.GuideEntry.CONTENT_URI,
                        initializedData);
                upDateFirstCityInfoValues(context);
            }

            NetUnit.getCityContentFromBaiduAPI("");

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private static void upDateFirstCityInfoValues(Context context) throws JSONException {

        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(GuideContract.GuideEntry.CONTENT_URI,null,
                null,null,null);


        ContentValues cityInfoCV = new ContentValues();

        if (cursor!=null){
            cursor.moveToFirst();
            int cityid = cursor.getInt(cursor.getColumnIndex(GuideContract.GuideEntry._ID));
            Uri cityUri = GuideContract.GuideEntry.buildUriWithId(cityid);
            String citylink = cursor.getColumnName(cursor.getColumnIndex(GuideContract.GuideEntry.COLUMN_CITY_LINK));
            cursor.close();

            JSONObject cityInfo = NetUnit.getCityContentFromBaiduAPI(citylink);

            cityInfoCV.put(GuideContract.GuideEntry.COLUMN_CITY_INFO,
                    cityInfo.getString("info"));
            cityInfoCV.put(GuideContract.GuideEntry.COLUMN_CITY_IMAGES,
                    cityInfo.getString("images"));
            cityInfoCV.put(GuideContract.GuideEntry.COLUMN_CITY_ARTICLES,
                    cityInfo.getString("articles"));
            contentResolver.update(cityUri,cityInfoCV,null,null);
        }

    }
}
