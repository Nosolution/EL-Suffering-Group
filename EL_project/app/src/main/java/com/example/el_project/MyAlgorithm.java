package com.example.el_project;

/**
 * Created by ns on 2018/5/16.
 */

public class MyAlgorithm {
    public static int calcScores(int totalTime,int usedTime,int breakCount){
        int breakWeight=0;
        double timeRate=(double)usedTime/totalTime;
        int scores=timeRate>=0.75? 98:(int)(timeRate*100+20);
        while(breakCount>0){
            scores-=(int)(Math.random()*(4+0.4*breakWeight));
            breakWeight++;
            breakCount--;
        }
        return scores;
    }
}
