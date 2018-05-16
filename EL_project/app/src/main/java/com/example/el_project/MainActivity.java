package com.example.el_project;

import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.widget.Toolbar;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener{
    private List<com.example.el_project.Task> mTaskList = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;
    private MyDatabaseHelper dbHelper;
    private FloatingActionButton baseButton;
    private android.support.design.widget.FloatingActionButton fabDelete, fabDetail, fabStart;
    private boolean isFabOpened = false;
    private Animation in_from_fab;
    private Animation out_to_fab;
	private DrawerLayout mDrawerLayout;
	private Switch switchClockStatus;
	private Switch switchMusicStatus;
	private TextView textClockTime;
    private LinearLayout clockTimeLayout;
    private TextView textBreakClockTime;
    private LinearLayout breakClockTimeLayout;
    private Button btnCleanShareStorage;

    private int[] colors = {R.drawable.task_bar_blue, R.drawable.task_bar_brown, R.drawable.task_bar_green, R.drawable.task_bar_purple, R.drawable.task_bar};
    private int[] checkedColors={R.drawable.taskbar_chosen,R.drawable.taskbar_chosen,R.drawable.taskbar_chosen,R.drawable.taskbar_chosen,R.drawable.taskbar_chosen};
    //颜色ID数组，用于循环改变任务背景颜色

    private ArrayList<String[]> taskList = new ArrayList<>();//也许会有用
    private int selectedPosition;//被选中的Item位置

    private DrawerLayout drawerLayoutMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new MyDatabaseHelper(this, "TaskStore.db", null, 3);
        selectedPosition=-1;

        Toolbar toolbar = findViewById(R.id.title_toolbar);
        setSupportActionBar(toolbar);
	    mDrawerLayout=findViewById(R.id.activity_main_drawer_layout);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);  //如果确定每个item的内容不会改变RecyclerView的大小，设置这个选项可以提高性能


	    //设置背景
        BackgroundCollection backgroundCollection = new BackgroundCollection();
        mDrawerLayout.setBackgroundResource(backgroundCollection.getTodayBackground());
        RelativeLayout layoutSetting = findViewById(R.id.activity_main_setting_upper);
        layoutSetting.setBackgroundColor(backgroundCollection.getTodayColor());

        //设置部分
        textClockTime = findViewById(R.id.text_choose_time);
        clockTimeLayout = (LinearLayout)findViewById(R.id.main_clock_set_layout);
        textBreakClockTime = findViewById(R.id.text_choose_break_time);
        breakClockTimeLayout = (LinearLayout)findViewById(R.id.main_break_clock_set_layout);
	    switchClockStatus=findViewById(R.id.switch_if_tomato_clock_on);
	    switchMusicStatus=findViewById(R.id.switch_if_music_on);
	    btnCleanShareStorage = findViewById(R.id.activity_main_clean_share_storage);
	    switchClockStatus.setOnCheckedChangeListener(this);
	    switchMusicStatus.setOnCheckedChangeListener(this);
	    clockTimeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog;
                final String[] itemToSelect = {"20分钟", "30分钟", "40分钟", "50分钟"};
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("时长");
                builder.setItems(itemToSelect, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        GeneralSetting.setTomatoClockTime(MainActivity.this, which * 10 + 20);
                        refreshSetting();
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog = builder.create();
                dialog.show();
            }
        });
	    breakClockTimeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog;
                final String[] itemToSelect = {"5分钟", "10分钟", "15分钟", "20分钟"};
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("休息时长");
                builder.setItems(itemToSelect, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        GeneralSetting.setTomatoBreakTime(MainActivity.this, which * 5 + 5);
                        refreshSetting();
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog = builder.create();
                dialog.show();
            }
        });
	    btnCleanShareStorage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TempPicStorageManager storageManager = new TempPicStorageManager(MainActivity.this, "tempPicToShare");
                storageManager.clean();
            }
        });


        //创建默认的线性LayoutManager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //设置Adapter
        adapter = new RecyclerViewAdapter(mTaskList);
        recyclerView.setAdapter(adapter);

        //初始化接口实例，实现点击功能
        adapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                Task task = mTaskList.get(position);
