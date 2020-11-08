package org.suyueqiuliang.pilipili;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import org.suyueqiuliang.pilipili.tool.QualityList;
import org.suyueqiuliang.pilipili.tool.ToolClass;
import org.suyueqiuliang.pilipili.tool.VideoInformation;

import java.net.URL;

public class VideoActivity extends AppCompatActivity {
    static int av;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_layout);
        hideSystemNavigationBar();
        Intent intent = getIntent();
        av = intent.getIntExtra("av",0);
        new Thread(() -> {
            ToolClass toolClass = new ToolClass();
            VideoInformation videoInformation = toolClass.getVideoInformation(av);
            QualityList qualityList = toolClass.getVideoStreamuality(av,videoInformation.pages.get(0).cid);
        }).start();
    }
    private void hideSystemNavigationBar() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }
}
