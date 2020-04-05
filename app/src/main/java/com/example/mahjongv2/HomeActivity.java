package com.example.mahjongv2;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

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


                //抓完資料庫資料後才能跳轉頁面
                Intent intent = new Intent(HomeActivity.this,RoomsActivity.class);
                startActivity(intent);
                HomeActivity.this.finish();




            }
        });


    }



}
