package com.example.mahjongv2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    private TextView name , email;
    private Button btn_logout , btn_gotoRooms ,btn_gotoSmallGame;
    SessionManager sessionManager;

    //Firebase
//    private FirebaseDatabase database;
//    private DatabaseReference myRef;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

//        database = FirebaseDatabase.getInstance();
//        myRef = database.getReference();

        //SessionManager.java
//        sessionManager = new SessionManager(this);
//        sessionManager.checkLogin();
//
        //檢查是否正在玩,如果是則進入遊戲畫面
//        if(sessionManager.isPlaying()){
//            //單次監聽詢問該房間是否還在玩,如果是的話就進入，如果沒有的話清空沙盒內的資料
//            final String temp = sessionManager.getROOM_ID()+"gaming";
//            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    for(DataSnapshot childSnapshot: dataSnapshot.getChildren()){
//                        if(childSnapshot.getKey().equals(temp)){
//                            Log.v("leo","hi");
//                            MainApp.myTurn= sessionManager.getMY_TURN();
//                            MainApp.RoomId= sessionManager.getROOM_ID();
//                            Intent intent = new Intent(HomeActivity.this,PlayingActivity.class);
//                            startActivity(intent);
//                            HomeActivity.this.finish();
//                            break;
//                        }
//                    }
//                }
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//                }
//            });
//        }

        name = findViewById(R.id.name);
        email= findViewById(R.id.email);
        btn_logout = findViewById(R.id.btn_logout);
        btn_gotoRooms = findViewById(R.id.btn_gotoRooms);
        btn_gotoSmallGame = findViewById(R.id.btn_gotoSmallGame);

        HashMap<String,String> user = sessionManager.getUserDetail();
        String mName = user.get(sessionManager.NAME);
        String mEmail = user.get(sessionManager.EMAIL);

        name.setText(mName);
        email.setText(mEmail);


        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionManager.logout();
            }
        });

        //房間列表按鈕監聽
        btn_gotoRooms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //抓完資料庫資料後才能跳轉頁面
                Intent intent = new Intent(HomeActivity.this,RoomsActivity.class);
                startActivity(intent);
                HomeActivity.this.finish();

            }
        });

        //單機小遊戲
        btn_gotoSmallGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
                Intent intent = new Intent(HomeActivity.this,SmallGameActivity.class);
                startActivity(intent);
                HomeActivity.this.finish();
            }
        });



    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
        );
    }
}
