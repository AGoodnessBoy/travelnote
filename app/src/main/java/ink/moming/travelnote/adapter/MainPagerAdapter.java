package ink.moming.travelnote.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import ink.moming.travelnote.fragment.GuideFragment;
import ink.moming.travelnote.fragment.MyFragment;
import ink.moming.travelnote.fragment.NoteFragment;

/**
 * Created by Moming-Desgin on 2018/2/12.
 */

public class MainPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragments = new ArrayList<>();

    public MainPagerAdapter(FragmentManager fm) {
        super(fm);

        fragments.add(new GuideFragment());
        fragments.add(new NoteFragment());
        fragments.add(new MyFragment());
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
