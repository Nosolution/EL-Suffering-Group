package com.example.el_project;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class FinishTaskDataBaseHelper extends SQLiteOpenHelper {
    public static final String CREATE_FINISHTASKTABLE = "create table FinishTaskTable ("
            +"detail_time_start text primary key,"   //开始任务的具体时间，精确到秒
            +"date integer,"                            //完成任务的日期，用于筛选
            +"week_count integer,"                       //完成任务的周数，用于筛选
            +"week text,"                            //完成任务时的周几，用于筛选
            +"task_name text,"
            +"task_time_used integer,"                     //任务耗时
            +"statue integer,"                           //任务完成状态
            +"break_count integer" + ")";                //任务打断次数

    private Context mContext;

    FinishTaskDataBaseHelper(Context context, String name,
                             SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_FINISHTASKTABLE);
        Toast.makeText(mContext, "Create succeeded", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
