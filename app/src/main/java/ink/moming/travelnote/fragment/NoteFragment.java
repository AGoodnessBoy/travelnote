package ink.moming.travelnote.fragment;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import ink.moming.travelnote.LoginActivity;
import ink.moming.travelnote.NoteUploadActivity;
import ink.moming.travelnote.R;
import ink.moming.travelnote.data.GuidePerference;
import ink.moming.travelnote.data.NoteContract;
import ink.moming.travelnote.sync.GuideSyncUtils;
import ink.moming.travelnote.unit.NetUnit;

/**
 * A simple {@link Fragment} subclass.
 */
public class NoteFragment extends Fragment {

    private Unbinder unbinder;

    private RecyclerView mNoteList;
    private FloatingActionButton mAddBtn;
    private TextView mNoData;
    private ProgressBar mPb;
    private SwipeRefreshLayout mNoteRefreshLayout;

    public static final int USER_RES_NOTE = 44;
    public static final int UP_RES_NOTE = 45;
    public static final int USER_NOTE_LOAD = 28;
    public static final String TAG = NoteFragment.class.getSimpleName();


    public LoaderManager.LoaderCallbacks<Cursor> callbacks;

    public static final String[] MAIN_NOTE_PROJECTION ={
            NoteContract.NoteEntry.COLUMN_NOTE_ID,
            NoteContract.NoteEntry.COLUMN_NOTE_TEXT,
            NoteContract.NoteEntry.COLUMN_NOTE_IMAGE,
            NoteContract.NoteEntry.COLUMN_NOTE_TIME,
            NoteContract.NoteEntry.COLUMN_NOTE_USER,
    };



    private NoteAdapter adapter;


