package com.example.el_project;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Main2Activity extends AppCompatActivity implements View.OnClickListener{

    private RelativeLayout selectTime;
    private TextView ddlTime;
    private CustomDatePicker cdp;
    private ImageView iv1,iv2,iv3,iv4,iv5;
    private Switch swc;

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

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
        iv1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                ImageView iv=(ImageView)v;
                AnimationDrawable ad=(AnimationDrawable)iv.getDrawable();
                ad.stop();
                ad.start();
                return true;
            }
        });

        iv2=(ImageView)findViewById(R.id.circle_two);
        iv2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                ImageView iv=(ImageView)v;
                AnimationDrawable ad=(AnimationDrawable)iv.getDrawable();
                ad.stop();
                ad.start();
                return true;
            }
        });

        iv3=(ImageView)findViewById(R.id.circle_three);
        iv3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                ImageView iv=(ImageView)v;
                AnimationDrawable ad=(AnimationDrawable)iv.getDrawable();
                ad.stop();
                ad.start();
                return true;
            }
        });

        iv4=(ImageView)findViewById(R.id.circle_four);
        iv4.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                ImageView iv=(ImageView)v;
                AnimationDrawable ad=(AnimationDrawable)iv.getDrawable();
                ad.stop();
                ad.start();
                return true;
            }
        });

        iv5=(ImageView)findViewById(R.id.circle_five);
        iv5.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                ImageView iv=(ImageView)v;
                AnimationDrawable ad=(AnimationDrawable)iv.getDrawable();
                ad.stop();
                ad.start();
                return true;
            }
        });
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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.selectTime:
                cdp.show(ddlTime.getText().toString());
        }

    }
}
