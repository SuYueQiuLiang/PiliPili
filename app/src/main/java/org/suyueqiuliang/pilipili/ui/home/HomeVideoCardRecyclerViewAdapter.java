package org.suyueqiuliang.pilipili.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import org.suyueqiuliang.pilipili.R;

public class HomeVideoCardRecyclerViewAdapter extends RecyclerView.Adapter {

    String[] imageURL,title,pubdate,duration,owner;
    public HomeVideoCardRecyclerViewAdapter(String[] imageURL,String[] title,String[] pubdate,String[] duration,String[] owner){
        this.imageURL = imageURL;
        this.title = title;
        this.pubdate = pubdate;
        this.duration = duration;
        this.owner = owner;
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_home_recyclerview, null);
        int height = parent.getMeasuredHeight() / 4;
        itemView.setMinimumHeight(height);
        return new VideoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ImageView image_title = holder.itemView.findViewById(R.id.image_title);
        CardView cardView = holder.itemView.findViewById(R.id.home_fragment_recycler_card_view);
    }

    @Override
    public int getItemCount() {
        return 100;
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static class VideoViewHolder extends RecyclerView.ViewHolder {
        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }


}