//                Toast.makeText(view.getContext(), "you clicked view " + task.getName(), Toast.LENGTH_SHORT).show();
                if (!isFabOpened&&selectedPosition==-1) {
                    openMenu(baseButton);
                    selectedPosition=position;
                    task.switchBackground();
                    new Thread(){     //几个线程
                        @Override
                        public void run() {
                            super.run();
                            adapter.changeItemBackGround(position);
                        }
                    }.start();
                }
                else if(isFabOpened&&selectedPosition!=position){
                    Task previousTask=mTaskList.get(selectedPosition);
                    previousTask.switchBackground();
                    new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            adapter.changeItemBackGround(selectedPosition);
                        }
                    }.start();
                    closeMenu(baseButton);
                    selectedPosition=-1;
                }
                else{
                    closeMenu(baseButton);
                    selectedPosition=-1;
                    task.switchBackground();
                    new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            adapter.changeItemBackGround(position);
                        }
                    }.start();
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
                closeMenu(baseButton);
                selectedPosition=-1;
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
                deleteTask(selectedPosition);
                closeMenu(baseButton);
                selectedPosition=-1;
            }
        });

    }

    @Override
    protected void onResume() {
        MyDatabaseOperation.refreshAllDailyTask(MainActivity.this);
        refreshTask();
        adapter.refreshItemView();
        refreshSetting();
        super.onResume();
    }


    // 点击task后，悬浮按钮产生的动画
    public void openMenu(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "rotation", 0, -155, -135 );
        animator.setDuration(500);
        animator.setAutoCancel(true);
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
//                updateTasks();
            }
            else if(requestCode==2){
//                String taskId=data.getStringExtra("task_ID");
//                modifyTask(taskId);
            }
        }
    }

    private void refreshTask(){
        mTaskList.clear();
        taskList.clear();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("Tasklist", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            int id;
            do {
                if(cursor.getInt(cursor.getColumnIndex("isdailytask"))==2)
                    continue;
                id = cursor.getInt(cursor.getColumnIndex("id"));
                Task task = new Task(cursor.getString(cursor.getColumnIndex("task")),
                        MyDatabaseOperation.getTaskRestDays(MainActivity.this,id),R.drawable.task_bar,R.drawable.taskbar_chosen);
                mTaskList.add(task);
                String[] tempstring = {cursor.getString(cursor.getColumnIndex("id")),
                        cursor.getString(cursor.getColumnIndex("task")),
                        cursor.getString(cursor.getColumnIndex("assumedtime")),
                        cursor.getString(cursor.getColumnIndex("deadline")),
                        String.valueOf(cursor.getInt(cursor.getColumnIndex("emergencydegree"))),
                        String.valueOf(cursor.getInt(cursor.getColumnIndex("isdailytask"))),
                        cursor.getString(cursor.getColumnIndex("comments")),
                        String.valueOf(cursor.getInt(cursor.getColumnIndex("last_finished_date")))};
                taskList.add(tempstring);
            } while (cursor.moveToNext());
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
        intent.putExtra("intent_is_daily_task",Integer.parseInt(taskList.get(selectedPosition)[5]));
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
                closeMenu(baseButton);
                selectedPosition=-1;
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

    private void deleteTask(int position){
        MyDatabaseOperation.deleteTask(MainActivity.this,Integer.parseInt(taskList.get(position)[0]));
        refreshTask();
        adapter.refreshItemView();
    }

    //	绑定Menu布局
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar,menu);
        return true;
    }

    //对HomeAsUp按钮点击事件进行处理
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.setting:
                mDrawerLayout.openDrawer(GravityCompat.END);
            default:
        }
        return true;
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()){
            case R.id.switch_if_music_on:
                if(compoundButton.isChecked()) {
                    GeneralSetting.setMusicOn(MainActivity.this, true);
                }
                else {
                    GeneralSetting.setMusicOn(MainActivity.this, false);
                }
                break;
            case R.id.switch_if_tomato_clock_on:
                if(compoundButton.isChecked()){
                    GeneralSetting.setTomatoClockEnable(this, true);
                }else {
                    GeneralSetting.setTomatoClockEnable(this, false);
                }
                refreshSetting();
                break;
        }
    }

    private void refreshSetting(){
        switchMusicStatus.setChecked(GeneralSetting.getMusicOn(this));
        switchClockStatus.setChecked(GeneralSetting.getTomatoClockEnable(this));

        //修改设置内显示番茄钟时长
        if(GeneralSetting.getTomatoClockEnable(this)){
            clockTimeLayout.setVisibility(View.VISIBLE);
            breakClockTimeLayout.setVisibility(View.VISIBLE);
        }else {
            clockTimeLayout.setVisibility(View.GONE);
            breakClockTimeLayout.setVisibility(View.GONE);
        }
        textClockTime.setText(GeneralSetting.getTomatoClockTime(this) + "分钟");
        textBreakClockTime.setText(GeneralSetting.getTomatoBreakTime(this) + "分钟");
    }


}


