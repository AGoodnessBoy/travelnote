package ink.moming.travelnote.fragment;

import android.content.ContentValues;
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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import ink.moming.travelnote.CityListActivity;
import ink.moming.travelnote.R;
import ink.moming.travelnote.adapter.GuideListAdapter;
import ink.moming.travelnote.data.GuideContract;
import ink.moming.travelnote.data.GuidePerference;
import ink.moming.travelnote.sync.GuideSyncTask;
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
            Log.d(TAG,savedInstanceState.getBundle(CITY_STATUS).getString("city-name"));
            cityBundle =savedInstanceState.getBundle(CITY_STATUS);
            getActivity().getSupportLoaderManager().restartLoader(ID_GUIDE_LOADER,cityBundle,callbacks);
        }
        mCityMap.getMapAsync(this);

    }

    @Override
    public void onStart() {
        super.onStart();
        mCityMap.onStart();
        Log.d(TAG,"onStart");

    }

    @Override
    public void onStop() {
        super.onStop();
        mCityMap.onStop();
        Log.d(TAG,"onStop");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG,"onAttach");
        //同步基础数据
        GuideSyncUtils.initialize(getContext());
        Log.d(TAG,"initialize");

    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG,"onDetach");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCityMap.onDestroy();
        Log.d(TAG,"onDestroy");
    }

    @Override
    public void onResume() {
        super.onResume();
        mCityMap.onResume();
        Log.d(TAG,"onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG,"onPause");
        mCityMap.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG,"onSaveInstanceState");

        if (cityBundle!=null){

            outState.putBundle(CITY_STATUS,cityBundle);
            Log.d(TAG,outState.getBundle(CITY_STATUS).getString("city-name"));
        }
    }

    //初始化UI

    private void initUI(View view){
        Log.d(TAG,"initUI");

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
                startActivity(intent);
            }
        });



    }

    private GuideListAdapter.ArticleBean[] getArticleFromJson(String s) throws JSONException {

        GuideListAdapter.ArticleBean[] articleBeans =null;
        JSONArray array = new JSONArray(s);
        if (array.length()>0){
            articleBeans = new GuideListAdapter.ArticleBean[array.length()];
            for (int i = 0;i<array.length();i++){
                GuideListAdapter.ArticleBean item = new GuideListAdapter.ArticleBean();
                item.setA_id(array.getJSONObject(i).getString("nid"));
                item.setA_image(array.getJSONObject(i)
                        .getJSONArray("album_pic_list")
                        .getJSONObject(0)
                        .getString("pic_url"));
                item.setA_title(array.getJSONObject(i).getString("title"));
                articleBeans[i] = item;
            }
            return articleBeans;
        }else {
            return null;
        }
    }

    private LoaderManager.LoaderCallbacks<Cursor> getCallbacks(final Context context){
        Log.d(TAG,"getCallbacks");
        return new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                switch (id){
                    case ID_GUIDE_LOADER:
                        //显示加载...
                        mProgressBar.setVisibility(View.VISIBLE);
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
                UpdateCityInfo updateCityInfo = new UpdateCityInfo();
                updateCityInfo.execute(cityname);

            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {

                mGuideListAdapter.swapData(null);
            }
        };

    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG,"onMapReady");

        boolean success = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(getContext(),R.raw.map_style)
        );

        if (!success){
            Log.e(TAG, "Style parsing failed.");
        }

        map = googleMap;
        MapGeocodingTask geocodingTask = new MapGeocodingTask();
        geocodingTask.execute(GuidePerference.getCityName(getContext()));





    }

    //更新城市
    private class UpdateCityInfo extends AsyncTask<String,Void,Cursor>{

        @Override
        protected Cursor doInBackground(String... strings) {
            String cityName = strings[0];
            return GuideSyncUtils.updateCitySync(getContext(),cityName);
        }
        @Override
        protected void onPostExecute(Cursor data) {
            super.onPostExecute(data);
            data.moveToFirst();
            mCityDescTextView.setText(data.getString(data.getColumnIndex(GuideContract.GuideEntry.COLUMN_CITY_INFO)));
            String image_link = "http://gss0.baidu.com/7LsWdDW5_xN3otqbppnN2DJv/lvpics/pic/item/"+
                    data.getString(data.getColumnIndex(GuideContract.GuideEntry.COLUMN_CITY_IMAGES));
            Picasso.with(getContext()).load(image_link).into(mCityImageView);
            GuideListAdapter.ArticleBean[] articleBeans = null;
            try {
                articleBeans = getArticleFromJson(
                        data.getString(data.getColumnIndex(GuideContract.GuideEntry.COLUMN_CITY_ARTICLES)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (articleBeans!=null){

                mNoDataTextView.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.GONE);
                mGuideListAdapter.swapData(articleBeans);
                Log.d(TAG,"article swapData");
            }else {
                //list 无数据
                mNoDataTextView.setVisibility(View.VISIBLE);
                mGuideList.setVisibility(View.INVISIBLE);
                mProgressBar.setVisibility(View.GONE);
            }

        }

    }



    //地理位置逆向计算
    private class MapGeocodingTask extends AsyncTask<String,Void,LatLng>{

        @Override
        protected LatLng doInBackground(String... strings) {

            String address = strings[0];
            Log.d(TAG,address);
            return NetUnit.getCityLocationFromGoogleMap(address);
        }

        @Override
        protected void onPostExecute(LatLng latLng) {
            super.onPostExecute(latLng);
            if (latLng!=null){
                Log.d(TAG,"MapGeocodingTask"+latLng.toString());
            }
            cityLatlng = latLng;
            if (cityLatlng!=null) {
                map.addMarker(new MarkerOptions().position(cityLatlng));
                map.moveCamera(CameraUpdateFactory.newLatLng(cityLatlng));
            }
        }
    }



    //无数据
    private void showNoData(){
        mGuideList.setVisibility(View.INVISIBLE);
        mCityDescTextView.setText("暂无信息");
        mCityNameTextView.setText("暂无信息");
        mNoDataTextView.setVisibility(View.VISIBLE);

    }


    public void upDateGuide(){
        Log.d(TAG,"upDateGuide");

            Bundle bundle = new Bundle();
            bundle.putString("city-name",GuidePerference.getCityName(getContext()));
            callbacks = getCallbacks(getContext());
            getActivity().getSupportLoaderManager().restartLoader(
                    ID_GUIDE_LOADER,bundle,callbacks
            );

    }

}
