package ink.moming.travelnote;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
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
import ink.moming.travelnote.fragment.GuideFragment;
import ink.moming.travelnote.fragment.MyFragment;
import ink.moming.travelnote.fragment.NoteFragment;

public class MainActivity extends AppCompatActivity {

    private TabLayout mTabs;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViewPager();
        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();
                HtmlAnalysis test = new  HtmlAnalysis();
                String o=null;
                String tet = test.htmlTest("https://lvyou.baidu.com/yunnan/");
                Log.d("tag",tet);
                try {
                    String tex2 = test.getArticleFromAjax("杭州");
                    Log.d("tag",tex2);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //JSONArray jsonArray =test.getCityList();
                JSONObject object =test.getCityGuide("https://lvyou.baidu.com/yunnan/");
                FileOutputStream out;
                BufferedWriter writer = null;
                try{
                    out = openFileOutput("data", Context.MODE_PRIVATE);
                    writer = new BufferedWriter(new OutputStreamWriter(out));
                    writer.write(object.toString());
                }catch (IOException e){
                    e.printStackTrace();
                }finally {
                    try {
                        writer.close();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }
        };
        thread.start();


    }

    //初始化 ViewPager TabLayout
    private void initViewPager(){
        MainPagerAdapter mainPagerAdapter = new MainPagerAdapter(getSupportFragmentManager());

        mTabs = findViewById(R.id.main_tabs);
        mViewPager = findViewById(R.id.main_view_pager);

        List<String> titles = new ArrayList<>();
        titles.add(getString(R.string.guide));
        titles.add(getString(R.string.note));
        titles.add(getString(R.string.my));



        mViewPager.setAdapter(mainPagerAdapter);
        mTabs.setupWithViewPager(mViewPager);

        for (int i = 0 ;i<titles.size();i++){
            mTabs.getTabAt(i).setText(titles.get(i));
        }




    }


}
