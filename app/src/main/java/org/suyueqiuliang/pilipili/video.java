package org.suyueqiuliang.pilipili;

import android.graphics.Bitmap;

public class video{
    public video(String title, Bitmap title_image, int id, int up_id, String up_name, String play_times, String bullet_chat, videoIdType videoIdType) {
        this.title = title;
        this.title_image = title_image;
        this.up_name = up_name;
        this.id = id;
        this.up_id = up_id;
        this.play_times = play_times;
        this.bullet_chat = bullet_chat;
        this.videoIdType = videoIdType;
    }
    public Bitmap title_image;
    public String title,up_name,play_times,bullet_chat;
    public int id,up_id;
    public videoIdType videoIdType;
}
enum videoIdType{
    av,bv;
}