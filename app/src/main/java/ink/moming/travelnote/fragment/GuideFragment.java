package ink.moming.travelnote.fragment;

import android.content.ContentValues;
import android.content.Context;
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
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import ink.moming.travelnote.R;
import ink.moming.travelnote.adapter.GuideListAdapter;
import ink.moming.travelnote.data.GuideContract;
import ink.moming.travelnote.data.GuidePerference;
import ink.moming.travelnote.sync.GuideSyncTask;
import ink.moming.travelnote.sync.GuideSyncUtils;


public class GuideFragment extends Fragment {
    //http://gss0.baidu.com/7LsWdDW5_xN3otqbppnN2DJv/lvpics/pic/item/
    //http://hiphotos.baidu.com/lvpics/pic/item/574e9258d109b3de92f45cfbc7bf6c81810a4cdd.jpg


    //UI控件
    private RecyclerView mGuideList;
    private TextView mCityDescTextView;
    private TextView mCityNameTextView;
    private TextView mNoDataTextView;
    private ImageView mCityImageView;
    private GuideListAdapter mGuideListAdapter;



    //常量
    public static final String TAG = GuideFragment.class.getSimpleName();
    public static final int ID_GUIDE_LOADER = 32;
    public static final String CITY_TAG = "city_tag";

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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_guide, container, false);

        initUI(view);


        callbacks = getCallbacks(getContext());

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        cityBundle = new Bundle();
        cityBundle.putString("city-name",GuidePerference.getCityName(getContext()));
        getActivity().getSupportLoaderManager().initLoader(ID_GUIDE_LOADER,cityBundle,callbacks);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        GuideSyncUtils.initialize(getContext());
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    //个人方法

    private void initUI(View view){
        mGuideList = view.findViewById(R.id.guide_list);
        mCityNameTextView = view.findViewById(R.id.city_name);
        mCityDescTextView = view.findViewById(R.id.city_desc);
        mCityImageView = view.findViewById(R.id.city_img);
        mNoDataTextView = view.findViewById(R.id.no_article);


        LinearLayoutManager layoutManager = new
                LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        mGuideList.setLayoutManager(layoutManager);

        mGuideListAdapter = new GuideListAdapter(getContext());
        mGuideList.setAdapter(mGuideListAdapter);
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

        return new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                switch (id){
                    case ID_GUIDE_LOADER:
                        //显示加载...
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

                if (data!=null){

                    if (data.getCount()!=0){
                        //展示数据
                        data.moveToFirst();
                        String cityname = data.getString(data.getColumnIndex(GuideContract.GuideEntry.COLUMN_CITY_NAME));
                        mCityNameTextView.setText(cityname);

                        if (data.getString(data.getColumnIndex(GuideContract.GuideEntry.COLUMN_UPDATE_TAG)).equals("1")){
                            GuideListAdapter.ArticleBean[] articleBeans = null;
                            try {
                                articleBeans = getArticleFromJson(
                                        data.getString(data.getColumnIndex(GuideContract.GuideEntry.COLUMN_CITY_ARTICLES)));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (articleBeans!=null){

                                mNoDataTextView.setVisibility(View.GONE);
                                mGuideListAdapter.swapData(articleBeans);
                            }else {
                                //list 无数据
                                mNoDataTextView.setVisibility(View.VISIBLE);
                                mGuideList.setVisibility(View.INVISIBLE);
                            }
                            mCityDescTextView.setText(data.getString(data.getColumnIndex(GuideContract.GuideEntry.COLUMN_CITY_INFO)));
                            String image_link = "http://gss0.baidu.com/7LsWdDW5_xN3otqbppnN2DJv/lvpics/pic/item/"+
                                    data.getString(data.getColumnIndex(GuideContract.GuideEntry.COLUMN_CITY_IMAGES));
                            Picasso.with(getContext()).load(image_link).into(mCityImageView);

                        }else {
                            //更新数据
                            Log.d(TAG,cityname);
                            UpdateCityInfo task = new UpdateCityInfo();

                            task.execute(cityname);



                        }


                    }else if (data.getCount()==0){
                        //无数据展示
                        showNoData();
                    }
                }else {
                    //无数据展示
                    showNoData();

                }

            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {

                mGuideListAdapter.swapData(null);
            }
        };

    }

    private void showNoData(){
        mGuideList.setVisibility(View.INVISIBLE);
        mCityDescTextView.setText("暂无信息");
        mCityNameTextView.setText("暂无信息");
        mNoDataTextView.setVisibility(View.VISIBLE);

    }

    private class UpdateCityInfo extends AsyncTask<String,Void,ContentValues>{


        @Override
        protected ContentValues doInBackground(String... strings) {
            String cityName = strings[0];
            return GuideSyncUtils.updateCitySync(getContext(),cityName);
        }
        @Override
        protected void onPostExecute(ContentValues contentValues) {
            super.onPostExecute(contentValues);
            if (contentValues!=null){
                GuideListAdapter.ArticleBean[] beans = null;
                try {
                    beans =getArticleFromJson(contentValues.getAsString("articles"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (beans!=null){
                    mNoDataTextView.setVisibility(View.GONE);
                    mGuideListAdapter.swapData(beans);
                }else {
                    //list 无数据
                    mNoDataTextView.setVisibility(View.VISIBLE);
                    mGuideList.setVisibility(View.INVISIBLE);
                }
                mCityDescTextView.setText(contentValues.getAsString("info"));
                String image_link = "http://gss0.baidu.com/7LsWdDW5_xN3otqbppnN2DJv/lvpics/pic/item/"+
                        contentValues.getAsString("image");
                Picasso.with(getContext()).load(image_link).into(mCityImageView);

            }else {
                //无数据
                mGuideList.setVisibility(View.INVISIBLE);
                mCityDescTextView.setText("暂无信息");
                mNoDataTextView.setVisibility(View.VISIBLE);
            }
        }

    }



}
