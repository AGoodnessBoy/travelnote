package ink.moming.travelnote;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;


public class GuideWidgetIntentService extends IntentService {

    public static final String ACTION_UPDATA_WIDGET = "ink.moming.travelnote.action.update_widget";


    public GuideWidgetIntentService() {
        super("GuideWidgetIntentService");
    }

    public static void startActionUpdataWidget(Context context) {
        Intent intent = new Intent(context, GuideWidgetIntentService.class);
        intent.setAction(ACTION_UPDATA_WIDGET);
        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPDATA_WIDGET.equals(action)) {
                handleActionUpdataWidget();
            }
        }
    }

    private void handleActionUpdataWidget() {

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, GuidelistWidget.class));
        //Trigger data update to handle the GridView widgets and force a data refresh
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_lv_guide_list);
        //Now update all widgets
       GuidelistWidget.updateGuideWidgets(getApplicationContext(),appWidgetManager,appWidgetIds);

    }


}
