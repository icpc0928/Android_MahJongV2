package com.leo0928.mahjongv2;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class EatList extends Fragment {
    private View view;
    private Timer timer;
    public TextView count;
    private PlayingActivity playingActivity;
    private MyHandler handler;
    private List<Integer> cardsDataforEat= new ArrayList<>();//保存符合吃的牌,並印出要吃的牌以供使用者選擇
    private EatPGListAdapter EatPGListAdapter;
    private RecyclerView cardsforEat;
    private LinearLayoutManager linearLayoutManager;
    private MyItemDecoration myItemDecoration;
    private OriginMJ MJObj;
    private CountDownTimer countDownTimer;
    //建立listener
    private OnItemListener onItemListener=new OnItemListener() {
        @Override
        public void onItemClick(int position) {
            //當item被點擊的時候要做的事情

            countDownTimer.cancel();
            playingActivity.Eatwhat(position);
            playingActivity.closeEatList();
        }
    };
    public EatList() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_eat_list, container, false);
        count = view.findViewById(R.id.count);
        cardsforEat=view.findViewById(R.id.EatPG_List);
        //列出可選牌行的設置
        linearLayoutManager=new LinearLayoutManager(inflater.getContext(),RecyclerView.HORIZONTAL,false);
        cardsforEat.setLayoutManager(linearLayoutManager);
        //設置adapter
        EatPGListAdapter=new EatPGListAdapter(onItemListener);
        cardsforEat.setAdapter(EatPGListAdapter);
        //設置邊線
        myItemDecoration=new MyItemDecoration();
        cardsforEat.addItemDecoration(myItemDecoration);
        //取得MJObj
        MJObj=playingActivity.MJObj;

        EatWhat(MJObj.getSeaCards().get(MJObj.getSeaCards().size() - 1));
        Log.v("leo","cards:"+MJObj.getSeaCards().get(MJObj.getSeaCards().size() - 1));//把符合條件的牌塞到cardforEat裡,顯示出來給使用者選
        EatPGListAdapter.notifyDataSetChanged();
        for(int i=0;i<cardsDataforEat.size();i++){
            int y=cardsDataforEat.get(i);//分別取出
            Log.v("leo","eat:"+y);
            playingActivity.temp_p1Out.add(y);
        }


        timer = new Timer();
