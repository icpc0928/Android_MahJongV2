package com.example.mahjongv2;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class OriginMJ {
    private ArrayList<Integer>  p1Hand ,p1Out,p1Flower ,p2Hand,p2Out,p2Flower ,p3Hand,p3Out,p3Flower ,p4Hand,p4Out,p4Flower;
    private ArrayList<Integer> MJCards ,seaCards, lastCards;
    private int whosTurn , needPon,needEat ,whoWin;

    public OriginMJ(){
        MJCards = new ArrayList<>();
        lastCards = new ArrayList<>();
        seaCards = new ArrayList<>();

        p1Hand = new ArrayList<>();



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


}
