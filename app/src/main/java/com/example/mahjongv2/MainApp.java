package com.example.mahjongv2;

import android.app.Application;

public class MainApp extends Application {
    public static String RoomId;
    public static String myName;


    @Override
    public void onCreate() {
        super.onCreate();
        RoomId = "";
        myName = "";

    }
}
