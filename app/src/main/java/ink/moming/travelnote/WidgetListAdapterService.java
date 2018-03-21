package ink.moming.travelnote;

import android.appwidget.AppWidgetManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import ink.moming.travelnote.data.ArticleContract;
import ink.moming.travelnote.data.GuidePerference;

/**
 * Created by jml on 2018/3/21.
 */

public class WidgetListAdapterService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return null;
    }
}

class WidgetListRemoteViewFactory implements RemoteViewsService.RemoteViewsFactory{

    private Context mContext;
    private Cursor mCursor;
    private int mAppWidgetId;
    private static final String BAIDU_LVYOU_BASE_URL="https://lvyou.baidu.com/notes/";
    public static final String[] ARTICLE_PROJECTION ={
            ArticleContract.ArticleEntry.COLUMN_ARTICLE_ID,
            ArticleContract.ArticleEntry.COLUMN_ARTICLE_TITLE,
            ArticleContract.ArticleEntry.COLUMN_ARTICLE_IMAGE
    };

    public WidgetListRemoteViewFactory(Context context,Intent intent){
        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {

        mCursor = null ;

        ContentResolver contentResolver = mContext.getContentResolver();
        String selection = ArticleContract.ArticleEntry.COLUMN_ARTICLE_CITY+ "= ?";
        String[] selectionArgs = new String[]{GuidePerference.getCityName(mContext)};
        mCursor = contentResolver.query(
                ArticleContract.ArticleEntry.CONTENT_URI,ARTICLE_PROJECTION,selection,selectionArgs,null);


    }

    @Override
    public void onDestroy() {

        mCursor = null;

    }

    @Override
    public int getCount() {

        if (mCursor!=null){
           return mCursor.getCount();
        }
        else {
            return 0;
        }
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(),
                R.layout.widget_guide_list_item);
        mCursor.moveToPosition(position);
        remoteViews.setTextViewText(R.id.widget_guide_title,
                mCursor.getString(mCursor.getColumnIndex(ArticleContract.ArticleEntry.COLUMN_ARTICLE_TITLE)));
        String id=  mCursor.getString(mCursor.getColumnIndex(ArticleContract.ArticleEntry.COLUMN_ARTICLE_ID));
        Intent fillInIntent = new Intent();
        String url =BAIDU_LVYOU_BASE_URL+id;

        fillInIntent.putExtra("url",url);
        remoteViews.setOnClickFillInIntent(R.id.widget_guide_title,fillInIntent);


        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
