package com.example.el_project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by ns on 2018/5/1.
 */

public class MyDatabaseOperation {
    /*
    传入上下文context与任务id
     */
    public static void deleteTask(Context context,int id){
        MyDatabaseHelper dbHelper=new MyDatabaseHelper(context,"TaskStore.db",null,1);
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        db.delete("Tasklist","id=?",new String[]{String.valueOf(id)});
    }

    public static int queryLatestTaskId(Context context){
        MyDatabaseHelper dbHelper=new MyDatabaseHelper(context,"TaskStore.db",null,1);
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        Cursor cursor = db.query("Tasklist", null, null, null, null, null, null);
        if(cursor.moveToPosition(cursor.getCount()-1)){
            int id=Integer.parseInt(cursor.getString(cursor.getColumnIndex("id")));
            cursor.close();
            return id;
        }
        else return 0;
    }

    //完成每日任务，传入上下文context和id
    public static void finishDailyTask(Context context,int id){
        MyDatabaseHelper dbHelper=new MyDatabaseHelper(context,"TaskStore.db",null,1);
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("isdailytask","2");
        db.update("Tasklist",values,"id=?",new String[]{String.valueOf(id)});
    }
}
