package com.example.mahjongv2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;

public class HomeActivity extends AppCompatActivity {

    private TextView name , email;
    private Button btn_logout , btn_gotoRooms;
    SessionManager sessionManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //SessionManager.java
        sessionManager = new SessionManager(this);
        sessionManager.checkLogin();




        name = findViewById(R.id.name);
        email= findViewById(R.id.email);
        btn_logout = findViewById(R.id.btn_logout);
        btn_gotoRooms = findViewById(R.id.btn_gotoRooms);

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
                Intent intent = new Intent(HomeActivity.this,RoomsActivity.class);
                startActivity(intent);
                HomeActivity.this.finish();


            }
        });


    }
}
