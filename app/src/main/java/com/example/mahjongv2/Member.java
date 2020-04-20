package com.example.mahjongv2;

import java.util.ArrayList;

public class Member {
    private String player1,player2,player3,player4;
    private ArrayList<String> names;
    private Boolean isReady;

    public Member(){
        names = new ArrayList<>();

        isReady = false;
    }

    public void addName(String name){
        names.add(name);
    }

    public ArrayList<String> getNames(){
        return names;
    }


    public void setIsReady(Boolean isReady){
        this.isReady = isReady;
    }


    public Boolean getIsReady(){
        return isReady;
    }


}
