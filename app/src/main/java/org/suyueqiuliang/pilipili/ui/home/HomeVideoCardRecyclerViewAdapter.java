package org.suyueqiuliang.pilipili.ui.home;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import org.suyueqiuliang.pilipili.R;
import org.suyueqiuliang.pilipili.tool.video;

import java.util.ArrayList;

public class HomeVideoCardRecyclerViewAdapter extends RecyclerView.Adapter {

    ArrayList<video> arrayList;
    public HomeVideoCardRecyclerViewAdapter(ArrayList<video> arrayList){
        this.arrayList = arrayList;
    }
    public void addNewVideo(ArrayList<video> arrayList){
        this.arrayList.addAll(arrayList);
        notifyDataSetChanged();
    }
    public void flushAdapter(){

    }
    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_home_recyclerview, null);
        int height = parent.getMeasuredHeight() / 3;
        itemView.setMinimumHeight(height);
        return new VideoViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ImageView image_title = holder.itemView.findViewById(R.id.image_title);
        TextView text_up_name_time = holder.itemView.findViewById(R.id.text_up_name_time);
        TextView text_title = holder.itemView.findViewById(R.id.text_title);
        CardView cardView = holder.itemView.findViewById(R.id.home_fragment_recycler_card_view);
        video video = arrayList.get(position);
        image_title.setImageBitmap(video.title_image);
        text_title.setText(video.title);
        text_up_name_time.setText(video.up_name);
    }

    @Override
    public int getItemCount() {
        if(arrayList == null)
            return 0;
        return arrayList.size();
    }

    public static class VideoViewHolder extends RecyclerView.ViewHolder {
        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

}
