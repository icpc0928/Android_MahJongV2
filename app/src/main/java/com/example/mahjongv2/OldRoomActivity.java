package com.example.mahjongv2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;

public class OldRoomActivity extends AppCompatActivity {

    private Button btn_backToRooms;
    private TextView myRoomID, player1,player2,player3,player4;

    SessionManager sessionManager;
    String name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_old_room);

        btn_backToRooms = findViewById(R.id.btn_backToRooms);
        myRoomID = findViewById(R.id.myRoomID);
        player1 = findViewById(R.id.tv_player1);
        player2 = findViewById(R.id.tv_player2);
        player3 = findViewById(R.id.tv_player3);
        player4 = findViewById(R.id.tv_player4);





        //抓自己的名字
        sessionManager = new SessionManager(this);
        HashMap<String,String> user = sessionManager.getUserDetail();
        name = user.get(sessionManager.NAME);
        Log.v("leo","myNameIs: "+name);

    }



    //返回房間列表監聽事件
    public void backToRooms(View view) {
    }
}
