package ink.moming.travelnote.fragment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import ink.moming.travelnote.CityListActivity;
import ink.moming.travelnote.R;
import ink.moming.travelnote.adapter.GuideListAdapter;
import ink.moming.travelnote.data.ArticleContract;
import ink.moming.travelnote.data.GuideContract;
import ink.moming.travelnote.data.GuidePerference;
import ink.moming.travelnote.sync.GuideSyncUtils;
import ink.moming.travelnote.unit.NetUnit;


public class GuideFragment extends Fragment  implements  OnMapReadyCallback{
    //http://gss0.baidu.com/7LsWdDW5_xN3otqbppnN2DJv/lvpics/pic/item/
    //http://hiphotos.baidu.com/lvpics/pic/item/574e9258d109b3de92f45cfbc7bf6c81810a4cdd.jpg


    //UI控件
    private RecyclerView mGuideList;
    private TextView mCityDescTextView;
    private TextView mCityNameTextView;
    private TextView mNoDataTextView;
    private ImageView mCityImageView;
    private GuideListAdapter mGuideListAdapter;
    private ProgressBar mProgressBar;
    private MapView mCityMap;
    private GoogleMap map;
    private LatLng cityLatlng;



    //常量
    public static final String TAG = GuideFragment.class.getSimpleName();
    public static final int ID_GUIDE_LOADER = 32;
    public static final int CITY_LIST_RES = 31;
    public static final String MAPVIEW_BUNDLE_KEY ="map_data";
    public static final String CITY_STATUS = "city-status";

    public static final String[] MAIN_GUIDE_PROJECTION ={
            GuideContract.GuideEntry._ID,
            GuideContract.GuideEntry.COLUMN_CITY_NAME,
            GuideContract.GuideEntry.COLUMN_CITY_INFO,
            GuideContract.GuideEntry.COLUMN_CITY_LINK,
            GuideContract.GuideEntry.COLUMN_CITY_IMAGES,
            GuideContract.GuideEntry.COLUMN_CITY_ARTICLES,
            GuideContract.GuideEntry.COLUMN_UPDATE_TAG
    };

    public static final String[] MAIN_ARTICLE_PROJECTION ={
            ArticleContract.ArticleEntry.COLUMN_ARTICLE_ID,
            ArticleContract.ArticleEntry.COLUMN_ARTICLE_TITLE,
            ArticleContract.ArticleEntry.COLUMN_ARTICLE_IMAGE,
            ArticleContract.ArticleEntry.COLUMN_ARTICLE_CITY
    };

    //内容加载器
    public LoaderManager.LoaderCallbacks<Cursor> callbacks;
    public Bundle cityBundle = null;


    public interface GuideFragmentListener {
       //
    }


