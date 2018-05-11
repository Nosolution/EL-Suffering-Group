package com.example.el_project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.net.SocketImpl;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by ns on 2018/5/1.
 */

public class MyDatabaseOperation {

//    private MyDatabaseHelper dbHelper= new MyDatabaseHelper("TaskStore.db", null, 2);

    /*
    传入上下文context与任务id
     */
    public static void deleteTask(Context context,int id){
        MyDatabaseHelper dbHelper=new MyDatabaseHelper(context,"TaskStore.db",null,3);
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        db.delete("Tasklist","id=?",new String[]{String.valueOf(id)});
    }

    public static int queryLatestTaskId(Context context){
        MyDatabaseHelper dbHelper=new MyDatabaseHelper(context,"TaskStore.db",null,3);
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        Cursor cursor = db.query("Tasklist", null, null, null, null, null, null);
        if(cursor.moveToPosition(cursor.getCount()-1)){
            int id=Integer.parseInt(cursor.getString(cursor.getColumnIndex("id")));
            cursor.close();
            return id;
        }
        else return 0;
    }

    //提供两种查询方法，可分别根据id和任务名查询是否是每日任务
    public static boolean isDailyTask(Context context,int id){
        MyDatabaseHelper dbHelper=new MyDatabaseHelper(context,"TaskStore.db",null,3);
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        Cursor cursor=db.query("Tasklist",null,"id=?",new String[]{String.valueOf(id)},null,null,null);
        if(cursor.moveToFirst()){
            String temp=cursor.getString(cursor.getColumnIndex("isdailytask"));
            if(temp.equals("0"))
                return false;
            else
                return true;
        }
        cursor.close();
        return false;

    }
    public static boolean isDailyTask(Context context,String taskName){
        MyDatabaseHelper dbHelper=new MyDatabaseHelper(context,"TaskStore.db",null,3);
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        Cursor cursor=db.query("Tasklist",null,"task=?",new String[]{taskName},null,null,null);
        if(cursor.moveToFirst()){
            String temp=cursor.getString(cursor.getColumnIndex("isdailytask"));
            if(temp.equals("0"))
                return false;
            else
                return true;
        }
        cursor.close();
        return false;
    }

    public static int lastFinishedDate(Context context,int id){
        MyDatabaseHelper dbHelper=new MyDatabaseHelper(context,"TaskStore.db",null,3);
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        Cursor cursor=db.query("Tasklist",null,"id=?",new String[]{String.valueOf(id)},null,null,null);
        if(cursor.moveToFirst()){
            int lastFinishedDate=cursor.getInt(cursor.getColumnIndex("last_finished_date"));
            return lastFinishedDate;
        }
        return 0;
    }

    //方法重载，完成每日任务，更新状态
    public static void setDailyTaskFinished(Context context,int id){
        MyDatabaseHelper dbHelper=new MyDatabaseHelper(context,"TaskStore.db",null,3);
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        Calendar calendar = new GregorianCalendar();
        SimpleDateFormat formatDate = new SimpleDateFormat("yyMMdd", Locale.getDefault());
        ContentValues values = new ContentValues();
        values.put("isdailytask",2);
        values.put("last_finished_date",Integer.parseInt(formatDate.format(calendar.getTime())));
        db.update("Tasklist",values,"id=?",new String[]{String.valueOf(id)});
    }
    public static void setDailyTaskFinished(Context context,String taskName){
        MyDatabaseHelper dbHelper=new MyDatabaseHelper(context,"TaskStore.db",null,3);
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        Calendar calendar = new GregorianCalendar();
        SimpleDateFormat formatDate = new SimpleDateFormat("yyMMdd", Locale.getDefault());
        ContentValues values = new ContentValues();
        values.put("isdailytask",2);
        values.put("last_finished_date",Integer.parseInt(formatDate.format(calendar.getTime())));
        db.update("Tasklist",values,"task=?",new String[]{taskName});
    }

