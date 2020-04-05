package com.example.mahjongv2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class RoomsActivity extends AppCompatActivity {

    private Button btn_createRoom ,btn_gotoRoom , btn_gotoHome;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rooms);

        btn_createRoom = findViewById(R.id.btn_createRoom);
        btn_gotoHome = findViewById(R.id.btn_gotoHome);
        btn_gotoRoom = findViewById(R.id.btn_gotoRoom);

        btn_gotoHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(RoomsActivity.this,HomeActivity.class);
                startActivity(intent);
                RoomsActivity.this.finish();
            }
        });
    }
}
