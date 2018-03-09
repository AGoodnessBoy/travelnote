package ink.moming.travelnote.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by admin on 2018/3/1.
 */

public class ArticleContract {

    public static final String CONTENT_AUTHORITY =
            "ink.moming.travelnote.data.ArticleProvider";

    public static final Uri BASE_CONTENT_URI
            =Uri.parse("content://"+CONTENT_AUTHORITY);


    public static final String PATH_NOTE = "article";

    public static final class ArticleEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_NOTE)
                .build();
        public static final String TABLE_NAME = "article";

        public static final String COLUMN_ARTICLE_ID = "article_id";

        public static final String COLUMN_ARTICLE_TITLE = "article_title";

        public static final String COLUMN_ARTICLE_IMAGE = "article_image";

        public static final String COLUMN_ARTICLE_CITY = "article_city";


        public static Uri buildUriWithId(int id) {

            return CONTENT_URI.buildUpon()
                    .appendPath(Integer.toString(id))
                    .build();
        }
    }
}
