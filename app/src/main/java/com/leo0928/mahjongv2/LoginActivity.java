package com.leo0928.mahjongv2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText email , password ;
    private Button btn_login;
    private TextView link_regist;
    private ProgressBar loading;
    private View.OnFocusChangeListener textFocusChangeListener;
//    private static String URL_LOGIN = "http://192.168.0.101/android_register_login/login.php";
    private static String URL_LOGIN = "http://leo0928.synology.me/android_register_login/login.php";
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(this);

        loading = findViewById(R.id.loading);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btn_login = findViewById(R.id.btn_login);
        link_regist = findViewById(R.id.link_regist);

        //login按鈕監聽
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //.trim會將輸入的框框前面空白去掉  將email 跟 password 取得文字
                String mEmail = email.getText().toString().trim();
                String mPassword = password.getText().toString().trim();
                myService.playkeydown_sound();

                //如果email跟password "沒有" empty
                if( !mEmail.isEmpty()|| !mPassword.isEmpty()){
                    Login(mEmail,mPassword);
                }else{
                    //如果帳號或密碼空白於EditText顯示錯誤訊息
                    email.setError("Email不得為空!");
                    password.setError("Password不得為空!");
                }

            }
        });

        //Regist按鈕監聽 去RegistarActivity頁面
        link_regist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myService.playkeydown_sound();
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });

        //輸入完第一個後,按下軟鍵盤的右下角,到下一個輸入地方--->按鍵遮擋的問題V
        email.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                myService.playkeydown_sound();
                if (actionId == EditorInfo.IME_ACTION_NEXT) {

                }
                return false;
            }
        });

        //點選textfield內輸入,再點textfield外時,把返回隱藏
        textFocusChangeListener=new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub

                if(v.getId()==email.getId()){
                    //text獲取交點的時候把返回,home隱藏
                    Log.v("wei","wei");
                    hideSystemUI();
                }else{
                    //text失去焦點的時候關閉軟鍵盤
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                }
            }
        };




    }


    //畫面真正可以看到的時間點...就是在此生命周期被執行時。
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    /**
     * SYSTEM_UI_FLAG_IMMERSIVE------------->點出隱藏的狀態欄後,隔不久不隱藏
     * SYSTEM_UI_FLAG_IMMERSIVE_STICKY------>點出隱藏的狀態欄後,隔不久會再隱藏
     * View.SYSTEM_UI_FLAG_LAYOUT_STABLE---->固定住layout的位置
     * View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN 狀態欄上浮(你可以再調顏色)
     * View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION導覽列上浮,也就是看起來浮在activivty上
     * View.SYSTEM_UI_FLAG_FULLSCREEN------->狀態欄隱藏
     * SYSTEM_UI_FLAG_HIDE_NAVIGATION------->隱藏導覽列
     */

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        // Set the content to appear under the system bars so that the
                        //設定系統要顯示的內容,這樣內容才不會因為系統bar隱藏或顯示時縮放
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
        );
    }

    // Shows the system bars by removing all the flags
    // except for the ones that make the content appear under the system bars.
    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }
    //按返回
    long lastExitTime=0;
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
                // 判断2次点击事件时间
                if ((System.currentTimeMillis() - lastExitTime) > 2000) {
                    Toast.makeText(LoginActivity.this, "再按一次退出",Toast.LENGTH_SHORT).show();
                    lastExitTime = System.currentTimeMillis();
                } else {
                    finish();
                }
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
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

    //LOGIN按鈕能被按做的事情
    private void Login(final String email, final String password) {
        //按鈕確定可以按下去後 轉圈圈 並把按鈕GONE 88
        loading.setVisibility(View.VISIBLE);
        btn_login.setVisibility(View.GONE);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.v("leo","Login response = "+response);
                            //拿回來的response我要JSONObject   且該JSONObj 裡面會拿到一個物件名稱叫做success 該物件值放進String success   (在PHP裡面寫成功取得資料回傳result)
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("login");
                            Log.v("leo","String Success: "+success);
                            //當success內的值為1 表示有拿到東西
                            if(success.equals("1")){
                                for(int i = 0; i<jsonArray.length();i++){
                                    //解JSONObject
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    String name = object.getString("name").trim();
                                    String email = object.getString("email").trim();
                                    //只有在LOGIN按下後確實抓到帳號跟名稱 才能創造Session
                                    sessionManager.createSession(name,email);
                                    Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
                                    intent.putExtra("name",name);
                                    intent.putExtra("email",email);
                                    startActivity(intent);
                                    loading.setVisibility(View.GONE);
                                    btn_login.setVisibility(View.VISIBLE);
                                }
                            }
                        } catch (Exception e) {
                            Log.v("leo","Catch Error : "+e.toString());
                            loading.setVisibility(View.GONE);
                            btn_login.setVisibility(View.VISIBLE);
                            Toast.makeText(LoginActivity.this,
                                    "Catch Error : "+e.toString(),Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.setVisibility(View.GONE);
                        btn_login.setVisibility(View.VISIBLE);
                        Log.v("leo","Response Error : "+error.toString());
                        Toast.makeText(LoginActivity.this,
                                "Response Error : "+error.toString(),Toast.LENGTH_SHORT).show();
                    }
                })

                //如果要實作POST Request 必須覆寫getParams 這個方法 取得參數
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("email",email);
                params.put("password",password);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }
}
