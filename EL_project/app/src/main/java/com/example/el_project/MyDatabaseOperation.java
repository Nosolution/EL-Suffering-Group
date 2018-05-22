package com.example.el_project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
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
        MyDatabaseHelper dbHelper=new MyDatabaseHelper(context,"TaskStore.db",null,4);
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        db.delete("Tasklist","id=?",new String[]{String.valueOf(id)});
    }

    public static int queryLatestTaskId(Context context){
        MyDatabaseHelper dbHelper=new MyDatabaseHelper(context,"TaskStore.db",null,4);
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        Cursor cursor = db.query("Tasklist", null, null, null, null, null, null);
        if(cursor.moveToPosition(cursor.getCount()-1)){
            int id=Integer.parseInt(cursor.getString(cursor.getColumnIndex("id")));
            cursor.close();
            return id;
        }
        else return 0;
    }

    /*
    **放弃任务时更新已进行的时间
     */
    public static void giveUpFinishingTask(Context context,int id,int time){
        MyDatabaseHelper dbHelper=new MyDatabaseHelper(context,"TaskStore.db",null,4);
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        Cursor cursor = db.query("Tasklist", null, "id=?", new String[]{String.valueOf(id)}, null, null, null);
        if(cursor.moveToFirst()){
            int oldTime=cursor.getInt(cursor.getColumnIndex("time_used"));
            ContentValues values = new ContentValues();
            values.put("time_used",time+oldTime);
            db.update("Tasklist",values,"id=?",new String[]{String.valueOf(id)});
        }
    }

    //提供两种查询方法，可分别根据id和任务名查询是否是每日任务
    public static boolean isDailyTask(Context context,int id){
        MyDatabaseHelper dbHelper=new MyDatabaseHelper(context,"TaskStore.db",null,4);
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
        MyDatabaseHelper dbHelper=new MyDatabaseHelper(context,"TaskStore.db",null,4);
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
        MyDatabaseHelper dbHelper=new MyDatabaseHelper(context,"TaskStore.db",null,4);
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
        MyDatabaseHelper dbHelper=new MyDatabaseHelper(context,"TaskStore.db",null,4);
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        Calendar calendar = new GregorianCalendar();
        SimpleDateFormat formatDate = new SimpleDateFormat("yyMMdd", Locale.getDefault());
        ContentValues values = new ContentValues();
        values.put("isdailytask",2);
        values.put("last_finished_date",Integer.parseInt(formatDate.format(calendar.getTime())));
        db.update("Tasklist",values,"id=?",new String[]{String.valueOf(id)});
    }
    public static void setDailyTaskFinished(Context context,String taskName){
        MyDatabaseHelper dbHelper=new MyDatabaseHelper(context,"TaskStore.db",null,4);
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
        MyDatabaseHelper dbHelper=new MyDatabaseHelper(context,"TaskStore.db",null,4);
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        Calendar calendar = new GregorianCalendar();
        SimpleDateFormat formatDate = new SimpleDateFormat("yyMMdd", Locale.getDefault());
        ContentValues values = new ContentValues();
        values.put("isdailytask",1);

        Cursor cursor=db.query("Tasklist",null,"isdailytask=?",new String[]{"2"},null,null,null);
        if(cursor.moveToFirst()){
           do{
                if(cursor.getInt(cursor.getColumnIndex("last_finished_date"))!=Integer.parseInt(formatDate.format(calendar.getTime()))){
//                    idList.add(String.valueOf(cursor.getInt(cursor.getColumnIndex("id"))));
                    db.update("Tasklist",values,"id=?",new String[]{cursor.getString(cursor.getColumnIndex("id"))});
                }
           }while(cursor.moveToNext());
        }
        cursor.close();
//        if(!idList.isEmpty())
//            db.update("Tasklist",values,"id=?",(String[])idList.toArray(new String[idList.size()]));
    }

    public static List<Task> getTaskList(Context context){
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(context, "TaskStore.db", null, 4);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor=db.query("Tasklist",null,null,null,null,null,null);
        if(cursor != null && cursor.moveToFirst()){
            List<Task> taskList=new ArrayList<>();
            do{
                int isDailyTask=cursor.getInt(cursor.getColumnIndex("isdailytask"));
                if(isDailyTask!=2){
                    Task tempTask=new Task(
                            cursor.getInt(cursor.getColumnIndex("id")),
                            cursor.getString(cursor.getColumnIndex("task")),
                            cursor.getString(cursor.getColumnIndex("assumedtime")),
                            cursor.getString(cursor.getColumnIndex("deadline")),
                            cursor.getInt(cursor.getColumnIndex("emergencydegree")),
                            isDailyTask,
                            cursor.getString(cursor.getColumnIndex("comments")),
                            cursor.getInt(cursor.getColumnIndex("time_used"))
                    );
                    taskList.add(tempTask);
                }
            }while(cursor.moveToNext());
            cursor.close();
            Collections.sort(taskList);
            return taskList;
        }
        cursor.close();
        return null;
    }

    public static List<Task> getRecommendedTaskList(Context context){
        List<Task> finalList=getTaskList(context);
        if(finalList != null && finalList.size()>3)
            return finalList.subList(0,3);
        else
            return finalList;
    }

    //任务开始时向数据库插入一条完成信息，此信息只保存具体开始时间，任务名，其他置为默认
    public static String addFinishTaskWithStartTime(Context context, String taskName){
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(context, "TaskStore.db", null, 4);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        Calendar calendar = new GregorianCalendar();
        SimpleDateFormat format = new SimpleDateFormat("yyMMddHHmmss", Locale.getDefault());
        String startTime = format.format(calendar.getTime());
        values.put("detail_time_start",startTime);
        values.put("date", 0);
        values.put("week_count", 0);
        values.put("week", 0);
        values.put("task_name", taskName);
        values.put("task_time_used", 0);
        values.put("statue", 0);
        values.put("break_count", 0);
        db.insert("FinishTaskTable", null, values);
        values.clear();
        return startTime;
    }

    //当任务完成或放弃后修改对应的完成信息，保存完成时日期，总任务耗时，完成状态，切换次数等
    public static void editFinishTaskWhenFinishing(Context context, String startTime, int taskTimeUsed, int statue, int breakCount){
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(context, "TaskStore.db", null, 4);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Calendar calendar = new GregorianCalendar();
        SimpleDateFormat formatDate = new SimpleDateFormat("yyMMdd", Locale.getDefault());
        SimpleDateFormat formatWeekCount = new SimpleDateFormat("yyw", Locale.getDefault());
        SimpleDateFormat formatWeek = new SimpleDateFormat("EEE", Locale.getDefault());
        ContentValues values = new ContentValues();
        values.put("date", Integer.parseInt(formatDate.format(calendar.getTime())));
        values.put("week_count", Integer.parseInt(formatWeekCount.format(calendar.getTime())));
        values.put("week", getWeekNum());
        values.put("task_time_used", taskTimeUsed);
        values.put("statue", statue);
        values.put("break_count", breakCount);

        db.update("FinishTaskTable", values, "detail_time_start = ?", new String[]{startTime});
    }

    //更新任务计时记录表，删除无效记录
    public static void refreshFinishTaskTable(Context context){
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(context, "TaskStore.db", null, 4);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("FinishTaskTable", "week = ?", new String[]{"0"});
    }

    //得到某日总计学习工作时间
    public static int getTotalSomeDayTimeUsed(Context context, int date){
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(context, "TaskStore.db", null, 4);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        int totalTime = 0;

        Cursor cursor = db.query("FinishTaskTable", null, "date = ?",
                new String[]{Integer.toString(date)}, null, null, null);
        if(cursor.moveToFirst()){
            do{
                int timeUsed = cursor.getInt(cursor.getColumnIndex("task_time_used"));
                Log.d("TEST", "getTotalSomeDayTimeUsed: " + timeUsed);
                totalTime += timeUsed;
            }while (cursor.moveToNext());
        }
        cursor.close();
        return totalTime;
    }

    //得到今日总计学习工作时间
    public static int getTotalTimeUsedToday(Context context){
        Calendar calendar = new GregorianCalendar();
        SimpleDateFormat format = new SimpleDateFormat("yyMMdd", Locale.getDefault());
        return getTotalSomeDayTimeUsed(context, Integer.parseInt(format.format(calendar.getTime())));
    }

    //得到一周内每日学习工作时间，从周一开始
    public static int[] getWeekPerDayTimeUsed(Context context, int weekCount){
        int[] timesWeek = new int[7];

        MyDatabaseHelper dbHelper = new MyDatabaseHelper(context, "TaskStore.db", null, 4);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query("FinishTaskTable", null, "week_count = ?",
                new String[]{Integer.toString(weekCount)}, null, null, null);
        if(cursor.moveToFirst()){
            do {
                int week = cursor.getInt(cursor.getColumnIndex("week"));
                timesWeek[week-1] += cursor.getInt(cursor.getColumnIndex("task_time_used"));
            }while (cursor.moveToNext());
        }
        return timesWeek;
    }

    public static int[] getThisWeekPerDayTimeUsed(Context context){
        Calendar calendar = new GregorianCalendar();
        SimpleDateFormat format = new SimpleDateFormat("yyw", Locale.getDefault());
        return getWeekPerDayTimeUsed(context, Integer.parseInt(format.format(calendar.getTime())));
    }

    public static int[] getThisWeekPerDayTimeUsedWithCorrection(Context context, int correction){
        Calendar calendar = new GregorianCalendar();
        SimpleDateFormat format = new SimpleDateFormat("yyw", Locale.getDefault());
        int[] result = getWeekPerDayTimeUsed(context, Integer.parseInt(format.format(calendar.getTime())));
        result[getWeekNum() - 1] += correction;
        return result;
    }

    public static int getWeekNum(){
        Calendar calendar = new GregorianCalendar();
        SimpleDateFormat format = new SimpleDateFormat("EEE", Locale.getDefault());
        String week = format.format(calendar.getTime());
        int weekNum = 1;
        switch (week){
            case "周一":
            case "Mon": weekNum = 1; break;
            case "周二":
            case "Tue": weekNum = 2; break;
            case "周三":
            case "Wed": weekNum = 3; break;
            case "周四":
            case "The": weekNum = 4; break;
            case "周五":
            case "Fri": weekNum = 5; break;
            case "周六":
            case "Sat": weekNum = 6; break;
            case "周日":
            case "Sun": weekNum = 7; break;
        }
        Log.d("TEST", "getWeekNum: " + weekNum + week);
        return weekNum;
    }
}
