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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class RoomsActivity extends AppCompatActivity {

    private Button btn_createRoom ,btn_gotoRoom , btn_gotoHome;
    private ListView listView;
    private static String URL_ROOMS = "http://192.168.0.101/android_register_login/roomlist.php";
    private SimpleAdapter adapter;
    private String[] from = {"id","players"};
    private int[] to ={R.id.item_id,R.id.item_players};
    private LinkedList<HashMap<String,String>> data = new LinkedList<>();





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rooms);

        btn_createRoom = findViewById(R.id.btn_createRoom);
        btn_gotoHome = findViewById(R.id.btn_gotoHome);
        btn_gotoRoom = findViewById(R.id.btn_gotoRoom);
        listView = findViewById(R.id.listView);


        //先把RoomsAct所需的資料從資料庫抓到
        roomList();
        //然後放進ListView
        initListView();


        //返回首頁
        btn_gotoHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RoomsActivity.this,HomeActivity.class);
                startActivity(intent);
                RoomsActivity.this.finish();

            }
        });

        //創建房間按鈕監聽
        btn_createRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RoomsActivity.this,NewRoomActivity.class);
                startActivity(intent);
                RoomsActivity.this.finish();
            }
        });

    }

//顯示listView列表 從資料庫
    private void roomList(){
        Log.v("leo","roomList()");

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_ROOMS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v("leo","response :  "+response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            //拿到Obj叫parseJSON去做
                            parseJSON(jsonObject);

                        } catch (Exception e) {
                            Log.v("leo","catch : "+e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.v("leo","debug4  " + error.toString());
                    }
                })

                /////////////////////////////////////////////
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Log.v("leo","debug5");
                return super.getParams();
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void initListView(){
        adapter = new SimpleAdapter(this,data,R.layout.item,from,to);
        listView.setAdapter(adapter);

    }

    //解拿到response的JSONObject
    private void parseJSON(JSONObject jsonObject){
        try{
            JSONArray idArr = jsonObject.getJSONArray("id");
            JSONArray playersArr = jsonObject.getJSONArray("players");

            //解陣列 放入HashMap
            for(int i = 0;i<idArr.length();i++){
                HashMap<String,String> temp = new HashMap<>();
                temp.put(from[0],idArr.getString(i));
                temp.put(from[1],playersArr.getString(i));
                data.add(temp);
            }
            uiHandler.sendEmptyMessage(0);
        }catch(Exception e ){
            Log.v("leo",e.toString());
        }
    }

    private UIHandler uiHandler = new UIHandler();
    private class UIHandler extends Handler{
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            //告訴UI該改變資訊了
            adapter.notifyDataSetChanged();
        }
    }



}
