package com.example.el_project;


/*
 * @author NA
* 任务计时界面，即任务进行中的界面，右侧划出可进行设置
* 我把layout几项有改过名，Activity名字我也改了
* 番茄钟功能暂未实现
* 番茄钟到时通知功能暂未实现
* */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.CountDownTimer;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.tauth.Tencent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskTimingActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
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
	private TextView text_clock_on;
	private TextView text_chosen_time;
	private TextView timeLeft;
	private MusicController musicController;
	private CountDownTimer tomatoClockCountDown;   //番茄钟倒计时
	private CountDownTimer tomatoClockBreakCountDown;//番茄钟休息倒计时

	private int taskId;                              //任务id
	private long taskMillisRequired;                 //任务预计用时，毫秒
	private String taskName;                         //任务名
	private String taskComments;                      //任务备注

	private LinearLayout remarkLayout;             //备注所在的布局
	private LinearLayout.LayoutParams remarkLayoutLayoutParams; //布局大小
	private TextView remarkText;                   //备注
	private Spinner spinner_choose_time;
	private List<Map<String,Object>> data_time;
	private Toolbar toolbar;

	private Tencent mTencent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_timing);


		mTencent = Tencent.createInstance("1106810223", getApplicationContext());



		//初始化Toolbar
		toolbar=findViewById(R.id.setting_toolbar);
		setSupportActionBar(toolbar);
		mDrawerLayout=findViewById(R.id.drawer_layout);
		ActionBar actionBar=getSupportActionBar();

		//初始化设置界面具体内容
		switch_clock_status = findViewById(R.id.switch_if_tomato_clock_on);
		switch_music_status = findViewById(R.id.switch_if_music_on);
		spinner_choose_time = findViewById(R.id.spinner_choose_time);
		text_clock_on = findViewById(R.id.text_clock_on);
		text_chosen_time = findViewById(R.id.text_chosen_time);
		timeLeft = findViewById(R.id.time_left);
		remarkText = findViewById(R.id.edit_remark);
		switch_clock_status.setChecked(GeneralSetting.getTomatoClockEnable(this));
		switch_music_status.setChecked(GeneralSetting.getMusicOn(this));
		switch_clock_status.setOnCheckedChangeListener(this);
		switch_music_status.setOnCheckedChangeListener(this);
		text_chosen_time.setText("20分钟");

		//计时动画
		RelativeLayout rl=(RelativeLayout) findViewById(R.id.time_act);
		AnimationDrawable ad=(AnimationDrawable)rl.getBackground();
		ad.start(); //TODO:BUG!!若动画启动会报错

		//番茄钟动画
		ImageView iv=(ImageView)findViewById(R.id.tomato_act);
		AnimationDrawable ad2=(AnimationDrawable)iv.getBackground();
		ad2.start();

		if(actionBar!=null){
			actionBar.setDisplayHomeAsUpEnabled(true);  //显示导航按钮
//			actionBar.setHomeAsUpIndicator(R.drawable.category_white_31);  //设置导航按钮图标
		}

		initMainFindView();              //初始那些计时控制部分控件对象
		onClickListenerMainSetter();     //设置计时控制部分所有涉及到的有关的监听器

		//取得开始任务时传来的任务详细信息
		Intent intentTaskInfo = getIntent();
		taskId = intentTaskInfo.getIntExtra("intent_task_id", 0);
		if (taskId == 0) finish();                                            //若未收到任务ID，退出
		taskName = intentTaskInfo.getStringExtra("intent_task_name");
		int taskHoursRequired = intentTaskInfo.getIntExtra("intent_task_hours_required", 0);
		int taskMintersRequired = intentTaskInfo.getIntExtra("intent_task_minutes_required", 0);
		taskComments = intentTaskInfo.getStringExtra("intent_task_comments");
		Log.d("ReceiveTaskInfo", "onCreate: " + taskComments);
		taskMillisRequired = hourMinSec2Millis(taskHoursRequired, taskMintersRequired, 0);

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


		//初始化并启动番茄钟
		if(GeneralSetting.getTomatoClockEnable(this)){
			initStartTomatoClock();
		}

		//开启番茄钟设置
		//数据源
		data_time=new ArrayList<>();
		//创建一个SimpleAdapter适配器
		//第一个参数：上下文，第二个参数：数据源，第三个参数：item子布局，第四、五个参数：键值对，获取item布局中的控件id
		final SimpleAdapter s_adapter=new SimpleAdapter(this,getData(),R.layout.spinner_choose_time,
				new String[]{"text"}, new int[]{R.id.text_time});
		//控件与适配器绑定
		spinner_choose_time.setAdapter(s_adapter);
		//点击事件
		spinner_choose_time.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				//为TextView控件赋值，在适配器中获取一个值赋给tv_time_length
				text_chosen_time.setText(""+s_adapter.getItem(position));
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

	}

	//Spinner数据源
	private List<Map<String,Object>> getData(){
		Map<String,Object> map_20min = new HashMap<>();
		map_20min.put("text","20分钟");
		data_time.add(map_20min);

		Map<String,Object> map_30min = new HashMap<>();
		map_30min.put("text","30分钟");
		data_time.add(map_30min);

		Map<String,Object> map_40min = new HashMap<>();
		map_40min.put("text","40分钟");
		data_time.add(map_40min);

		return data_time;
	}


	public boolean onCreateOptionsMenu(Menu menu){
		getMenuInflater().inflate(R.menu.toolbar,menu);
		return true;
	}


	//对HomeAsUp按钮点击事件进行处理
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
//			case android.R.id.home:
//				mDrawerLayout.openDrawer(GravityCompat.START);  //展示滑动菜单，传入Gravity参数
//				break;
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
				    GeneralSetting.setMusicOn(TaskTimingActivity.this, true);
					Toast.makeText(this,"音乐已打开",Toast.LENGTH_SHORT).show();
					if(!musicController.isPlaying() && havingTaskOngoing){
						musicController.restart();
					}
				}
				else {
				    GeneralSetting.setMusicOn(TaskTimingActivity.this, false);
				    Toast.makeText(this,"音乐已关闭",Toast.LENGTH_SHORT).show();
				    if(musicController.isPlaying()){
				    	musicController.stop();
					}
				}
				break;
			case R.id.switch_if_tomato_clock_on:
				if(compoundButton.isChecked()) {
				    GeneralSetting.setTomatoClockEnable(TaskTimingActivity.this, true);
				    Toast.makeText(this,"番茄钟已打开",Toast.LENGTH_SHORT).show();
					text_clock_on.setVisibility(View.VISIBLE);
					text_chosen_time.setVisibility(View.VISIBLE);
					spinner_choose_time.setVisibility(View.VISIBLE);
				}
				else {
				    GeneralSetting.setTomatoClockEnable(TaskTimingActivity.this, false);
				    Toast.makeText(this,"番茄钟已关闭",Toast.LENGTH_SHORT).show();
					text_clock_on.setVisibility(View.GONE);
					text_chosen_time.setVisibility(View.GONE);
					spinner_choose_time.setVisibility(View.GONE);
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

	@Override
	public void onBackPressed() {
		final AlertDialog.Builder onBackAskQuitDialog =
				new AlertDialog.Builder(this);
		onBackAskQuitDialog.setTitle("放弃任务");
		onBackAskQuitDialog.setMessage("是否放弃当前任务退出");
		onBackAskQuitDialog.setPositiveButton("放弃任务", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//TODO:放弃完成任务
				//    changeTask();
				musicController.stop();
				showDropActivity();
				havingTaskOngoing = false;
			}
		});
		onBackAskQuitDialog.setNegativeButton("继续任务", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		});
		onBackAskQuitDialog.show();
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
		tomatoClockTime = findViewById(R.id.tomato_text);
		btnTaskFinished = (ImageButton)findViewById(R.id.finish_button);
		btnThrowTask = (ImageButton)findViewById(R.id.give_up_button);
		btnPause = (ImageButton) findViewById(R.id.pause_button);
	}


	private void initView(){
		toolbar.setTitle(taskName);                                        //设置toolbar标题显示任务名
		remarkText.setText(taskComments);                                  //设置备注显示
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
				}
				else {
					resume();
				}
			}
		});

	}


	//从数据库删除当前任务
	private void removeTaskFromDB(){
		MyDatabaseOperation.deleteTask(this, taskId);
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

		taskStatuePaused = true;
		btnPause.setBackgroundResource(R.drawable.doing_watercolor);


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

		taskStatuePaused = false;
		btnPause.setBackgroundResource(R.drawable.stop_watercolor);

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
				timeLeft.setText("距离完成还有" + millis2HourMinSecString(Math.max((taskMillisRequired - millisGoneThrough + 60000), 0), 2));
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
				tomatoClockTime.setText("据下次休息还有" + millis2HourMinSecString(millisUntilFinished));
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
				tomatoClockTime.setText("据休息结束还有" + millis2HourMinSecString(millisUntilFinished));
			}

			@Override
			public void onFinish() {
				sendNotification("番茄钟计时到", "休息有一会了，可以工作了吧");
			}
		};
		tomatoClockBreakCountDown.start();
	}

	//发送通知，暂只用于番茄钟提醒
	//@param:提醒的标题，提醒的正文内容
	private void sendNotification(String title, String text){

		Intent intent = new Intent(this, TaskTimingActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
		NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
			NotificationChannel channel = new NotificationChannel("channel_tomato_clock", "ChannelTomatoClock", NotificationManager.IMPORTANCE_DEFAULT);
			channel.enableLights(false); //是否在桌面icon右上角展示小红点
			channel.setShowBadge(false); //是否在久按桌面图标时显示此渠道的通知
			channel.enableVibration(false);
			channel.setSound(null, null);
			notificationManager.createNotificationChannel(channel);
		}
		Notification notification= new NotificationCompat.Builder(this, "channel_tomato_clock")
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

	public String millis2HourMinSecString(long millis, int itemNum){
		long second = millis / 1000;
		long minute = second / 60;
		long hour = minute / 60;
		minute = minute % 60;
		second = second % 60;
		if(itemNum == 2){
			return String.format("%02d:%02d", hour, minute);
		}else {
			return String.format("%02d:%02d:%02d", hour, minute, second);
		}
	}

	//从时分秒计转换到秒
	public int hourMinSec2Seconds(int hour, int minute, int second){
		return hour * 3600 + minute * 60 + second;
	}

	//从时分秒计转换到毫秒，注意：返回类型为long
	public long hourMinSec2Millis(int hour, int minute, int second){
		return (long) hourMinSec2Seconds(hour, minute, second) * 1000;
	}

}
