package ink.moming.travelnote.adapter;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ink.moming.travelnote.R;
import ink.moming.travelnote.data.GuideContract;
import ink.moming.travelnote.ui.StickyRecyclerView;

/**
 * Created by Moming-Desgin on 2018/2/22.
 */

public class CityListAdapter extends StickyRecyclerView.StickyAdapter<CityListAdapter.CityListViewHolder>{

    private Cursor mCursor;

    private final CityListChooseHandler mClickHandler;

    public CityListAdapter(CityListChooseHandler clickHandler) {
        mClickHandler=clickHandler;
    }


    public interface CityListChooseHandler {
        void onClick(String city);
    }
    @Override
    public String getItemViewTitle(int position) {
        mCursor.moveToPosition(position);
        return mCursor.getString(mCursor.getColumnIndex(GuideContract.GuideEntry.COLUMN_CITY_REGION));
    }

    @Override
    public CityListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.city_list_item,parent,false);
        view.setFocusable(true);
        return new CityListViewHolder(view) ;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        ((CityListAdapter.CityListViewHolder)holder).textView.setText(mCursor.getString(mCursor.getColumnIndex(GuideContract.GuideEntry.COLUMN_CITY_NAME)));
    }


    public  class CityListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView textView;

        public CityListViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.city_item);
            textView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            int postion = getAdapterPosition();
            mCursor.moveToPosition(postion);
            String city_name = mCursor.getString(mCursor.getColumnIndex(GuideContract.GuideEntry.COLUMN_CITY_NAME));
            mClickHandler.onClick(city_name);

        }
    }




    @Override
    public int getItemCount() {
        if (mCursor==null)return 0;
        return mCursor.getCount();
    }

    public void swapData(Cursor newCursor){
        if (newCursor!=null&&newCursor.getCount()!=0){
            mCursor = newCursor;
        }
        notifyDataSetChanged();
    }

}
