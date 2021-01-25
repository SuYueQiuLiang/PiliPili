package org.suyueqiuliang.pilipili.ui.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import org.suyueqiuliang.pilipili.R;
import org.suyueqiuliang.pilipili.VideoActivity;
import org.suyueqiuliang.pilipili.tool.ToolClass;
import org.suyueqiuliang.pilipili.tool.video;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
public class HomeVideoCardRecyclerViewAdapter extends RecyclerView.Adapter {

    ArrayList<video> arrayList;
    static Activity activity;
    public HomeVideoCardRecyclerViewAdapter(ArrayList<video> arrayList, Activity activity){
        this.arrayList = arrayList;
        HomeVideoCardRecyclerViewAdapter.activity = activity;
    }
    public int getSize(){
        return arrayList.size();
    }
    public void setVideo(ArrayList<video> arrayList){
        this.arrayList = arrayList;
        notifyDataSetChanged();
    }
    public void addNewVideo(ArrayList<video> arrayList){
        int as = this.arrayList.size();
        this.arrayList.addAll(arrayList);
        notifyItemRangeChanged(as,this.arrayList.size());
    }
    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_home_recyclerview, null);
        double height = parent.getMeasuredHeight() / 2.5;
        itemView.setMinimumHeight((int)height);
        return new VideoViewHolder(itemView);
    }
    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        final ImageView image_title = holder.itemView.findViewById(R.id.image_title);
        final TextView text_up_name_time = holder.itemView.findViewById(R.id.text_up_name_time);
        final TextView text_title = holder.itemView.findViewById(R.id.text_title);
        final CardView cardView = holder.itemView.findViewById(R.id.home_fragment_recycler_card_view);
        final TextView text_video_time = holder.itemView.findViewById(R.id.text_video_time);
        image_title.setImageDrawable(null);
        final video video = arrayList.get(position);
        new Thread(() -> {
            ToolClass toolClass = new ToolClass();
            Bitmap bitmap = null;
            try {
                bitmap = toolClass.getUrlImageBitmap(video.title_image);
                Bitmap finalBitmap = bitmap;
                activity.runOnUiThread(() -> image_title.setImageBitmap(finalBitmap));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        text_video_time.setText(video.duration);
        text_title.setText(video.title);
        text_up_name_time.setText(video.up_name);
        cardView.setOnClickListener(v -> {
            Intent intent = new Intent(activity, VideoActivity.class);
            intent.putExtra("av", arrayList.get(position).id);
            activity.startActivity(intent);
        });
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
