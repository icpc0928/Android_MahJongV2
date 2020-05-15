package com.leo0928.mahjongv2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.sql.Time;
import java.util.Timer;
import java.util.TimerTask;

public class BeginApp extends AppCompatActivity {
    private Timer timer;
    private ImageView logo;
    private int nowPicPos=0;
    private ProgressBar loading;
    private Handler handler=new BeginApp.MyHandler();
    private int[] imgRes = {R.drawable.img_icon_xxxhdpi};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_begin_app);
        logo=findViewById(R.id.logo);
        loading = findViewById(R.id.loading);
        fadeOutAndHideImage(logo);
        timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(BeginApp.this,HomeActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                BeginApp.this.finish();
            }
        },5000);
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                new Thread(new LoadingIcon()).start();
//            }
//        },4000);
    }


    //畫面真正可以看到的時間點...就是在此生命周期被執行時。
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }
    /**
     * SYSTEM_UI_FLAG_IMMERSIVE------------->點出隱藏的狀態欄後,隔不久不隱藏
     * SYSTEM_UI_FLAG_IMMERSIVE_STICKY------>點出隱藏的狀態欄後,隔不久會再隱藏
     * View.SYSTEM_UI_FLAG_LAYOUT_STABLE---->固定住layout的位置
     * View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN 狀態欄上浮(你可以再調顏色)
     * View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION導覽列上浮,也就是看起來浮在activivty上
     * View.SYSTEM_UI_FLAG_FULLSCREEN------->狀態欄隱藏
     * SYSTEM_UI_FLAG_HIDE_NAVIGATION------->隱藏導覽列
     */

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        // Set the content to appear under the system bars so that the
                        //設定系統要顯示的內容,這樣內容才不會因為系統bar隱藏或顯示時縮放
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
        );
    }

    // Shows the system bars by removing all the flags
    // except for the ones that make the content appear under the system bars.
    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }


    private void fadeOutAndHideImage(final ImageView img){
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(2000);

        fadeOut.setAnimationListener(new Animation.AnimationListener()
        {
            public void onAnimationEnd(Animation animation)
            {
                if(nowPicPos==1){

                }else {
                    nowPicPos %= 1;
                    img.setImageResource(imgRes[nowPicPos]);
                    nowPicPos++;
                    fadeInAndShowImage(img);
                }
            }
            public void onAnimationRepeat(Animation animation) {}
            public void onAnimationStart(Animation animation) {}
        });

        img.startAnimation(fadeOut);
    }
    private void fadeInAndShowImage(final ImageView img){
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new AccelerateInterpolator());
        fadeIn.setDuration(2000);

        fadeIn.setAnimationListener(new Animation.AnimationListener()
        {
            public void onAnimationEnd(Animation animation)
            {
                fadeOutAndHideImage(img);
            }
            public void onAnimationRepeat(Animation animation) {}
            public void onAnimationStart(Animation animation) {}
        });

        img.startAnimation(fadeIn);
    }


    public class LoadingIcon implements Runnable{

        @Override
        public void run() {
            try{
                handler.sendEmptyMessage(0);
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }
    private class MyHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            loading.setVisibility(View.VISIBLE);
        }

    }
}
