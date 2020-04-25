package com.example.mahjongv2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class SmallGameActivity extends AppCompatActivity {

    private ImageView playerCard1,playerCard2,playerCard3,playerCard4,playerCard5;
    private ImageView aiCard1,aiCard2,aiCard3,aiCard4,aiCard5;
    private Button btn_startGame,btn_cancel;
    private TextView tv_playerCount ,tv_aiCount ,tv_betMoney ,tv_myMoney;
    private Button btn_need , btn_noNeed;
    private boolean aiSecCard = false;
    private ImageView m100,m500,m1000;






    private int[] cards ;
    //補牌按鈕計數器
    private int countCard=0;
    private float aiCount;
    private float playerCount;
    String uri = "@drawable/"+"card";
    private int myMoney;







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_small_game);
        playerCard1 = findViewById(R.id.playerCard1);
        playerCard2 = findViewById(R.id.playerCard2);
        playerCard3 = findViewById(R.id.playerCard3);
        playerCard4 = findViewById(R.id.playerCard4);
        playerCard5 = findViewById(R.id.playerCard5);
        aiCard1=findViewById(R.id.aiCard1);
        aiCard2=findViewById(R.id.aiCard2);
        aiCard3=findViewById(R.id.aiCard3);
        aiCard4=findViewById(R.id.aiCard4);
        aiCard5=findViewById(R.id.aiCard5);
        btn_startGame = findViewById(R.id.btn_startGame);
        btn_cancel = findViewById(R.id.btn_cancel);
        tv_playerCount = findViewById(R.id.tv_playerCount);
        tv_aiCount = findViewById(R.id.tv_aiCount);
        tv_betMoney = findViewById(R.id.tv_betMoney);
        tv_myMoney = findViewById(R.id.tv_myMoney);
        btn_need = findViewById(R.id.btn_need);
        btn_noNeed = findViewById(R.id.btn_noNeed);
        m100 = findViewById(R.id.m100);
        m500 = findViewById(R.id.m500);
        m1000 = findViewById(R.id.m1000);

        myMoney = Integer.parseInt(tv_myMoney.getText().toString());

        m100.setImageResource(R.drawable.chip100);
        m500.setImageResource(R.drawable.chip500);
        m1000.setImageResource(R.drawable.chip1000);


    }


    //返回按鈕監聽
    public void btn_backToHome(View view) {
        Intent intent = new Intent(SmallGameActivity.this,HomeActivity.class);
        startActivity(intent);
        SmallGameActivity.this.finish();
    }

    //補牌按鈕監聽
    public void btn_need(View view) {

        //已經發三張牌 所以countCard從3開始算
        switch (countCard){
            case 3 :
                playerCard3.setImageResource(imgURI(countCard));
                countCard++;
                playerCount += transformCount(cards[3]);
                tv_playerCount.setText(setCountText(playerCount));
                //爆牌--直接賭金回預設值
                if(playerCount>=22){
                    Toast.makeText(this,"輸"+tv_betMoney.getText(),Toast.LENGTH_SHORT).show();
                    tv_betMoney.setText("0");
                    btn_startGame.setVisibility(View.VISIBLE);
                    btn_cancel.setVisibility(View.VISIBLE);
                    btn_need.setVisibility(View.INVISIBLE);
                    btn_noNeed.setVisibility(View.INVISIBLE);
                }
                break;
            case 4 :
                playerCard4.setImageResource(imgURI(countCard));
                countCard++;
                playerCount += transformCount(cards[4]);
                tv_playerCount.setText(setCountText(playerCount));
                //爆牌--直接賭金回預設值
                if(playerCount>=22){
                    Toast.makeText(this,"輸"+tv_betMoney.getText(),Toast.LENGTH_SHORT).show();
                    tv_betMoney.setText("0");
                    btn_startGame.setVisibility(View.VISIBLE);
                    btn_cancel.setVisibility(View.VISIBLE);
                    btn_need.setVisibility(View.INVISIBLE);
                    btn_noNeed.setVisibility(View.INVISIBLE);
                }
                break;
            case 5 :
                playerCard5.setImageResource(imgURI(5));
                countCard++;
                playerCount += transformCount(cards[countCard]);
                tv_playerCount.setText(setCountText(playerCount));
                //爆牌--直接賭金回預設值
                if(playerCount>=22){
                    Toast.makeText(this,"輸"+tv_betMoney.getText(),Toast.LENGTH_SHORT).show();
                    tv_betMoney.setText("0");
                    btn_startGame.setVisibility(View.VISIBLE);
                    btn_cancel.setVisibility(View.VISIBLE);
                    btn_need.setVisibility(View.INVISIBLE);
                    btn_noNeed.setVisibility(View.INVISIBLE);
                //過五關贏 拿回三倍賭金
                }else {
                    Toast.makeText(this,"過五關贏雙倍",Toast.LENGTH_SHORT).show();
                    myMoney =myMoney+  Integer.parseInt(tv_betMoney.getText().toString())*3;
                    tv_myMoney.setText(""+myMoney);
                    tv_betMoney.setText("0");
                    btn_startGame.setVisibility(View.VISIBLE);
                    btn_need.setVisibility(View.INVISIBLE);
                }
                break;
        }

    }

    //不補牌按鈕監聽  AI going
    public void btn_noNeed(View view)  {

        aiSecCard=false;
        btn_need.setVisibility(View.INVISIBLE);
        btn_noNeed.setVisibility(View.INVISIBLE);
        btn_startGame.setVisibility(View.INVISIBLE);
        btn_cancel.setVisibility(View.INVISIBLE);

        new CountDownTimer(3200,800){
            @Override
            public void onTick(long millisUntilFinished) {
                if(aiCount<17){
                    aiSetCard();
                }else{
                    onFinish(); //onFinish會持續執行 表示補到四張牌 會做兩次onFinish
                }
            }
            @Override
            public void onFinish() {
                //TODO AI補牌結束判斷輸贏
                if(Math.floor(aiCount)>=Math.floor(playerCount) && aiCount<22){
                    //ai贏
                    Toast.makeText(getApplicationContext(),"輸"+tv_betMoney.getText(),Toast.LENGTH_SHORT).show();

                }else{
                    //player贏
                    Toast.makeText(getApplicationContext(),"贏"+tv_betMoney.getText(),Toast.LENGTH_SHORT).show();
                    myMoney =myMoney+Integer.parseInt(tv_betMoney.getText().toString())*2;
                    tv_myMoney.setText(myMoney+"");
                }
                tv_betMoney.setText("0");
                btn_startGame.setVisibility(View.VISIBLE); // 開始遊戲恢復 btn_start
                btn_cancel.setVisibility(View.VISIBLE);    // 取消籌碼恢復 btn_cancel
                btn_noNeed.setVisibility(View.VISIBLE);
                this.cancel();

            }
        }.start();





    }

    //ai補牌2~5張
    private void aiSetCard()  {

            if(!aiSecCard){
                Log.v("leo","ai2");
                aiCard2.setImageResource(imgURI(countCard));
                aiCount += transformCount(cards[countCard]);
                tv_aiCount.setText(setCountText(aiCount));
                countCard++;
                aiSecCard=true;
            }else if(aiCard3.getDrawable()==null ){
                aiCard3.setImageResource(imgURI(countCard));
                aiCount += transformCount(cards[countCard]);
                tv_aiCount.setText(setCountText(aiCount));
                countCard++;
            }else if(aiCard4.getDrawable()==null  ){
                aiCard4.setImageResource(imgURI(countCard));
                aiCount += transformCount(cards[countCard]);
                tv_aiCount.setText(setCountText(aiCount));
                countCard++;
            }else if(aiCard5.getDrawable()==null ){
                aiCard5.setImageResource(imgURI(countCard));
                aiCount += transformCount(cards[countCard]);
                tv_aiCount.setText(setCountText(aiCount));
                countCard++;
            }
    }


    //開始遊戲 按鈕監聽
    public void btn_startGame(View view) {
        if(Integer.parseInt(tv_betMoney.getText().toString())>0){
            myMoney =myMoney- Integer.parseInt(tv_betMoney.getText().toString());
            tv_myMoney.setText(""+myMoney);
            initNewGame();
            btn_startGame.setVisibility(View.INVISIBLE);
            btn_cancel.setVisibility(View.INVISIBLE);
            btn_need.setVisibility(View.VISIBLE);
            btn_noNeed.setVisibility(View.VISIBLE);

            playerCard1.setImageResource(imgURI(0));
            playerCard2.setImageResource(imgURI(1));
            aiCard1.setImageResource(imgURI(2));
            aiCard2.setImageResource(imgURI(52));
            countCard =3;

            playerCount = transformCount(cards[0])+transformCount(cards[1]);
            tv_playerCount.setText(setCountText(playerCount));

            //發牌直接BJ 拿回三倍賭金
            if(playerCount >= 11.109f && playerCount <= 11.111f){
                Toast.makeText(this,"BlackJack",Toast.LENGTH_SHORT).show();
                myMoney =myMoney+  Integer.parseInt(tv_betMoney.getText().toString())*3;
                tv_myMoney.setText(""+myMoney);
                tv_betMoney.setText("0");
                btn_startGame.setVisibility(View.VISIBLE);
                btn_cancel.setVisibility(View.VISIBLE);
                btn_need.setVisibility(View.INVISIBLE);
                btn_noNeed.setVisibility(View.INVISIBLE);
            }


            aiCount =transformCount(cards[2]);
            tv_aiCount.setText(setCountText(aiCount));

            Log.v("leo","playCount :"+playerCount);
            Log.v("leo","aiCount :"+aiCount);
        }else {
            Toast.makeText(this,"請先下注再開始遊戲",Toast.LENGTH_SHORT).show();
        }



    }

    //做個工具判斷要怎麼顯示在Player跟AI的 TV_count上
    private String setCountText(float f){
        //A+ J/Q/K
        if(f >= 11.109f && f <=11.111f){
            //BlackJack youWin
            Log.v("leo","blackJack");
            return ""+(int)Math.floor(f);
            //A + A~10
        }else if((int)(Math.floor(f*100)) - (int)(Math.floor(f*10)*10) >0 && f<12){
            return (int)Math.floor(f)+"/"+((int)Math.floor(f)+10);
            //2-K + 2-K
        }else{
            return ""+(int)Math.floor(f);
        }
    }


    //弄個工具來將cards[i] 跟卡片路徑做個連接  i從0開始發
    private int imgURI(int i){
        int  imgMYURI = getResources().getIdentifier(uri+String.format("%02d", cards[i]),null,getPackageName());
//        Log.v("leo","StringFor :"+String.format("%02d", cards[i]));
        return imgMYURI;
    }

    //弄個工具來將cards[i] 跟點數做轉換
    //a:1  ,2-10 ,JQK:10
    private float transformCount(int i){
        float[] countValues ={1.01f,2,3,4,5,6,7,8,9,10,10.1f,10.1f,10.1f};
        int [] temp ={0,1,2,3,4,5,6,7,8,9,10,11,12};
        int position = temp[i%13];
        float value = countValues[position];
        return value;
    }




    //遊戲重新開始
    private void initNewGame(){
        cards = new int[53];
        //給cards0-51的值  52是牌背
        for(int i = 0; i<cards.length;i++){
            cards[i]=i;
        }
        //洗牌 只洗前面52張 第53張是牌背
        for(int i =cards.length-2;i>0;i--){
            int rand = (int)(Math.random()*(i));
            int temp ;
            temp = cards[rand];
            cards[rand]=cards[i];
            cards[i]=temp;
        }

        playerCard1.setImageResource(0);
        playerCard2.setImageResource(0);
        playerCard3.setImageResource(0);
        playerCard4.setImageResource(0);
        playerCard5.setImageResource(0);
        aiCard1.setImageResource(0);
        aiCard2.setImageResource(0);
        aiCard3.setImageResource(0);
        aiCard4.setImageResource(0);
        aiCard5.setImageResource(0);
    }


    //清除籌碼 按鈕監聽 清除回預設值
    public void btn_cancel(View view) {
        tv_betMoney.setText("0");

    }

    public void add100(View view) {
        if(myMoney>=100+Integer.parseInt(tv_betMoney.getText().toString())){
            int i ;
            i=Integer.parseInt(tv_betMoney.getText().toString())+100;
            tv_betMoney.setText(i+"");
        }else {
            Toast.makeText(this,"金額不足",Toast.LENGTH_SHORT).show();
        }

    }

    public void add500(View view) {
        if(myMoney>=500+Integer.parseInt(tv_betMoney.getText().toString())){
            int i ;
            i=Integer.parseInt(tv_betMoney.getText().toString())+500;
            tv_betMoney.setText(i+"");
        }else {
            Toast.makeText(this,"金額不足",Toast.LENGTH_SHORT).show();
        }
    }

    public void add1000(View view) {
        if(myMoney>=1000+Integer.parseInt(tv_betMoney.getText().toString())){
            int i ;
            i=Integer.parseInt(tv_betMoney.getText().toString())+1000;
            tv_betMoney.setText(i+"");
        }else {
            Toast.makeText(this,"金額不足",Toast.LENGTH_SHORT).show();
        }
    }
}

