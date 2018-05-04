package com.example.el_project;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by ns on 2018/5/1.
 */

public class MyDatabaseOperation {
//    private MyDatabaseHelper dbHelper= new MyDatabaseHelper("TaskStore.db", null, 1);
    /*
    传入上下文context与任务id
     */
    public static void deleteTask(Context context,int id){
        MyDatabaseHelper dbHelper=new MyDatabaseHelper(context,"TaskStore.db",null,1);
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        db.delete("Tasklist","id=?",new String[]{String.valueOf(id)});
    }
}
