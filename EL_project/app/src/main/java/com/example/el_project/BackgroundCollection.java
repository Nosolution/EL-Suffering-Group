package com.example.el_project;

import android.graphics.Color;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class BackgroundCollection {
    private List<Integer> backgroundCollection;
    private List<Integer> colorCollection;

    BackgroundCollection(){
        backgroundCollection = new ArrayList<Integer>();
        colorCollection = new ArrayList<Integer>();
        init();
    }

    private void init(){
        backgroundCollection.add(R.drawable.background_1);
        backgroundCollection.add(R.drawable.background_2);
        backgroundCollection.add(R.drawable.background_3);
        backgroundCollection.add(R.drawable.background_4);
        backgroundCollection.add(R.drawable.background_5);
        backgroundCollection.add(R.drawable.background_6);
        backgroundCollection.add(R.drawable.background_7);

        colorCollection.add(Color.rgb(255, 168, 108));
        colorCollection.add(Color.rgb(73, 102, 88));
        colorCollection.add(Color.rgb(141, 167, 242));
        colorCollection.add(Color.rgb(242, 99, 97));
        colorCollection.add(Color.rgb(128, 57, 181));
        colorCollection.add(Color.rgb(98, 168, 153));
        colorCollection.add(Color.rgb(146, 168, 165));
    }

    private int getWeekNum(){

        Calendar calendar = new GregorianCalendar();
        SimpleDateFormat format = new SimpleDateFormat("EEE", Locale.getDefault());
        String week = format.format(calendar.getTime());
        int weekNum = 1;
        switch (week){
            case "Mon": weekNum = 1; break;
            case "Tue": weekNum = 2; break;
            case "Wed": weekNum = 3; break;
            case "The": weekNum = 4; break;
            case "Fri": weekNum = 5; break;
            case "Sat": weekNum = 6; break;
            case "Sun": weekNum = 7; break;
        }
        return weekNum;
    }

    public int getTodayBackground(){
        return backgroundCollection.get(getWeekNum() - 1);
    }

    public int getTodayColor(){
        return colorCollection.get(getWeekNum() - 1);
    }

}
