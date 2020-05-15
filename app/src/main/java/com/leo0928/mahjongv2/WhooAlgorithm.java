package com.leo0928.mahjongv2;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;

public class WhooAlgorithm {
    private ArrayList<Integer> temp1,temp2,temp3,temp20;
    public boolean canWhoo(ArrayList<Integer> arrayList){
        boolean result = false;
        temp1 = new ArrayList<>();
        temp2 = new ArrayList<>();
        temp3 = new ArrayList<>();
        temp20 = new ArrayList<>();
        for(int i=0;i<arrayList.size()-1;i++){
            if(arrayList.get(i).equals(arrayList.get(i+1))){
                temp1.add(arrayList.get(i));
                temp1.add(arrayList.get(i+1));
                temp2 = (ArrayList<Integer>)arrayList.clone();
                temp2.remove(temp2.get(i+1));
                temp2.remove(temp2.get(i));
                //TODO 將temp2 >40的牌取出(即為大字)  如果沒有則不做 (全求單吊)
                if(temp2.size() != 0){
                    for(int j=temp2.size()-1;j>=0;j--){
                        if(temp2.get(j)>=40){
                            temp3.add(0,temp2.get(j));
                            temp2.remove(j);
                        }
                    }
                }
                if(temp3.size() ==0 ){        //沒大字
                    // TODO temp2刻順演算();
                    if(isWinning(temp2)){
                        //胡牌
                        Log.v("leo","算完isWinning :"+isWinning(temp2));
                        result = true;
                        break;
                    }else {
                        //沒胡
                        result = false;
                    }
                    // TODO temp3.size 是否為3n
                }else if (temp3.size()%3 ==0){
                    for(int k=0 ; k< (temp3.size()/3);k++){
                        if( Collections.frequency(temp3,temp3.get(k*3)) == 3  ){
                            // TODO  temp2刻順演算(); 大字為 三三一組
                            if(isWinning(temp2)){
                                Log.v("leo","算完isWinning :"+isWinning(temp2));
                                result=true;
                                break;
                            }else {
                                result = false;
                            }

                        }else{
                            // TODO 大字不齊
                            Log.v("leo","此牌無法胡"+temp1.toString()+":"+temp2.toString()+":"+temp3.toString());
                            result=false;
                        }
                    }
                }
                temp1.clear();
                temp3.clear();
            }
        }
        return result;
    }
    private boolean isWinning (ArrayList temp2){
        boolean isWin = false ;
        if(temp2.size()==0) return true;
        temp20 = (ArrayList<Integer>)temp2.clone();
        //需要幾組小組 (最多五組)
        int num = temp2.size()/3;
        //有 num 組 所以要做num次
        for(int i = 0; i<num;i++){
            if(Collections.frequency(temp20,temp20.get(0)) >= 3 ){
//                temp21.add(0,temp20.get(0));
//                temp21.add(1,temp20.get(1));
//                temp21.add(2,temp20.get(2));
                temp20.remove(0);
                temp20.remove(0);
                temp20.remove(0);
                isWin=true;
            }else if(temp20.contains(temp20.get(0)+1)&&temp20.contains(temp20.get(0)+2)){
//                temp21.add(0,temp20.get(0));
//                temp21.add(1,temp20.get(temp20.indexOf(temp20.get(0)+1)));
                int temp ;
                temp = temp20.get(0);
                temp20.remove(temp20.indexOf(temp));
                temp20.remove(temp20.indexOf(temp+1));
                temp20.remove(temp20.indexOf(temp+2));
                isWin=true;
            }else {
                isWin=false;
            }
        }
        return isWin;
    }
}


