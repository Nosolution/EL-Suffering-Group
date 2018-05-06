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

public class MainActivity extends AppCompatActivity {
    private List<com.example.el_project.Task> mTaskList = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;
    private MyDatabaseHelper dbHelper;
    private FloatingActionButton baseButton;
    private android.support.design.widget.FloatingActionButton fabDelete, fabDetail, fabStart;
    private boolean isFabOpened = false;
    private Animation in_from_fab;
    private Animation out_to_fab;

    private int[] colors = {R.drawable.task_pink, R.drawable.task_red, R.drawable.task_purple, R.drawable.task_gray, R.drawable.task_green};
    private int[] checkedColors={R.drawable.task_pink_chosen,R.drawable.task_red_chosen,R.drawable.task_purple_chosen,R.drawable.task_grey_chosen,R.drawable.task_green_chosen};
    //颜色ID数组，用于循环改变任务背景颜色

    private ArrayList<String[]> taskList = new ArrayList<>();//也许会有用
    private int selectedPosition;//被选中的Item位置

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new MyDatabaseHelper(this, "TaskStore.db", null, 1);
        initTasks();  //初始化数据
        selectedPosition=-1;

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
                if (!isFabOpened&&selectedPosition==-1) {
                    openMenu(baseButton);
                    selectedPosition=position;
                    task.switchBackground();
                    adapter.changeItemBackGround(position);
                }
                else if(isFabOpened&&selectedPosition!=position){
                    Task previousTask=mTaskList.get(selectedPosition);
                    previousTask.switchBackground();
                    adapter.changeItemBackGround(selectedPosition);
                    closeMenu(baseButton);
                    selectedPosition=-1;
                }
                else{
                    closeMenu(baseButton);
                    selectedPosition=-1;
                    task.switchBackground();
                    adapter.changeItemBackGround(position);
                }
            }

            //长按与短按功能相同
            @Override
            public void onItemLongClick(View view, int position) {
                Task task = mTaskList.get(position);
//                Toast.makeText(view.getContext(), "you longclicked view " + task.getName(), Toast.LENGTH_SHORT).show();
                if (!isFabOpened&&selectedPosition==-1) {
                    openMenu(baseButton);
                    selectedPosition=position;
                    task.switchBackground();
                    adapter.changeItemBackGround(position);
                }
                else if(isFabOpened&&selectedPosition!=position){
                    Task previousTask=mTaskList.get(selectedPosition);
                    previousTask.switchBackground();
                    adapter.changeItemBackGround(selectedPosition);
                    closeMenu(baseButton);
                    selectedPosition=-1;
                }
                else{
                    closeMenu(baseButton);
                    selectedPosition=-1;
                    task.switchBackground();
                    adapter.changeItemBackGround(position);
                }

            }
        });

        baseButton =findViewById(R.id.fab_base);
        fabDelete = findViewById(R.id.fab_delete);
        fabDetail = findViewById(R.id.fab_detail);
        fabStart = findViewById(R.id.fab_start);
        fabDelete.setVisibility(View.INVISIBLE);
        fabDetail.setVisibility(View.INVISIBLE);
        fabStart.setVisibility(View.INVISIBLE);

        baseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFabOpened) {
                    Intent intent = new Intent(MainActivity.this,EditTaskActivity.class);
                    startActivityForResult(intent,1);//对是否点击了完成按钮实现监听
                } else {
                    closeMenu(baseButton);
                    Task task = mTaskList.get(selectedPosition);
                    task.switchBackground();
                    adapter.changeItemBackGround(selectedPosition);
                    selectedPosition=-1;
                }
            }
        });
        //立即开始任务
        fabStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startTask();
            }
        });

        //显示详情
        fabDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDetails(selectedPosition);
            }
        });

        //删除任务
        fabDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO:删除任务
            }
        });


    }

    // 点击task后，悬浮按钮产生的动画
    public void openMenu(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "rotation", 0, -155, -135 );
        animator.setDuration(500);
        animator.start();
        isFabOpened = true;
        in_from_fab = AnimationUtils.loadAnimation(MainActivity.this, R.anim.in_from_fab);
        fabDelete.setVisibility(View.VISIBLE);
        fabDetail.setVisibility(View.VISIBLE);
        fabStart.setVisibility(View.VISIBLE);
        fabDelete.startAnimation(in_from_fab);
        fabDetail.startAnimation(in_from_fab);
        fabStart.startAnimation(in_from_fab);

    }

    // 点击关闭后，悬浮按钮的动画
    public void closeMenu(View view){
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "rotation", -135, 20, 0);
        animator.setDuration(500);
        animator.start();
        isFabOpened = false;
        out_to_fab = AnimationUtils.loadAnimation(MainActivity.this, R.anim.out_to_fab);
        fabDelete.startAnimation(out_to_fab);
        fabDetail.startAnimation(out_to_fab);
        fabStart.startAnimation(out_to_fab);
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
        mTaskList.clear();
        taskList.clear();
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

    private void startTask(){
        Intent intent=new Intent(MainActivity.this,TaskTimingActivity.class);
        intent.putExtra("intent_task_id",Integer.parseInt(taskList.get(selectedPosition)[0]));
        intent.putExtra("intent_task_name",taskList.get(selectedPosition)[1]);
        String[] tempString=taskList.get(selectedPosition)[2].split(":");
        intent.putExtra("intent_task_hours_required", Integer.parseInt(tempString[0]));
        intent.putExtra("intent_task_minutes_required", Integer.parseInt(tempString[1]));
        intent.putExtra("intent_task_comments",taskList.get(selectedPosition)[6]);
        startActivity(intent);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
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


