package ink.moming.travelnote.unit;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Scanner;

import ink.moming.htmlanalysislib.HtmlAnalysis;
import ink.moming.travelnote.R;
import ink.moming.travelnote.data.GuideContract;

/**
 * Created by Moming-Desgin on 2018/2/14.
 */

public class NetUnit {
    public static final String TAG = NetUnit.class.getSimpleName();
    public static final String GOOGLE_MAP_GEOCODING_API =
            "https://maps.googleapis.com/maps/api/geocode/json";

    public static final String HE_WEATHER_API ="https://free-api.heweather.com/now";
    public static final String USER_API ="http://api.moming.ink/db_user_func.php";


    final static String APIKEY_PARAM="key";
    final static String ADDRESS_PARAM="address";

    public static URL buildGoogleMapGeocodingUrl(String address,String key){
        Uri buildUri = Uri.parse(GOOGLE_MAP_GEOCODING_API).buildUpon()
                .appendQueryParameter(ADDRESS_PARAM,address)
                .appendQueryParameter(APIKEY_PARAM,key)
                .build();
        Log.d(TAG,buildUri.toString());
        URL url = null;
        try{
            url = new URL(buildUri.toString());
        }catch (MalformedURLException e){
            e.printStackTrace();
        }

        return url;
    }

    public static String getResponseFromHttpUrl(URL url)throws IOException {
        HttpURLConnection urlConnection =(HttpURLConnection)url.openConnection();
        urlConnection.setConnectTimeout(5000);
        urlConnection.setReadTimeout(10000);
        try{
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");
            boolean hasInput =scanner.hasNext();
            if (hasInput){
                return  scanner.next();
            }else {
                return null;
            }
        }finally {
            urlConnection.disconnect();
        }
    }

    //获取cityList

    public static JSONArray getCityListFromBaiduAPI(){

        HtmlAnalysis htmlTools = new  HtmlAnalysis();
        return htmlTools.getCityList();
    }

    //获取cityContent

    public static JSONObject getCityContentFromBaiduAPI(String link){

        Log.d(TAG,link);
        HtmlAnalysis htmlTools = new  HtmlAnalysis();

        String cityurl = "https://lvyou.baidu.com"+link;
        Log.d(TAG,cityurl);
        JSONObject cityContent = htmlTools.getCityGuide(cityurl);

        return cityContent;
    }

    public static ArrayList<ContentValues> buildGuideCityListValues()
        throws JSONException {
        JSONArray list = getCityListFromBaiduAPI();
        Log.d(TAG,list.toString());
        Log.d(TAG,Integer.toString(list.length()));

        ArrayList<ContentValues> contentValues = new ArrayList<ContentValues>();

        if (list!=null){
            Log.v("tag",Integer.toString(list.length()));
            for (int i =0;i<list.length();i++){

                JSONArray region = list.getJSONObject(i).getJSONArray("regions");
                Log.v("tag",Integer.toString(region.length()));
                for (int j=0;j<region.length();j++){

                    JSONArray city = region.getJSONObject(j).getJSONArray("citys");
                    Log.v("tag",Integer.toString(city.length()));
                    for (int k =0;k<city.length();k++){
                        ContentValues value = new ContentValues();
                        value.put(GuideContract.GuideEntry.COLUMN_CITY_NAME,city.getJSONObject(k).getString("city"));
                        value.put(GuideContract.GuideEntry.COLUMN_CITY_LINK,city.getJSONObject(k).getString("url"));
                        value.put(GuideContract.GuideEntry.COLUMN_UPDATE_TAG,"0");
                        contentValues.add(value);
                    }
                }

            }

            return contentValues;


        }else {
            return null;
        }
    }

