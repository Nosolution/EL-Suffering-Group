package com.example.el_project;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class EditTaskActivity extends AppCompatActivity implements View.OnClickListener{

    private RelativeLayout selectTime;
    private TextView ddlTime;
    private CustomDatePicker cdp;
    private ImageView iv1,iv2,iv3,iv4,iv5;
    private Switch swc;
    private MyDatabaseHelper dbHelper;
    private EditText taskNameEditText;
    private EditText commentEditText;
    private Button finishEditing;//加入待办按钮
    private Button startNow;//立即开始按钮
    private Map ivMap;//iv字典，Id到位置的映射
    private int selectedImageViewPosition;//被选择的ImageView的位置
    private boolean editMode;//是否是编辑任务
    private String taskId;//数据库中task的ID，作为唯一标识符
    private Spinner hourSpinner,minuteSpinner;
    private String hour, minute;//Spinner的显示文本
    private String[]hourPosition={"00","01","02","03","04","05"};
    private String[]minutePosition={"00","10","20","30","40","50",};

//    private SparseBooleanArray emergencyDegree;
    private boolean updateFlag;
    private Drawable draw1;

//TODO:定义变量

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);


        editMode=false;
        taskId=null;
        finishEditing = (Button)findViewById(R.id.finish_editing);
        startNow = (Button)findViewById(R.id.start_now);
        finishEditing.setOnClickListener(this);
        startNow.setOnClickListener(this);
        textChange tc = new textChange();//文本改变监视器
        hour ="00";
        minute ="00";

        taskNameEditText = (EditText)findViewById(R.id.task_name_et);
        taskNameEditText.addTextChangedListener(tc);
        hourSpinner=findViewById(R.id.hour_spinner);
        minuteSpinner=findViewById(R.id.minute_spinner);
        commentEditText = (EditText)findViewById(R.id.comment_et);

        //两个Spinner的点击事件
        hourSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                hour =getResources().getStringArray(R.array.hour_list)[position];
                decideButtonEnable();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        minuteSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                minute =getResources().getStringArray(R.array.minute_list)[position];
                decideButtonEnable();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        draw1=getDrawable(R.drawable.circle_animation);//点亮动画
        //打开数据库
        dbHelper = new MyDatabaseHelper(this,"TaskStore.db",null,1);
        updateFlag=false;

        //DDL选择
        selectTime=(RelativeLayout)findViewById(R.id.selectTime);
        selectTime.setOnClickListener(this);
        ddlTime=(TextView)findViewById(R.id.ddlTime);
        initTimePicker();

        //建立字典
        ivMap= new HashMap();
        ivMap.put(R.id.circle_one,1);
        ivMap.put(R.id.circle_two,2);
        ivMap.put(R.id.circle_three,3);
        ivMap.put(R.id.circle_four,4);
        ivMap.put(R.id.circle_five,5);
        selectedImageViewPosition=0;//被选择位置默认为0

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

        finishEditing = (Button)findViewById(R.id.finish_editing);
        finishEditing.setOnClickListener(this);
        startNow = (Button)findViewById(R.id.start_now);
        startNow.setOnClickListener(this);

        //判断Intent来源，决定后续操作
        Intent intent=getIntent();
        if(intent.hasExtra("details")){
            editMode=true;
            String[] taskDetails=intent.getStringArrayExtra("details");
            taskId=taskDetails[0];
            setTaskDetails(taskDetails);
            finishEditing.setText("修改完成");
        }
        initButton(editMode);//根据是否是编辑模式来设置按钮可按或不可按
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

    private void setTaskDetails(String[] taskDetails){
        taskId=taskDetails[0];
        taskNameEditText.setText(taskDetails[1]);
        String[]tempString=taskDetails[2].split(":");
//        assumedTimeEditText1.setText(tempString[0]);
//        assumedTimeEditText2.setText(tempString[1]);
        for(int i=0;i<hourPosition.length;i++){
            if(tempString[0].equals(hourPosition[i])){
                hourSpinner.setSelection(i);
                break;
            }
        }
        for(int j=0;j<minutePosition.length;j++){
            if(tempString[1].equals(minutePosition[j])){
                minuteSpinner.setSelection(j);
                break;
            }
        }
        hour =tempString[0];
        minute =tempString[1];

        for(Object key :ivMap.keySet()){
            if(taskDetails[4].equals(String.valueOf(ivMap.get(key)))){
                setSelected((int)key);
            }
        }
        if(taskDetails[5].equals("1")){
            swc.setChecked(true);
        }
        commentEditText.setText(taskDetails[6]);
    }

    @Override
    //TODO:onclick
    public void onClick(View v) {

        //开始处理点击事件
        int id=v.getId();
        if(ivMap.containsKey(id)) {//判断是否是紧急程度的ImageView
            if (isSelected(id)) {
                setNotSelected(id);
            }
            else{
                setDefaultImageView();
                setSelected(id);
            }
        }
        else {
            switch (id) {
                case R.id.selectTime:
                    cdp.show(ddlTime.getText().toString());
                    break;
                case R.id.finish_editing:
                    if(!editMode) addTask();
                    else modifyTask();
                    updateFlag = true;
                    break;
                case R.id.start_now:
                    Intent intent=new Intent(EditTaskActivity.this,TaskTimingActivity.class);
                    intent.putExtra("intent_task_id",Integer.parseInt(taskId));
                    intent.putExtra("intent_task_name",taskNameEditText.getText().toString());
                    intent.putExtra("intent_task_hours_required", Integer.parseInt(hour));
                    intent.putExtra("intent_task_minutes_required", Integer.parseInt(minute));
                    intent.putExtra("intent_task_comments",commentEditText.getText().toString());
                    startActivity(intent);
                    break;
            }
        }
    }


    //完成添加任务
    private void addTask(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();//真正打开数据库
        ContentValues values = new ContentValues();//传值工具
        values.put("task",taskNameEditText.getText().toString() );
        values.put("assumedtime", hour +":"+ minute);
        values.put("deadline",ddlTime.getText().toString());//对应每一列传值
        values.put("emergencydegree",selectedImageViewPosition>=0? selectedImageViewPosition : 1);//默认值为1
        values.put("isdailytask",isDailyTask());
        values.put("comments",commentEditText.getText().toString());
        db.insert("Tasklist",null,values);//将值传入数据库中的"Tasklist"表
        Toast toast = Toast.makeText(EditTaskActivity.this,"成功添加任务",Toast.LENGTH_SHORT);
        showMyToast(toast,1000);//提示成功添加任务
        taskNameEditText.setText("");
        hourSpinner.setSelection(0);
        minuteSpinner.setSelection(0);
        swc.setChecked(false);
        commentEditText.setText("");
    }

    //修改任务
    private void modifyTask(){
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("task",taskNameEditText.getText().toString() );
//        values.put("assumedtime",assumedTimeEditText1.getText().toString()+":"+assumedTimeEditText2.getText().toString());
        values.put("assumedtime", hour +":"+ minute);
        values.put("deadline",ddlTime.getText().toString());
        values.put("emergencydegree",selectedImageViewPosition>=0? selectedImageViewPosition : 1);
        values.put("isdailytask",isDailyTask());
        values.put("comments",commentEditText.getText().toString());
        db.update("Tasklist",values,"id=?",new String[]{taskId});
        Toast toast = Toast.makeText(EditTaskActivity.this,"成功修改任务",Toast.LENGTH_SHORT);
        showMyToast(toast,1000);
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

    //判断该id对应的ImageView是否已被选择
    private boolean isSelected(int id){
        return selectedImageViewPosition == (int) ivMap.get(id);
    }

    //设置ImageView为被选中
    private void setSelected(int id){
        selectedImageViewPosition=(int)ivMap.get(id);
        ImageView iv =(ImageView)findViewById(id);
        AnimationDrawable ad =(AnimationDrawable) iv.getDrawable();
        ad.start();
    }

    //设置ImageView不被选中
    private void setNotSelected(int id){
        selectedImageViewPosition=0;
        ImageView iv = (ImageView) findViewById(id);
        AnimationDrawable ad = (AnimationDrawable) iv.getDrawable();
        ad.selectDrawable(0);//回到第一帧并暂停
        ad.stop();
    }

    //将点亮的ImageView设为未点亮状态
    private void setDefaultImageView(){
        for(Object key:ivMap.keySet()){
            if((int)ivMap.get(key)==selectedImageViewPosition){
                setNotSelected((int)key);
                break;
            }
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
            intent.putExtra("task_ID",taskId);
            setResult(RESULT_OK, intent);
            finish();
        }
        else{
            super.onBackPressed();
        }
    }

    private void initButton(boolean editMode){
        finishEditing.setEnabled(editMode);
        startNow.setEnabled(editMode);
    }

    private void decideButtonEnable(){//检测文件名是否为空和Spinner选择的时间是否为0
        boolean flag1=taskNameEditText.getText().toString().length()>0;
        boolean flag2=!hour.equals("00")||!minute.equals("00");
        if(flag1&&flag2){
            finishEditing.setEnabled(true);
            startNow.setEnabled(true);
        }
        else{
            finishEditing.setEnabled(false);
            startNow.setEnabled(false);
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
           decideButtonEnable();
        }
    }

}
