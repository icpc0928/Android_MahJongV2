package com.leo0928.mahjongv2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewRoomActivity extends AppCompatActivity {
    private Button btn_starGame , btn_gotoRooms;
    private TextView myRoomID,player1,player2,player3,player4;

        //http://leo0814.synology.me/android_register_login
//    private static String URL_CREATE= "http://192.168.0.101/android_register_login/createroom.php";
//    private static String URL_DELETE= "http://192.168.0.101/android_register_login/deleteroom.php";
    private static String URL_CREATE= "http://leo0928.synology.me/android_register_login/createroom.php";
    private static String URL_DELETE= "http://leo0928.synology.me/android_register_login/deleteroom.php";
    private static String URL_SETROOMNUM="http://leo0928.synology.me/android_register_login/setroomnum.php";
    SessionManager sessionManager;
    String name  , lastId;
    Timer timer = new Timer();


    //Firebase-Database
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private  Member member;

    //第二個Firebase
    private DatabaseReference gameRef;
    private OriginMJ MJObj;

    private MyService myService;
    private boolean isBind;
    private ServiceConnection mConnection=new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            //假如有連到,操作 iBinder
            MyService.LocalBinder binder=(MyService.LocalBinder)iBinder;
            myService=binder.getService();
            isBind=true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBind=false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_room);

        btn_gotoRooms = findViewById(R.id.btn_gotoRooms);
        btn_starGame = findViewById(R.id.btn_startGame);
        myRoomID = findViewById(R.id.myRoomID);
        player1 = findViewById(R.id.tv_player1);
        player2 = findViewById(R.id.tv_player2);
        player3 = findViewById(R.id.tv_player3);
        player4 = findViewById(R.id.tv_player4);

        database = FirebaseDatabase.getInstance();

        //抓自己的名字
        sessionManager = new SessionManager(this);
        HashMap<String,String> user = sessionManager.getUserDetail();
        name = user.get(sessionManager.NAME);

        //一進來之後馬上創建一個房間  INSERT INTO 一筆資料
        createRoom();
        MainApp.myTurn =0;
    }
    //只要此頁面離開自動刪除 兩個資料庫 並將此頁面結束 所有指向

    @Override
    protected void onStart() {
        super.onStart();
        //繫結Service
        Intent intent=new Intent(this,MyService.class);
        bindService(intent,mConnection,BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //解除繫結
        if (isBind){
            unbindService(mConnection);
        }
        Intent intent=new Intent(this,MyService.class);
        stopService(intent);
    }
    @Override
    protected void onPause() {
        myRef.child("names").child("0").setValue("");
        //解除Firebase的監聽
        myRef.removeEventListener(listener);
        //延遲執行刪除資料庫行為
        timer.schedule(removeFirebaseDataBase,8000);
        //返回要做刪除房間的資料
        deleteRoom();
        //將此頁面關閉並強制指回房間列表的頁面  此地有點怪，感覺點擊"開始遊戲'跳轉頁面後不知道這裡會不會有問題   且走且看  TODO 是不是能夠不用這行??
//        backToRooms(null);
        super.onPause();
    }
    //創建一筆房間資料表  並且拿出最後一個資料表的ID(也就是自己的)   然後要再創一個Firebase的房間供四人
    private void createRoom(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_CREATE,
                //Response只會回傳最後一個資料的ID 所以就是這間房間的ID
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        lastId = response;
                        //lastId後面有太多\r\n 用正規表示法將其去除
                        Pattern pattern = Pattern.compile("\r|\n|\\s*");
                        Matcher matcher = pattern.matcher(lastId);
                        lastId = matcher.replaceAll("");

                        myRoomID.setText("房間號："+lastId);
                        MainApp.RoomId=lastId;
                        //去新增人員名單
                        setFirebase();

                        //右側四個TextView的fireBase資料庫監聽   會閃退 跟退出鍵 刪除Firebase 資料有關
                        myRef.addValueEventListener(listener);
                }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.v("leo","createR ResErr"+ error.toString());
                    }
                }
        )
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("name",name);
                return params;
            }
        }
        ;
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    //按下返回鍵後要刪除 我們創建出來資料庫的房間
    private void deleteRoom(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_DELETE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v("leo","goodRes"+response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.v("leo","delRoom ResErr"+error);
                    }
                })
                {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String,String> params = new HashMap<>();
                        params.put("lastId",lastId);
                        return params;
                    }
                };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void setFirebase(){
        member = new Member();
        member.addName(name);
        member.addName("");
        member.addName("");
        member.addName("");
        member.setIsReady(false);
        myRef = database.getReference(lastId);
        myRef.setValue(member);
    }


    //右側四個TextView監聽事件
    ValueEventListener listener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Member obj = dataSnapshot.getValue(Member.class);
            player1.setText(obj.getNames().get(0));
            player2.setText(obj.getNames().get(1));
            player3.setText(obj.getNames().get(2));
            player4.setText(obj.getNames().get(3));
            int players =0 ;
            for( int i=0;i<obj.getNames().size();i++){
                if(!obj.getNames().get(i).equals("")){
                    players++;
                }
            }
            setRoomNum(players);  //改變RoomsList人數
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
        }
    };

