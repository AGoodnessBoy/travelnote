package ink.moming.travelnote.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import ink.moming.travelnote.unit.NetUnit;

/**
 * Created by Moming-Desgin on 2018/2/17.
 */

public class GuidePerference {

    public static final String PREF_CITY_NAME = "pref_city";
    public static final String TAG = GuidePerference.class.getSimpleName();


    public static String getCityName(Context context){
        SharedPreferences cityInfo = context.getSharedPreferences(PREF_CITY_NAME,Context.MODE_PRIVATE);

        String city = cityInfo.getString("city",getDefaultCityName(context));

        return city;

    }

    public static void saveCityName(Context context,String city){
        SharedPreferences cityInfo = context.getSharedPreferences(PREF_CITY_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor =   cityInfo.edit();
        editor.putString("city",city);
        editor.commit();
    }


    private static String getDefaultCityName(Context context) {

        String cityName = "";

        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(GuideContract.GuideEntry.CONTENT_URI,null,
                null,null,null);
        if (cursor!=null){
            cursor.moveToFirst();
            cityName = cursor.getString(cursor.getColumnIndex(GuideContract.GuideEntry.COLUMN_CITY_NAME));


            cursor.close();
        }

        Log.d(TAG,cityName);

        return cityName;


    }

}
