package com.example.el_project;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by ns on 2018/4/9.
 */
//执行数据库有关的工作
public class MyDatabaseHelper extends SQLiteOpenHelper {
    public static final String CREATE_TASKLIST = "create table Tasklist ("
            +"id integer primary key autoincrement,"
            +"task text,"
            +"assumedtime text,"
            +"deadline text,"
            +"priority text)";

    private Context mContext;

    public MyDatabaseHelper(Context context, String name,
                            SQLiteDatabase.CursorFactory factory, int version){
        super(context, name,factory,version);
        mContext=context;
    }

    @Override
    //创建数据库的同时创建表
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TASKLIST);
        Toast.makeText(mContext,"Create succeeded",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }

}
