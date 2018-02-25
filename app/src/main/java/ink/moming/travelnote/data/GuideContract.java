package ink.moming.travelnote.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Moming-Desgin on 2018/2/14.
 */

public class GuideContract {


    public static final String CONTENT_AUTHORITY =
            "ink.moming.travelnote";

    public static final Uri BASE_CONTENT_URI
            =Uri.parse("content://"+CONTENT_AUTHORITY);


    public static final String PATH_MOVIE = "guide";

    public static final class GuideEntry implements BaseColumns {

        public static final Uri CONTENT_URI =BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIE)
                .build();
        public static final String TABLE_NAME = "guide";

        public static final String COLUMN_CITY_NAME = "name";

        public static final String COLUMN_CITY_LINK = "link";

        public static final String COLUMN_CITY_AREA = "area";

        public static final String COLUMN_CITY_REGION = "region";


        public static final String COLUMN_CITY_INFO = "info";

        public static final String COLUMN_CITY_IMAGES = "image";

        public static final String COLUMN_CITY_ARTICLES = "articles";

        public static final String COLUMN_UPDATE_TAG = "update_tag";



        public static Uri buildUriWithId(int id){

            return CONTENT_URI.buildUpon()
                    .appendPath(Integer.toString(id))
                    .build();
        }

    }



}
