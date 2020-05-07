package com.example.mahjongv2;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.Inflater;


/**
 * A simple {@link Fragment} subclass.
 */
public class framlayout extends Fragment{
    private TextView count_text;
    private Timer timer;
    private View view;
    private PlayingActivity playingActivity;
    private Button eat,pong,gong,cancel,whoo;
    private MyHandler handler;




    private View.OnClickListener clickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.eat:
                    //點了吃
                    playingActivity.gotoEatList();
//                    playingActivity.getMJObj().getP1Hand().remove(playingActivity.getMJObj().getP1Hand().size()-1);
//                    Log.v("leo",playingActivity.getMJObj().getP1Hand().toString());

                    break;
                case R.id.pong:
                    //點了碰
                    playingActivity.Pongwhat();
                    break;
                case R.id.gong:
                    //點了槓
                    playingActivity.Gongwhat();
                    break;

                case R.id.whoo:
                    //點了胡
                    //...
                    break;
                default:
                    break;
            }
            playingActivity.closeFragment();
        }
    };
    public framlayout() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
            view= inflater.inflate(R.layout.fragment_framlayout, container, false);
            eat= view.findViewById(R.id.eat);
            pong = view.findViewById(R.id.pong);
            gong = view.findViewById(R.id.gong);
            cancel = view.findViewById(R.id.cancel);
            whoo = view.findViewById(R.id.whoo);
            count_text = view.findViewById(R.id.countdown);


            eat.setOnClickListener(clickListener);
            pong.setOnClickListener(clickListener);
            gong.setOnClickListener(clickListener);
            cancel.setOnClickListener(clickListener);
            whoo.setOnClickListener(clickListener);

            handler = new MyHandler();

            if(getArguments()!=null){

                boolean[] temp=getArguments().getBooleanArray("name");
                //...判斷陣列內的true false決定調用哪個按鈕消失
                setButton(temp[0],temp[1],temp[2],temp[3]);
            }


            //timer設定7秒關閉fragment
            timer = new Timer();
            timer.schedule(new DoNothing(), 7000);
            new Thread(new CountDown()).start();

        return view;
    }




    //認得自己依附在哪個activity中
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        playingActivity=(PlayingActivity)context;


    }

    @Override
    public void onDestroy() {
        timer.cancel();
        timer.purge();

        super.onDestroy();
    }

    //
    private class DoNothing extends TimerTask{

        @Override
        public void run() {
            playingActivity.closeFragment();
            //看看什麼也不幹要傳回什麼
            //...
        }
    }
    private class CountDown implements Runnable{

        @Override
        public void run() {
            Bundle bundle = new Bundle();
            for (int i=5;i>=0;i--){
                Message msg = new Message();
                try {
                    bundle.putInt("time",i);
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                    Thread.sleep(1000);
                } catch (Exception e) {
                    Log.v("wei","countdown_error:"+e.toString());
                }
            }
        }
    }
    //用來處理每次收到數據要怎麼更新自己頁面
    private class MyHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
//            每次收到數據
            Bundle b = msg.getData();
            int time = b.getInt("time");
            count_text.setText("剩餘:"+time+"秒");
        }
    }

    //帶參數
    public static framlayout EatPongGongWhoo(Boolean...params){
        framlayout framlayout=new framlayout();
        Bundle bundle=new Bundle();
        boolean[] temp=new boolean[4];
        ArrayList<Boolean> temp2=new ArrayList();
        for (Boolean param:params
             ) {
            temp2.add(param);
        }
        for(int i=0;i<4;i++){
            temp[i]=temp2.get(i);
        }
        bundle.putBooleanArray("name",temp);
        framlayout.setArguments(bundle);
        return framlayout;
    }




    public void setButton(boolean a,boolean b,boolean c,boolean d){
        if (!a) eat.setVisibility(View.INVISIBLE);
        if (!b) pong.setVisibility(View.INVISIBLE);
        if (!c) gong.setVisibility(View.INVISIBLE);
        if (!d) whoo.setVisibility(View.INVISIBLE);

    }



}
