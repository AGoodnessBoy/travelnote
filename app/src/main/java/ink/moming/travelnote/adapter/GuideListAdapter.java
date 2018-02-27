package ink.moming.travelnote.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import ink.moming.travelnote.GuideDetailActivity;
import ink.moming.travelnote.R;

/**
 * Created by Moming-Desgin on 2018/2/16.
 */

public class GuideListAdapter extends RecyclerView.Adapter<GuideListAdapter.GuideListViewHolder> {


    private final Context mContext;

    private ArticleBean[] mArticles;


    public GuideListAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public String getAricleId(int postion){
        return mArticles[postion].getA_id();
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
                String url ="https://lvyou.baidu.com/notes/"+getAricleId(viewHolder.getAdapterPosition());
                intent.putExtra("url",url);
                mContext.startActivity(intent);
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(GuideListViewHolder holder, int position) {
        ArticleBean articleBean = mArticles[position];
        Context context = holder.itemView.getContext();
        String image_small = "https://gss0.bdstatic.com/6b1IcTe9RMgBo1vgoIiO_jowehsv/maps/services/thumbnails?width=215&height=145&quality=120&align=middle,middle&src=";
        String image_base = "http://hiphotos.baidu.com/lvpics/pic/item/";
        String image_url = image_small+ image_base+articleBean.a_image+".jpg";
        holder.mArticleTitle.setText(articleBean.getA_title());
        Picasso.with(context).load(image_url)
                .into(holder.mArticleImage);
        holder.itemView.setTag(articleBean.getA_id());
    }

    @Override
    public int getItemCount() {
        if (null == mArticles) return 0;
        return mArticles.length;
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

    public void swapData(ArticleBean[] newData){
        mArticles = newData;
        notifyDataSetChanged();
    }

    public static class ArticleBean {
        public String getA_id() {
            return a_id;
        }

        public void setA_id(String a_id) {
            this.a_id = a_id;
        }

        public String getA_title() {
            return a_title;
        }

        public void setA_title(String a_title) {
            this.a_title = a_title;
        }

        public String getA_image() {
            return a_image;
        }

        public void setA_image(String a_image) {
            this.a_image = a_image;
        }

        public String a_id;
        public String a_title;
        public String a_image;

    }
}
