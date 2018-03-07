package ink.moming.travelnote.sync;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.support.annotation.Nullable;


public class GuideSyncIntentService extends IntentService {



    private static final String ACTION_GUIDE_LIST = "ink.moming.travelnote.sync.action.guide_list";
    private static final String ACTION_NOTE_LIST = "ink.moming.travelnote.sync.action.note_list";

    public GuideSyncIntentService() {
        super("GuideSyncIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        if (intent!=null) {
            final String action = intent.getAction();
            switch (action){
                case ACTION_GUIDE_LIST:
                    handleActionSyncGuideList();
                    break;
                case ACTION_NOTE_LIST:
                    handleActionSyncNoteList();
                    break;
                default:
                    break;
            }
        }

    }


    public static void startActionSyncGuideList(Context context){
        Intent intent = new Intent(context,GuideSyncIntentService.class);
        intent.setAction(ACTION_GUIDE_LIST);
        context.startService(intent);
    }

    public static void startActionSyncNoteList(Context context){
        Intent intent = new Intent(context,GuideSyncIntentService.class);
        intent.setAction(ACTION_NOTE_LIST);
        context.startService(intent);
    }


    private void handleActionSyncGuideList(){
        GuideSyncTask.syncGuide(this);
        //startActionSyncGuideList(this);
    }

    private void handleActionSyncNoteList(){
        GuideSyncTask.syncNote(this);
    }



}
