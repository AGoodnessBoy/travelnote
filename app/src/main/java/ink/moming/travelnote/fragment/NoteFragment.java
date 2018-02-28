package ink.moming.travelnote.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import ink.moming.travelnote.LoginActivity;
import ink.moming.travelnote.NoteUploadActivity;
import ink.moming.travelnote.R;
import ink.moming.travelnote.data.GuidePerference;

/**
 * A simple {@link Fragment} subclass.
 */
public class NoteFragment extends Fragment implements LoaderManager.LoaderCallbacks<NoteFragment.NoteBean[]>{

    private RecyclerView mNoteList;
    private FloatingActionButton mAddBtn;
    public static final int USER_RES_NOTE = 44;

    private NoteBean[] mData;


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

        NoteAdapter adapter = new NoteAdapter();
        mNoteList.setAdapter(adapter);

        mAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (GuidePerference.getUserStatus(getContext())){
                    Intent intent = new Intent(getActivity(), NoteUploadActivity.class);
                    startActivity(intent);
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
            //刷新数据
        }
    }

    @Override
    public Loader<NoteBean[]> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<NoteBean[]> loader, NoteBean[] data) {

    }

    @Override
    public void onLoaderReset(Loader<NoteBean[]> loader) {

    }


    public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder>{


        @Override
        public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            int layout = R.layout.note_list_item;
            LayoutInflater inflater = LayoutInflater.from(getContext());
            final View view = inflater.inflate(layout,parent,false);

            return new NoteViewHolder(view);
        }

        @Override
        public void onBindViewHolder(NoteViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
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

    public class NoteBean {
        int id;
        String text;
        String image;
        String user;
        String time;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }
    }
}
