package ink.moming.travelnote.unit;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import ink.moming.htmlanalysislib.HtmlAnalysis;
import ink.moming.travelnote.data.GuideContract;

/**
 * Created by Moming-Desgin on 2018/2/14.
 */

public class NetUnit {
    public static final String TAG = NetUnit.class.getSimpleName();

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



}
