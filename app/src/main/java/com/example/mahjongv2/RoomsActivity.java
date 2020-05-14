package com.example.mahjongv2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

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

    private Button btn_createRoom ,btn_gotoRoom , btn_gotoHome , btn_refreshList;
    private ListView listView;

//    private static String URL_ROOMS = "http://192.168.0.101/android_register_login/roomlist.php";
    private static String URL_ROOMS = "http://leo0928.synology.me/android_register_login/roomlist.php";

    private SimpleAdapter adapter;
    private String[] from = {"id","players"};
    private int[] to ={R.id.item_id,R.id.item_players};
    private LinkedList<HashMap<String,String>> data = new LinkedList<>();
    private String selectedRoomId="";
    public String selectedRoomPlayers="";
    private View selectedView = null;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rooms);

        btn_createRoom = findViewById(R.id.btn_createRoom);
        btn_gotoHome = findViewById(R.id.btn_gotoHome);
        btn_gotoRoom = findViewById(R.id.btn_gotoRoom);
        btn_refreshList = findViewById(R.id.btn_refreshList);
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
        //重新整理列表按鈕監聽
        btn_refreshList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.clear();
                roomList();
                initListView();
            }
        });
        //加入房間按鈕監聽
        btn_gotoRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedRoomId != ""){
                    MainApp.RoomId = selectedRoomId;

                    Intent intent = new Intent(RoomsActivity.this,OldRoomActivity.class);


                    startActivity(intent);
                    RoomsActivity.this.finish();
                }else {
                    //TODO 可以做SnackBar 或吐司麵包
                }

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

//顯示listView列表 從資料庫
    private void roomList(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_ROOMS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v("leo","roomListResponse="+response);
                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            //拿到Obj叫parseJSON去做
                            parseJSON(jsonObject);
                        } catch (Exception e) {
                            Log.v("leo","roomListJSON Exc : "+e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.v("leo","roomList()RespErr:   " + error.toString());
                    }
                })
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


    //adapter 跟 listView要幹的事情
    private void initListView(){
        //開一個adapter將資料放到裡面
        adapter = new SimpleAdapter(this,data,R.layout.item,from,to);
        //將adapter放入listView
        listView.setAdapter(adapter);
        //listView單選監聽
        listView.setOnItemClickListener(onItemClickListener);
    }

    //ListView單選監聽事件
    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(selectedView != null){
                selectedView.setBackgroundColor(Color.alpha(256));
            }
            selectedView = view;
            selectedView.setBackgroundResource(R.drawable.gradient_listview);
//            Log.v("leo","data:"+data.get(position));//拿到ListView內的資料的物件(HashMap)
//            Log.v("leo","="+data.get(position).get("id"));//用HashMap 以 物件屬性名稱 取得該值
            selectedRoomId = data.get(position).get("id");
            selectedRoomPlayers= data.get(position).get("players");


        }
    };

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
