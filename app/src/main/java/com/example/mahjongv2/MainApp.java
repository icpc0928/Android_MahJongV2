package com.example.mahjongv2;

import android.app.Application;

public class MainApp extends Application {
    public static String RoomId;
    public static String myName;
    public static int myTurn;


    @Override
    public void onCreate() {
        super.onCreate();
        RoomId = "";
        myName = "";
        myTurn = 0;

    }
}
