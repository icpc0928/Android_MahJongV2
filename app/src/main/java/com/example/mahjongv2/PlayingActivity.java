package com.example.mahjongv2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class PlayingActivity extends AppCompatActivity {
    private RecyclerView recyclerView ;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView.Adapter adapter ;
    private TextView tv_test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing);

        //測試用
        tv_test = findViewById(R.id.tv_test);

        recyclerView=findViewById(R.id.rv_myHandCards);
        linearLayoutManager=new LinearLayoutManager(this,RecyclerView.HORIZONTAL,true);


        //測試用
        tv_test.setText("iam: "+MainApp.myTurn);


    }

    private class myAdapter extends RecyclerView.Adapter{

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }

        private class viewHolder extends RecyclerView.ViewHolder{

            public viewHolder(@NonNull View itemView) {
                super(itemView);
            }
        }

    }
}
