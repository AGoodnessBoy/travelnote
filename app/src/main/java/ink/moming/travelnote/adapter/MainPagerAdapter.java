package ink.moming.travelnote.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ink.moming.travelnote.data.GuidePerference;
import ink.moming.travelnote.fragment.GuideFragment;
import ink.moming.travelnote.fragment.MyFragment;
import ink.moming.travelnote.fragment.NoteFragment;

import static ink.moming.travelnote.fragment.GuideFragment.ID_GUIDE_LOADER;

/**
 * Created by Moming-Desgin on 2018/2/12.
 */

public class MainPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragments = new ArrayList<>();
    private Context mContext;

    public MainPagerAdapter(FragmentManager fm,Context context) {
        super(fm);
        mContext = context;
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

    @Override
    public int getItemPosition(Object object) {

        if (object instanceof GuideFragment){
            GuideFragment gf = (GuideFragment)object;
            gf.upDateGuide();
            notifyDataSetChanged();
        }


        return super.getItemPosition(object);
    }

}
