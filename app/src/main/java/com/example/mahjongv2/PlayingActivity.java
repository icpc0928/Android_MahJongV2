package com.example.mahjongv2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

public class PlayingActivity extends AppCompatActivity {
    private LinearLayoutManager p1_linearLayoutManager,p2_linearLayoutManager,p3_linearLayoutManager,p4_linearLayoutManager;
    private GridLayoutManager gridLayoutManager;
    private RecyclerView rv_p1Hand,rv_p2Hand,rv_p3Hand,rv_p4Hand,rv_sea;
    private Button btn_mask;
    private ImageView iv_p2GetCard,iv_p3GetCard,iv_p4GetCard;

    private ArrayList<Integer> p1Hand,p2Hand,p3Hand,p4Hand,seaCards;


    private p1_HansListAdapter p1_handadapter;
    private p2_HansListAdapter p2_handadapter;
    private p3_HansListAdapter p3_handadapter;
    private p4_HansListAdapter p4_handadapter;
    private seaAdapter seaAdapter;

    String uri = "@drawable/"+"mj";

    //Firebase
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private OriginMJ MJObj ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing);

        //測試用
        rv_p1Hand=findViewById(R.id.rv_p1HandCards);
        rv_p2Hand=findViewById(R.id.rv_p2HandCards);
        rv_p3Hand=findViewById(R.id.rv_p3HandCards);
        rv_p4Hand=findViewById(R.id.rv_p4HandCards);
        iv_p2GetCard=findViewById(R.id.iv_p2GetCard);
        iv_p3GetCard=findViewById(R.id.iv_p3GetCard);
        iv_p4GetCard=findViewById(R.id.iv_p4GetCard);
        rv_sea=findViewById(R.id.rv_sea);
        btn_mask = findViewById(R.id.btn_mask);



        //測試用firebase
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference(MainApp.RoomId+"gaming");
        Log.v("leo","D1");
        myRef.addListenerForSingleValueEvent(singleListener);
        myRef.addValueEventListener(valueEventListener);
        Log.v("leo","D2");
        p1Hand = new ArrayList<>();
        p2Hand = new ArrayList<>();
        p3Hand = new ArrayList<>();
        p4Hand = new ArrayList<>();
        seaCards = new ArrayList<>();








        //設置,調整手牌item的配置
        p1_linearLayoutManager=new LinearLayoutManager(this,RecyclerView.HORIZONTAL,true);
        rv_p1Hand.setLayoutManager(p1_linearLayoutManager);
        //設置手牌的調變器
        p1_handadapter=new p1_HansListAdapter();
        rv_p1Hand.setAdapter(p1_handadapter);
        //設定子view的滑動動畫
        rv_p1Hand.setItemAnimator(new DefaultItemAnimator());
        //建立ItemTouchHelper實例,把我們的adapter當成監聽傳進去
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new RZItemTouchHelperCallback((ItemMoveSwipeListener) p1_handadapter));
        itemTouchHelper.attachToRecyclerView(rv_p1Hand);


        //設置其他三家的手牌
        p2_linearLayoutManager=new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        p3_linearLayoutManager=new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false);
        p4_linearLayoutManager=new LinearLayoutManager(this,RecyclerView.VERTICAL,true);
        rv_p2Hand.setLayoutManager(p2_linearLayoutManager);
        rv_p3Hand.setLayoutManager(p3_linearLayoutManager);
        rv_p4Hand.setLayoutManager(p4_linearLayoutManager);
        //設置手牌的調變器
        p2_handadapter=new p2_HansListAdapter();
        rv_p2Hand.setAdapter(p2_handadapter);
        p3_handadapter=new p3_HansListAdapter();
        rv_p3Hand.setAdapter(p3_handadapter);
        p4_handadapter=new p4_HansListAdapter();
        rv_p4Hand.setAdapter(p4_handadapter);

        //海底
        gridLayoutManager=new GridLayoutManager(this,19);

        //同手牌
        rv_sea.setLayoutManager(gridLayoutManager);
        seaAdapter=new seaAdapter();
        rv_sea.setAdapter(seaAdapter);





    }




    ValueEventListener singleListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            MJObj = dataSnapshot.getValue(OriginMJ.class);

            p1Hand= MJObj.findMyHand((MainApp.myTurn+0)%4);   //TODO myTrun= 0|1|2|3; 0=>自己
            p2Hand= MJObj.findMyHand((MainApp.myTurn+1)%4);
            p3Hand= MJObj.findMyHand((MainApp.myTurn+2)%4);
            p4Hand= MJObj.findMyHand((MainApp.myTurn+3)%4);

            p1_handadapter.notifyDataSetChanged();
            p2_handadapter.notifyDataSetChanged();
            p3_handadapter.notifyDataSetChanged();
            p4_handadapter.notifyDataSetChanged();
            seaCards=MJObj.getSeaCards();
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            MJObj = dataSnapshot.getValue(OriginMJ.class);
            Log.v("leo","DataChange!");
            // if條件內放這個 MJObj.getWhosTurn()==MainApp.myTurn
            if(true){
                btn_mask.setVisibility(View.INVISIBLE);
                iv_p2GetCard.setVisibility(View.INVISIBLE);
                iv_p3GetCard.setVisibility(View.INVISIBLE);
                iv_p4GetCard.setVisibility(View.INVISIBLE);
                //摸牌
                p1Hand.add(0,MJObj.getLastCards().get(MJObj.getLastCards().size()-1));
                MJObj.getLastCards().remove(MJObj.getLastCards().size()-1);
                MJObj.setMyHand(p1Hand);
            }else{
                if(MJObj.getWhosTurn()==(MainApp.myTurn+1)%4){
                    iv_p2GetCard.setVisibility(View.VISIBLE);
                    iv_p3GetCard.setVisibility(View.INVISIBLE);
                    iv_p4GetCard.setVisibility(View.INVISIBLE);
                }else if(MJObj.getWhosTurn()==(MainApp.myTurn+2)%4){
                    iv_p2GetCard.setVisibility(View.INVISIBLE);
                    iv_p3GetCard.setVisibility(View.VISIBLE);
                    iv_p4GetCard.setVisibility(View.INVISIBLE);
                }else if(MJObj.getWhosTurn()==(MainApp.myTurn+3)%4){
                    iv_p2GetCard.setVisibility(View.INVISIBLE);
                    iv_p3GetCard.setVisibility(View.INVISIBLE);
                    iv_p4GetCard.setVisibility(View.VISIBLE);
                }
                btn_mask.setVisibility(View.VISIBLE);
            }
            p1Hand= MJObj.findMyHand((MainApp.myTurn+0)%4);   //TODO myTrun= 0|1|2|3; 0=>自己
            p2Hand= MJObj.findMyHand((MainApp.myTurn+1)%4);
            p3Hand= MJObj.findMyHand((MainApp.myTurn+2)%4);
            p4Hand= MJObj.findMyHand((MainApp.myTurn+3)%4);

            p1_handadapter.notifyDataSetChanged();
            p2_handadapter.notifyDataSetChanged();
            p3_handadapter.notifyDataSetChanged();
            p4_handadapter.notifyDataSetChanged();
            seaCards=MJObj.getSeaCards();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    //弄個工具來將cards[i] 跟卡片路徑做個連接  i從0開始發
    private int imgURI(int i){

        int getCardsImg = getResources().getIdentifier(uri+i,null,getPackageName());
        return getCardsImg;
    }



    ///調配器

    public interface ItemMoveSwipeListener {
        /**
         * 設置1個監聽的interface
         *
         * onItemMove : 當item移動完的時候
         * onItemSwipe : 當item滑動完的時候
         */
        boolean onItemMove(int fromPosition, int toPosition);

        void onItemSwipe(int position);
    }
    public class RZItemTouchHelperCallback extends ItemTouchHelper.Callback {
        private ItemMoveSwipeListener itemMoveSwipeListener;
        // 設定1個帶 ItemMoveSwipeListener 的參數建構式
        public RZItemTouchHelperCallback(ItemMoveSwipeListener itemMoveSwipeListener) {
            this.itemMoveSwipeListener = itemMoveSwipeListener;
        }
        /**
         * 這個方法決定RecyclerView Item可以移動&滑動的方向
         *
         * @param recyclerView
         * @param viewHolder
         * @return
         */
        @Override
        public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            int dragFlags=ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            int swipeFlags = ItemTouchHelper.UP;
            // 如果想讓「移動」或是「滑動」，無效用則設為0即可
            // 再透過 makeMovementFlags()方法去設置
            return makeMovementFlags(dragFlags, swipeFlags);
        }
        /**
         * 移動完成後，要做甚麼事
         *
         * @param recyclerView
         * @param viewHolder   當前的手指正在移動的item
         * @param target       要被交換的item
         * @return 決定當次的移動是否要執行，true 執行 ; false 不執行
         */
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            // 透過itemMoveSwipeListener的onItemMove，讓adapter實作該方法
            return itemMoveSwipeListener.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        }
        /**
         * 滑動完成後，要做甚麼事
         *
         * @param viewHolder
         * @param direction  當前滑動的方向
         */
        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            // 透過itemMoveSwipeListener的onItemSwipe，讓adapter實作該方法
            itemMoveSwipeListener.onItemSwipe(viewHolder.getAdapterPosition());
        }
    }

    public class p1_HansListAdapter extends RecyclerView.Adapter<p1_HansListAdapter.ViewHolder> implements ItemMoveSwipeListener{
        @NonNull
        @Override
        public p1_HansListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.handcard_recyclerview,null);
            p1_HansListAdapter.ViewHolder viewHolder=new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            ImageView iv = holder.iv;
            holder.itemView.setTag(position);//将position保存在itemView的tag中，一边点击时获取
            iv.setImageResource(imgURI(p1Hand.get(position)));

        }

        @Override
        public int getItemCount() {
            return p1Hand.size();
        }

        /////實做介面/////
        @Override
        public boolean onItemMove(int fromPosition, int toPosition) {
            Collections.swap(p1Hand, fromPosition, toPosition);
            notifyItemMoved(fromPosition, toPosition);
            return true;
        }

        //打牌
        @Override
        public void onItemSwipe( int position) {
            seaCards.add(p1Hand.get(position));
            seaAdapter.notifyItemChanged(position);
            p1Hand.remove(position);
            //改為物件內容
            Collections.sort(p1Hand,Collections.<Integer>reverseOrder());
            MJObj.setMyHand(p1Hand);            //myRef.child("p"+(MainApp.myTurn%4+1)+"Hand").setValue(p1Hand);
            MJObj.setSeaCards(seaCards);        //myRef.child("seaCards").setValue(seaCards);



            //只要手牌打出去就改Firebase 換下一位
            MJObj.setWhosTurn((MainApp.myTurn+1)%4);
            myRef.setValue(MJObj); //同步物件


            notifyItemRemoved(position);
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            ImageView iv;
            public ViewHolder(View v){
                super(v);
                iv=v.findViewById(R.id.iv_handCards);
            }
        }
    }

    public class p2_HansListAdapter extends RecyclerView.Adapter<p2_HansListAdapter.ViewHolder>{
        @NonNull
        @Override
        public p2_HansListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.handcard_rightside,null);
            p2_HansListAdapter.ViewHolder viewHolder=new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull p2_HansListAdapter.ViewHolder holder, int position) {
            ImageView iv = holder.iv;
            holder.itemView.setTag(position);//将position保存在itemView的tag中，一边点击时获取
            iv.setImageResource(imgURI(p2Hand.get(position)));
        }

        @Override
        public int getItemCount() {
            return p2Hand.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            ImageView iv;
            public ViewHolder(View v){
                super(v);
                iv=v.findViewById(R.id.iv_handCards);
            }
        }
    }

    public class p3_HansListAdapter extends RecyclerView.Adapter<p3_HansListAdapter.ViewHolder>{
        @NonNull
        @Override
        public p3_HansListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.handcard_topside,null);
            p3_HansListAdapter.ViewHolder viewHolder=new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull p3_HansListAdapter.ViewHolder holder, int position) {
            ImageView iv = holder.iv;
            holder.itemView.setTag(position);//将position保存在itemView的tag中，一边点击时获取
            iv.setImageResource(imgURI(p3Hand.get(position)));
        }

        @Override
        public int getItemCount() {
            return p3Hand.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            ImageView iv;
            public ViewHolder(View v){
                super(v);
                iv=v.findViewById(R.id.iv_handCards);
            }
        }
    }

    public class p4_HansListAdapter extends RecyclerView.Adapter<p4_HansListAdapter.ViewHolder>{
        @NonNull
        @Override
        public p4_HansListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.handcard_leftside,null);
            p4_HansListAdapter.ViewHolder viewHolder=new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull p4_HansListAdapter.ViewHolder holder, int position) {
            ImageView iv = holder.iv;
            holder.itemView.setTag(position);//将position保存在itemView的tag中，一边点击时获取
            iv.setRotation(90);
            iv.setImageResource(imgURI(60));


        }

        @Override
        public int getItemCount() {
            return p4Hand.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            ImageView iv;
            public ViewHolder(View v){
                super(v);
                iv=v.findViewById(R.id.iv_handCards);
            }
        }
    }

    private class seaAdapter extends RecyclerView.Adapter<seaAdapter.viewHolder>{

        @NonNull
        @Override
        public seaAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.tablesea_cards_item, parent, false);
            return new viewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull seaAdapter.viewHolder holder, int position) {
            ImageView iv = holder.iv;
            iv.setImageResource(imgURI(seaCards.get(position)));
        }

        @Override
        public int getItemCount() {
            return seaCards.size();
        }

        private class viewHolder extends RecyclerView.ViewHolder{
            ImageView iv;
            public viewHolder(@NonNull View itemView) {
                super(itemView);
                iv=itemView.findViewById(R.id.table_card);
            }
        }
    }

}
