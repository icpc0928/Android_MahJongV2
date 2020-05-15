package com.leo0928.mahjongv2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;

public class HomeActivity extends AppCompatActivity {

    private TextView name , email;
    private Button btn_logout , btn_gotoRooms ,btn_gotoSmallGame;
    SessionManager sessionManager;
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
        sessionManager = new SessionManager(this);
        sessionManager.checkLogin();
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
                myService.playkeydown_sound();
                sessionManager.logout();
            }
        });

        //房間列表按鈕監聽
        btn_gotoRooms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //抓完資料庫資料後才能跳轉頁面
                myService.playkeydown_sound();
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
                myService.playkeydown_sound();
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

    @Override
    protected void onStart() {
        super.onStart();
        //繫結Service
        Intent intent=new Intent(this,MyService.class);
        bindService(intent,mConnection,BIND_AUTO_CREATE);
        intent.putExtra("ACTION","NOTPLAY");
        startService(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        myService.pauseMedia();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        myService.playMedia();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //解除繫結
        if (isBind){
            unbindService(mConnection);
        }
        myService.stopMedia();
        Intent intent=new Intent(this,MyService.class);
        stopService(intent);

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
