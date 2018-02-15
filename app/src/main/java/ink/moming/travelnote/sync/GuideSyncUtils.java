package ink.moming.travelnote.sync;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.concurrent.TimeUnit;

/**
 * Created by Moming-Desgin on 2018/2/14.
 */

public class GuideSyncUtils {

    private static boolean sInitialized;
    private static final int SYNC_INTERVAL_HOURS = 3;
    private static final int SYNC_INTERVAL_SECONDS = (int) TimeUnit.HOURS.toSeconds(SYNC_INTERVAL_HOURS);
    private static final int SYNC_FLEXTIME_SECONDS = SYNC_INTERVAL_SECONDS / 3;

    private static final String GUIDE_SYNC_TAG = "guide-sync";

    static void scheduleJobDispatcherSync(@NonNull final  Context context){}

    synchronized public static void initialize(@NonNull final Context context){}

    public static void startImmediateSync(@NonNull final Context context){

    }

}
