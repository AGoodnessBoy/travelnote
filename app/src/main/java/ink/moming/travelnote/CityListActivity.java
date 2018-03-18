package ink.moming.travelnote;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import ink.moming.travelnote.adapter.CityListAdapter;
import ink.moming.travelnote.data.GuideContract;
import ink.moming.travelnote.data.GuidePerference;
import ink.moming.travelnote.ui.StickyRecyclerView;

public class CityListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        CityListAdapter.CityListChooseHandler{

    private final String TAG = CityListActivity.class.getSimpleName();
    public static final int ID_CITYLIST_LOADER = 24;
    public static final String[] CITY_LIST_PROJECTION ={
            GuideContract.GuideEntry._ID,
            GuideContract.GuideEntry.COLUMN_CITY_NAME,
            GuideContract.GuideEntry.COLUMN_CITY_LINK,
            GuideContract.GuideEntry.COLUMN_CITY_REGION
    };


    private  CityListAdapter cityListAdapter;
    private  StickyRecyclerView stickyRecyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_list);
        stickyRecyclerView = findViewById(R.id.city_list);
        cityListAdapter = new CityListAdapter(this);
        stickyRecyclerView.setAdapter(cityListAdapter);
        getSupportLoaderManager().initLoader(ID_CITYLIST_LOADER,null,this);

    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id){
            case ID_CITYLIST_LOADER:
                //loading
                Context context = this;
                Uri cityListUri = GuideContract.GuideEntry.CONTENT_URI;
                return new CursorLoader(context,cityListUri,CITY_LIST_PROJECTION,null,null,null);
            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cityListAdapter.swapData(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cityListAdapter.swapData(null);
    }

    @Override
    public void onClick(String city) {
        Intent intent = new Intent();
        intent.putExtra("city",city);
        setResult(33,intent);
        finish();
    }
}
