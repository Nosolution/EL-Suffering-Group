package com.example.el_project;

import android.animation.ObjectAnimator;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
    private android.support.design.widget.FloatingActionButton fab_1, fab_text, fab_start;
    private boolean fabOpened = false;
    private Animation in_from_fab;
    private Animation out_to_fab;

    private int[] colors = {R.drawable.task_pink, R.drawable.task_red, R.drawable.task_purple, R.drawable.task_gray, R.drawable.task_green};
    private int[] checkedColors={R.drawable.task_pink_chosen,R.drawable.task_red_chosen,R.drawable.task_purple_chosen,R.drawable.task_grey_chosen,R.drawable.task_green_chosen};
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
                Task task = mTaskList.get(position);
//                Toast.makeText(view.getContext(), "you clicked view " + task.getName(), Toast.LENGTH_SHORT).show();
                if (!getEditMode()){
                    showDetails(position);
                    if (!fabOpened) {
                        openMenu(button1);
                    }

                }
                else{
                    task.switchBackground();
                    adapter.changeItemBackGround(position);
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
//                Task task = mTaskList.get(position);
////                Toast.makeText(view.getContext(), "you longclicked view " + task.getName(), Toast.LENGTH_SHORT).show();
//                if(!getEditMode()){
//                    setEditMode(true);
//                    task.switchBackground();
//                    adapter.changeItemBackGround(position);
//                }
            }
        });

        button1 =findViewById(R.id.fab_add);
        fab_1 = findViewById(R.id.fab_1);
        fab_text = findViewById(R.id.fab_text);
        fab_start = findViewById(R.id.fab_start);
        fab_1.setVisibility(View.INVISIBLE);
        fab_text.setVisibility(View.INVISIBLE);
        fab_start.setVisibility(View.INVISIBLE);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!fabOpened) {
                    Intent intent = new Intent(MainActivity.this,EditTaskActivity.class);
                    startActivityForResult(intent,1);//对是否点击了完成按钮实现监听
                } else {
                    closeMenu(button1);
                }
            }
        });
    }

    // 点击task后，悬浮按钮产生的动画
    public void openMenu(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "rotation", 0, -155, -135 );
        animator.setDuration(500);
        animator.start();
        fabOpened = true;
        in_from_fab = AnimationUtils.loadAnimation(MainActivity.this, R.anim.in_from_fab);
        fab_1.setVisibility(View.VISIBLE);
        fab_text.setVisibility(View.VISIBLE);
        fab_start.setVisibility(View.VISIBLE);
        fab_1.startAnimation(in_from_fab);
        fab_text.startAnimation(in_from_fab);
        fab_start.startAnimation(in_from_fab);

    }

    // 点击关闭后，悬浮按钮的动画
    public void closeMenu(View view){
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "rotation", -135, 20, 0);
        animator.setDuration(500);
        animator.start();
        fabOpened = false;
        out_to_fab = AnimationUtils.loadAnimation(MainActivity.this, R.anim.out_to_fab);
        fab_1.startAnimation(out_to_fab);
        fab_text.startAnimation(out_to_fab);
        fab_start.startAnimation(out_to_fab);
    }
    //重载方法，若点击了完成按钮，返回此Acivity时更新recyclerview
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK ){
            if(requestCode==1) {
                updateTasks();
            }
            else if(requestCode==2){
                String taskId=data.getStringExtra("task_ID");
                modifyTask(taskId);
            }
        }
    }

    //初始化任务列表
    private void initTasks() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("Tasklist", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Task task = new Task(cursor.getString(cursor.getColumnIndex("task")), colors[mTaskList.size() % 5],checkedColors[mTaskList.size()%5]);
                mTaskList.add(task);
                String[] tempstring = {cursor.getString(cursor.getColumnIndex("id")),
                        cursor.getString(cursor.getColumnIndex("task")),
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
                Task task = new Task(cursor.getString(cursor.getColumnIndex("task")), colors[mTaskList.size() % 5],checkedColors[mTaskList.size()%5]);
                adapter.addItem(task);
                Toast.makeText(this, "Succeeded to update", Toast.LENGTH_SHORT).show();
                String[] tempstring = {cursor.getString(cursor.getColumnIndex("id")),
                        cursor.getString(cursor.getColumnIndex("task")),
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

    private void modifyTask(String taskId){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("Tasklist",null,"id="+taskId,null,null,null,null);
        if(cursor.moveToFirst()) {
            for (int i = 0; i < taskList.size(); i++) {
                if (taskId.equals(taskList.get(i)[0])) {
                    Task task = new Task(cursor.getString(cursor.getColumnIndex("task")), mTaskList.get(i).getBackgroundId(), mTaskList.get(i).getSelectedBackgroundId());
                    adapter.modifyItem(i, task);
                    String[] tempstring = {cursor.getString(cursor.getColumnIndex("id")),
                            cursor.getString(cursor.getColumnIndex("task")),
                            cursor.getString(cursor.getColumnIndex("assumedtime")),
                            cursor.getString(cursor.getColumnIndex("deadline")),
                            String.valueOf(cursor.getInt(cursor.getColumnIndex("emergencydegree"))),
                            String.valueOf(cursor.getInt(cursor.getColumnIndex("isdailytask"))),
                            cursor.getString(cursor.getColumnIndex("comments"))};
                    taskList.set(i, tempstring);
                    break;
                }
            }
        }
        cursor.close();
    }

    private void setEditMode(boolean flag) {editMode=flag;}
    private boolean getEditMode(){return editMode;}

    @Override
    public void onBackPressed() {
        if(getEditMode()){
            setEditMode(false);
            adapter.cleanSelected();//编辑模式下点击返回键退出编辑模式
        }
        else{
            super.onBackPressed();
        }
    }

    private void showDetails(final int position){
        final AlertDialog.Builder detailsDialog =
                new AlertDialog.Builder(MainActivity.this);
        detailsDialog.setIcon(R.drawable.circle);
        detailsDialog.setTitle("详情");
        detailsDialog.setMessage(constructDetails(position));
        detailsDialog.setPositiveButton("返回", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        detailsDialog.setNegativeButton("修改", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String[] taskDetails=taskList.get(position);
                Intent intent=new Intent(MainActivity.this,EditTaskActivity.class);
                intent.putExtra("details",taskDetails);
                startActivityForResult(intent,2);
            }
        });
        detailsDialog.show();
    }

    private String constructDetails(int position){
        String finalString= "";
        String temp="";
        finalString+="任务："+taskList.get(position)[1]+"\n";
        String[] tempString=taskList.get(position)[2].split(":");
        finalString+="预计时间:"+tempString[0]+"时"+tempString[1]+"分\n";
        temp=taskList.get(position)[3];
        finalString+="最后日期："+(temp==null ? "无":temp)+"\n";
        finalString+="紧急程度："+taskList.get(position)[4]+"\n";
        finalString+="是否是每日任务："+(taskList.get(position)[5].equals( "1" )? "是":"不是")+"\n";
        finalString+="备注:"+taskList.get(position)[6]+"\n";
        return finalString;
    }

}


