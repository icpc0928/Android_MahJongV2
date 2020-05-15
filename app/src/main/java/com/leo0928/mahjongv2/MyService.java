package com.leo0928.mahjongv2;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class MyService extends Service {
    private final Binder mbinder=new LocalBinder();
    private MediaPlayer mediaPlayer;
    private MediaPlayer mediaPlayer2;
    private SoundPool.Builder soundPoolBuilder;
    private SoundPool soundPool;
    private int btn_sound;

    String uriaudio="@raw/"+"au";

    public class LocalBinder extends Binder{//建一個內部類別裡的方法,方法是回傳父類別物件,也就是自己
        public MyService getService(){
            return MyService.this;//傳回自己
        }
    }

    public MyService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer=MediaPlayer.create(this,R.raw.blues_infusion);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer2=MediaPlayer.create(this,R.raw.bright_eyed_blues);
        mediaPlayer2.setAudioStreamType(AudioManager.STREAM_MUSIC);
        soundPoolBuilder=new SoundPool.Builder();
        soundPoolBuilder.setMaxStreams(5);
        //建立用來建立soundpool的builder
        soundPoolBuilder=new SoundPool.Builder();
        soundPoolBuilder.setMaxStreams(50);
        soundPool=soundPoolBuilder.build();
        soundPoolLoad();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.

        return mbinder;
    }

    //啟動型用
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String act=intent.getStringExtra("ACTION");
        if (act.equals("start")){
            mediaPlayer.start();
            mediaPlayer.setLooping(true);
            mediaPlayer.setVolume(0.4f,0.4f);
        }
        else if (act.equals("pause")){
            mediaPlayer.pause();
        }
        else if(act.equals("NOTPLAY")){
            mediaPlayer2.start();
        }
        return super.onStartCommand(intent, flags, startId);
    }



    @Override
    public void onDestroy() {
        if (mediaPlayer!=null){
            if (mediaPlayer.isPlaying()){
                mediaPlayer.stop();
                mediaPlayer.release();
            }
        }
        super.onDestroy();
    }
    public void playMedia(){
        if (!mediaPlayer2.isPlaying()){
        mediaPlayer2.start();}
    }
    public void pauseMedia(){
        if (mediaPlayer2.isPlaying()){
        mediaPlayer2.pause();}
    }

    public void stopMedia(){
        if (mediaPlayer2!=null){
        if (mediaPlayer2.isPlaying()){
        mediaPlayer2.stop();}}
    }
    public void soundPoolLoad(){
        // 放入萬筒條 11~19(萬) 21~29(筒) 31~39(條) 41~47(東南西北中發白)  20(吃) 30(碰) 40(槓) 48(胡)
        // 計算上     1~9 (萬)  11~19(筒) 21~29(條) 31~37(東南西北中發白)  10(吃) 20(碰) 30(槓) 38(胡) 總共38個檔案
        for (int i=1;i<=3;i++){
            for (int j=1;j<=9;j++){
                int k=i*10+j;
                soundPool.load(getApplicationContext(),AudioURI(k),1);

            }
            soundPool.load(getApplicationContext(),AudioURI(i*10+10),1);
        }
        for (int i = 41;i<=48;i++){
            soundPool.load(getApplicationContext(),AudioURI(i),1);

        }
        btn_sound=soundPool.load(getApplicationContext(),R.raw.keydown,1);//

    }

    public int AudioURI(int i){
        int getCardsAudio = getResources().getIdentifier(uriaudio+i,null,getPackageName());
        return getCardsAudio;
    }


    public void play(int soundID){
        soundPool.play(soundID-10 ,1,1,1,0,1);

    }
    public void playkeydown_sound(){
        soundPool.play(btn_sound ,1,1,1,0,1);
    }
}
