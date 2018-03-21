package ink.moming.travelnote.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import ink.moming.travelnote.GuideDetailActivity;
import ink.moming.travelnote.R;
import ink.moming.travelnote.data.ArticleContract;

/**
 * Created by Moming-Desgin on 2018/2/16.
 */

public class GuideListAdapter extends RecyclerView.Adapter<GuideListAdapter.GuideListViewHolder> {


    private final Context mContext;

    private static final String BAIDU_IMAGE_SMALL_URL="https://gss0.bdstatic.com/6b1IcTe9RMgBo1vgoIiO_jowehsv/maps/services/thumbnails?width=215&height=145&quality=120&align=middle,middle&src=";

    private static final String BAIDU_IMAGE_BASE_URL="http://hiphotos.baidu.com/lvpics/pic/item/";
    private static final String BAIDU_LVYOU_BASE_URL="https://lvyou.baidu.com/notes/";
    private Cursor mCursor;
    private final static String TAG = GuideListAdapter.class.getSimpleName();


    public GuideListAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public String getAricleId(int postion){
        mCursor.moveToPosition(postion);
        return mCursor.getString(mCursor.getColumnIndex(ArticleContract.ArticleEntry.COLUMN_ARTICLE_ID));
    }

    @Override
    public GuideListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        int layout = R.layout.guide_list_item;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(layout,parent,false);
        final GuideListViewHolder viewHolder =new GuideListViewHolder(view);
        view.setFocusable(true);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, GuideDetailActivity.class);
                String url =BAIDU_LVYOU_BASE_URL+getAricleId(viewHolder.getAdapterPosition());
                Log.d(TAG,url);

                intent.putExtra("url",url);
                mContext.startActivity(intent);
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(GuideListViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        Context context = holder.itemView.getContext();
        String image_url = BAIDU_IMAGE_SMALL_URL+ BAIDU_IMAGE_BASE_URL+mCursor.getString(mCursor.getColumnIndex(ArticleContract.ArticleEntry.COLUMN_ARTICLE_IMAGE))+".jpg";
        holder.mArticleTitle.setText(mCursor.getString(mCursor.getColumnIndex(ArticleContract.ArticleEntry.COLUMN_ARTICLE_TITLE)));
        Picasso.with(context).load(image_url)
                .into(holder.mArticleImage);
        holder.itemView.setTag(mCursor.getString(mCursor.getColumnIndex(ArticleContract.ArticleEntry.COLUMN_ARTICLE_ID)));
    }

    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;
        return mCursor.getCount();
    }

    public class GuideListViewHolder extends RecyclerView.ViewHolder {

        public final TextView mArticleTitle;
        public final ImageView mArticleImage;


        public GuideListViewHolder(View itemView) {
            super(itemView);
            mArticleTitle = itemView.findViewById(R.id.article_title);
            mArticleImage = itemView.findViewById(R.id.article_image);
        }
    }

    public void swapData(Cursor newData){
        mCursor = newData;
        notifyDataSetChanged();
    }


}
