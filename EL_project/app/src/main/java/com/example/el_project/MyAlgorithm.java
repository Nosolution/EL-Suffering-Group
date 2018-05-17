package com.example.el_project;

/**
 * Created by ns on 2018/5/16.
 */

public class MyAlgorithm {
    public static int calcScores(int totalTime,int usedTime,int breakCount){
        int[] highestScores={96,97,98,99,100};
        int breakWeight=0;
        double timeRate=(double)usedTime/totalTime;
        int scores=timeRate>=0.75? highestScores[(int)(Math.random()*5)]:(int)(timeRate*100+20);
        while(breakCount>0){
            scores-=(int)(Math.random()*(4+0.4*breakWeight));
            breakWeight++;
            breakCount--;
        }
        return scores;
    }

}
