package ink.moming.travelnote;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import ink.moming.htmlanalysislib.HtmlAnalysis;
import ink.moming.travelnote.adapter.MainPagerAdapter;
import ink.moming.travelnote.data.GuidePerference;
import ink.moming.travelnote.fragment.GuideFragment;
import ink.moming.travelnote.fragment.MyFragment;
import ink.moming.travelnote.fragment.NoteFragment;

public class MainActivity extends AppCompatActivity {
    public static final String TAG  = MainActivity.class.getSimpleName();

    private TabLayout mTabs;
    private ViewPager mViewPager;
    public  MainPagerAdapter mainPagerAdapter;
    public static final int ID_GUIDE_LOADER = 32;

    List<String> titles = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViewPager();

        mViewPager.setAdapter(mainPagerAdapter);
        mTabs.setupWithViewPager(mViewPager);

        for (int i = 0 ;i<titles.size();i++){
            mTabs.getTabAt(i).setText(titles.get(i));
        }



    }

    //初始化 ViewPager TabLayout
    private void initViewPager(){
        mainPagerAdapter = new MainPagerAdapter(getSupportFragmentManager(),this);

        mTabs = findViewById(R.id.main_tabs);
        mViewPager = findViewById(R.id.main_view_pager);
        titles.add(getString(R.string.guide));
        titles.add(getString(R.string.note));
        titles.add(getString(R.string.my));

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
    }

    public  void updataGuideList(){
        GuideFragment guideFragment = (GuideFragment)mainPagerAdapter.getItem(0);
        mainPagerAdapter.getItemPosition(guideFragment);
    }


}