    public static ArrayList<ContentValues> buildGuideCityListValuesFromFile(Context context)
            throws JSONException {


        JSONArray list = new JSONArray(openRawResource(context,R.raw.city_list));
        Log.d(TAG,list.toString());
        Log.d(TAG,Integer.toString(list.length()));

        ArrayList<ContentValues> contentValues = new ArrayList<ContentValues>();

        if (list!=null){
            Log.v("tag",Integer.toString(list.length()));
            for (int i =0;i<list.length();i++){

                ContentValues value = new ContentValues();
                value.put(GuideContract.GuideEntry.COLUMN_CITY_NAME,list.getJSONObject(i).getString("city"));
                value.put(GuideContract.GuideEntry.COLUMN_CITY_LINK,list.getJSONObject(i).getString("url"));
                value.put(GuideContract.GuideEntry.COLUMN_CITY_AREA,list.getJSONObject(i).getString("area"));
                value.put(GuideContract.GuideEntry.COLUMN_CITY_REGION,list.getJSONObject(i).getString("region"));
                value.put(GuideContract.GuideEntry.COLUMN_UPDATE_TAG,"0");
                contentValues.add(value);

            }
            return contentValues;
        }else {
            return null;
        }

    }


    public static LatLng getCityLocationFromGoogleMap(String address){

        String api_key = "AIzaSyDayp6XbBRgmBCsuDNRXBzKRNeKKCL1m2k";
        URL base_url = buildGoogleMapGeocodingUrl(address,api_key);
        String res = null;
        LatLng loc = null;

        try {
            res = getResponseFromHttpUrl(base_url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (res!=null){

            try {

                Log.d(TAG,res);
                JSONObject resJson  = new JSONObject(res);
                if (resJson.getString("status").equals("OK")){

                    Double lat =resJson.getJSONArray("results").getJSONObject(0)
                            .getJSONObject("geometry")
                            .getJSONObject("location")
                            .getDouble("lat");
                    Double lng =resJson.getJSONArray("results").getJSONObject(0)
                            .getJSONObject("geometry")
                            .getJSONObject("location")
                            .getDouble("lng");
                    loc = new LatLng(lat,lng);

                }else {
                    Log.d(TAG,resJson.getString("error_message"));
                    loc = new LatLng(0,0);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return  loc;
    }


    public static String getWeatherFromHe(String city){
        Uri weather_uri = Uri.parse(HE_WEATHER_API).buildUpon()
                .appendQueryParameter("loaction",city)
                .appendQueryParameter("key","252e690a660546bf8fc934967e9d5611")
                .appendQueryParameter("lang","en")
                .appendQueryParameter("unit","m")
                .build();
        URL url = null;
        String json = null;
        try{
            Log.d(TAG,weather_uri.toString());
            url = new URL(weather_uri.toString());
            json = getResponseFromHttpUrl(url);
        }catch (MalformedURLException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return json;

    }




    private static String openRawResource(Context context,int id)throws Resources.NotFoundException{
        InputStream stream = context.getResources().openRawResource(id);
        String res=null;
        if (stream!=null){
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream,"utf-8"));
                StringBuffer stringBuffer = new StringBuffer();
                String line = null;
                while ((line=reader.readLine())!=null){
                    stringBuffer.append(line+"\n");
                }
                stream.close();
                res = stringBuffer.toString();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            res =  "";
        }
        return res;
    }



    public static String md5(String content) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(content.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("NoSuchAlgorithmException",e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UnsupportedEncodingException", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10){
                hex.append("0");
            }
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }


    public static String userLogin(String email){
        Uri uri = Uri.parse(USER_API).buildUpon()
                .appendQueryParameter("action","login")
                .appendQueryParameter("email",email)
                .build();
        URL url = null;
        String json = null;
        try{
            Log.d(TAG,uri.toString());
            url = new URL(uri.toString());
            json = getResponseFromHttpUrl(url);
        }catch (MalformedURLException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;

    }

    public static String userRegister(String name,String email,String pass){
        Uri uri = Uri.parse(USER_API).buildUpon()
                .appendQueryParameter("action","register")
                .appendQueryParameter("name",name)
                .appendQueryParameter("email",email)
                .appendQueryParameter("pass",pass)

                .build();
        URL url = null;
        String json = null;
        try{
            Log.d(TAG,uri.toString());
            url = new URL(uri.toString());
            json = getResponseFromHttpUrl(url);
        }catch (MalformedURLException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;

    }
}
