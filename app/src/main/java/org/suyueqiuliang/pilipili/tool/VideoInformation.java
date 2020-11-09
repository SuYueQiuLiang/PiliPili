package org.suyueqiuliang.pilipili.tool;

import java.util.ArrayList;

public class VideoInformation {
    public VideoInformation(int aid, String bvid, String title, String pic, String desc, Owner owner, ArrayList<Page> pages, int videos, int duration, int pubdate, int ctime, int tid, String tname, int copyright, int state) {
        this.aid = aid;
        this.videos = videos;
        this.tid = tid;
        this.copyright = copyright;
        this.pubdate = pubdate;
        this.ctime = ctime;
        this.state = state;
        this.duration = duration;
        this.bvid = bvid;
        this.tname = tname;
        this.pic = pic;
        this.title = title;
        this.desc = desc;
        this.owner = owner;
        this.pages = pages;
    }

    //aid和bvid分别为视频的两个唯一标识符，一般情况下app使用aid，web使用bvid
    //tid为分区id，tname为子分区名称
    //copyright是否转载,1：原创2：转载
    //pic为视频封面图片url,title是标题,desc是视频简介
    //pubdate是发布时间戳，ctime是上传时间戳
    //state是视频状态
    // 0：开放浏览
    //1：橙色通过
    //-1：待审
    //-2：被打回
    //-3：网警锁定
    //-4：被锁定
    //-5：管理员锁定（可浏览）
    //-6：修复待审
    //-7：暂缓审核
    //-8：补档待审
    //-9：等待转码
    //-10：延迟审核
    //-11：视频源待修
    //-12：转储失败
    //-13：允许评论待审
    //-14：临时回收站
    //-15：分发中
    //-16：转码失败
    //-20：创建未提交
    //-30：创建已提交
    //-40：定时发布
    //-100：用户删除
    //duration稿件总时长，单位秒
    public int aid,videos,tid,copyright,pubdate,ctime,state,duration;
    public String bvid,tname,pic,title,desc;
    public Owner owner;
    public ArrayList<Page> pages;
}
