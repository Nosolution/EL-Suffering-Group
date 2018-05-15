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
            +"emergencydegree integer,"
            +"isdailytask integer,"
            +"comments text)";

    public static final String CREATE_FINISHTASKTABLE = "create table FinishTaskTable ("
            +"detail_time_start text primary key,"   //开始任务的具体时间，精确到秒
            +"date integer,"                            //完成任务的日期，用于筛选
            +"week_count integer,"                       //完成任务的周数，用于筛选
            +"week integer,"                            //完成任务时的周几，用于筛选
            +"task_name text,"
            +"task_time_used integer,"                     //任务耗时
            +"statue integer,"                           //任务完成状态
            +"break_count integer" + ")";                //任务打断次数

    public static final String ADD_COLUMN_LAST_FINISHED_TIME="alter table Tasklist add column last_finished_date integer";

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
        sqLiteDatabase.execSQL(CREATE_FINISHTASKTABLE);
        sqLiteDatabase.execSQL(ADD_COLUMN_LAST_FINISHED_TIME);
        Toast.makeText(mContext,"Create succeeded",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion <= 1){
            db.execSQL(CREATE_FINISHTASKTABLE);
            Toast.makeText(mContext,"Create succeeded",Toast.LENGTH_SHORT).show();
        }
        else if(oldVersion<=2){
            db.execSQL(ADD_COLUMN_LAST_FINISHED_TIME);
            Toast.makeText(mContext,"succeeded upgrade to version 3",Toast.LENGTH_SHORT).show();
        }
    }
}
