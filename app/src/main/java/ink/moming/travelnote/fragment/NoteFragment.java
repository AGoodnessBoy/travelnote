package ink.moming.travelnote.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
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
import ink.moming.travelnote.adapter.NoteListLoader;
import ink.moming.travelnote.data.GuidePerference;
import ink.moming.travelnote.data.NoteBean;

/**
 * A simple {@link Fragment} subclass.
 */
public class NoteFragment extends Fragment implements LoaderManager.LoaderCallbacks<NoteBean[]>{

    private RecyclerView mNoteList;
    private FloatingActionButton mAddBtn;
    private TextView mNoData;
    private ProgressBar mPb;
    public static final int USER_RES_NOTE = 44;
    public static final int UP_RES_NOTE = 45;
    public static final int USER_NOTE_LOAD = 28;
    public static final String TAG = NoteFragment.class.getSimpleName();



    private NoteBean[] mData;
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

        adapter = new NoteAdapter();
        mNoteList.setAdapter(adapter);

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
            getActivity().getSupportLoaderManager().restartLoader(USER_NOTE_LOAD,idBundle,this);
            //刷新数据

        }

        if (requestCode == UP_RES_NOTE && resultCode == 80){
            boolean status = data.getBooleanExtra("status",false);
            if (status){
                Bundle idBundle = new Bundle();
                idBundle.putInt("userid",GuidePerference.getUserId(getContext()));
                getActivity().getSupportLoaderManager().restartLoader(USER_NOTE_LOAD,idBundle,this);
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
            Bundle bundle = new Bundle();
            bundle.putInt("userid",userid);
            Log.d(TAG,"onResume"+userid);
            getActivity().getSupportLoaderManager().restartLoader(USER_NOTE_LOAD,bundle,this);
        }
    }

    @Override
    public Loader<NoteBean[]> onCreateLoader(int id, Bundle args) {

       switch (id){
           case USER_NOTE_LOAD:
               Log.d(TAG,"onCreateLoader");
               mPb.setVisibility(View.VISIBLE);
               mNoteList.setVisibility(View.GONE);
               mNoData.setVisibility(View.GONE);
               int userid = args.getInt("userid");
               Log.d(TAG,"onCreateLoader "+userid);
               return new NoteListLoader(getContext(),userid);
           default:
              throw  new IllegalArgumentException() ;
       }
    }

    @Override
    public void onLoadFinished(Loader<NoteBean[]> loader, NoteBean[] data) {
        mPb.setVisibility(View.GONE);
        if (data!=null){
            mData = data;
            //移除无数据
            Log.d(TAG,"onLoadFinished has");
            mNoData.setVisibility(View.GONE);
            mNoteList.setVisibility(View.VISIBLE);
            adapter.swapData(mData);

        }else {
            //显示无数据
            Log.d(TAG,"onLoadFinished none");
            mNoData.setVisibility(View.VISIBLE);
            mNoData.setText("还没有上传笔记");
            mNoteList.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(Loader<NoteBean[]> loader) {
        adapter.swapData(null);
    }



    public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder>{

        private NoteBean[] mNoteData;


        @Override
        public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            int layout = R.layout.note_list_item;
            LayoutInflater inflater = LayoutInflater.from(getContext());
            final View view = inflater.inflate(layout,parent,false);

            return new NoteViewHolder(view);
        }

        @Override
        public void onBindViewHolder(NoteViewHolder holder, int position) {
            NoteBean note = mNoteData[position];
            if (note.getNotetext().isEmpty()){
                holder.notetext.setVisibility(View.GONE);
            }else {
                holder.notetext.setVisibility(View.VISIBLE);
                holder.notetext.setText(note.getNotetext());
            }
            String imagePath = "http://api.moming.ink/images/"+note.getNoteimage()+".jpg";
            holder.notetime.setText(note.getNotetime());
            holder.noteuser.setText(GuidePerference.getUserName(getContext()));
            Picasso.with(getContext()).load(imagePath).into(holder.imageView);
        }


        public void swapData(NoteBean[] newData){
            if (newData!=null){
                mNoteData = newData;

                notifyDataSetChanged();
            }
        }

        @Override
        public int getItemCount() {
            return mNoteData.length;
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