    public NoteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_note, container, false);

        unbinder = ButterKnife.bind(this,view);
        mNoteList = view.findViewById(R.id.note_list);
        mAddBtn = view.findViewById(R.id.fab_note);
        mNoData = view.findViewById(R.id.no_note);
        mPb = view.findViewById(R.id.note_pb);
        mNoteRefreshLayout = view.findViewById(R.id.note_frash);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());



        adapter = new NoteAdapter();
        mNoteList.setAdapter(adapter);
        mNoteList.setLayoutManager(linearLayoutManager);

        mAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (GuidePerference.getUserStatus(getContext())){
                    Intent intent = new Intent(getActivity(), NoteUploadActivity.class);
                    startActivityForResult(intent,UP_RES_NOTE);
                }else {
                    Toast.makeText(getContext(),"请先登录",Toast.LENGTH_SHORT);
                    Intent loginintent = new Intent(getActivity(), LoginActivity.class);
                    startActivityForResult(loginintent,USER_RES_NOTE);
                }
            }
        });

        callbacks=getCallbacks(getContext());

        mNoteRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (GuidePerference.getUserStatus(getContext())){
                    Bundle bundle = new Bundle();
                    bundle.putInt("userid",GuidePerference.getUserId(getContext()));
                    NoteFlashTask task = new NoteFlashTask(bundle);
                    task.execute();
                }else {
                    showSnackbar(mNoteRefreshLayout,"暂未登录",800);
                    mNoteRefreshLayout.setRefreshing(false);
                }

            }
        });





        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == USER_RES_NOTE && resultCode == 42){
            String mEmail = data.getStringExtra("useremail");
            String name = data.getStringExtra("username");
            int id = data.getIntExtra("userid",0);
            Log.d(TAG,"Note 返回 用户登录");
            GuidePerference.saveUserStatus(getContext(),mEmail,name,id);
            Bundle idBundle = new Bundle();
            idBundle.putInt("userid",GuidePerference.getUserId(getContext()));
            //刷新数据
            NoteFlashTask task = new NoteFlashTask(idBundle);
            task.execute();

        }

        if (requestCode == UP_RES_NOTE && resultCode == 80){
            boolean status = data.getBooleanExtra("status",false);
            Log.d(TAG,"Note 返回 用户上传");

            if (status){
                Log.d(TAG," Note request TRUE");
                Bundle idBundle = new Bundle();
                idBundle.putInt("userid",GuidePerference.getUserId(getContext()));
                NoteFlashTask task = new NoteFlashTask(idBundle);
                task.execute();

            }else {
                Log.d(TAG," Note request FALUSE");
            }


        }
    }


    public void showSnackbar(View view, String message, int duration)
    {
        Snackbar.make(view, message, duration).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!GuidePerference.getUserStatus(getContext())){
            Log.d(TAG,"Note onResume no login");
            mPb.setVisibility(View.GONE);
            mNoData.setVisibility(View.VISIBLE);
            mNoData.setText("登录后查看");
            mNoteList.setVisibility(View.GONE);
        }else {
            int userid = GuidePerference.getUserId(getContext());
            Bundle bundle = new Bundle();
            bundle.putInt("userid",userid);
            Log.d(TAG,"Note onResume "+userid);
            GuideSyncUtils.flashNoteList(getContext());
            getActivity().getSupportLoaderManager().initLoader(USER_NOTE_LOAD,bundle,callbacks);
        }
    }

    private class NoteFlashTask extends AsyncTask<Void,Void,Boolean>{
        //下拉刷新
        //上传后刷新 AcitivityResult;
        //登录刷新

        private Bundle mBundle;

        public NoteFlashTask(Bundle bundle){
            mBundle = bundle;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            mNoteRefreshLayout.setRefreshing(true);
            String update = NetUnit.getNoteList(GuidePerference.getUserId(getContext()),getContext());
            try {
                JSONObject data =new JSONObject(update);
                if (data.getString("status").equals("400")){
                    JSONArray noteArr = data.getJSONArray("data");
                    ContentValues[] contentValues = new ContentValues[noteArr.length()];
                    for (int i=0;i<noteArr.length();i++){
                        ContentValues cv = new ContentValues();
                        cv.put(NoteContract.NoteEntry.COLUMN_NOTE_ID,noteArr.getJSONObject(i).getInt("note_id"));
                        cv.put(NoteContract.NoteEntry.COLUMN_NOTE_TEXT,noteArr.getJSONObject(i).getString("note_text"));
                        cv.put(NoteContract.NoteEntry.COLUMN_NOTE_IMAGE,noteArr.getJSONObject(i).getString("note_img"));
                        cv.put(NoteContract.NoteEntry.COLUMN_NOTE_TIME,noteArr.getJSONObject(i).getString("note_time"));
                        cv.put(NoteContract.NoteEntry.COLUMN_NOTE_USER,noteArr.getJSONObject(i).getInt("note_user"));
                        contentValues[i]=cv;
                    }
                    ContentResolver noteresolver = getContext().getContentResolver();
                    noteresolver.delete(NoteContract.NoteEntry.CONTENT_URI,null,null);
                    int status = noteresolver.bulkInsert(NoteContract.NoteEntry.CONTENT_URI,contentValues);

                    if (status!=0){
                        Log.d(TAG,getContext().getString(R.string.note_update_success_str));
                        return true;
                    }else {
                        Log.d(TAG,getContext().getString(R.string.note_update_failed_str));
                        return false;

                    }

                }else {
                    return false;
                }

            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }

        }


        @Override
        protected void onPostExecute(Boolean b) {
            super.onPostExecute(b);
            mNoteRefreshLayout.setRefreshing(false);

            if (b){
                getActivity().getSupportLoaderManager().restartLoader(USER_NOTE_LOAD,mBundle,callbacks);
            }

        }
    }


    private LoaderManager.LoaderCallbacks<Cursor> getCallbacks(final Context context){
        return new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                switch (id){
                    case USER_NOTE_LOAD:
                        //显示加载...
                        mPb.setVisibility(View.VISIBLE);
                        int userid = args.getInt("userid");
                        Uri uri = NoteContract.NoteEntry.CONTENT_URI;
                        String selection = NoteContract.NoteEntry.COLUMN_NOTE_USER+ "= ?";
                        String[] selectionArgs = new String[]{Integer.toString(userid)};
                        String sortOrder = NoteContract.NoteEntry.COLUMN_NOTE_TIME+" DESC";
                        return new CursorLoader(context,uri,MAIN_NOTE_PROJECTION,selection,selectionArgs,sortOrder);

                    default:
                        throw new RuntimeException("Loader Not Implemented: " + id);
                }
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                boolean cursorHasValidData = false;
                mPb.setVisibility(View.GONE);
                if (data!=null && data.moveToFirst()){
                    cursorHasValidData = true;
                }
                if (!cursorHasValidData){
                    mNoData.setVisibility(View.VISIBLE);
                    mNoData.setText(R.string.not_find_note_str);
                    mNoteList.setVisibility(View.GONE);
                    return;
                }
                showSnackbar(mNoteRefreshLayout,getString(R.string.data_input_success_str),800);
                mNoteList.setVisibility(View.VISIBLE);
                mNoData.setVisibility(View.GONE);
                adapter.swapData(data);


            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {

                adapter.swapData(null);
            }
        };

    }



    public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder>{

        private Cursor mNoteData;


        @Override
        public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            int layout = R.layout.note_list_item;
            LayoutInflater inflater = LayoutInflater.from(getContext());
            final View view = inflater.inflate(layout,parent,false);

            return new NoteViewHolder(view);
        }

        @Override
        public void onBindViewHolder(NoteViewHolder holder, int position) {
            mNoteData.moveToPosition(position);

            String text = mNoteData.getString(mNoteData.getColumnIndex(NoteContract.NoteEntry.COLUMN_NOTE_TEXT));
            if (text.equals("")){
                holder.notetext.setVisibility(View.GONE);
            }else {
                holder.notetext.setVisibility(View.VISIBLE);
                holder.notetext.setText(text);
            }

            String imagebase =  mNoteData.getString(mNoteData.getColumnIndex(NoteContract.NoteEntry.COLUMN_NOTE_IMAGE));
            String imagePath = "http://api.moming.ink/images/"+imagebase+".jpg";

            String time = mNoteData.getString(mNoteData.getColumnIndex(NoteContract.NoteEntry.COLUMN_NOTE_TIME));
            holder.notetime.setText(time);
            Picasso.with(getContext()).load(imagePath).into(holder.imageView);
        }


        public void swapData(Cursor newData){
            if (newData!=null){
                mNoteData = newData;

                notifyDataSetChanged();
            }
        }

        @Override
        public int getItemCount() {
            if (mNoteData!=null){
                return mNoteData.getCount();
            }else {
                return 0;
            }
        }

        public class NoteViewHolder extends RecyclerView.ViewHolder {

            public final TextView notetext;
            public final TextView notetime;
            public final ImageView imageView;


            public NoteViewHolder(View itemView) {
                super(itemView);
                notetext = itemView.findViewById(R.id.note_text);
                notetime = itemView.findViewById(R.id.note_time);
                imageView = itemView.findViewById(R.id.note_img);

            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