//返回按鈕監聽
    public void backToRooms(View view) {
        //再將頁面關閉
        myService.playkeydown_sound();
        Intent intent = new Intent(NewRoomActivity.this,RoomsActivity.class);
        startActivity(intent);
        NewRoomActivity.this.finish();
    }

    TimerTask removeFirebaseDataBase = new TimerTask() {
        @Override
        public void run() {
            //也要刪除Firebase內的資料  會閃退但是還是會刪除資料  原因在於Firebase資料庫監聽
            myRef.removeValue();
        }
    };

    //開始遊戲按鈕 先判斷四人在場 改isReady=true Intent
    public void startGame(View view) {
//        if(){

            if(!player2.getText().toString().equals("") && !player3.getText().toString().equals("") && !player4.getText().toString().equals("")){  //測試
                myService.playkeydown_sound();

            int[] cards =  {11,11,11,11,12,12,12,12,13,13,13,13,14,14,14,14,15,15,15,15,16,16,16,16,17,17,17,17,18,18,18,18,19,19,19,19,    //萬
                            21,21,21,21,22,22,22,22,23,23,23,23,24,24,24,24,25,25,25,25,26,26,26,26,27,27,27,27,28,28,28,28,29,29,29,29,    //筒
                            31,31,31,31,32,32,32,32,33,33,33,33,34,34,34,34,35,35,35,35,36,36,36,36,37,37,37,37,38,38,38,38,39,39,39,39,    //條
                            41,41,41,41,42,42,42,42,43,43,43,43,44,44,44,44,45,45,45,45,46,46,46,46,47,47,47,47,                            //東南西北中發白
                            51,52,53,54,55,56,57,58,60};                                                                                    //花 + 牌背

            boolean removeFlower = true;       //這裡設定要不要移除花
            washCards(cards,removeFlower);    //洗牌 true為不玩花

            MJObj = new OriginMJ();
            MJObj.addMJCards(cards);
            MJObj.addLastCards(MJObj.getMJCards());
            MJObj.setAllHand();     //初始化p1~p4的手牌  且刪除lastCards所發出去的牌
            MJObj.removeFlower(removeFlower);
            MJObj.setAllOut();      //初始化p1~p4吃碰牌
            MJObj.setWhosTurn(0);  //一開始由0號摸牌
            MJObj.setIsEPGW(false);
            MJObj.setIsTimeStop(false);
            MJObj.setIsWhoo(false);
            MJObj.setDecision();
            MJObj.addPlayersList(player1.getText().toString());
            MJObj.addPlayersList(player2.getText().toString());
            MJObj.addPlayersList(player3.getText().toString());
            MJObj.addPlayersList(player4.getText().toString());
            MJObj.setWinnerLoser(4,4);
            MJObj.setPlayEPGWMusic(0);




            gameRef = database.getReference(lastId+"gaming");
            gameRef.setValue(MJObj);

            sessionManager.createPlayingPath(lastId,MainApp.myTurn);   //將遊戲的path 跟 你是第幾位 放進沙盒 做到斷線重連的機制 OldRoomAct內也有一個















            myRef.child("isReady").setValue(true);
            //Intent  果然跳轉後經過onPouse會出問題  這裡暫時先不要finish
                Intent intent = new Intent(NewRoomActivity.this,PlayingActivity.class);
                startActivity(intent);
                NewRoomActivity.this.finish();


        }else{
            Toast.makeText(this, "人數未滿無法開始", Toast.LENGTH_SHORT).show();
        }
    }

    //洗牌程序
    private int[] washCards(int[] cards,boolean removeFlower){
        //洗牌 只洗前面144張 第145張是牌背

        int playFlower =0;
        if(removeFlower){
            playFlower=8;
        }
        for(int i =cards.length-2-playFlower;i>0;i--){
            int rand = (int)(Math.random()*(i));
            int temp ;
            temp = cards[rand];
            cards[rand]=cards[i];
            cards[i]=temp;
        }

        return cards;
    }
    //有人進來就改變RoomsList人數
    private void setRoomNum(final int players){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_SETROOMNUM,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("roomID",lastId);
                params.put("players",Integer.toString(players));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
