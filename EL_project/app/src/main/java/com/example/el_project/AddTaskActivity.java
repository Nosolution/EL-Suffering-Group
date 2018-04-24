package com.example.el_project;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.SparseBooleanArray;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class AddTaskActivity extends AppCompatActivity implements View.OnClickListener{

    private RelativeLayout selectTime;
    private TextView ddlTime;
    private CustomDatePicker cdp;
    private ImageView iv1,iv2,iv3,iv4,iv5;
    private Switch swc;
    private MyDatabaseHelper dbHelper;
    private EditText taskNameEditText;
    private EditText assumedTimeEditText1;
    private EditText assumedTimeEditText2;
    private EditText commentEditText;
    private Button addTaskList;//加入待办按钮
    private Button startNow;//立即开始按钮

    private SparseBooleanArray emergencyDegree;
    private boolean updateFlag;
    private int time1,time2,time3,time4,time5,sumTime=0;
    private Drawable draw1,draw2;

//TODO:定义变量

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);


        addTaskList = (Button)findViewById(R.id.add_to_tasklist);
        startNow = (Button)findViewById(R.id.start_now);
        addTaskList.setOnClickListener(this);
        textChange tc = new textChange();//文本改变监视器
        emergencyDegree = new SparseBooleanArray(5);

        taskNameEditText = (EditText)findViewById(R.id.task_name_et);
        taskNameEditText.addTextChangedListener(tc);
        assumedTimeEditText1 = (EditText)findViewById(R.id.assumed_time_et1);
        assumedTimeEditText1.addTextChangedListener(tc);
        assumedTimeEditText2 = (EditText)findViewById(R.id.assumed_time_et2);
        assumedTimeEditText2.addTextChangedListener(tc);
        commentEditText = (EditText)findViewById(R.id.comment_et);
        //两张动画
        draw1=(AnimationDrawable)((ImageView)findViewById(R.id.circle_one)).getDrawable();
        draw2=(AnimationDrawable)((ImageView)findViewById(R.id.circle_back)).getDrawable();
        //打开数据库
        dbHelper = new MyDatabaseHelper(this,"TaskStore.db",null,1);
        updateFlag=false;

        //DDL选择
        selectTime=(RelativeLayout)findViewById(R.id.selectTime);
        selectTime.setOnClickListener(this);
        ddlTime=(TextView)findViewById(R.id.ddlTime);
        initTimePicker();

        //是否打开为每日任务
        swc=(Switch)findViewById(R.id.open);
        swc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    //TODO:打开每日工作 待补充
                }else{

                }
            }
        });

        //紧急程度设置
        iv1=(ImageView)findViewById(R.id.circle_one);
        iv1.setOnClickListener(this);

        iv2=(ImageView)findViewById(R.id.circle_two);
        iv2.setOnClickListener(this);

        iv3=(ImageView)findViewById(R.id.circle_three);
        iv3.setOnClickListener(this);

        iv4=(ImageView)findViewById(R.id.circle_four);
        iv4.setOnClickListener(this);

        iv5=(ImageView)findViewById(R.id.circle_five);
        iv5.setOnClickListener(this);

        addTaskList = (Button)findViewById(R.id.add_to_tasklist);
        addTaskList.setOnClickListener(this);
        startNow = (Button)findViewById(R.id.start_now);
        startNow.setOnClickListener(this);
    }

    private void initTimePicker() {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        String now=sdf.format(new Date());
        ddlTime.setText(now);

        cdp=new CustomDatePicker(this,new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(String time) { // 回调接口，获得选中的时间
                ddlTime.setText(time);
            }
        }, "2018-01-01 00:00", now);
        cdp.showSpecificTime(true);
        cdp.setIsLoop(true);

    }

    @Override
    //TODO:onclick
    public void onClick(View v) {

        //全部熄灭
        if(v.getId()==R.id.circle_five||v.getId()==R.id.circle_four||v.getId()==R.id.circle_three||v.getId()==R.id.circle_two||v.getId()==R.id.circle_one){
            ImageView iv5 = (ImageView)findViewById(R.id.circle_five);
            iv5.setImageDrawable(draw2);
            AnimationDrawable ad5 = (AnimationDrawable) iv5.getDrawable();
            ad5.stop();
            ad5.start();
            iv5.setSelected(false);

            ImageView iv4 = (ImageView)findViewById(R.id.circle_four);
            iv4.setImageDrawable(draw2);
            AnimationDrawable ad4 = (AnimationDrawable) iv4.getDrawable();
            ad4.stop();
            ad4.start();
            iv4.setSelected(false);

            ImageView iv3 = (ImageView)findViewById(R.id.circle_three);
            iv3.setImageDrawable(draw2);
            AnimationDrawable ad3 = (AnimationDrawable) iv3.getDrawable();
            ad3.stop();
            ad3.start();
            iv3.setSelected(false);

            ImageView iv2 = (ImageView)findViewById(R.id.circle_two);
            iv2.setImageDrawable(draw2);
            AnimationDrawable ad2 = (AnimationDrawable) iv2.getDrawable();
            ad2.stop();
            ad2.start();
            iv2.setSelected(false);

            ImageView iv1 = (ImageView)findViewById(R.id.circle_five);
            iv1.setImageDrawable(draw2);
            AnimationDrawable ad1 = (AnimationDrawable) iv1.getDrawable();
            ad1.stop();
            ad1.start();
            iv1.setSelected(false);
        }

        //开始处理点击事件
        switch (v.getId()){
            case R.id.selectTime:
                cdp.show(ddlTime.getText().toString());
                break;
            case R.id.circle_five :
                iv5.setImageDrawable(draw1);
                AnimationDrawable ad5 = (AnimationDrawable) iv5.getDrawable();
                ad5.stop();
                ad5.start();
                iv5.setSelected(true);
            case R.id.circle_four :
                iv4.setImageDrawable(draw1);
                AnimationDrawable ad4 = (AnimationDrawable) iv4.getDrawable();
                ad4.stop();
                ad4.start();
                iv4.setSelected(true);
            case R.id.circle_three :
                iv3.setImageDrawable(draw1);
                AnimationDrawable ad3 = (AnimationDrawable) iv3.getDrawable();
                ad3.stop();
                ad3.start();
                iv3.setSelected(true);
            case R.id.circle_two :
                iv2.setImageDrawable(draw1);
                AnimationDrawable ad2 = (AnimationDrawable) iv2.getDrawable();
                ad2.stop();
                ad2.start();
                iv2.setSelected(true);
                //TODO:不能点亮，有待解决
            case R.id.circle_one :
                iv1.setImageDrawable(draw1);
                AnimationDrawable ad1 = (AnimationDrawable) iv1.getDrawable();
                ad1.stop();
                ad1.start();
                iv1.setSelected(true);
//                if(!getSelected(v.getId())) {
//                    ImageView iv = (ImageView) v;
//                    AnimationDrawable ad = (AnimationDrawable) iv.getDrawable();
//                    ad.stop();
//                    ad.start();
//                    setSelected(v.getId(),true);
//                }
//                else{
//                    ImageView iv = (ImageView) v;
//                    AnimationDrawable ad = (AnimationDrawable) iv.getDrawable();
//                    ad.stop();
//                }
                break;
            case R.id.add_to_tasklist:
                addTask();
                updateFlag=true;
                break;
            case R.id.start_now:
                //待完成
        }
    };


    //完成添加任务
    private void addTask(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();//真正打开数据库
        ContentValues values = new ContentValues();//传值工具
        values.put("task",taskNameEditText.getText().toString() );
        values.put("assumedtime",assumedTimeEditText1.getText().toString()+":"+assumedTimeEditText2.getText().toString());
        values.put("deadline",ddlTime.getText().toString());//对应每一列传值
        values.put("emergencydegree",emergencyDegree.indexOfValue(true)>=0? emergencyDegree.indexOfValue(true)+1 : 1);//默认值为1
        values.put("isdailytask",isDailyTask());
        values.put("comments",commentEditText.getText().toString());
        db.insert("Tasklist",null,values);//将值传入数据库中的"Tasklist"表
        Toast toast = Toast.makeText(AddTaskActivity.this,"成功添加任务",Toast.LENGTH_SHORT);
        showMyToast(toast,1000);//提示成功添加任务
        taskNameEditText.setText("");assumedTimeEditText1.setText("");
        assumedTimeEditText2.setText("");commentEditText.setText("");//清空所有Edittext中的内容
        swc.setChecked(false);
    }

    //自定义Toast显示时间，cnt为所需显示时间
    public void showMyToast(final Toast toast, final int cnt){
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                toast.show();
            }
        },0,3000);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                toast.cancel();
                timer.cancel();
            }
        },cnt);
    }

    //查询对应位置是否已经被选择
    private boolean getSelected(int id){
        switch (id){
            case R.id.circle_one:
                return emergencyDegree.valueAt(0);
            case R.id.circle_two:
                return emergencyDegree.valueAt(1);
            case R.id.circle_three:
                return emergencyDegree.valueAt(2);
            case R.id.circle_four:
                return emergencyDegree.valueAt(3);
            case R.id.circle_five:
                return emergencyDegree.valueAt(4);
            default:
                return false;
        }
    }

    //设置对应的布尔数组
    private void setSelected(int id,boolean flag){
        switch (id){
            case R.id.circle_one:
                emergencyDegree.clear();
                emergencyDegree.put(0,flag);
                break;
            case R.id.circle_two:
                emergencyDegree.clear();
                emergencyDegree.put(1,flag);
                break;
            case R.id.circle_three:
                emergencyDegree.clear();
                emergencyDegree.put(2,flag);
                break;
            case R.id.circle_four:
                emergencyDegree.clear();
                emergencyDegree.put(3,flag);
                break;
            case R.id.circle_five:
                emergencyDegree.clear();
                emergencyDegree.put(4,flag);
                break;
        }
    }

    private int isDailyTask(){
        if(swc.isChecked())
            return 1;
        else
            return 0;
    }

    //若已添加任务，返回时更新主界面
    @Override
    public void onBackPressed() {
        if (updateFlag) {
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        }
        else{
            super.onBackPressed();
        }
    }

    //内部类，监控Edittext文本变化，实现必填与选填功能
    class textChange implements TextWatcher{
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            boolean flag1=taskNameEditText.getText().toString().length()>0;
            boolean flag2=assumedTimeEditText1.getText().toString().length()>0;
            boolean flag3=assumedTimeEditText2.getText().toString().length()>0;
            if(flag1&&flag2&&flag3){
                addTaskList.setEnabled(true);
                startNow.setEnabled(true);
            }
            else{
                addTaskList.setEnabled(false);
                startNow.setEnabled(false);
            }
        }
    }

}
