package ink.moming.travelnote.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by admin on 2018/3/1.
 */

public class NoteContract {

    public static final String CONTENT_AUTHORITY =
            "ink.moming.travelnote";

    public static final Uri BASE_CONTENT_URI
            =Uri.parse("content://"+CONTENT_AUTHORITY);


    public static final String PATH_NOTE = "note";

    public static final class NoteEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_NOTE)
                .build();
        public static final String TABLE_NAME = "note";

        public static final String COLUMN_NOTE_ID = "note_id";

        public static final String COLUMN_NOTE_TEXT = "note_text";

        public static final String COLUMN_NOTE_IMAGE = "note_image";

        public static final String COLUMN_NOTE_USER = "note_user";

        public static final String COLUMN_NOTE_TIME = "note_time";


        public static Uri buildUriWithId(int id) {

            return CONTENT_URI.buildUpon()
                    .appendPath(Integer.toString(id))
                    .build();
        }
    }
}
