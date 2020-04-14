package com.example.mahjongv2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class SmallGameActivity extends AppCompatActivity {

    private ImageView playerCard1,playerCard2,playerCard3,playerCard4,playerCard5;
    private ImageView aiCard1,aiCard2,aiCard3,aiCard4,aiCard5;
    private Button btn_startGame,btn_cancel;
    private TextView tv_playerCount;

    private int[] cards ;
    //補牌按鈕計數器
    private int countCard=0;
    private float playerCount;
    String uri = "@drawable/"+"card";





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
                playerCard3.setImageResource(imgURI(3));
                countCard++;
                break;
            case 4 :
                playerCard4.setImageResource(imgURI(4));
                countCard++;
                break;
            case 5 :
                playerCard5.setImageResource(imgURI(5));
                countCard++;
                break;


        }




    }



    //弄個工具來將cards[i] 跟卡片路徑做個連接  i從0開始發
    private int imgURI(int i){
        int  imgMYURI = getResources().getIdentifier(uri+String.format("%02d", cards[i]),null,getPackageName());
        Log.v("leo","StringFor :"+String.format("%02d", cards[i]));
        return imgMYURI;
    }

    //弄個工具來將cards[i] 跟點數做轉換
    //a:1  ,2-10 ,JQK:10
    private float transformCount(int i){
        float[] countValues ={1.1f,2,3,4,5,6,7,8,9,10,10.01f,10.01f,10.01f};
        int [] temp ={0,1,2,3,4,5,6,7,8,9,10,11,12};
        int position = temp[i%13];
        float value = countValues[position];
        return value;
    }


    //不補牌按鈕監聽
    public void btn_noNeed(View view) {

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
    }


    //清除籌碼 按鈕監聽
    public void btn_cancel(View view) {
    }

    //開始遊戲 按鈕監聽
    public void btn_startGame(View view) {

        initNewGame();

        btn_startGame.setVisibility(View.INVISIBLE);
        btn_cancel.setVisibility(View.INVISIBLE);

        playerCard1.setImageResource(imgURI(0));
        playerCard2.setImageResource(imgURI(1));
        aiCard1.setImageResource(imgURI(2));
        aiCard2.setImageResource(imgURI(52));
        countCard =3;

        playerCount = transformCount(cards[0])+transformCount(cards[1]);
        //A+ J/Q/K
        if(playerCount == 11.11f){
            //BlackJack youWin
            Log.v("leo","blackJack");
            tv_playerCount.setText("BJ"+Math.floor(playerCount));
        //JQK+ 2~10  || //JQK+ JQK
        }else if ((int)(playerCount*100) - (int)(Math.floor(playerCount*10)*10) ==1 || playerCount>20) {
            tv_playerCount.setText((int) Math.floor(playerCount) + "");

        }else if(playerCount-(int)(Math.floor(playerCount)) >0) {

            tv_playerCount.setText((int)Math.floor(playerCount)+"/"+((int)Math.floor(playerCount)+10));
        }else{
            tv_playerCount.setText((int)Math.floor(playerCount)+"");
        }

        Log.v("leo",""+playerCount);




    }


}
