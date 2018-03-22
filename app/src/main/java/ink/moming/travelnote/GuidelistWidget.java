package ink.moming.travelnote;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import ink.moming.travelnote.data.GuidePerference;

/**
 * Implementation of App Widget functionality.
 */
public class GuidelistWidget extends AppWidgetProvider {


    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.guidelist_widget);
        views.setTextViewText(R.id.widget_city_name, GuidePerference.getCityName(context));
        Intent intent = new Intent(context,WidgetListAdapterService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        views.setRemoteAdapter(R.id.widget_lv_guide_list,intent);
        Intent articleIntent = new Intent(context,GuideDetailActivity.class);
        PendingIntent goToDetailIntent = PendingIntent.getActivity(context,0,
                articleIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.widget_lv_guide_list,goToDetailIntent);
        views.setEmptyView(R.id.widget_lv_guide_list,R.id.widget_no_data);
        // Instruct the widget manager to update the widget
        setReflashBtnClickIntent(context,views);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        GuideWidgetIntentService.startActionUpdataWidget(context);
    }
    public static void updateGuideWidgets(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }
    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }


    public static void setReflashBtnClickIntent(Context context,RemoteViews views){
        Intent intent= new Intent(context,GuideWidgetIntentService.class);
        intent.setAction(GuideWidgetIntentService.ACTION_UPDATA_WIDGET);
        PendingIntent pendingIntent = PendingIntent.getService(context,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.widget_bt_refresh,pendingIntent);
    }
}

