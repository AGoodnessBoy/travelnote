package ink.moming.travelnote.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TimeUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import ink.moming.travelnote.unit.NetUnit;

/**
 * Created by Moming-Desgin on 2018/2/17.
 */

public class GuidePerference {

    public static final String PREF_CITY_NAME = "pref_city";
    public static final String PREF_USER_TOKEN = "pref_user";
    public static final String TAG = GuidePerference.class.getSimpleName();


    public static String getCityName(Context context){
        SharedPreferences cityInfo = context.getSharedPreferences(PREF_CITY_NAME,Context.MODE_PRIVATE);

        String city = cityInfo.getString("city","北京");

        return city;

    }

    public static void saveCityName(Context context,String city){
        SharedPreferences cityInfo = context.getSharedPreferences(PREF_CITY_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor =   cityInfo.edit();
        editor.putString("city",city);
        editor.commit();
    }

    public static void saveUserStatus(Context context,String email,String name) {
        SharedPreferences userSp = context.getSharedPreferences(PREF_USER_TOKEN,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor =   userSp.edit();
        editor.putString("user_email",email);
        editor.putString("user_name",name);
        editor.putLong("login_date",System.currentTimeMillis());
    }

    public static void removeUserStatus(Context context) {

        SharedPreferences userSp = context.getSharedPreferences(PREF_USER_TOKEN,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor =   userSp.edit();
        editor.clear();

    }

    public static Boolean getUserStatus(Context context){
        final long interval =604800000;
        SharedPreferences userSp = context.getSharedPreferences(PREF_USER_TOKEN,Context.MODE_PRIVATE);
        if (userSp!=null){
            long loginTime = userSp.getLong("login_date",0);
            long nowTime = System.currentTimeMillis();
            if ((nowTime-loginTime)<interval){
                return true;
            }else {
                return false;
            }
        }else {
            return false;
        }

    }


}
