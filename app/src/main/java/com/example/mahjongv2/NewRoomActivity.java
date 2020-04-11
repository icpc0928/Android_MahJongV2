package com.example.mahjongv2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewRoomActivity extends AppCompatActivity {
    private Button btn_starGame , btn_gotoRooms;
    private TextView myRoomID,player1,player2,player3,player4;

    private static String URL_CREATE= "http://192.168.0.101/android_register_login/createroom.php";
    private static String URL_DELETE= "http://192.168.0.101/android_register_login/deleteroom.php";
    SessionManager sessionManager;
    String name  , lastId;

    //Firebase-Database
    private FirebaseDatabase database;
    private DatabaseReference myRef;


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

        //返回 房間列表(RoomsAct)按鈕監聽
        btn_gotoRooms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //也要刪除Firebase內的資料  會閃退但是還是會刪除資料  原因在於Firebase資料庫監聽
                myRef = database.getReference(lastId);
                myRef.removeValue();

                //返回要做刪除房間的資料
                deleteRoom();


                //再將頁面關閉
                Intent intent = new Intent(NewRoomActivity.this,RoomsActivity.class);
                startActivity(intent);
                NewRoomActivity.this.finish();



            }
        });


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
                        //去新增人員名單
                        setFirebase();
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
                        Log.v("leo","DeleteRoom");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
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
        Member member = new Member();
        member.addName(name);
        member.addName("");
        member.addName("");
        member.addName("");

        member.setIsReady(false);
        myRef = database.getReference(lastId);

        //fireBase資料庫監聽   會閃退 跟退出鍵 刪除Firebase 資料有關
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Member obj = dataSnapshot.getValue(Member.class);
                player1.setText(obj.getNames().get(0));
                player2.setText(obj.getNames().get(1));
                player3.setText(obj.getNames().get(2));
                player4.setText(obj.getNames().get(3));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        myRef.setValue(member);









    }



}
