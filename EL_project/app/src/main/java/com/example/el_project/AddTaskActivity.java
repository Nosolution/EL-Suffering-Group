package com.example.el_project;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;

public class AddTaskActivity extends AppCompatActivity {

    private MyDatabaseHelper dbHelper;
    private  Button addTask;//完成添加任务
    private EditText et1;//任务名
    private EditText et2;//预计时间
    private EditText et3;//最后日期
    private EditText et4;//优先级
    private ItemsDialogFragment itemsDialogFragment = new ItemsDialogFragment();//选项对话框
    private Calendar calendar;
    private int mYear;
    private int mMonth;
    private int mDay;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        addTask = (Button)findViewById(R.id.add_task);
        textChange tc = new textChange();//文本改变监视器
        et1 = (EditText) findViewById(R.id.input_taskname);
        et1.addTextChangedListener(tc);//监控et1与et2是否不为空
        et2 = (EditText) findViewById(R.id.input_assumed_time);
        et2.addTextChangedListener(tc);
        et2.setInputType(InputType.TYPE_NULL);//设置et2至et4不可编辑
        et3 = (EditText) findViewById(R.id.input_deadline);
        et3.setInputType(InputType.TYPE_NULL);
        et4 = (EditText) findViewById(R.id.input_priority);
        et4.setInputType(InputType.TYPE_NULL);

        calendar = Calendar.getInstance();//获取当前时间

        //点击et2跳出对话框
        et2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    showTimeDialogFragment();
                    et2.requestFocus();
                    et2.setSelection(et2.getText().toString().length());
                }
            }
        });
        et2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeDialogFragment();
                et2.requestFocus();
                et2.setSelection(et2.getText().toString().length());
            }
        });

        //点击et3跳出日历
        et3.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    showDatePickerDialog();
                    et3.requestFocus();
                    et3.setSelection(et3.getText().toString().length());
                }
            }
        });
        et3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
                et3.requestFocus();
                et3.setSelection(et3.getText().toString().length());
            }
        });

        //点击et4跳出对话框
        et4.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    showPriorityDialog();
                }
            }
        });
        et4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPriorityDialog();
            }
        });

        //打开数据库
        dbHelper = new MyDatabaseHelper(this,"TaskStore.db",null,1);

        //完成添加按钮的点击事件
        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               addTask();
            }
        });
    }

    //完成添加任务
    private void addTask(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();//真正打开数据库
        ContentValues values = new ContentValues();//传值工具
        values.put("deadline",et3.getText().toString());//对应每一列传值
        values.put("assumedtime",et2.getText().toString());
        values.put("priority",et4.getText().toString());
        values.put("task",et1.getText().toString() );
        db.insert("Tasklist",null,values);//将值传入数据库中的"Tasklist"表
        Toast.makeText(AddTaskActivity.this,"成功添加任务",Toast.LENGTH_SHORT).show();//提示成功添加任务
        et1.setText("");et2.setText("");et3.setText("");et4.setText("");//清空所有Edittext中的内容
    }

    //初始化并显示"预计时间"对话框
    public void showTimeDialogFragment() {
        final String[] items={"45mins", "1h", "1h30mins"};
        itemsDialogFragment.show("预计时间", items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                Toast.makeText(AddTaskActivity.this, "点击了第 " + (which + 1) + " 个选项", Toast.LENGTH_SHORT).show();
              et2.setText(items[which]);
            }
        }, getFragmentManager());
    }

    //初始化并显示日历
    public void showDatePickerDialog() {
        new DatePickerDialog(AddTaskActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                mYear = i;
                mMonth = i1;
                mDay = i2;
                et3.setText(new StringBuffer()
                        .append(mYear)
                        .append("-")
                        .append((mMonth + 1) < 10 ? "0" + (mMonth + 1) : (mMonth + 1))
                        .append("-")
                        .append((mDay + 1) < 10 ? "0" + mDay : mDay));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();

    }
    //初始化并显示"优先级"对话框
    public void showPriorityDialog(){
        final String[] items={"最高", "次级", "三级","平常"};
        itemsDialogFragment.show("优先级", items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                Toast.makeText(AddTaskActivity.this, "点击了第 " + (which + 1) + " 个选项", Toast.LENGTH_SHORT).show();
                et4.setText(items[which]);
            }
        }, getFragmentManager());

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
            boolean flag1=et1.getText().toString().length()>0;
            boolean flag2=et2.getText().toString().length()>0;
            if(flag1&&flag2){
                addTask.setEnabled(true);
            }
            else{
                addTask.setEnabled(false);
            }
        }
    }


}
