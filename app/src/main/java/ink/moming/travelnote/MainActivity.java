package ink.moming.travelnote;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import ink.moming.travelnote.adapter.MainPagerAdapter;

public class MainActivity extends AppCompatActivity {
    public static final String TAG  = MainActivity.class.getSimpleName();


    TabLayout mTabs;

    ViewPager mViewPager;

    public  MainPagerAdapter mainPagerAdapter;

    List<String> titles = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTabs = findViewById(R.id.main_tabs);
        mViewPager = findViewById(R.id.main_view_pager);
        mainPagerAdapter = new MainPagerAdapter(getSupportFragmentManager(),this);
        titles.add(getString(R.string.guide));
        titles.add(getString(R.string.note));
        titles.add(getString(R.string.my));
        mViewPager.setAdapter(mainPagerAdapter);
        mTabs.setupWithViewPager(mViewPager);

        for (int i = 0 ;i<titles.size();i++){
            mTabs.getTabAt(i).setText(titles.get(i));
        }



    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