    public GuideFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guide, container, false);

        initUI(view);

        //地图控件启动
        mCityMap.onCreate(savedInstanceState);

        callbacks = getCallbacks(getContext());

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //初始化攻略界面
        if (savedInstanceState==null){
            cityBundle = new Bundle();
            cityBundle.putString("city-name",GuidePerference.getCityName(getContext()));
            getActivity().getSupportLoaderManager().initLoader(ID_GUIDE_LOADER,cityBundle,callbacks);

        }else {
            Log.d(TAG,"状态保存：城市"+savedInstanceState.getBundle(CITY_STATUS).getString("city-name"));
            cityBundle =savedInstanceState.getBundle(CITY_STATUS);
            getActivity().getSupportLoaderManager().restartLoader(ID_GUIDE_LOADER,cityBundle,callbacks);
        }
        mCityMap.getMapAsync(this);

    }

    @Override
    public void onStart() {
        super.onStart();
        mCityMap.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();
        mCityMap.onStop();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG,"onAttach");
        //同步基础数据
        GuideSyncUtils.initialize(getContext());
        Log.d(TAG,"初始化城市数据initialize");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCityMap.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        mCityMap.onResume();
        Log.d(TAG,"onResume");
        if (cityBundle!=null){
            getActivity().getSupportLoaderManager().restartLoader(ID_GUIDE_LOADER,cityBundle,callbacks);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mCityMap.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG,"Guide onSaveInstanceState");

        if (cityBundle!=null){

            outState.putBundle(CITY_STATUS,cityBundle);
            Log.d(TAG,"状态输出："+outState.getBundle(CITY_STATUS).getString("city-name"));
        }
    }

    //初始化UI

    private void initUI(View view){
        Log.d(TAG,"初始化UIinitUI");

        mGuideList = view.findViewById(R.id.guide_list);
        mCityNameTextView = view.findViewById(R.id.city_name);
        mCityDescTextView = view.findViewById(R.id.city_desc);
        mCityImageView = view.findViewById(R.id.city_img);
        mNoDataTextView = view.findViewById(R.id.no_article);
        mProgressBar = view.findViewById(R.id.pb_article);
        mCityMap = view.findViewById(R.id.guide_map);


        LinearLayoutManager layoutManager = new
                LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        mGuideList.setLayoutManager(layoutManager);

        mGuideListAdapter = new GuideListAdapter(getContext());
        mGuideList.setAdapter(mGuideListAdapter);

        mCityNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CityListActivity.class);
                startActivityForResult(intent,CITY_LIST_RES);
            }
        });



    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CITY_LIST_RES && resultCode == 33){
            String city = data.getStringExtra("city");
            GuidePerference.saveCityName(getContext(),city);
            if (cityBundle!=null){
                cityBundle.clear();
            }
            cityBundle.putString("city-name",city);

            Toast.makeText(getActivity(),GuidePerference.getCityName(getContext()),Toast.LENGTH_LONG).show();
            getActivity().getSupportLoaderManager().restartLoader(ID_GUIDE_LOADER,cityBundle,callbacks);
        }

    }

    private LoaderManager.LoaderCallbacks<Cursor> getCallbacks(final Context context){
        Log.d(TAG,"获取加载器getCallbacks");
        return new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                switch (id){
                    case ID_GUIDE_LOADER:
                        //显示加载...
                        mProgressBar.setVisibility(View.VISIBLE);
                        mGuideList.setVisibility(View.INVISIBLE);
                        String cityname = args.getString("city-name");
                        Uri uri = GuideContract.GuideEntry.CONTENT_URI;
                        String selection = GuideContract.GuideEntry.COLUMN_CITY_NAME+ "= ?";
                        String[] selectionArgs = new String[]{cityname};
                        return new CursorLoader(context,uri,MAIN_GUIDE_PROJECTION,selection,selectionArgs,null);

                    default:
                        throw new RuntimeException("Loader Not Implemented: " + id);
                }
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                boolean cursorHasValidData = false;

                if (data!=null && data.moveToFirst()){
                    cursorHasValidData = true;
                }
                if (!cursorHasValidData){
                    return;
                }
                String cityname = data.getString(data.getColumnIndex(GuideContract.GuideEntry.COLUMN_CITY_NAME));
                mCityNameTextView.setText(cityname);
                if (data.getString(data.getColumnIndex(GuideContract.GuideEntry.COLUMN_UPDATE_TAG)).equals("0")){
                    Log.d(TAG,"网络加载");
                    UpdateCityInfo updateCityInfo = new UpdateCityInfo();
                    updateCityInfo.execute(cityname);
                }else {
                    mProgressBar.setVisibility(View.GONE);
                    mGuideList.setVisibility(View.VISIBLE);
                    mCityDescTextView.setText(data.getString(data.getColumnIndex(GuideContract.GuideEntry.COLUMN_CITY_INFO)));
                    String image_link = "http://gss0.baidu.com/7LsWdDW5_xN3otqbppnN2DJv/lvpics/pic/item/"+
                            data.getString(data.getColumnIndex(GuideContract.GuideEntry.COLUMN_CITY_IMAGES));
                    Picasso.with(getContext()).load(image_link).into(mCityImageView);
                    //loader article
                    Log.d(TAG,"本地加载");
                    Uri uri = ArticleContract.ArticleEntry.CONTENT_URI;
                    String selection = ArticleContract.ArticleEntry.COLUMN_ARTICLE_CITY+ "= ?";
                    String[] selectionArgs = new String[]{cityname};
                    Cursor cursor = getActivity().getContentResolver().query(uri,MAIN_ARTICLE_PROJECTION,
                            selection,selectionArgs,null);
                    if (cursor!=null&&cursor.getCount()>0){
                        mGuideListAdapter.swapData(cursor);
                    }
                }

            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {

                mGuideListAdapter.swapData(null);
            }
        };

    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG,"地图加载onMapReady");

        boolean success = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(getContext(),R.raw.map_style)
        );

        if (!success){
            Log.e(TAG, "地图样式加载失败Style parsing failed.");
        }

        map = googleMap;
        MapGeocodingTask geocodingTask = new MapGeocodingTask();
        geocodingTask.execute(GuidePerference.getCityName(getContext()));


    }

    //更新城市
    private class UpdateCityInfo extends AsyncTask<String,Void,Cursor>{

        String cityname;

        @Override
        protected Cursor doInBackground(String... strings) {
            cityname = strings[0];
            return GuideSyncUtils.updateCitySync(getContext(),cityname);
        }
        @Override
        protected void onPostExecute(Cursor data) {
            super.onPostExecute(data);
            data.moveToFirst();
            mProgressBar.setVisibility(View.GONE);
            mGuideList.setVisibility(View.VISIBLE);
            mCityDescTextView.setText(data.getString(data.getColumnIndex(GuideContract.GuideEntry.COLUMN_CITY_INFO)));
            String image_link = "http://gss0.baidu.com/7LsWdDW5_xN3otqbppnN2DJv/lvpics/pic/item/"+
                    data.getString(data.getColumnIndex(GuideContract.GuideEntry.COLUMN_CITY_IMAGES));
            Picasso.with(getContext()).load(image_link).into(mCityImageView);

            Uri uri = ArticleContract.ArticleEntry.CONTENT_URI;
            String selection = ArticleContract.ArticleEntry.COLUMN_ARTICLE_CITY+ "= ?";
            String[] selectionArgs = new String[]{cityname};
            Cursor cursor = getActivity().getContentResolver().query(uri,MAIN_ARTICLE_PROJECTION,
                    selection,selectionArgs,null);
            if (cursor!=null&&cursor.getCount()>0){
                mGuideListAdapter.swapData(cursor);
            }

        }

    }



    //地理位置逆向计算
    private class MapGeocodingTask extends AsyncTask<String,Void,LatLng>{

        @Override
        protected LatLng doInBackground(String... strings) {

            String address = strings[0];
            Log.d(TAG,"Map定位 当前选择城市："+address);
            return NetUnit.getCityLocationFromGoogleMap(address);
        }

        @Override
        protected void onPostExecute(LatLng latLng) {
            super.onPostExecute(latLng);
            if (latLng!=null){
                Log.d(TAG,"Map定位 MapGeocodingTask"+latLng.toString());
            }
            cityLatlng = latLng;
            if (cityLatlng!=null) {
                map.addMarker(new MarkerOptions().position(cityLatlng));
                map.moveCamera(CameraUpdateFactory.newLatLng(cityLatlng));
            }
        }
    }


    public void upDateGuide(){
        Log.d(TAG,"刷新 upDateGuide");

            Bundle bundle = new Bundle();
            bundle.putString("city-name",GuidePerference.getCityName(getContext()));
            callbacks = getCallbacks(getContext());
            getActivity().getSupportLoaderManager().restartLoader(
                    ID_GUIDE_LOADER,bundle,callbacks
            );

    }

}
