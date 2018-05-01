package com.example.el_project;


/*
* 任务计时界面，即任务进行中的界面，右侧划出可进行设置
* 我把layout几项有改过名，Activity名字我也改了
* 番茄钟功能暂未实现
* 番茄钟到时通知功能暂未实现
* 项目名等显示也暂未实现
* ——NA
* */

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.CountDownTimer;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class TaskTimingActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {
	private DrawerLayout mDrawerLayout;
	private Switch switch_clock_status;
	private Switch switch_music_status;

	private CountTimer timer;
	private Activity thisActivity = this;
	private boolean havingTaskOngoing = false;     //是否有任务正在进行中
	private boolean taskStatuePaused = false;        //任务暂停或进行中Flag
	private ImageButton btnTaskFinished;                //任务完成，按钮
	private ImageButton btnThrowTask;                   //放弃任务，按钮
	private ImageButton btnPause;                       //暂停
	private TextView taskInfo;                     //显示一些关于任务的信息
	private TextView taskTimeCount;                //显示任务已经过时间
	private TextView tomatoClockTime;              //显示番茄钟倒计时
	private MusicController musicController;
	private CountDownTimer tomatoClockCountDown;   //番茄钟倒计时
	private CountDownTimer tomatoClockBreakCountDown;//番茄钟休息倒计时
	private LinearLayout remarkLayout;             //备注所在的布局
	private LinearLayout.LayoutParams remarkLayoutLayoutParams; //布局大小
	private TextView remareText;                   //备注

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_timing);

		//初始化Toolbar
		Toolbar toolbar=findViewById(R.id.setting_toolbar);
		setSupportActionBar(toolbar);
		mDrawerLayout=findViewById(R.id.drawer_layout);
		ActionBar actionBar=getSupportActionBar();

		//初始化设置界面具体内容
		switch_clock_status=findViewById(R.id.switch_if_tomato_clock_on);
		switch_music_status=findViewById(R.id.switch_if_music_on);
		switch_clock_status.setChecked(GeneralSetting.getTomatoClockEnable(this));
		switch_music_status.setChecked(GeneralSetting.getMusicOn(this));
		switch_clock_status.setOnCheckedChangeListener(this);
		switch_music_status.setOnCheckedChangeListener(this);
		//计时动画
		TextView tv=(TextView)findViewById(R.id.time_action);
		AnimationDrawable ad=(AnimationDrawable)tv.getBackground();
		ad.start();

		if(actionBar!=null){
			actionBar.setDisplayHomeAsUpEnabled(true);  //显示导航按钮
			actionBar.setHomeAsUpIndicator(R.drawable.category_white_31);  //设置导航按钮图标
		}

		initMainFindView();              //初始那些计时控制部分控件对象
		onClickListenerMainSetter();     //设置计时控制部分所有涉及到的有关的监听器
		initView();                      //初始化整个布局的其余部分，将包括显示的Task各项信息

		//设置音乐开启
		if(GeneralSetting.getMusicOn(this)) {
			musicController = new MusicController(this);
			musicController.start();
		}

		initCountTimer();
		//从被回收内存恢复，但感觉问题还是挺大
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

		//设置有任务正在进行的Flag，可能会有用
		havingTaskOngoing = true;

		/*TODO:初始化的启动番茄钟部分
		//初始化并启动番茄钟
		if(GeneralSetting.getTomatoClockEnable(this)){
			initStartTomatoClock();
		}
		*/

		//TODO:不太懂现有的点击事件，自己写了单独一个 cz
		remarkLayout=(LinearLayout)findViewById(R.id.layout_remark);
		remarkLayout.setOnClickListener(this);
		remarkLayoutLayoutParams=(LinearLayout.LayoutParams)remarkLayout.getLayoutParams();
		remareText=(TextView)findViewById(R.id.edit_remark);

	}

	public boolean onCreateOptionsMenu(Menu menu){
		getMenuInflater().inflate(R.menu.toolbar,menu);
		return true;
	}


	//对HomeAsUp按钮点击事件进行处理
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				mDrawerLayout.openDrawer(GravityCompat.START);  //展示滑动菜单，传入Gravity参数
				break;
			default:
		}
		return true;
	}

	@Override
	public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
		switch (compoundButton.getId()){
			case R.id.switch_if_music_on:
				if(compoundButton.isChecked()) {
				    GeneralSetting.setMusicOn(TaskTimingActivity.this, true);
					Toast.makeText(this,"音乐已打开",Toast.LENGTH_SHORT).show();
				}
				else {
				    GeneralSetting.setMusicOn(TaskTimingActivity.this, false);
				    Toast.makeText(this,"音乐已关闭",Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.switch_if_tomato_clock_on:
				if(compoundButton.isChecked()) {
				    GeneralSetting.setTomatoClockEnable(TaskTimingActivity.this, true);
				    Toast.makeText(this,"番茄钟已打开",Toast.LENGTH_SHORT).show();
				}
				else {
				    GeneralSetting.setTomatoClockEnable(TaskTimingActivity.this, false);
				    Toast.makeText(this,"番茄钟已关闭",Toast.LENGTH_SHORT).show();
				}
				break;
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
	private void initMainFindView(){
		taskTimeCount = findViewById(R.id.time_action);
//		tomatoClockTime = findViewById(R.id.text_tomatoClock);
		btnTaskFinished = (ImageButton)findViewById(R.id.finish_button);
		btnThrowTask = (ImageButton)findViewById(R.id.give_up_button);
		btnPause = (ImageButton) findViewById(R.id.pause_button);
	}

	//TODO:初始化整个布局，包括显示的Task各项信息
	private void initView(){

	}

	//所有setOnClickListener具体内容放这里
	private void onClickListenerMainSetter(){
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
				if(!taskStatuePaused) {
					pause();
					taskStatuePaused = true;
				}
				else {
					resume();
					taskStatuePaused = false;
				}
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

		/*TODO:暂停的番茄钟部分
		//若开启番茄钟，开始番茄钟任务进行时间计时
		if (tomatoClockCountDown != null) {
			tomatoClockCountDown.cancel();
			tomatoClockCountDown = null;
		}
		if(GeneralSetting.getTomatoClockEnable(this) && GeneralSetting.getTomatoClockEnable(this)) {
			initStartTomatoClockBreak();
		}
		*/
	}

	private void resume(){
		if(GeneralSetting.getMusicOn(this)) {
			musicController.resume();
		}
		timer.resume();

		/*TODO:恢复计时的番茄钟部分
		//若开启番茄钟，开始番茄钟任务休息时间计时
		if(tomatoClockBreakCountDown != null){
			tomatoClockBreakCountDown.cancel();
			tomatoClockBreakCountDown = null;
		}
		if (GeneralSetting.getTomatoClockEnable(this) && GeneralSetting.getTomatoClockEnable(this)) {
			initStartTomatoClock();
		}
		*/
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
		Intent intent = new Intent(this, TaskTimingActivity.class);
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
			Log.d("TaskTimingActivity", "onFinish: TomatoClockStop");
		}catch (NullPointerException e){
			Log.e("TaskTimeActivity", "onFinish: " + e.toString());
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

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.layout_remark:
				if(remarkLayoutLayoutParams.height==90){
					remarkLayoutLayoutParams.height=100;
					remareText.setMaxLines(8);
				}else if(remarkLayoutLayoutParams.height==100){
					remarkLayoutLayoutParams.height=90;
					remareText.setMaxLines(2);
					remareText.setEllipsize(TextUtils.TruncateAt.END);
				}
		}
	}
}
