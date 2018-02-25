package ink.moming.travelnote.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import ink.moming.travelnote.LoginActivity;
import ink.moming.travelnote.R;
import ink.moming.travelnote.data.GuidePerference;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyFragment extends Fragment {

    private TextView mUserLoginTextView;
    private TextView mUserNameTextView;
    private TextView mLoginOutTextView;
    private TextView mUserColTextView;
    private ImageView mUserImageView;


    public MyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view =inflater.inflate(R.layout.fragment_my, container, false);

        mUserNameTextView = view.findViewById(R.id.user_name);
        mUserColTextView = view.findViewById(R.id.user_col);
        mUserLoginTextView = view.findViewById(R.id.login_text);
        mLoginOutTextView = view.findViewById(R.id.login_out);

        mUserLoginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),LoginActivity.class);
                startActivity(intent);
            }
        });

        if (GuidePerference.getUserStatus(getContext())){
            mUserLoginTextView.setVisibility(View.GONE);
            //mUserLoginTextView.setText();
        }



        return view;
    }

}