    //刷新所有已完成的每日任务
    public static void refreshAllDailyTask(Context context){
        ArrayList<String> idList=new ArrayList<>();
        MyDatabaseHelper dbHelper=new MyDatabaseHelper(context,"TaskStore.db",null,3);
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        Calendar calendar = new GregorianCalendar();
        SimpleDateFormat formatDate = new SimpleDateFormat("yyMMdd", Locale.getDefault());
        ContentValues values = new ContentValues();
        values.put("isdailytask",1);

        Cursor cursor=db.query("Tasklist",null,"isdailytask=?",new String[]{"2"},null,null,null);
        if(cursor.moveToFirst()){
           do{
                if(cursor.getInt(cursor.getColumnIndex("last_finished_date"))!=Integer.parseInt(formatDate.format(calendar.getTime()))){
                    idList.add(String.valueOf(cursor.getInt(cursor.getColumnIndex("id"))));
                }
           }while(cursor.moveToNext());
        }
        cursor.close();
        db.update("Tasklist",values,"id=?",idList.toArray(new String[idList.size()]));
    }


    //完成每日任务，传入上下文context和id
//    public static void finishDailyTask(Context context,int id){
//        MyDatabaseHelper dbHelper=new MyDatabaseHelper(context,"TaskStore.db",null,1);
//        SQLiteDatabase db=dbHelper.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put("isdailytask","2");
//        db.update("Tasklist",values,"id=?",new String[]{String.valueOf(id)});
//


    public static String addFinishTaskWithStartTime(Context context, String taskName){
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(context, "TaskStore.db", null, 3);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        Calendar calendar = new GregorianCalendar();
        SimpleDateFormat format = new SimpleDateFormat("yyMMddHHmmss", Locale.getDefault());
        String startTime = format.format(calendar.getTime());
        values.put("detail_time_start",startTime);
        values.put("date", 0);
        values.put("week_count", 0);
        values.put("week", "0");
        values.put("task_name", taskName);
        values.put("task_time_used", 0);
        values.put("statue", 0);
        values.put("break_count", 0);
        db.insert("FinishTaskTable", null, values);
        values.clear();
        return startTime;
    }

    public static void editFinishTaskWhenFinishing(Context context, String startTime, int taskTimeUsed, int statue, int breakCount){
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(context, "TaskStore.db", null, 3);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Calendar calendar = new GregorianCalendar();
        SimpleDateFormat formatDate = new SimpleDateFormat("yyMMdd", Locale.getDefault());
        SimpleDateFormat formatWeekCount = new SimpleDateFormat("yyw", Locale.getDefault());
        SimpleDateFormat formatWeek = new SimpleDateFormat("EEE", Locale.getDefault());
        ContentValues values = new ContentValues();
        values.put("date", Integer.parseInt(formatDate.format(calendar.getTime())));
        values.put("week_count", Integer.parseInt(formatWeekCount.format(calendar.getTime())));
        values.put("week", formatWeek.format(calendar.getTime()));
        values.put("task_time_used", taskTimeUsed);
        values.put("statue", statue);
        values.put("break_count", breakCount);

        db.update("FinishTaskTable", values, "detail_time_start = ?", new String[]{startTime});
    }

    //更新任务计时记录表，删除无效记录
    public static void refreshFinishTaskTable(Context context){
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(context, "TaskStore.db", null, 3);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("FinishTaskTable", "week = ?", new String[]{"0"});
    }

    public static int getTotalSomeDayTimeUsed(Context context, int date){
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(context, "TaskStore.db", null, 3);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query("FinishTaskTable", null, "date = ?",
                new String[]{Integer.toString(date)}, null, null, null);
        if(cursor.moveToFirst()){
            do{
                int timeUsed = cursor.getInt(cursor.getColumnIndex("task_time_used"));
                Log.d("TEST", "getTotalSomeDayTimeUsed: " + timeUsed);
            }while (cursor.moveToNext());
            cursor.close();
        }
        return 0;
    }
}
