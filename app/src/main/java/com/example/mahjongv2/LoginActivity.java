package com.example.mahjongv2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText email , password ;
    private Button btn_login;
    private TextView link_regist;
    private ProgressBar loading;
    private static String URL_LOGIN = "http://192.168.0.101/android_register_login/login.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });

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

                                    Toast.makeText(LoginActivity.this,
                                            "Success \nHello : "+name
                                                    + "\n Nice to see U again",Toast.LENGTH_SHORT)
                                            .show();

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
