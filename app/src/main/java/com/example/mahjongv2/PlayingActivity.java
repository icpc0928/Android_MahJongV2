package com.example.mahjongv2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
    public LinearLayoutManager p1_linearLayoutManager,p2_linearLayoutManager,p3_linearLayoutManager,p4_linearLayoutManager,p1Out_linearLayoutManager,p2Out_linearLayoutManager,p3Out_linearLayoutManager,p4Out_linearLayoutManager;
    private GridLayoutManager gridLayoutManager,p1_gridLayoutManager;
    private RZItemTouchHelperCallback mycallback;
    private RecyclerView rv_p1Hand,rv_p2Hand,rv_p3Hand,rv_p4Hand,rv_sea,rv_p1Out,rv_p2Out,rv_p3Out,rv_p4Out;
    private Button btn_mask;
    private ImageView iv_p2GetCard,iv_p3GetCard,iv_p4GetCard;
    public Timer timer=new Timer();

    public ArrayList<Integer> p1Hand,p2Hand,p3Hand,p4Hand,seaCards,p1Out,temp_p1Out,p2Out,p3Out,p4Out ,winnerHand,winnerOut;
    private TextView count;

    private p1_HansListAdapter p1_handadapter;
    private p2_HansListAdapter p2_handadapter;
    private p3_HansListAdapter p3_handadapter;
    private p4_HansListAdapter p4_handadapter;
    private seaAdapter seaAdapter;
    private p1Out_ListAdapter p1Out_listAdapter;
    private p2Out_ListAdapter p2Out_listAdapter;
    private p3Out_ListAdapter p3Out_listAdapter;
    private p4Out_ListAdapter p4Out_listAdapter;

    String uri = "@drawable/"+"mj";
    String uril = "@drawable/"+"mjl";
    String urir = "@drawable/"+"mjr";

    private FragmentManager frgm=getSupportFragmentManager();
    private FragmentTransaction frgT;
    private framlayout  framlayout;
    private EatList eatList;
    private  WhooDialog whooDialog;



    //Firebase
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    public OriginMJ MJObj ;

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
        rv_p1Out=findViewById(R.id.rv_p1Out);
        rv_p2Out=findViewById(R.id.rv_p2Out);
        rv_p3Out=findViewById(R.id.rv_p3Out);
        rv_p4Out=findViewById(R.id.rv_p4Out);
        count=findViewById(R.id.count);


        //firebase
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
        p1Out =new ArrayList<>();
        p2Out =new ArrayList<>();
        p3Out =new ArrayList<>();
        p4Out =new ArrayList<>();
        winnerHand = new ArrayList<>();
        winnerOut = new ArrayList<>();
        temp_p1Out=new ArrayList<>();







        //設置,調整手牌item的配置
