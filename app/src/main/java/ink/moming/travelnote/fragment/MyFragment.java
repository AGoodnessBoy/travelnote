package ink.moming.travelnote.fragment;


import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.DecimalFormat;

import ink.moming.travelnote.LoginActivity;
import ink.moming.travelnote.R;
import ink.moming.travelnote.data.GuidePerference;
import ink.moming.travelnote.data.NoteContract;
import ink.moming.travelnote.unit.NetUnit;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final int USER_RES = 41;

    public static final String TAG = MyFragment.class.getSimpleName();

    private TextView mUserLoginTextView;
    private TextView mUserNameTextView;
    private TextView mLoginOutTextView;

    private String mUsername;


    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;


    private final int FINE_PERMISSION_REQUEST = 544;


    public MyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        final View view = inflater.inflate(R.layout.fragment_my, container, false);

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
                            .setTitle("是否退出登录")
                            .setNegativeButton("否", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    mUsername = null;
                                    mUserNameTextView.setVisibility(View.GONE);
                                    mUserLoginTextView.setVisibility(View.VISIBLE);
                                    GuidePerference.clearUserStatus(getContext());
                                    ContentResolver resolver = getActivity().getContentResolver();
                                    resolver.delete(NoteContract.NoteEntry.CONTENT_URI, null, null);
                                    showSnackbar(view, "退出登录", Snackbar.LENGTH_SHORT);
                                    dialog.dismiss();
                                }
                            })

                            .create();
                    dialog.show();
                } else {
                    showSnackbar(view, "你还没有登录！", Snackbar.LENGTH_SHORT);
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
            Log.d(TAG, "My 状态保存onSaveInstanceState " + savedInstanceState.getString("user_name_save"));
            mUserNameTextView.setText(savedInstanceState.getString("user_name_save"));
            mUserLoginTextView.setVisibility(View.GONE);
        }

        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());




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
            Log.d(TAG, "My 状态保存输出 onSaveInstanceState: " + mUsername);
            outState.putString("user_name_save", mUsername);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (GuidePerference.getUserStatus(getContext())) {
            Log.d(TAG, "onResume:登录");
            mUserNameTextView.setVisibility(View.VISIBLE);
            mUserNameTextView.setText(GuidePerference.getUserName(getContext()));
            mUserLoginTextView.setVisibility(View.GONE);
        } else {
            Log.d(TAG, "onResume:未登录");
            mUserNameTextView.setVisibility(View.GONE);
            mUserLoginTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(10000);

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_REQUEST);

            return;
        }


        mFusedLocationClient
                .requestLocationUpdates(mLocationRequest, mLocationCallback,null);

        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                     if (location!=null){
                         Log.d("loaction",Double.toString(location.getLatitude()));
                         LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());

                     }else {

                     }
                    }
                });



    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private class WeatherTask extends AsyncTask<LatLng,Void,String>{

        @Override
        protected String doInBackground(LatLng... latLngs) {

            LatLng loaction = latLngs[0];

            String lan = new DecimalFormat("0.00").format(loaction.latitude);
            String lng = new DecimalFormat("0.00").format(loaction.longitude);


            return  NetUnit.getWeatherFromHe(lan+" , "+lng);
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s!=null){

            }
        }
    }
}
