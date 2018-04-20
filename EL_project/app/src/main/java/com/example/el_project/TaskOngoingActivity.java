package com.example.el_project;


/*
 * 此部分是任务执行中的活动
 * 目前仅加入一个计时器
 *
 * */


import android.app.Activity;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class TaskOngoingActivity extends AppCompatActivity {

    private CountDownTimer timer;
    private TextView textView;
    private Activity thisActivity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_ongoing);

        //该方法下面的部分都是用于测试Timer有没有起作用，可删
        textView = findViewById(R.id.textView_test_task_ongoing);
        initTimer(60);
        timer.start();
    }

    @Override
    protected void onDestroy() { //活动Destroy时，为避免计时器的可能问题，取消并释放定时器
        if(timer != null){
            timer.cancel();
            timer = null;
        }
        super.onDestroy();
    }

    private void initTimer(int secondToCount){ //初始化计时器，内部修改每秒行为和结束行为，计时器开始计时前必须初始化
        timer = new CountDownTimer(secondToCount * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if(!thisActivity.isFinishing()){ //为避免内存泄漏等问题，仅当当前活动存活时执行
                    //每秒此倒计时器的行为
                    textView.setText(millis2HourMinSecString(millisUntilFinished));
                }
            }

            @Override
            public void onFinish() {
                //当倒计时结束时，行为
            }
        };
    }

    public String millis2HourMinSecString(long millis){ //从毫秒转换到一个字符串的时间，显示剩余时间时可调用
        long second = millis / 1000 + 1;
        long minute = second / 60;
        long hour = minute / 60;
        minute = minute % 60;
        second = second % 60;
        return hour + ":" + minute + ":" + second;
    }

    public int hourMinSec2Seconds(int hour, int minute, int second){ //从时分秒计转换到秒
        return hour * 3600 + minute * 60 + second;
    }

    public long hourMinSec2Miillis(int hour, int minute, int second){ //从时分秒计转换到毫秒，注意：返回类型为long
        return (long) hourMinSec2Seconds(hour, minute, second) * 1000;
    }
}
