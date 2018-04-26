package com.example.el_project;


/*
 * 此部分是任务执行中的活动
 * 目前仅加入一个计时器
 *
 * */


import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TaskOngoingActivity extends AppCompatActivity {

    private CountTimer timer;
    private Activity thisActivity = this;
    private boolean havingTaskOngoing = false;     //是否有任务正在进行中
    private Button btnTaskFinished;                //任务完成，按钮
    private Button btnThrowTask;                   //放弃任务，按钮
    private Button btnPause;                       //暂停
    private TextView taskInfo;                     //显示一些关于任务的信息
    private TextView taskTimeCount;                //显示任务已经过时间
    private TextView tomatoClockTime;              //显示番茄钟倒计时
    private MusicController musicController;
    private CountDownTimer tomatoClockCountDown;   //番茄钟倒计时
    private CountDownTimer tomatoClockBreakCountDown;//番茄钟休息倒计时

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_ongoing);

        initFindView();              //初始那些控件对象
        onClickListenerSetter();     //设置所有涉及到的有关的监听器
        initView();                  //初始化整个布局，包括显示的Task各项信息

        if(GeneralSetting.getMusicOn(this)) {
            musicController = new MusicController(this);
            musicController.start();
        }

        initCountTimer();
        //从被回收内存恢复
        if(savedInstanceState !=null) {
            if (savedInstanceState.getBoolean("is_task_going_on", false)) {
                timer.cancel();
                timer.startWithPassedTime(savedInstanceState.getLong("millis_gone", 0));
            } else {
                timer.start();
            }
        }
        else {
            timer.start();
        }

        havingTaskOngoing = true;

        if(GeneralSetting.getTomatoClockEnable(this)){
            initStartTomatoClock();
        }
    }

    @Override //活动Destroy时，为避免倒计时器的可能问题，取消并释放计时器
    protected void onDestroy() {
        if(timer != null){
            timer.cancel();
            timer = null;
        }

        //释放音乐控制器
        if(musicController != null) {
            musicController.release();
            musicController = null;
        }

        //释放两个番茄钟计时器
        if(tomatoClockCountDown != null){
            tomatoClockCountDown.cancel();
            tomatoClockCountDown = null;
        }
        if(tomatoClockBreakCountDown != null){
            tomatoClockBreakCountDown.cancel();
            tomatoClockBreakCountDown = null;
        }

        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        pause();
    }

    @Override //保存实体状态，在内存被回收时也可恢复
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("millis_gone", timer.getMillisPassed());
        outState.putBoolean("is_task_going_on", havingTaskOngoing);
    }

    //初始化所有要FindViewById的都在这里初始化
    private void initFindView(){
        taskTimeCount = findViewById(R.id.textView_test_task_ongoing);
        tomatoClockTime = findViewById(R.id.text_tomatoClock);
        btnTaskFinished = (Button)findViewById(R.id.btn_task_finished);
        btnThrowTask = (Button)findViewById(R.id.btn_throw_task);
        btnPause = (Button)findViewById(R.id.btn_pause);
    }

    //初始化整个布局，包括显示的Task各项信息
    private void initView(){

    }

    //所有setOnClickListener具体内容放这里
    private void onClickListenerSetter(){
        btnTaskFinished.setOnClickListener(new View.OnClickListener() {
            @Override //任务完成
            public void onClick(View v) {
                removeTaskFromDB();     //从数据库中移除相应Task
                musicController.stop(); //音乐停止播放
                showFinishingActivity();//显示完成界面
                havingTaskOngoing = false;
            }
        });

        btnThrowTask.setOnClickListener(new View.OnClickListener() {
            @Override //放弃任务
            public void onClick(View v) {
                //TODO:放弃完成任务
            //    changeTask();
                musicController.stop();
                showDropActivity();
                havingTaskOngoing = false;
            }
        });

        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override //暂停任务
            public void onClick(View v) {

                Intent tempIntent = new Intent(thisActivity, DrawerSettingActivity.class);
                startActivity(tempIntent);
//                pause();
            }
        });
    }

    //从数据库删除当前任务
    private void removeTaskFromDB(){
        //TODO:移除当前任务
    }

    //显示完成界面
    private void showFinishingActivity(){
        //TODO:完成界面，目前暂时回到主界面
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    //修改任务，主要是修改预计完成时间
    private void changeTask(long dueTimeToFinish){
        //TODO:修改任务描述，自动修改预计完成时间
    }

    //显示放弃完成后的活动，应为主活动
    private void showDropActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void pause(){
        musicController.pause();
        timer.pause();

        //若开启番茄钟，开始番茄钟任务进行时间计时
        if (tomatoClockCountDown != null) {
            tomatoClockCountDown.cancel();
            tomatoClockCountDown = null;
        }
        if(GeneralSetting.getTomatoClockEnable(this) && GeneralSetting.getTomatoClockEnable(this)) {
            initStartTomatoClockBreak();
        }
    }

    private void resume(){
        if(GeneralSetting.getMusicOn(this)) {
            musicController.resume();
        }
        timer.resume();

        //若开启番茄钟，开始番茄钟任务休息时间计时
        if(tomatoClockBreakCountDown != null){
            tomatoClockBreakCountDown.cancel();
            tomatoClockBreakCountDown = null;
        }
        if (GeneralSetting.getTomatoClockEnable(this) && GeneralSetting.getTomatoClockEnable(this)) {
            initStartTomatoClock();
        }
    }

    //显示暂停时活动
    private void showPauseActivity(){
        //TODO:显示暂停时活动，应有一个恢复按键
    }

    //初始化计时器，内部修改每秒行为，计时器开始计时前必须初始化
    private void initCountTimer(){
        timer = new CountTimer(1000) {
            @Override
            public void onTick(long millisGoneThrough) {
                taskTimeCount.setText(millis2HourMinSecString(millisGoneThrough));
            }
        };
    }

    private void initStartTomatoClock(){

        if (tomatoClockCountDown != null){
            tomatoClockCountDown.cancel();
        }
        tomatoClockCountDown = new CountDownTimer(GeneralSetting.getTomatoClockTime(this) * 60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tomatoClockTime.setText(millis2HourMinSecString(millisUntilFinished));
            }

            @Override
            public void onFinish() {
                sendNotification("番茄钟计时到", "工作很久了，休息一下吧");
            }
        };
        tomatoClockCountDown.start();
    }

    private void initStartTomatoClockBreak(){
        if (tomatoClockBreakCountDown != null){
            tomatoClockBreakCountDown.cancel();
        }
        tomatoClockBreakCountDown = new CountDownTimer(GeneralSetting.getTomatoBreakTime(this) * 60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                millis2HourMinSecString(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                sendNotification("番茄钟计时到", "休息有一会了，可以工作了吧");
            }
        };
        tomatoClockBreakCountDown.start();
    }

    private void sendNotification(String title, String text){

        //TODO:通知发不出去
        Intent intent = new Intent(this, TaskOngoingActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        Notification notification= new Notification.Builder(this)
                .setAutoCancel(true)
                .setContentText(text)
                .setContentTitle(title)
                .setSmallIcon(R.drawable.clock)
                .setContentIntent(pendingIntent)
                .setVibrate(new long[]{1000, 1000, 1000, 1000})
                .build();

        try {
            notificationManager.notify(1, notification);
            Log.d("TaskOnGoingActivity", "onFinish: TomatoClockStop");
        }catch (NullPointerException e){
            Log.e("TaskOngoingActivity", "onFinish: " + e.toString());
        }
    }

    //从毫秒转换到一个字符串的时间，显示时间时可调用
    public String millis2HourMinSecString(long millis){
        long second = millis / 1000;
        long minute = second / 60;
        long hour = minute / 60;
        minute = minute % 60;
        second = second % 60;
        return String.format("%02d:%02d:%02d", hour, minute, second);
    }

    //从时分秒计转换到秒
    public int hourMinSec2Seconds(int hour, int minute, int second){
        return hour * 3600 + minute * 60 + second;
    }

    //从时分秒计转换到毫秒，注意：返回类型为long
    public long hourMinSec2Miillis(int hour, int minute, int second){
        return (long) hourMinSec2Seconds(hour, minute, second) * 1000;
    }
}
