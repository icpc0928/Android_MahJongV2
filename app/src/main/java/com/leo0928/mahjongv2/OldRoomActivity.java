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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class OldRoomActivity extends AppCompatActivity {


    private TextView myRoomID, player1,player2,player3,player4;
    public String p2="",p3="",p4="" ,myP="";

    SessionManager sessionManager;
    String name;

    //Firebase Database
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private Member obj;
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
        setContentView(R.layout.activity_old_room);

        myRoomID = findViewById(R.id.myRoomID);
        player1 = findViewById(R.id.tv_player1);
        player2 = findViewById(R.id.tv_player2);
        player3 = findViewById(R.id.tv_player3);
        player4 = findViewById(R.id.tv_player4);

        Log.v("leo",MainApp.RoomId);

        //抓自己的名字
        sessionManager = new SessionManager(this);
        HashMap<String,String> user = sessionManager.getUserDetail();
        name = user.get(sessionManager.NAME);
        Log.v("leo","myNameIs: "+name);

        //
        myRoomID.append(MainApp.RoomId);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference(MainApp.RoomId);
        Log.v("leo","++"+myRef.getKey());


        myRef.addListenerForSingleValueEvent(singleListener);
        myRef.addValueEventListener(listener);
    }
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
    //單次監聽取得人員名單 命且將位置補上
    ValueEventListener singleListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            obj = dataSnapshot.getValue(Member.class);

            //get information
            p2=obj.getNames().get(1);
            p3=obj.getNames().get(2);
            p4=obj.getNames().get(3);

           if(p2.equals("")){
               myP="1";
                myRef.child("names").child(myP).setValue(name);
                MainApp.myTurn = 1;

            }else if(p3.equals("")){
               myP="2";
                myRef.child("names").child(myP).setValue(name);
               MainApp.myTurn = 2;

            }else if(p4.equals("")){
               myP="3";
                myRef.child("names").child(myP).setValue(name);
               MainApp.myTurn = 3;

            }else {
               //人數已滿
               backToRooms(null);
           }

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
    ValueEventListener listener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
           obj = dataSnapshot.getValue(Member.class);
            //房主離開...返回
            if(obj.getNames().get(0).equals("")){
                backToRooms(null);
            }
            player1.setText(obj.getNames().get(0));
            player2.setText(obj.getNames().get(1));
            player3.setText(obj.getNames().get(2));
            player4.setText(obj.getNames().get(3));

            //TODO 房主按下開始遊戲後 isReady == true
            Log.v("leo","isReady: "+obj.getIsReady());
            if(obj.getIsReady()){
                sessionManager.createPlayingPath(MainApp.RoomId,MainApp.myTurn);  //將遊戲的path 跟 你是第幾位 放進沙盒 做到斷線重連的機制 NewRoomAct內也有一個
                Intent intent = new Intent(OldRoomActivity.this,PlayingActivity.class);
                myRef.removeEventListener(listener);
                startActivity(intent);
                OldRoomActivity.this.finish();

            }
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
        }
    };


    //返回房間列表監聽事件
    public void backToRooms(View view) {
        if(obj.getNames().get(0).equals("")){

            Toast.makeText(this,"房主離開",Toast.LENGTH_SHORT).show();
        }

        //取消拿到的ID
        MainApp.RoomId="";
        myService.playkeydown_sound();
        myRef.child("names").child(myP).setValue("");

        myRef.removeEventListener(listener);
        myRef.removeEventListener(singleListener);  //這行好像不影響，但保險起見還是關了
        p2=p3=p4="";

        Intent intent = new Intent(OldRoomActivity.this,RoomsActivity.class);
        startActivity(intent);
        OldRoomActivity.this.finish();
    }

}
