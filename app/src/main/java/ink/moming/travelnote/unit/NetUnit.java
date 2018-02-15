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
import java.util.Scanner;

import ink.moming.htmlanalysislib.HtmlAnalysis;
import ink.moming.travelnote.data.GuideContract;

/**
 * Created by Moming-Desgin on 2018/2/14.
 */

public class NetUnit {

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

        HtmlAnalysis htmlTools = new  HtmlAnalysis();

        String cityurl = "https://lvyou.baidu.com"+link;

        JSONObject cityContent = htmlTools.getCityGuide(cityurl);

        return cityContent;
    }

    public static ContentValues[] buildGuideCityListValues()
        throws JSONException {
        JSONArray list = getCityListFromBaiduAPI();
        ContentValues[] contentValues  = null;
        if (list!=null){
            Log.v("tag",Integer.toString(list.length()));
            for (int i =0;i<list.length();i++){
                JSONArray region = list.getJSONObject(i).getJSONArray("regions");
                for (int j=0;j<region.length();j++){
                    JSONArray city = region.getJSONObject(j).getJSONArray("citys");
                    contentValues = new ContentValues[city.length()];
                    for (int k =0;k<city.length();k++){
                        ContentValues value = new ContentValues();
                        value.put(GuideContract.GuideEntry.COLUMN_CITY_NAME,city.getJSONObject(k).getString("city"));
                        value.put(GuideContract.GuideEntry.COLUMN_CITY_LINK,city.getJSONObject(k).getString("url"));
                        contentValues[k] = value;
                    }
                }

            }

            return contentValues;


        }else {
            return null;
        }
    }



}
