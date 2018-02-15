package ink.moming.travelnote.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ink.moming.travelnote.R;


public class GuideFragment extends Fragment {
    //http://gss0.baidu.com/7LsWdDW5_xN3otqbppnN2DJv/lvpics/pic/item/
    //http://hiphotos.baidu.com/lvpics/pic/item/574e9258d109b3de92f45cfbc7bf6c81810a4cdd.jpg

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
        return inflater.inflate(R.layout.fragment_guide, container, false);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
