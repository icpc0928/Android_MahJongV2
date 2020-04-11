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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class NewRoomActivity extends AppCompatActivity {
    private Button btn_starGame , btn_gotoRooms;
    private TextView myRoomID;
    private ListView listView;
    private SimpleAdapter adapter;
    private String[] from = {"players"};
    private int[] to ={R.id.item_players};
    private LinkedList<HashMap<String,String>> data = new LinkedList<>();
    private static String URL_ROOM = "http://192.168.0.101/android_register_login/newroom.php";
    private static String URL_CREATE= "http://192.168.0.101/android_register_login/createroom.php";
    private static String URL_DELETE= "http://192.168.0.101/android_register_login/deleteroom.php";
    SessionManager sessionManager;
    String name  , lastId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_room);

        //抓自己的名字
        sessionManager = new SessionManager(this);
        HashMap<String,String> user = sessionManager.getUserDetail();
        name = user.get(sessionManager.NAME);

        btn_gotoRooms = findViewById(R.id.btn_gotoRooms);
        btn_starGame = findViewById(R.id.btn_startGame);
        listView = findViewById(R.id.listView);
        myRoomID = findViewById(R.id.myRoomID);

        //一進來之後馬上創建一個房間
        createRoom();

        //返回 房間列表(RoomsAct)按鈕監聽
        btn_gotoRooms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //返回要做刪除房間的資料
                deleteRoom();
                //再將頁面關閉
                Intent intent = new Intent(NewRoomActivity.this,RoomsActivity.class);
                startActivity(intent);
                NewRoomActivity.this.finish();

            }
        });


    }

    //右邊的房間列表
    private void playList(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_ROOM,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v("leo","PList response : "+response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            //拿到Obj叫parseJSON去做
                            parseJSON(jsonObject);

                        } catch (Exception e) {
                            Log.v("leo","PList catch : "+e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.v("leo","PList ResErr : " + error.toString());
                    }
                })
                {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String,String> params = new HashMap<>();
                        params.put("lastId",lastId);
                        Log.v("leo",lastId);
                        return params;
                    }
                };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void parseJSON(JSONObject jsonObject){
        try {
            JSONArray players = jsonObject.getJSONArray("players");
            for(int i =0; i<players.length();i++){
                HashMap<String,String> temp = new HashMap<>();
                temp.put(from[0],players.getString(i));
                data.add(temp);
            }
            uiHandler.sendEmptyMessage(0);
        }catch (Exception e ){
            Log.v("leo",e.toString());
        }
    }
    private void initListView(){
        adapter = new SimpleAdapter(this,data,R.layout.item,from,to);
        listView.setAdapter(adapter);
    }
    private UIHandler uiHandler = new UIHandler();
    private class UIHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            adapter.notifyDataSetChanged();
        }
    }


    //創建一筆房間資料表  並且拿出最後一個資料表的ID(也就是自己的)
    private void createRoom(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_CREATE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        lastId = response;
                        //去做右邊成員清單 的工作
                        playList();
                        //然後顯示到ListView
                        initListView();
                        //把TextView上的數字顯示為創出來房間的ID
                        myRoomID.setText("房間號："+lastId);
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
                        Log.v("leo",response);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.v("leo",error.toString());

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

}