//        p1_gridLayoutManager=new GridLayoutManager(this,16,RecyclerView.VERTICAL,true);
        p1_linearLayoutManager=new LinearLayoutManager(this,RecyclerView.HORIZONTAL,true);
        rv_p1Hand.setLayoutManager(p1_linearLayoutManager);
        //設置手牌的調變
        p1_handadapter=new p1_HansListAdapter();
        rv_p1Hand.setAdapter(p1_handadapter);
        //設定子view的滑動動畫
        rv_p1Hand.setItemAnimator(new DefaultItemAnimator());
        //建立ItemTouchHelper實例,把我們的adapter當成監聽傳進去
        mycallback=new RZItemTouchHelperCallback((ItemMoveSwipeListener) p1_handadapter);
        mycallback.setiDragListener(new IDragListener() {
            @Override
            public void deleteState(boolean delete) {
                if (delete){
                    //假設拖曳到刪除區域
                    //更改刪除區域顯示的顏色
                    //顯示出牌的字以提示使用者
                    //view1.setBackgroundColor(Color.DKGRAY);
                }else{
                    //view1.setBackgroundColor(Color.alpha(256));
                }
            }

            @Override
            public void dragState(boolean State) {
                    //是否在拖曳狀態,控制顯示,不然會閃爍
                if (State){
                    //是
                    //view1.setVisibility(View.VISIBLE);
                }else{
                    //否
                    //view1.setVisibility(View.INVISIBLE);
                }
            }
        });
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(mycallback);
        itemTouchHelper.attachToRecyclerView(rv_p1Hand);

        //吃碰區
        p1Out_linearLayoutManager=new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false);
        p1Out_listAdapter=new p1Out_ListAdapter();
        rv_p1Out.setLayoutManager(p1Out_linearLayoutManager);
        rv_p1Out.setAdapter(p1Out_listAdapter);
        rv_p1Out.addItemDecoration(new MyItemDecoration());

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
        //設置其他三家吃碰區
        p2Out_linearLayoutManager=new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        p3Out_linearLayoutManager=new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false);
        p4Out_linearLayoutManager=new LinearLayoutManager(this,RecyclerView.VERTICAL,true);
        p2Out_listAdapter=new p2Out_ListAdapter();
        p3Out_listAdapter=new p3Out_ListAdapter();
        p4Out_listAdapter=new p4Out_ListAdapter();
        rv_p2Out.setLayoutManager(p2Out_linearLayoutManager);
        rv_p3Out.setLayoutManager(p3Out_linearLayoutManager);
        rv_p4Out.setLayoutManager(p4Out_linearLayoutManager);
        rv_p2Out.setAdapter(p2Out_listAdapter);
        rv_p3Out.setAdapter(p3Out_listAdapter);
        rv_p4Out.setAdapter(p4Out_listAdapter);
        rv_p2Out.addItemDecoration(new MyItemDecoration());
        rv_p3Out.addItemDecoration(new MyItemDecoration());
        rv_p4Out.addItemDecoration(new MyItemDecoration());


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

            //遊戲結束dialog
            if(MJObj.getIsWhoo() && MJObj.getIsTimeStop() && MJObj.getIsEPGW()){
                winnerHand=MJObj.findMyHand(MJObj.getWinnerLoser().get(0));
                winnerOut=MJObj.findMyOut(MJObj.getWinnerLoser().get(0));
                openWhoofragment(MJObj.getWinnerLoser().get(0),MJObj.getWinnerLoser().get(1),MJObj.getSeaCards().get(MJObj.getSeaCards().size()-1));
            }


            //下家檢查有沒有人要吃碰槓胡 若有 觸發下面條件且讓時間暫停
            if(!MJObj.getIsEPGW() &&  !MJObj.getIsTimeStop()  && MJObj.getWhosTurn()==MainApp.myTurn && MJObj.getSeaCards().size()>0){
                if(allEPGW()){
                   MJObj.setIsEPGW(true);
                   myRef.setValue(MJObj);
                   return;
                }
            }
            //如果P2發現有人要EPGW 且 第一次執行到這(第二次為T直到有人打牌 且  非打牌那位    才要讓其他人判斷"我自己"要不要吃   (避免自己打了牌之後顯示要不要吃碰槓胡)
            if(MJObj.getIsEPGW()  && !MJObj.getIsTimeStop() && (MJObj.getWhosTurn()+3)%4 !=MainApp.myTurn && MJObj.getSeaCards().size()>0 ){
                EPGW();
                return;
            }

            //如果 接下來該我摸牌                   且  沒人EPGW
            // 才能摸牌    //這裡基本上只跟P2(下家)有關
            if(MJObj.getWhosTurn()==MainApp.myTurn && !MJObj.getIsEPGW() && !MJObj.getIsWhoo()){

                btn_mask.setVisibility(View.INVISIBLE);
                //摸牌
                p1Hand.add(0,MJObj.getLastCards().get(MJObj.getLastCards().size()-1));
                MJObj.getLastCards().remove(MJObj.getLastCards().size()-1);
                MJObj.setMyHand(p1Hand);
                myGW();

            }


                //假設我是1p,且是第一輪出牌
                // 摸牌  if內寫MJObj.getWhosTurn()==MainApp.myTurn

