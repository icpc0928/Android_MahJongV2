package com.example.mahjongv2;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;

public class OriginMJ {
    private ArrayList<Integer>  p1Hand ,p1Out,p1Flower ,p2Hand,p2Out,p2Flower ,p3Hand,p3Out,p3Flower ,p4Hand,p4Out,p4Flower ;
    private ArrayList<Integer> MJCards ,seaCards, lastCards;
    private int whosTurn , needPon,needEat ,whoWin;
    private boolean isEPGW;

    public OriginMJ(){
        MJCards = new ArrayList<>();
        lastCards = new ArrayList<>();
        seaCards = new ArrayList<>();

        p1Hand = new ArrayList<>();
        p2Hand = new ArrayList<>();
        p3Hand = new ArrayList<>();
        p4Hand = new ArrayList<>();

        p1Out = new ArrayList<>();
        p2Out = new ArrayList<>();
        p3Out = new ArrayList<>();
        p4Out = new ArrayList<>();

        p1Flower = new ArrayList<>();
        p2Flower = new ArrayList<>();
        p3Flower = new ArrayList<>();
        p4Flower = new ArrayList<>();

        seaCards = new ArrayList<>();

        whosTurn = -1;





    }

    public void addMJCards(int[] cards){
        for (int card : cards){
            MJCards.add(card);
        }

    }
    public ArrayList<Integer> getMJCards(){
        return MJCards;
    }

    public void addLastCards(ArrayList<Integer> temp){
        lastCards = (ArrayList<Integer>)temp.clone();
        lastCards.remove(lastCards.size()-1);
    }
    public ArrayList<Integer> getLastCards(){

        return lastCards;
    }

    public ArrayList<Integer> findMyHand(int x){
        if(x ==0){
            return p1Hand;
        }else if(x ==1){
            return p2Hand;
        }else if(x == 2){
            return p3Hand;
        }else{
            return p4Hand;
        }
    }


    public ArrayList<Integer> findMyOut(int x){
        if(x ==0){
            return p1Out;
        }else if(x ==1){
            return p2Out;
        }else if(x == 2){
            return p3Out;
        }else{
            return p4Out;
        }
    }
    public ArrayList<Integer> findMyFlower(int x){
        if(x ==0){
            return p1Flower;
        }else if(x ==1){
            return p2Flower;
        }else if(x == 2){
            return p3Flower;
        }else{
            return p4Flower;
        }
    }

    //此方法為房主於newRoomAct 點開始遊戲後自動發牌 所以只執行一次
    public void setAllHand(){
        for(int i =0 ; i<4;i++){
            for (int j =0 ; j<4 ;j++){
                p1Hand.add(lastCards.get(i*16+j));
                p2Hand.add(lastCards.get(i*16+j+4));
                p3Hand.add(lastCards.get(i*16+j+8));
                p4Hand.add(lastCards.get(i*16+j+12));
            }
        }
        Collections.sort(p1Hand,Collections.<Integer>reverseOrder());
        Collections.sort(p2Hand,Collections.<Integer>reverseOrder());
        Collections.sort(p3Hand,Collections.<Integer>reverseOrder());
        Collections.sort(p4Hand,Collections.<Integer>reverseOrder());
        for(int i = 0; i<64 ;i ++){
            lastCards.remove(0);    //發牌時一次性將剩餘牌數前面64張(16*4)刪除，所以 setAllHand 方法只能做一次
        }
    }
    public void setAllOut(){
        p1Out.add(0);
        p2Out.add(0);
        p3Out.add(0);
        p4Out.add(0);
    }
    public ArrayList<Integer> setSeaCards(){
        return this.seaCards;
    }


    public void setMyHand(ArrayList<Integer> myHand ){
        if (MainApp.myTurn==0){
            p1Hand = myHand;
        }else if(MainApp.myTurn==1){
            p2Hand = myHand;
        }else if(MainApp.myTurn==2){
            p3Hand = myHand;
        }else if(MainApp.myTurn==3){
            p4Hand = myHand;
        }
    }
    public void setMyOut(ArrayList<Integer> myOut){
        if (MainApp.myTurn==0){
            p1Out = myOut;
        }else if(MainApp.myTurn==1){
            p2Out = myOut;
        }else if(MainApp.myTurn==2){
            p3Out = myOut;
        }else if(MainApp.myTurn==3){
            p4Out = myOut;
        }
    }


    public ArrayList<Integer> getP1Hand(){
        return p1Hand;
    }
    public ArrayList<Integer> getP2Hand(){
        return p2Hand;
    }
    public ArrayList<Integer> getP3Hand(){
        return p3Hand;
    }
    public ArrayList<Integer> getP4Hand(){
        return p4Hand;
    }

    public ArrayList<Integer> getP1Out(){
        return p1Out;
    }
    public ArrayList<Integer> getP2Out(){
        return p2Out;
    }
    public ArrayList<Integer> getP3Out(){
        return p3Out;
    }
    public ArrayList<Integer> getP4Out(){
        return p4Out;
    }

    public void setSeaCards(ArrayList<Integer> seaCards){
        this.seaCards = seaCards;
    }

    public ArrayList<Integer> getSeaCards(){
        return seaCards;
    }

    public void setWhosTurn(int x){
         whosTurn=x;
    }

    public int getWhosTurn(){
        return whosTurn;
    }

    public void setIsEPGW(boolean x){
        isEPGW=x;
    }
    public boolean getIsEPGW(){
        return isEPGW;
    }



}
