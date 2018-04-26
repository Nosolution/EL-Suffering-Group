package com.example.el_project;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private List<com.example.el_project.Task> mTaskList = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;
    private MyDatabaseHelper dbHelper;
    private FloatingActionButton button1;

    private int[] colors = {R.drawable.pink, R.drawable.red, R.drawable.purple, R.drawable.gray, R.drawable.green};
    //颜色ID数组，用于循环改变任务背景颜色

    private ArrayList<String[]> taskList = new ArrayList<>();//也许会有用
    private boolean editMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editMode = false;
        dbHelper = new MyDatabaseHelper(this, "TaskStore.db", null, 1);
        initTasks();  //初始化数据

        Toolbar toolbar = findViewById(R.id.title_toolbar);
        setSupportActionBar(toolbar);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);  //如果确定每个item的内容不会改变RecyclerView的大小，设置这个选项可以提高性能

        //创建默认的线性LayoutManager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //设置Adapter
        adapter = new RecyclerViewAdapter(mTaskList);
        recyclerView.setAdapter(adapter);

        //初始化接口实例，实现点击功能
        adapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
//                Task task = mTaskList.get(position);
//                Toast.makeText(view.getContext(), "you clicked view " + task.getName(), Toast.LENGTH_SHORT).show();
                if (!editMode){
                    //单点事件
                    showDetails(position);
                }
                else{
                    adapter.setItemChecked(position,adapter.isItemChecked(position));
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
                Task task = mTaskList.get(position);
                Toast.makeText(view.getContext(), "you longclicked view " + task.getName(), Toast.LENGTH_SHORT).show();
                if(!editMode){
                    setEditMode(true);
                }
            }
        });

        button1 =(FloatingActionButton)findViewById(R.id.fab_add);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,AddTaskActivity.class);
                startActivityForResult(intent,1);//对是否点击了完成按钮实现监听
            }
        });
    }

    //重载方法，若点击了完成按钮，返回此Acivity时更新recyclerview
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            updateTasks();
        }
    }

    //初始化任务列表
    private void initTasks() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("Tasklist", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Task task = new Task(cursor.getString(cursor.getColumnIndex("task")), colors[mTaskList.size() % 5]);
                mTaskList.add(task);
                String[] tempstring = {cursor.getString(cursor.getColumnIndex("task")),
                        cursor.getString(cursor.getColumnIndex("assumedtime")),
                        cursor.getString(cursor.getColumnIndex("deadline")),
                        String.valueOf(cursor.getInt(cursor.getColumnIndex("emergencydegree"))),
                        String.valueOf(cursor.getInt(cursor.getColumnIndex("isdailytask"))),
                        cursor.getString(cursor.getColumnIndex("comments"))};
                taskList.add(tempstring);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    //更新recyclerview
    private void updateTasks() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("Tasklist", null, null, null, null, null, null);
        if (cursor.moveToPosition(mTaskList.size())) {
            do {
                Task task = new Task(cursor.getString(cursor.getColumnIndex("task")), colors[mTaskList.size() % 5]);
                adapter.addItem(task);
                Toast.makeText(this, "Succeeded to update", Toast.LENGTH_SHORT).show();
                String[] tempstring = {cursor.getString(cursor.getColumnIndex("task")),
                        cursor.getString(cursor.getColumnIndex("assumedtime")),
                        cursor.getString(cursor.getColumnIndex("deadline")),
                        String.valueOf(cursor.getInt(cursor.getColumnIndex("emergencydegree"))),
                        String.valueOf(cursor.getInt(cursor.getColumnIndex("isdailytask"))),
                        cursor.getString(cursor.getColumnIndex("comments"))};
                taskList.add(tempstring);
            } while (cursor.moveToNext());
        } else {
            Toast.makeText(this, "Failed to update", Toast.LENGTH_SHORT).show();
        }
        cursor.close();
    }

    private void setEditMode(boolean flag) {
        editMode=flag;
    }

//    @Override
//    public void onBackPressed() {
//        if(editMode){
//            setEditMode(false);
//        }
//        else{
//            super.onBackPressed();
//        }
//    }

    private void showDetails(int position){
        final AlertDialog.Builder detailsDialog =
                new AlertDialog.Builder(MainActivity.this);
        detailsDialog.setIcon(R.drawable.circle);
        detailsDialog.setTitle("详情");
        detailsDialog.setMessage(constructDetails(position));
        detailsDialog.setPositiveButton("返回", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, "点击了返回按钮", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        detailsDialog.setNegativeButton("修改", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, "点击了修改按钮", Toast.LENGTH_SHORT).show();
            }
        });
        detailsDialog.show();
    }

    private String constructDetails(int position){
        String finalString= "";
        String temp="";
        finalString+="任务："+taskList.get(position)[0]+"\n";
        String[] tempstring=taskList.get(position)[1].split(":");
        finalString+="预计时间:"+tempstring[0]+"时"+tempstring[1]+"分\n";
        temp=taskList.get(position)[2];
        finalString+="最后日期："+(temp==null ? "无":temp)+"\n";
        finalString+="紧急程度："+taskList.get(position)[3]+"\n";
        finalString+="是否是每日任务："+(taskList.get(position)[4].equals( "1" )? "是":"不是")+"\n";
        finalString+="备注:"+taskList.get(position)[5]+"\n";
        return finalString;
    }

}


