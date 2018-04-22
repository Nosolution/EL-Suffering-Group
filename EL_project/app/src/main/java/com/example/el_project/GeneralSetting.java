package com.example.el_project;

/*
*
* 这是控制设置的静态类，较为复杂的采用了各种get和set来控制元素。
* 当为某次打开应用程序第一次获取设置内容时，自动为这次打开应用程序初始化设置。
* 每次调用方法修改设置，自动同步设置内容到SharePreferences。
* 做点修改然后push上去
*
* */

import android.content.Context;
import android.content.SharedPreferences;

public class GeneralSetting {
    private static boolean settingInitialized = false;    //设置是否初始化过了，即是否从SharedPreferences中取出过所有设置了
    private static boolean musicOn = false;               //音乐是否开启
    private static boolean tomatoClockEnable = false;     //番茄钟是否开启
    private static boolean callWhenTimeUpEnable = false;  //是否倒计时到时时提醒
    private static int tomatoClockTime = 25;              //番茄钟设置时长
    private static int tomatoBreakTime = 5;               //番茄钟间隔休息时间

    private static void initializeSetting(Context context){
        SharedPreferences pref = context.getSharedPreferences("general_setting", Context.MODE_PRIVATE);
        musicOn = pref.getBoolean("music_on", false);
        tomatoClockEnable = pref.getBoolean("tomato_clock_enable", false);
        callWhenTimeUpEnable = pref.getBoolean("call_when_time_up_enable", false);
        tomatoClockTime = pref.getInt("tomato_clock_time", 25);
        tomatoBreakTime = pref.getInt("tomato_break_time", 5);

        settingInitialized = true;
    }

    private static void syncSetting(Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences("general_setting", Context.MODE_PRIVATE).edit();
        editor.putBoolean("music_on", musicOn);
        editor.putBoolean("tomato_clock_enable", tomatoClockEnable);
        editor.putBoolean("call_when_time_up_enable", callWhenTimeUpEnable);
        editor.putInt("tomato_clock_time", tomatoClockTime);
        editor.putInt("tomato_break_time", tomatoBreakTime);
        editor.apply();
    }

    public static boolean getMusicOn(Context context){
        if(!settingInitialized){
            initializeSetting(context);
        }
        return musicOn;
    }

    public static void setMusicOn(Context context, boolean enable){
        musicOn = enable;
        syncSetting(context);
    }

    public static boolean getTomatoClockEnable(Context context){
        if(!settingInitialized){
            initializeSetting(context);
        }
        return tomatoClockEnable;
    }

    public static void setTomatoClockEnable(Context context, boolean enable){
        tomatoClockEnable = enable;
        syncSetting(context);
    }

    public static boolean getCallWhenTimeUpEnable(Context context){
        if(!settingInitialized){
            initializeSetting(context);
        }
        return callWhenTimeUpEnable;
    }

    public static void setCallWhenTimeUpEnable(Context context, boolean enable){
        callWhenTimeUpEnable = enable;
        syncSetting(context);
    }

    public static int getTomatoClockTime(Context context){
        if(!settingInitialized)
            initializeSetting(context);
        return tomatoClockTime;
    }

    public static void setTomatoClockTime(Context context, int time){
        tomatoClockTime = time;
        syncSetting(context);
    }

    public static int getTomatoBreakTime(Context context){
        if(!settingInitialized){
            initializeSetting(context);
        }
        return tomatoBreakTime;
    }

    public static void setTomatoBreakTime(Context context, int time){
        tomatoBreakTime = time;
        syncSetting(context);
    }


}