//                if(false ){
//
//                    btn_mask.setVisibility(View.INVISIBLE);
//                    iv_p2GetCard.setVisibility(View.INVISIBLE);
//                    iv_p3GetCard.setVisibility(View.INVISIBLE);
//                    iv_p4GetCard.setVisibility(View.INVISIBLE);
//                    //摸牌
//                    p1Hand.add(0,MJObj.getLastCards().get(MJObj.getLastCards().size()-1));
//                    MJObj.getLastCards().remove(MJObj.getLastCards().size()-1);
//                    MJObj.setMyHand(p1Hand);
//                    //還沒上船firebase
//                }else{//假設我是2p,3p,4p,執行這裡
//                    if(MJObj.getWhosTurn()==(MainApp.myTurn+1)%4){
//                        iv_p2GetCard.setVisibility(View.VISIBLE);
//                        iv_p3GetCard.setVisibility(View.INVISIBLE);
//                        iv_p4GetCard.setVisibility(View.INVISIBLE);
//                    }else if(MJObj.getWhosTurn()==(MainApp.myTurn+2)%4){
//                        iv_p2GetCard.setVisibility(View.INVISIBLE);
//                        iv_p3GetCard.setVisibility(View.VISIBLE);
//                        iv_p4GetCard.setVisibility(View.INVISIBLE);
//                    }else if(MJObj.getWhosTurn()==(MainApp.myTurn+3)%4){
//                        iv_p2GetCard.setVisibility(View.INVISIBLE);
//                        iv_p3GetCard.setVisibility(View.INVISIBLE);
//                        iv_p4GetCard.setVisibility(View.VISIBLE);
//                    }
//                    btn_mask.setVisibility(View.VISIBLE);
//                }

            p1Hand= MJObj.findMyHand((MainApp.myTurn+0)%4);     //TODO myTrun= 0|1|2|3; 0=>自己
            p2Hand= MJObj.findMyHand((MainApp.myTurn+1)%4);
            p3Hand= MJObj.findMyHand((MainApp.myTurn+2)%4);
            p4Hand= MJObj.findMyHand((MainApp.myTurn+3)%4);
            p1Out = MJObj.findMyOut((MainApp.myTurn+0)%4);      //TODO myTrun= 0|1|2|3; 0=>自己
            p2Out = MJObj.findMyOut((MainApp.myTurn+1)%4);
            p3Out = MJObj.findMyOut((MainApp.myTurn+2)%4);
            p4Out = MJObj.findMyOut((MainApp.myTurn+3)%4);

            p1_handadapter.notifyDataSetChanged();
            p2_handadapter.notifyDataSetChanged();
            p3_handadapter.notifyDataSetChanged();
            p4_handadapter.notifyDataSetChanged();
            p1Out_listAdapter.notifyDataSetChanged();
            p2Out_listAdapter.notifyDataSetChanged();
            p3Out_listAdapter.notifyDataSetChanged();
            p4Out_listAdapter.notifyDataSetChanged();
            seaAdapter.notifyDataSetChanged();


            seaCards=MJObj.getSeaCards();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    //弄個工具來將cards[i] 跟卡片路徑做個連接  i從0開始發
    public int imgURI(int i){
        int getCardsImg = getResources().getIdentifier(uri+i,null,getPackageName());
        return getCardsImg;
    }
    public int imgURIl(int i){
        int getCardsImg = getResources().getIdentifier(uril+i,null,getPackageName());
        return getCardsImg;
    }
    public int imgURIr(int i){
        int getCardsImg = getResources().getIdentifier(urir+i,null,getPackageName());
        return getCardsImg;
    }

    //參數帶入前 winner,loser是 0~3需轉換為名稱 gunCard為海底陣列最後一張的值 也需轉
    public void openWhoofragment(int winner ,int loser, int gunCard){
        Log.v("leo","贏家:"+MJObj.getPlayersList().get(winner));
        Log.v("leo","輸家:"+MJObj.getPlayersList().get(loser));
        Log.v("leo","尾張imgRes:"+imgURI(gunCard));



        whooDialog=new WhooDialog(MJObj.getPlayersList().get(winner),MJObj.getPlayersList().get(loser),imgURI(gunCard));
        frgT=frgm.beginTransaction();
        frgT.add(R.id.framlayout,whooDialog).commit();
    }

    //給fragment使用的
    //關閉fragment
    public void closeFragment(){
        frgT=frgm.beginTransaction();
        frgT.remove(framlayout).commit();
    }
    public void closeEatList(){

        frgT=frgm.beginTransaction();
        frgT.remove(eatList).commit();
    }

    //點選到eatlist fragment
    public void gotoEatList(){

        eatList=new EatList();
        frgT=frgm.beginTransaction();
        frgT.add(R.id.framlayout,eatList).commit();

    }
    //顯示出吃了什麼
    public void Eatwhat(int position){//由EatList呼叫,在EatList時已經把可以吃的選項全部存在temp,現在要依照點了哪個item,把選的丟到p1Out
        if(p1Out.size()==1){
            p1Out.remove(0);
        }
        if (position==0){//選了第一個
            for (int i=0;i<3;i++){
                p1Out.add(temp_p1Out.get(i));
            }
            p1Out.add(0);//配合四張一組,吃只有三張,第四張為0

            //取得手牌,並刪除
            p1Hand.remove(temp_p1Out.get(0));
            p1Hand.remove(temp_p1Out.get(2));
        }
        else if (position==1){//選了第二個,可能有3
            for (int i=3;i<6;i++){
                p1Out.add(temp_p1Out.get(i));
            }
            p1Out.add(0);
            //取得手牌,並刪除
            p1Hand.remove(temp_p1Out.get(3));
            p1Hand.remove(temp_p1Out.get(5));
        }else if (position==2){//選了第三個
            for (int i=6;i<9;i++){
                p1Out.add(temp_p1Out.get(i));
            }
            p1Out.add(0);
            //取得手牌,並刪除
            p1Hand.remove(temp_p1Out.get(6));
            p1Hand.remove(temp_p1Out.get(8));
        }
        seaCards.remove(seaCards.size()-1);//移除海底最後一張
        int count=seaAdapter.getItemCount();
        seaAdapter.notifyItemChanged(count);
        p1Out_listAdapter.notifyDataSetChanged();
        temp_p1Out.clear();//清空暫存區
        //TODO 上傳MJObj 並且讓你能打牌
        new Thread(new countdown()).start();
        updateMJObj(true,0);
    }
    //顯示出碰了什麼
    public void Pongwhat(){
        int lastSeaCard=MJObj.getSeaCards().get(MJObj.getSeaCards().size() - 1);
        if(p1Out.size()==1){
            p1Out.remove(0);
        }
        for (int i=0;i<3;i++){
            p1Out.add(lastSeaCard);
            if (i<=1){
                p1Hand.remove(p1Hand.indexOf(lastSeaCard));
            }
        }
        p1Out.add(0);//吃碰的第四張為0
        Log.v("leo",p1Out.toString());
        //更新畫面
        seaCards.remove(seaCards.size()-1);//移除海底最後一張

        //TODO 上傳MJObj
        new Thread(new countdown()).start();

        updateMJObj(true,0);


    }
    public void Gongwhat(boolean isSelfGong){
        if(p1Out.size()==1){
            p1Out.remove(0);
        }

        if(isSelfGong==true){
            int theGongCard;
            for(int i=0;i<p1Hand.size();i++){
                theGongCard=p1Hand.get(i);
                for(int j=i;j<p1Hand.size();j++){
                    int count =0;
                    if(p1Hand.get(j).equals(theGongCard)){
                        count++;
                        if(count==4){
                            //...
                            for(int x=0;x<4;x++){
                                p1Out.add(theGongCard);
                                p1Hand.remove(theGongCard);
                            }
                            break;
                        }
                    }
                }

            }


        }else if(isSelfGong==false){
            int lastSeaCard=MJObj.getSeaCards().get(MJObj.getSeaCards().size() - 1);
            for (int i=0;i<4;i++){
                p1Out.add(lastSeaCard);
                if (i<=2){
                    p1Hand.remove(p1Hand.indexOf(lastSeaCard));
                }
            }
            //更新畫面
            seaCards.remove(seaCards.size()-1);//移除海底最後一張
        }


        //TODO 上傳MJObj
        new Thread(new countdown()).start();
        updateMJObj(false,0);

    }
    public void whooGame(int whoWin,int whoLoser){
        MJObj.setWinnerLoser(whoWin,whoLoser);

//        if(whoWin==whoLoser){       //自摸
//            //傳進dialog getWhosTurn的牌 (無須整理)
//            //誰自摸(玩家名稱)
//            //MJObj.findMyOut(MJObj.getWhosTurn()); 自摸贏家外面的牌
//            //MJObj.findMyHand(MJObj.getWhosTurn());自摸贏家的手牌 Arr
//
//            Log.v("leo","自摸"+MJObj.getPlayersList().get(whoWin));
//            //這上下都有小錯誤，目前winerl跟loser都只有贏家才會給值沒上傳到firebase!!!
//
//        }else{                      //胡牌
//            //whoLoser為放槍玩家的當時排序的順位
//            //seaCards.get(seaCards.size()-1); 放槍的那張牌 int
//            //whoWin 胡牌的(玩家名稱)
//            //MJObj.findMyOut(whoWin);  胡牌家外面的牌
//            //MJObj.findMyHand(whoWin); 胡牌家的手牌
//
//            Log.v("leo","胡牌:"+MJObj.getPlayersList().get(whoWin));
//            Log.v("leo","放槍:"+MJObj.getPlayersList().get(whoLoser));
//        }
        MJObj.setIsWhoo(true);
        MJObj.setIsTimeStop(true);
        updateMJObj(true,0);

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
    public interface IDragListener{
        /**
         * 是否拖曳到出牌區
         * @param delete
         */
        void deleteState(boolean delete);
        /**
         * 是否處於拖曳狀態
         * @param State
         */
        void dragState(boolean State);
    }
    public class RZItemTouchHelperCallback extends ItemTouchHelper.Callback {
        private ItemMoveSwipeListener itemMoveSwipeListener;
        private IDragListener iDragListener;
        private boolean up;
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
            int dragFlags=ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.DOWN | ItemTouchHelper.UP;
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
            //viewHolder.getAdapterPosition()--->移動到別的item上時,移動起點的item的位置
            //target.getAdapterPosition()---->移動到別的item上時,移動終點的item的位置
            int toPosition=target.getAdapterPosition();
            int fromPosition=viewHolder.getAdapterPosition();
            return itemMoveSwipeListener.onItemMove(fromPosition, toPosition);
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
//        /**
//         *  RecyclerView调用onDraw時调用，調用後會再調用 onChildDrawOver
//         *  @dx item item滑動距離
//         */
//        @Override
//        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
//            //控制繪畫
//            //先判斷有沒有設置listener,沒有的話什麼也不執行
//            if (iDragListener==null){
//                return;
//            }
//            //右下角是0,0
//            //左上角是-recyclerView.getWidth(),-recyclerView.getHeight()
//            //假設位置在畫面一半以上,而且是拖曳狀態
//            if (dY<-recyclerView.getHeight()/2 ){
//                iDragListener.deleteState(true);//改變背景,up 此時還是 false
//                //放手-->getAnimationDuration更改up為true,轉而執行以下判斷
//                if (up) {
//                    int position=viewHolder.getLayoutPosition();//獲取點選item在adapter上的位置
//                    //設置該item看不見,原因為remove是在viewHolder動畫執行完成(即回到原本位置後)才會刪除它
//                    seaCards.add(p1Hand.get(position));
//                    seaAdapter.notifyItemChanged(position);
//                    p1Hand.remove(position);
//                    p1_handadapter.notifyItemRemoved(position);
//                    Collections.sort(p1Hand,Collections.<Integer>reverseOrder());
//                    MJObj.setMyHand(p1Hand);            //myRef.child("p"+(MainApp.myTurn%4+1)+"Hand").setValue(p1Hand);
//                    MJObj.setSeaCards(seaCards);        //myRef.child("seaCards").setValue(seaCards);
//
//                    //只要手牌打出去就改Firebase 換下一位
//                    MJObj.setWhosTurn((MainApp.myTurn+1)%4);
//                    myRef.setValue(MJObj); //同步物件 上傳到firebase
//                    //把up改為false,把背景的state改為false,把拖曳狀態改為false
//                    initData();
//                    return;
//                }
//            }else{//假設位置畫面一半以下
//                iDragListener.deleteState(false);//改變背景
//                //把up改為false,避免我第一次出牌但沒移動到出牌區,改出另一張時,手指沒放開就自動觸發up=true
//                initData();
//            }
//            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
//        }
        //手指離開viewHolder後會調用這個函數,用意是指調用動畫前,接著會自動執行onSelectedChanged
        @Override
        public long getAnimationDuration(@NonNull RecyclerView recyclerView, int animationType, float animateDx, float animateDy) {
            up = true;
            return super.getAnimationDuration(recyclerView, animationType, animateDx, animateDy);
        }

        @Override
        public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
            //actionState==0,手指放開;==2,手指點按
            if (actionState==2){
                iDragListener.dragState(true);
            }else if (actionState==0){
                iDragListener.dragState(false);
            }
            super.onSelectedChanged(viewHolder, actionState);
        }

        //設置自己自定義介面,這樣在使用這個類別時,就會要你去實做介面定義的方法
        public void setiDragListener(IDragListener iDragListener){
            this.iDragListener=iDragListener;
        }

        /**
         * 重置
         */
        private void initData() {
            iDragListener.deleteState(false);
            up = false;
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
            Log.v("leo","打牌的Position"+position);
            seaCards.add(p1Hand.get(position));


//            seaAdapter.notifyItemChanged(position);
            seaAdapter.notifyItemChanged(seaCards.size()-1);
            p1Hand.remove(position);
            //改為物件內容
            Collections.sort(p1Hand,Collections.<Integer>reverseOrder());


            //目前可打牌的方式有： 摸牌，吃牌
            //TODO 未來檢察碰牌來的跟 槓牌完摸牌來的
            MJObj.setIsEPGW(false);
            MJObj.setIsTimeStop(false);
            MJObj.originDecision();

            //同步物件上傳到Firebase
            updateMJObj(false,1);
            notifyItemRemoved(position);


            //結束 要記得將Button遮罩開啟
            btn_mask.setVisibility(View.VISIBLE);


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
            iv.setImageResource(imgURIl(60));
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
            iv.setRotation(180);
            iv.setImageResource(imgURI(60));
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
            iv.setImageResource(imgURIr(60));


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
    public class p1Out_ListAdapter extends RecyclerView.Adapter<PlayingActivity.p1Out_ListAdapter.viewHolder>{
        @NonNull
        @Override
        public PlayingActivity.p1Out_ListAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.eat_p_g_item, parent, false);
            PlayingActivity.p1Out_ListAdapter.viewHolder vh=new PlayingActivity.p1Out_ListAdapter.viewHolder(view);
            //註冊點擊
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull PlayingActivity.p1Out_ListAdapter.viewHolder holder, int position) {
            ImageView iv1 = holder.iv1;
            ImageView iv2 = holder.iv2;
            ImageView iv3 = holder.iv3;
            ImageView iv4 = holder.iv4;
            //要判斷是吃碰還是槓,吃碰用不到iv4

            if(p1Out.size()!=1){
                iv1.setImageResource(imgURI(p1Out.get(position*4+0)));
                iv2.setImageResource(imgURI(p1Out.get(position*4+1)));
                iv3.setImageResource(imgURI(p1Out.get(position*4+2)));
                iv4.setImageResource(imgURI(p1Out.get(position*4+3)));
            }
        }
        @Override
        public int getItemCount() {
            int x=p1Out.size()/4;//每4張牌成一個item
            return x;        }

        private class viewHolder extends RecyclerView.ViewHolder{
            ImageView iv1,iv2,iv3,iv4;
            public viewHolder(@NonNull View itemView) {
                super(itemView);
                iv1=itemView.findViewById(R.id.eat1);
                iv2=itemView.findViewById(R.id.eat2);
                iv3=itemView.findViewById(R.id.eat3);
                iv4=itemView.findViewById(R.id.eat4);
            }
        }
    }
    public class p2Out_ListAdapter extends RecyclerView.Adapter<PlayingActivity.p2Out_ListAdapter.viewHolder>{
        @NonNull
        @Override
        public PlayingActivity.p2Out_ListAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.eat_p_g_item_rightside, parent, false);
            PlayingActivity.p2Out_ListAdapter.viewHolder vh=new PlayingActivity.p2Out_ListAdapter.viewHolder(view);
            //註冊點擊
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull PlayingActivity.p2Out_ListAdapter.viewHolder holder, int position) {
            ImageView iv1 = holder.iv1;
            ImageView iv2 = holder.iv2;
            ImageView iv3 = holder.iv3;
            ImageView iv4 = holder.iv4;
            //要判斷是吃碰還是槓,吃碰用不到iv4

            if(p2Out.size()!=1){
                iv1.setImageResource(imgURIl(p2Out.get(position*4+0)));
                iv2.setImageResource(imgURIl(p2Out.get(position*4+1)));
                iv3.setImageResource(imgURIl(p2Out.get(position*4+2)));
                iv4.setImageResource(imgURIl(p2Out.get(position*4+3)));
            }
        }
        @Override
        public int getItemCount() {
            int x=p2Out.size()/4;//每4張牌成一個item
            return x;        }

        private class viewHolder extends RecyclerView.ViewHolder{
            ImageView iv1,iv2,iv3,iv4;
            public viewHolder(@NonNull View itemView) {
                super(itemView);
                iv1=itemView.findViewById(R.id.eat1);
                iv2=itemView.findViewById(R.id.eat2);
                iv3=itemView.findViewById(R.id.eat3);
                iv4=itemView.findViewById(R.id.eat4);
            }
        }
    }
    public class p3Out_ListAdapter extends RecyclerView.Adapter<PlayingActivity.p3Out_ListAdapter.viewHolder>{
        @NonNull
        @Override
        public PlayingActivity.p3Out_ListAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.eat_p_g_item_topside, parent, false);
            PlayingActivity.p3Out_ListAdapter.viewHolder vh=new PlayingActivity.p3Out_ListAdapter.viewHolder(view);
            //註冊點擊
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull PlayingActivity.p3Out_ListAdapter.viewHolder holder, int position) {
            ImageView iv1 = holder.iv1;
            ImageView iv2 = holder.iv2;
            ImageView iv3 = holder.iv3;
            ImageView iv4 = holder.iv4;
            //要判斷是吃碰還是槓,吃碰用不到iv4

            if(p3Out.size()!=1){
                iv1.setImageResource(imgURI(p3Out.get(position*4+0)));
                iv2.setImageResource(imgURI(p3Out.get(position*4+1)));
                iv3.setImageResource(imgURI(p3Out.get(position*4+2)));
                iv4.setImageResource(imgURI(p3Out.get(position*4+3)));
            }
        }
        @Override
        public int getItemCount() {
            int x=p3Out.size()/4;//每4張牌成一個item
            return x;        }

        private class viewHolder extends RecyclerView.ViewHolder{
            ImageView iv1,iv2,iv3,iv4;
            public viewHolder(@NonNull View itemView) {
                super(itemView);
                iv1=itemView.findViewById(R.id.eat1);
                iv2=itemView.findViewById(R.id.eat2);
                iv3=itemView.findViewById(R.id.eat3);
                iv4=itemView.findViewById(R.id.eat4);
            }
        }
    }
    public class p4Out_ListAdapter extends RecyclerView.Adapter<PlayingActivity.p4Out_ListAdapter.viewHolder>{
        @NonNull
        @Override
        public PlayingActivity.p4Out_ListAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.eat_p_g_item_leftside, parent, false);
            PlayingActivity.p4Out_ListAdapter.viewHolder vh=new PlayingActivity.p4Out_ListAdapter.viewHolder(view);
            //註冊點擊
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull PlayingActivity.p4Out_ListAdapter.viewHolder holder, int position) {
            ImageView iv1 = holder.iv1;
            ImageView iv2 = holder.iv2;
            ImageView iv3 = holder.iv3;
            ImageView iv4 = holder.iv4;
            //要判斷是吃碰還是槓,吃碰用不到iv4

            if(p4Out.size()!=1){
                iv1.setImageResource(imgURIr(p4Out.get(position*4+0)));
                iv2.setImageResource(imgURIr(p4Out.get(position*4+1)));
                iv3.setImageResource(imgURIr(p4Out.get(position*4+2)));
                iv4.setImageResource(imgURIr(p4Out.get(position*4+3)));
            }
        }
        @Override
        public int getItemCount() {
            int x=p4Out.size()/4;//每4張牌成一個item
            return x;        }

        private class viewHolder extends RecyclerView.ViewHolder{
            ImageView iv1,iv2,iv3,iv4;
            public viewHolder(@NonNull View itemView) {
                super(itemView);
                iv1=itemView.findViewById(R.id.eat1);
                iv2=itemView.findViewById(R.id.eat2);
                iv3=itemView.findViewById(R.id.eat3);
                iv4=itemView.findViewById(R.id.eat4);
            }
        }
    }





    public class MyItemDecoration extends RecyclerView.ItemDecoration{
        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            if (parent.getChildAdapterPosition(view)!=0){
                outRect.left=20;
            }
        }
    }

    private void updateMJObj(boolean isEPGW,int nextTurn){
        MJObj.setMyHand(p1Hand);
        MJObj.setMyOut(p1Out);
        MJObj.setSeaCards(seaCards);
        MJObj.setIsEPGW(isEPGW);
        MJObj.setWhosTurn((MainApp.myTurn+nextTurn)%4);    //只要手牌打出去就改Firebase 換下一位
        myRef.setValue(MJObj);
    }

    public void changeMyWeight(int player,int weight){
        //去更改自己的權重,什麼都不做,吃碰槓胡,分別是0123
        MJObj.getDecision().set(player,weight);
        myRef.setValue(MJObj);
    }


    private boolean canEat(int lastSeaCard,ArrayList<Integer> whosHand){
        boolean result = false;
        //1.尾張需在11~19.21~29.31~39之間
        if(lastSeaCard <40){
            //2.三種吃牌條件  -2-1 , -1+1, +1+2 (需先將尾張取個位數 ==1 判斷+1.+2  ==9 判斷-1-2  其餘判斷三種)
            if(lastSeaCard%10==1 && whosHand.contains(lastSeaCard+1) && whosHand.contains(lastSeaCard+2)){  //判斷+1.+2
                result= true;
            }else if(lastSeaCard%10==9 && whosHand.contains(lastSeaCard-1) && whosHand.contains(lastSeaCard-2)){  //判斷-1.-2
                result= true;
            }else if((whosHand.contains(lastSeaCard-2)&&whosHand.contains(lastSeaCard-1)) ||(whosHand.contains(lastSeaCard+1)&&whosHand.contains(lastSeaCard-1))
                    ||(whosHand.contains(lastSeaCard+1)&&whosHand.contains(lastSeaCard+2))){
                result= true;
            }
        }
        return result;
    }
    private boolean canPong(int lastSeaCard ,ArrayList<Integer> whosHand){   //僅需判斷手牌中是否有2個以上的lastSeaCard
        boolean result = false;
        int count=0;
        for(int i=0;i<whosHand.size();i++){
            if(lastSeaCard==whosHand.get(i)){
                count++;
                if(count ==2){
                    result=true;
                    break;
                }
            }
        }
        return result;
    }
    private boolean canGong(int lastSeaCard,ArrayList<Integer> whosHand){  //僅需判斷手牌中是否有3個lastSeaCard
        boolean result = false;
        int count=0;
        for(int i=0;i<whosHand.size();i++){
            if(lastSeaCard==whosHand.get(i)){
                count++;
                if(count == 3){
                    result=true;
                    break;
                }
            }
        }
        return result;
    }
    private boolean canWhoo(int lastSeaCard,ArrayList<Integer> whosHand){
        boolean result ;
        WhooAlgorithm whooAlgorithm=new WhooAlgorithm();
        ArrayList<Integer> temp ;
        temp=(ArrayList<Integer>)whosHand.clone();
        temp.add(lastSeaCard);
        Collections.sort(temp);
        result = whooAlgorithm.canWhoo(temp);
        return result;
    }
    private boolean canMyGong(ArrayList<Integer> myHand){
        boolean result = false;
        for(int i=0;i<myHand.size()-1;i++){
            for(int j =i;j<myHand.size();j++){
                int count =0;
                if(myHand.get(i)==myHand.get(j)){
                    count++;
                }
                if(count == 4){
                    result=true;
                }
            }
        }
        return result;
    }


 private void EPGW(){   //現在會進來判斷的只有打牌之外的人
     int lastSeaCard = MJObj.getSeaCards().get(MJObj.getSeaCards().size() - 1);
     //判斷MJObj.p1Hand有沒有能吃碰槓胡的條件
     //把.吃.碰.槓.胡.叫出來
     boolean bEat,bPong,bGong,bWhoo;
     bEat= MJObj.getWhosTurn()==MainApp.myTurn && canEat(lastSeaCard,MJObj.findMyHand(MainApp.myTurn));

     bPong=canPong(lastSeaCard,MJObj.findMyHand(MainApp.myTurn));
     bGong= MJObj.getWhosTurn()!=MainApp.myTurn && canGong(lastSeaCard,MJObj.findMyHand(MainApp.myTurn));
     bWhoo=canWhoo(lastSeaCard,MJObj.findMyHand(MainApp.myTurn));
     if ( bEat||bPong||bGong||bWhoo ) {
         MJObj.setIsTimeStop(true);
         myRef.setValue(MJObj);

         framlayout = framlayout.EatPongGongWhoo(bEat, bPong, bGong, bWhoo);
         frgT=frgm.beginTransaction();
         frgT.add(R.id.framlayout, framlayout).commit();
     }
 }

 private void myGW(){ //判斷摸牌後 "目前的整副牌" 有沒有要 槓牌or自摸
        boolean bGong , bWhoo;
        WhooAlgorithm whooAlgorithm=new WhooAlgorithm();

        bGong=canMyGong(p1Hand);
        bWhoo=whooAlgorithm.canWhoo(p1Hand);
        if(bGong||bWhoo){
            MJObj.setIsTimeStop(true);
            myRef.setValue(MJObj);
            framlayout = framlayout.EatPongGongWhoo(false,false,bGong,bWhoo);
            frgT=frgm.beginTransaction();
            frgT.add(R.id.framlayout, framlayout).commit();
        }
 }

 private boolean allEPGW(){
        boolean epgw=false;

        int last = MJObj.getSeaCards().get(MJObj.getSeaCards().size() - 1);
        if(canEat(last,MJObj.findMyHand(MainApp.myTurn)) || canPong(last,MJObj.findMyHand(MainApp.myTurn)) || canWhoo(last,MJObj.findMyHand(MainApp.myTurn))
            || canPong(last,MJObj.findMyHand((MainApp.myTurn+1)%4)) || canGong(last,MJObj.findMyHand((MainApp.myTurn+1)%4)) || canWhoo(last,MJObj.findMyHand((MainApp.myTurn+1)%4))
            || canPong(last,MJObj.findMyHand((MainApp.myTurn+2)%4)) || canGong(last,MJObj.findMyHand((MainApp.myTurn+2)%4)) || canWhoo(last,MJObj.findMyHand((MainApp.myTurn+2)%4))){
            epgw=true;
        }
        return epgw;
 }

 private class myTimerTask extends TimerTask{

     @Override
     public void run() {

     }
 }
private Handler handler=new MyHandler();


    public class countdown implements Runnable{

        @Override
        public void run() {
            try{
                handler.sendEmptyMessage(0);
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            btn_mask.setVisibility(View.INVISIBLE);
        }

    }


    public OriginMJ getMJObj(){
        return MJObj;
    }
}
