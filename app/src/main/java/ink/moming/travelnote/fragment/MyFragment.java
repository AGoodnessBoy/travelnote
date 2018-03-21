package ink.moming.travelnote.fragment;


import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import ink.moming.travelnote.LoginActivity;
import ink.moming.travelnote.R;
import ink.moming.travelnote.data.GuidePerference;
import ink.moming.travelnote.data.NoteContract;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyFragment extends Fragment {

    public static final int USER_RES = 41;

    public static final String TAG = MyFragment.class.getSimpleName();

    private Unbinder unbinder;

    private TextView mUserLoginTextView;
    private TextView mUserNameTextView;
    private TextView mLoginOutTextView;

    private String mUsername;




    public MyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        final View view = inflater.inflate(R.layout.fragment_my, container, false);
        unbinder = ButterKnife.bind(this,view);
        mUserNameTextView = view.findViewById(R.id.user_name);
        mUserLoginTextView = view.findViewById(R.id.login_text);
        mLoginOutTextView = view.findViewById(R.id.login_out);

        mUserLoginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivityForResult(intent, USER_RES);
            }
        });
        mLoginOutTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (GuidePerference.getUserStatus(getContext())) {
                    AlertDialog dialog = new AlertDialog.Builder(getContext())
                            .setTitle(R.string.login_out_confirm_title_str)
                            .setNegativeButton(getString(R.string.no_str), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton(getString(R.string.yes_str), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    mUsername = null;
                                    mUserNameTextView.setVisibility(View.GONE);
                                    mUserLoginTextView.setVisibility(View.VISIBLE);
                                    GuidePerference.clearUserStatus(getContext());
                                    ContentResolver resolver = getActivity().getContentResolver();
                                    resolver.delete(NoteContract.NoteEntry.CONTENT_URI, null, null);
                                    showSnackbar(view, getString(R.string.login_out_str), Snackbar.LENGTH_SHORT);
                                    dialog.dismiss();
                                }
                            })

                            .create();
                    dialog.show();
                } else {
                    showSnackbar(view, getString(R.string.not_login_yet_str), Snackbar.LENGTH_SHORT);
                }
            }
        });


        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mUserNameTextView.setVisibility(View.VISIBLE);
            mUserNameTextView.setText(savedInstanceState.getString("user_name_save"));
            mUserLoginTextView.setVisibility(View.GONE);
        }






    }

    public void showSnackbar(View view, String message, int duration) {
        Snackbar.make(view, message, duration).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == USER_RES && resultCode == 42) {
            String mEmail = data.getStringExtra("useremail");
            String name = data.getStringExtra("username");
            int id = data.getIntExtra("userid", 0);
            GuidePerference.saveUserStatus(getContext(), mEmail, name, id);
            mUsername = name;
            mUserNameTextView.setVisibility(View.VISIBLE);
            mUserNameTextView.setText(name);
            mUserLoginTextView.setVisibility(View.GONE);
        }


    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mUsername != null) {
            outState.putString("user_name_save", mUsername);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (GuidePerference.getUserStatus(getContext())) {
            mUserNameTextView.setVisibility(View.VISIBLE);
            mUserNameTextView.setText(GuidePerference.getUserName(getContext()));
            mUserLoginTextView.setVisibility(View.GONE);
        } else {
            mUserNameTextView.setVisibility(View.GONE);
            mUserLoginTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
