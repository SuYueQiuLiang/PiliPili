package org.suyueqiuliang.pilipili.tool;

public class video{
    public video(String title, String title_image, int id, int up_id, String up_name, String play_times, String bullet_chat ,String duration) {
        this.title = title;
        this.title_image = title_image;
        this.up_name = up_name;
        this.id = id;
        this.up_id = up_id;
        this.play_times = play_times;
        this.bullet_chat = bullet_chat;
        this.duration = duration;
    }
    public String title,title_image,up_name,play_times,bullet_chat,duration;
    public int id,up_id;
}