//        timer.schedule(new EatList.MytimerTask(), 6000);

        countDownTimer = new CountDownTimer(5000,5000){

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                playingActivity.Eatwhat(0);
                playingActivity.closeEatList();
            }
        };
        countDownTimer.start();

        handler=new EatList.MyHandler();
        //這裡也一個執行緒,它會調用實做runnable物件的run方法
        new Thread(new EatList.countdown()).start();
        return view;
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        playingActivity=(PlayingActivity)context;
    }

    @Override
    public void onDestroy() {
        timer.cancel();
        timer.purge();
        cardsDataforEat.clear();
        super.onDestroy();
    }


    private void EatWhat(int lastSeaCard){
        boolean result = false;
        //1.尾張需在11~19.21~29.31~39之間
        if(lastSeaCard <40){
            //2.三種吃牌條件  -2-1 , -1+1, +1+2 (需先將尾張取個位數 ==1 判斷+1.+2  ==9 判斷-1-2  其餘判斷三種)
            if(lastSeaCard%10==1 & MJObj.findMyHand(MainApp.myTurn).contains(lastSeaCard+1) & MJObj.findMyHand(MainApp.myTurn).contains(lastSeaCard+2)){  //判斷+1.+2
                cardsDataforEat.add(lastSeaCard+1);
                cardsDataforEat.add(lastSeaCard);
                cardsDataforEat.add(lastSeaCard+2);
            }else if(lastSeaCard%10==9 &MJObj.findMyHand(MainApp.myTurn).contains(lastSeaCard-1) & MJObj.findMyHand(MainApp.myTurn).contains(lastSeaCard-2)){  //判斷-1.-2
                cardsDataforEat.add(lastSeaCard-1);
                cardsDataforEat.add(lastSeaCard);
                cardsDataforEat.add(lastSeaCard-2);
            }else {
                if ((MJObj.findMyHand(MainApp.myTurn).contains(lastSeaCard-2)&MJObj.findMyHand(MainApp.myTurn).contains(lastSeaCard-1))){
                    cardsDataforEat.add(lastSeaCard-1);
                    cardsDataforEat.add(lastSeaCard);
                    cardsDataforEat.add(lastSeaCard-2);
                }
                if (MJObj.findMyHand(MainApp.myTurn).contains(lastSeaCard+1)&MJObj.findMyHand(MainApp.myTurn).contains(lastSeaCard-1)){
                    cardsDataforEat.add(lastSeaCard-1);
                    cardsDataforEat.add(lastSeaCard);
                    cardsDataforEat.add(lastSeaCard+1);
                }
                if (MJObj.findMyHand(MainApp.myTurn).contains(lastSeaCard+1)&MJObj.findMyHand(MainApp.myTurn).contains(lastSeaCard+2)){
                    cardsDataforEat.add(lastSeaCard+1);
                    cardsDataforEat.add(lastSeaCard);
                    cardsDataforEat.add(lastSeaCard+2);
                }
            }
        }
    }


    public interface OnItemListener{
        void onItemClick(int position);
    }
    private class MytimerTask extends TimerTask {
        @Override
        public void run() {
//            playingActivity.Eatwhat(0);
//            playingActivity.closeEatList();
        }
    }
    public class countdown implements Runnable{

        @Override
        public void run() {
            Bundle bundle=new Bundle();
            for(int i=5;i>=0;i--){
                Message msg=new Message();//注意在sendmessage後,Handler 會將 Message 加入 MessageQueue 中，造成下次要處理的message，已經不是原來的對象,
                //因此每使用後,都要new一個出來,不然會出現 This message is already in use.的錯誤
                try {
                    bundle.putInt("time",i);
                    msg.setData(bundle);
                    handler.sendMessage(msg);//每秒丟出一個bundle給handler,注意每次都要丟給沒用過的message物件
                    //要提的是,似乎message物件有使用的上限?
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }}
    }
    //用來處理每次收到數據要怎麼更新自己頁面
    private class MyHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
//            每次收到數據
            Bundle b = msg.getData();
            int time = b.getInt("time");
            count.setText("剩餘:"+time+"秒");
        }
    }
    //顯示在list中的牌選項,賦予點按功能
    public class EatPGListAdapter extends RecyclerView.Adapter<EatList.EatPGListAdapter.viewHolder>{
        private OnItemListener onItemListener;
        //建構式
        public EatPGListAdapter(OnItemListener onItemListener){
            this.onItemListener=onItemListener;
        }
        @NonNull
        @Override
        public EatList.EatPGListAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.eat_p_g_item, parent, false);
            EatList.EatPGListAdapter.viewHolder vh=new EatList.EatPGListAdapter.viewHolder(view,onItemListener);
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull EatList.EatPGListAdapter.viewHolder holder, int position) {
            ImageView iv1 = holder.iv1;
            ImageView iv2 = holder.iv2;
            ImageView iv3 = holder.iv3;
            //有三個,之後改成如果只有一個的話,不用顯示直接吃

            if(position !=cardsDataforEat.size()/3){
                iv1.setImageResource(playingActivity.imgURI(cardsDataforEat.get(position*3+0)));
                iv2.setImageResource(playingActivity.imgURI(cardsDataforEat.get(position*3+1)));
                iv3.setImageResource(playingActivity.imgURI(cardsDataforEat.get(position*3+2)));
            }


        }
        @Override
        public int getItemCount() {
            int x=cardsDataforEat.size()/3 ;


            return x;        }

        private class viewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            ImageView iv1,iv2,iv3;
            OnItemListener onItemListener;

            public viewHolder(@NonNull View itemView, OnItemListener onItemListener) {
                super(itemView);
                iv1=itemView.findViewById(R.id.eat1);
                iv2=itemView.findViewById(R.id.eat2);
                iv3=itemView.findViewById(R.id.eat3);
                this.onItemListener=onItemListener;
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                onItemListener.onItemClick(getAdapterPosition());
            }
        }
    }
    //item間的排版
    public class MyItemDecoration extends RecyclerView.ItemDecoration{
        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            if (parent.getChildAdapterPosition(view)!=0){
                outRect.left=20;
            }
        }
    }


}
