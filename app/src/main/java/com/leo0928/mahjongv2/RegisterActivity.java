package com.leo0928.mahjongv2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText name , email , password , c_password;
    private Button btn_regist;
    private ProgressBar loading;
    String mname,mEmail,mPassword,mc_password;
//    private static String URL_REGIST="http://192.168.0.101/android_register_login/register.php";
    private static String URL_REGIST="http://leo0928.synology.me/android_register_login/register.php";
    SessionManager sessionManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        loading = findViewById(R.id.loading);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        c_password = findViewById(R.id.c_password);
        btn_regist = findViewById(R.id.btn_regist);



        //註冊按鈕監聽
        btn_regist.setOnClickListener(new View.OnClickListener() {



            @Override
            public void onClick(View v) {
                getString();
                if( !mname.isEmpty()&!mEmail.isEmpty()& !mPassword.isEmpty()){
                    Regist();
                }else{
                    //如果帳號或密碼空白於EditText顯示錯誤訊息
                    name.setError("name不得為空");
                    email.setError("Email不得為空!");
                    password.setError("Password不得為空!");
//                    c_password.setError("您還沒輸入");
                }

            }
        });
    }

    public void getString(){
        mname = name.getText().toString().trim();
        mEmail = email.getText().toString().trim();
        mPassword = password.getText().toString().trim();
        mc_password = c_password.getText().toString().trim();
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

    //btn做的監聽
    private void Regist(){
        //先跑圈圈
        loading.setVisibility(View.VISIBLE);
        btn_regist.setVisibility(View.GONE);
        //.trim() 移除前面所有空白字元
        final String name_str = this.name.getText().toString().trim();
        final String email_str = this.email.getText().toString().trim();
        final String password_str = this.password.getText().toString().trim();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_REGIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            if(success.equals("1")){
                                Toast.makeText(RegisterActivity.this,"註冊成功!",Toast.LENGTH_SHORT).show();
                                //抓完資料庫資料後才能跳轉頁面
                                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                                startActivity(intent);
                                RegisterActivity.this.finish();
                            }else{
                                //這一步只有帳號重複的可能
                                //清空輸入
                                name.setText("");
                                Toast.makeText(RegisterActivity.this,"已經有相同帳號存在!",Toast.LENGTH_SHORT).show();
                                loading.setVisibility(View.GONE);
                                btn_regist.setVisibility(View.VISIBLE);
                        }
                        } catch (Exception e) {
                            Log.v("leo","Exception:  "+e.toString());
                            e.printStackTrace();
                            Toast.makeText(RegisterActivity.this,"註冊發生錯誤!  " + e.toString(),Toast.LENGTH_SHORT).show();
                            loading.setVisibility(View.GONE);
                            btn_regist.setVisibility(View.VISIBLE);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(RegisterActivity.this,"連線發生問題!  " + error.toString(),Toast.LENGTH_SHORT).show();
                Log.v("leo","ERROR Response : "+error.toString());
                loading.setVisibility(View.GONE);
                btn_regist.setVisibility(View.VISIBLE);
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                //前面的name是資料庫的name 後面的name是這裡的name = findViewbyid的name
                params.put("name",name_str);
                params.put("email",email_str);
                params.put("password",password_str);
                return params;

            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);


    }

}
