package com.leo0928.mahjongv2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SessionManager {

    SharedPreferences sharedPreferences;
    SharedPreferences playingPreferences;
    public SharedPreferences.Editor editor , playingEditor;
    public Context context ;
    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "LOGIN";

    private static final String LOGIN = "IS_LOGIN";
    public static final String NAME = "NAME";
    public static final String EMAIL = "EMAIL";

    private static final String PLAY_NAME = "PLAYING";
    public String PLAYING = "IS_PLAYING";
    public String ROOM_ID ="ROOM_ID";
    public String MY_TURN="MY_TURN";


//SessionManager
    public SessionManager(Context context) {
        this.context = context;

        sharedPreferences = context.getSharedPreferences(PREF_NAME,PRIVATE_MODE);
        playingPreferences= context.getSharedPreferences(PLAY_NAME,PRIVATE_MODE);

        editor = sharedPreferences.edit();
        playingEditor = playingPreferences.edit();
    }

    //只有在LoginAct中 LOGIN按鈕按下確實有拿到帳號跟名稱後才能 創造Session 將需要的資料傳到editor
    public void createSession(String name,String email){
        editor.putBoolean(LOGIN,true);
        editor.putString(NAME,name);
        editor.putString(EMAIL,email);
        editor.apply();
    }





    //檢查有沒有Login機制  如果沒有預設沒有
    public boolean isLogin(){
        return sharedPreferences.getBoolean(LOGIN,false);

    }



    public void checkLogin(){
        //配合檢查機制
        //如果isLogin為否(預設為否) 則將頁面轉到LoginAct 並將HomeAct.finish //表示說不曾登入過或已登出進來後都先到LoginAct
        if(!this.isLogin()){
            Intent i = new Intent(context,LoginActivity.class);
            context.startActivity(i);
            ((HomeActivity) context).finish();
        }
    }

    //抓取user的ID 跟Email
    public HashMap<String,String> getUserDetail(){
        HashMap<String,String> user = new HashMap<>();
        user.put(NAME,sharedPreferences.getString(NAME,null));
        user.put(EMAIL,sharedPreferences.getString(EMAIL,null));

        return user;
    }



    //如果logout的話 把原本存入的editor清空 並且回到LoginAct畫面 並將HomeAct.finish
    public void logout(){
        editor.clear();
        editor.commit();
        Intent i = new Intent(context,LoginActivity.class);
        context.startActivity(i);
        ((HomeActivity) context).finish();
    }


    // 5/13 多做playing path的斷線重練機制 創造時先給值後續調用
    public void createPlayingPath(String roomId,int myTurn){
        playingEditor.putBoolean(PLAYING,true);
        playingEditor.putString(ROOM_ID,roomId);
        playingEditor.putInt(MY_TURN,myTurn);
        playingEditor.apply();
    }

    public boolean isPlaying(){
        return playingPreferences.getBoolean(PLAYING,false);
    }

    //檢查機制 如果正在遊玩中退出 則進入主畫面(HomeAct後自動跳轉到Playing)  這句應該不用做 應該只要上面的isPlaying做判斷即可
//    public void checkPlaying(){
//        if(this.isPlaying()){
//            Intent intent = new Intent(context,PlayingActivity.class);
//            context.startActivity(intent);
//            ((HomeActivity)context).finish();
//        }
//    }

    public String getROOM_ID(){

        return playingPreferences.getString(ROOM_ID,null);
    }
    public int getMY_TURN(){
        return playingPreferences.getInt(MY_TURN,0);
    }


    //要放在遊戲結束dialog 返回主頁按鈕的listener
    public void gameOver(){
        playingEditor.clear();
        playingEditor.commit();
    }
}
