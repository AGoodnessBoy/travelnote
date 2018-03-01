package ink.moming.travelnote.adapter;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ink.moming.travelnote.data.NoteBean;
import ink.moming.travelnote.unit.NetUnit;

/**
 * Created by admin on 2018/3/1.
 */

public class NoteListLoader extends AsyncTaskLoader<NoteBean[]> {

    public static final String TAG = NoteListLoader.class.getSimpleName();
    private int mId;

    public NoteListLoader(Context context,int id) {
        super(context);
        mId = id;
    }

    @Override
    public  NoteBean[] loadInBackground() {
        NoteBean[] mData = null;
        Log.d(TAG,"loadInBackground");
        String json = NetUnit.getNoteList(mId);
        if (json!=null)
        {
            Log.d(TAG,json);
            try {
                JSONObject jsonObject = new JSONObject(json);
                if (Integer.toString(jsonObject.getInt("status")).equals("400")){
                    JSONArray data = jsonObject.getJSONArray("data");
                    if (data.length()>=1){
                        mData= new NoteBean[data.length()];
                        for (int i=0;i<data.length();i++){
                            NoteBean note = new NoteBean();
                            note.setNoteid(data.getJSONObject(i).getInt("note_id"));
                            note.setNoteimage(data.getJSONObject(i).getString("note_img"));
                            note.setNotetext(data.getJSONObject(i).getString("note_text"));
                            note.setNoteuser(data.getJSONObject(i).getString("note_user"));
                            note.setNotetime(data.getJSONObject(i).getString("note_time"));

                            mData[i] = note;

                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return mData;
    }
}
