package ink.moming.travelnote.fragment;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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

import ink.moming.travelnote.LoginActivity;
import ink.moming.travelnote.NoteUploadActivity;
import ink.moming.travelnote.R;
import ink.moming.travelnote.data.GuidePerference;
import ink.moming.travelnote.data.NoteContract;
import ink.moming.travelnote.sync.GuideSyncUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class NoteFragment extends Fragment {

    private RecyclerView mNoteList;
    private FloatingActionButton mAddBtn;
    private TextView mNoData;
    private ProgressBar mPb;
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
        mNoteList = view.findViewById(R.id.note_list);
        mAddBtn = view.findViewById(R.id.fab_note);
        mNoData = view.findViewById(R.id.no_note);
        mPb = view.findViewById(R.id.note_pb);
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

        GuideSyncUtils.flashNoteList(getContext());





        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == USER_RES_NOTE && resultCode == 42){
            String mEmail = data.getStringExtra("useremail");
            String name = data.getStringExtra("username");
            int id = data.getIntExtra("userid",0);
            GuidePerference.saveUserStatus(getContext(),mEmail,name,id);
            Bundle idBundle = new Bundle();
            idBundle.putInt("userid",id);
            GuideSyncUtils.flashNoteList(getContext());
            getActivity().getSupportLoaderManager().restartLoader(USER_NOTE_LOAD,idBundle,callbacks);
            //刷新数据

        }

        if (requestCode == UP_RES_NOTE && resultCode == 80){
            boolean status = data.getBooleanExtra("status",false);
            if (status){
                GuideSyncUtils.flashNoteList(getContext());
                Bundle idBundle = new Bundle();
                idBundle.putInt("userid",GuidePerference.getUserId(getContext()));
                getActivity().getSupportLoaderManager().restartLoader(USER_NOTE_LOAD,idBundle,callbacks);
            }else {
                Toast.makeText(getContext(), "上传失败", Toast.LENGTH_SHORT).show();
            }


        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!GuidePerference.getUserStatus(getContext())){
            Log.d(TAG,"onResume no login");
            mPb.setVisibility(View.GONE);
            mNoData.setVisibility(View.VISIBLE);
            mNoData.setText("登录后查看");
            mNoteList.setVisibility(View.GONE);
        }else {
            int userid = GuidePerference.getUserId(getContext());
            GuideSyncUtils.flashNoteList(getContext());
            Bundle bundle = new Bundle();
            bundle.putInt("userid",userid);
            Log.d(TAG,"onResume"+userid);
            callbacks=getCallbacks(getContext());
            getActivity().getSupportLoaderManager().restartLoader(USER_NOTE_LOAD,bundle,callbacks);
        }
    }


    private LoaderManager.LoaderCallbacks<Cursor> getCallbacks(final Context context){
        Log.d(TAG,"getCallbacks");
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
                        return new CursorLoader(context,uri,MAIN_NOTE_PROJECTION,selection,selectionArgs,null);

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
                    mNoData.setText("未找到笔记记录！");
                    mNoteList.setVisibility(View.GONE);
                    return;
                }
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
            holder.noteuser.setText(GuidePerference.getUserName(getContext()));
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
            public final  TextView noteuser;


            public NoteViewHolder(View itemView) {
                super(itemView);
                notetext = itemView.findViewById(R.id.note_text);
                notetime = itemView.findViewById(R.id.note_time);
                imageView = itemView.findViewById(R.id.note_img);
                noteuser = itemView.findViewById(R.id.note_user);

            }
        }
    }

}
