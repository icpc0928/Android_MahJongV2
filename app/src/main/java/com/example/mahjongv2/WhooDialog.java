package com.example.mahjongv2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;


/**
 * A simple {@link Fragment} subclass.
 */
public class WhooDialog extends Fragment {
    private PlayingActivity playingActivity;
    private RecyclerView rv_dialog_outCards,rv_dialog_handCards;
    private View view;
    private rv_Dialog_HandCardsAdapter rv_dialog_handCardsAdapter;
    private rv_Dialog_OutAdapter rv_dialog_outAdapter;
    private LinearLayoutManager linearLayoutManager,linearLayoutManager2;
    private Button btn_dialog_gotoHome;
    public WhooDialog() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =inflater.inflate(R.layout.fragment_whoo_dialog, container, false);
        rv_dialog_handCards=view.findViewById(R.id.rv_dialog_handCards);
        rv_dialog_outCards=view.findViewById(R.id.rv_dialog_outCards);
        btn_dialog_gotoHome=view.findViewById(R.id.btn_dialog_gotoHome);
        btn_dialog_gotoHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(playingActivity,HomeActivity.class);
                startActivity(intent);
                playingActivity.finish();

            }
        });


        linearLayoutManager=new LinearLayoutManager(inflater.getContext(),RecyclerView.HORIZONTAL,true);
        linearLayoutManager2=new LinearLayoutManager(inflater.getContext(),RecyclerView.HORIZONTAL,true);
        rv_dialog_handCards.setLayoutManager(linearLayoutManager);

        rv_dialog_outCards.setLayoutManager(linearLayoutManager2);
        rv_dialog_handCardsAdapter=new rv_Dialog_HandCardsAdapter();
        rv_dialog_outAdapter=new rv_Dialog_OutAdapter();
        rv_dialog_handCards.setAdapter(rv_dialog_handCardsAdapter);
        rv_dialog_outCards.setAdapter(rv_dialog_outAdapter);
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        playingActivity=(PlayingActivity)context;
    }

    public class rv_Dialog_HandCardsAdapter extends RecyclerView.Adapter<WhooDialog.rv_Dialog_HandCardsAdapter.ViewHolder>{
        @NonNull
        @Override
        public WhooDialog.rv_Dialog_HandCardsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.handcard_topside,null);
            WhooDialog.rv_Dialog_HandCardsAdapter.ViewHolder viewHolder=new WhooDialog.rv_Dialog_HandCardsAdapter.ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull WhooDialog.rv_Dialog_HandCardsAdapter.ViewHolder holder, int position) {
            ImageView iv = holder.iv;
            iv.setImageResource(playingActivity.imgURI(playingActivity.winnerHand.get(position)));
        }

        @Override
        public int getItemCount() {
            return playingActivity.winnerHand.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            ImageView iv;
            public ViewHolder(View v){
                super(v);
                iv=v.findViewById(R.id.iv_handCards);
            }
        }
    }
    public class rv_Dialog_OutAdapter extends RecyclerView.Adapter<WhooDialog.rv_Dialog_OutAdapter.viewHolder>{
        @NonNull
        @Override
        public WhooDialog.rv_Dialog_OutAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.handcard_topside, parent, false);
            WhooDialog.rv_Dialog_OutAdapter.viewHolder vh=new WhooDialog.rv_Dialog_OutAdapter.viewHolder(view);
            //註冊點擊
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull WhooDialog.rv_Dialog_OutAdapter.viewHolder holder, int position) {
            ImageView iv = holder.iv;
            iv.setImageResource(playingActivity.imgURI(playingActivity.winnerOut.get(position)));
        }
        @Override
        public int getItemCount() { return playingActivity.winnerOut.size();  }

        private class viewHolder extends RecyclerView.ViewHolder{
            ImageView iv;
            public viewHolder(@NonNull View itemView) {
                super(itemView);
                iv=itemView.findViewById(R.id.iv_handCards);
            }
        }
    }
}
