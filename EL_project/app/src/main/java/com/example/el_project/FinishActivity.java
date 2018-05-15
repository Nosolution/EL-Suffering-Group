package com.example.el_project;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import com.github.mikephil.charting.charts.BarChart;
import com.tencent.connect.common.Constants;
import com.tencent.connect.share.QQShare;
import com.tencent.tauth.Tencent;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * 我不确定完成界面的专注度的得分下面要显示什么，就把它们都放上去了
 */

public class FinishActivity extends AppCompatActivity {
	private TextView tvGoal;  // 用于显示专注度的分数，格式：“__分”
	private TextView tvSingleTaskConsumeTime;  // 单个任务的耗时，格式：“本次任务共耗时 __ 分钟”
	private TextView tvSingleTaskConcentrateTime;  // 单个任务的专注时间，格式：“专注 __ 分钟”
	private TextView tvWeekConcentrateTime;  // 一周的专注时间，格式：“本周已专注 __ 分钟”
	private Button buttonReturnMain;  // 返回主界面按钮
	private Button buttonNextTask;  // 开始下一项任务的按钮
	private Button buttonShare;     //分享按钮
	private String strGoal;
	private String strSingleTaskConsumeTime;
	private String strSingleTaskConcentrateTime;
	private String strWeekConcentrateTime;
	private SpannableStringBuilder ssbGoal;
	private SpannableStringBuilder ssbSingleTaskConsumeTime;
	private SpannableStringBuilder ssbSingleTaskConcentrateTime;
	private SpannableStringBuilder ssbWeekConcentrateTime;
	int intGoal = 96;  // 传入数据
	int intSingleTaskConsumeTime = 120;
	int intSingleTaskConcentrateTime = 80;
	int intWeekConcentrateTime = 400;
	int intStart;  // 字符串索引
	int intEnd;

	private int taskTotalTimeUsed;         //任务总计完成时间
	private int taskTimeUsed;              //任务有效完成时间
	private int timeUsedToday;             //今日用于完成任务的时间
	private int breakCount;                //任务执行时切出次数
	private int[] taskTimeUsedWeek;        //本周任务每日总计的有效时间

	private Tencent mTencent;
	private MyIUiListener myIUiListener;
	private Bundle params;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_finish);

		BarChart barChart=findViewById(R.id.data_bar_chart);
		MyBarChartManager myBarChartManager=new MyBarChartManager(barChart);
		tvGoal = findViewById(R.id.tv_goal);
		tvSingleTaskConsumeTime = findViewById(R.id.tv_single_task_consume_time);
		tvSingleTaskConcentrateTime = findViewById(R.id.tv_single_task_concentrate_time);
		tvWeekConcentrateTime = findViewById(R.id.tv_week_concentrate_time);
		buttonReturnMain = findViewById(R.id.button_return_main);
		buttonNextTask = findViewById(R.id.button_next_task);
		buttonShare = findViewById(R.id.button_share_to_qzone);
		LinearLayout layoutMain = findViewById(R.id.activity_finish_layout);

		BackgroundCollection backgroundCollection = new BackgroundCollection();
		layoutMain.setBackgroundResource(backgroundCollection.getTodayBackground());

		// 设置TextView的显示格式
		strGoal = intGoal + "分";
		strSingleTaskConsumeTime = "本次任务共耗时 " + intSingleTaskConsumeTime + " 分钟";
		strSingleTaskConcentrateTime = "专注 " + intSingleTaskConcentrateTime + " 分钟";
		strWeekConcentrateTime = "本周已专注 " +intWeekConcentrateTime + " 分钟";

		// 设置各个TextView显示的值
		ssbGoal = new SpannableStringBuilder(strGoal);
		intStart = strGoal.indexOf(String.valueOf(intGoal));
		intEnd = intStart + String.valueOf(intGoal).length();
		ssbGoal.setSpan(new RelativeSizeSpan(2.8f), intStart, intEnd, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
		tvGoal.setText(ssbGoal);

		ssbSingleTaskConsumeTime = new SpannableStringBuilder(strSingleTaskConsumeTime);
		intStart = strSingleTaskConsumeTime.indexOf(String.valueOf(intSingleTaskConsumeTime));
		intEnd = intStart + String.valueOf(intSingleTaskConsumeTime).length();
		ssbSingleTaskConsumeTime.setSpan(new RelativeSizeSpan(1.4f), intStart, intEnd, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
		ssbSingleTaskConsumeTime.setSpan(new StyleSpan(Typeface.BOLD), intStart, intEnd, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
		tvSingleTaskConsumeTime.setText(ssbSingleTaskConsumeTime);

		ssbSingleTaskConcentrateTime = new SpannableStringBuilder(strSingleTaskConcentrateTime);
		intStart = strSingleTaskConcentrateTime.indexOf(String.valueOf(intSingleTaskConcentrateTime));
		intEnd = intStart + String.valueOf(intSingleTaskConcentrateTime).length();
		ssbSingleTaskConcentrateTime.setSpan(new RelativeSizeSpan(1.4f), intStart, intEnd, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
		ssbSingleTaskConcentrateTime.setSpan(new StyleSpan(Typeface.BOLD), intStart, intEnd, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
		tvSingleTaskConcentrateTime.setText(ssbSingleTaskConcentrateTime);

		ssbWeekConcentrateTime = new SpannableStringBuilder(strWeekConcentrateTime);
		intStart = strWeekConcentrateTime.indexOf(String.valueOf(intWeekConcentrateTime));
		intEnd = intStart + String.valueOf(intWeekConcentrateTime).length();
		ssbWeekConcentrateTime.setSpan(new RelativeSizeSpan(1.4f), intStart, intEnd, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
		ssbWeekConcentrateTime.setSpan(new StyleSpan(Typeface.BOLD), intStart, intEnd, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
		tvWeekConcentrateTime.setText(ssbWeekConcentrateTime);

		Log.d("TEST", "onCreate: FinishActivity");

		//设置x轴的数据
		ArrayList<Float> xValues = new ArrayList<>();
		for (int i = 0; i <= 6; i++) {
			xValues.add((float) i);
		}

		//设置y轴的数据()
		List<List<Float>> yValues = new ArrayList<>();
		for (int i = 0; i < 4; i++) {
			List<Float> yValue = new ArrayList<>();
			for (int j = 0; j <= 6; j++) {
				yValue.add((float) (Math.random() * 240));
			}
			yValues.add(yValue);
		}

		//颜色集合
		List<Integer> colors = new ArrayList<>();
		colors.add(Color.GREEN);
		colors.add(Color.BLUE);
		colors.add(Color.RED);
		colors.add(Color.CYAN);

		myBarChartManager.showBarChart(xValues,yValues.get(0),"数据一",colors.get(3));


		//得到任务完成信息和本周每日工作学习时长（有效，总未记录）
		getTaskFinishInfo();

		buttonReturnMain.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(FinishActivity.this, MainActivity.class);
				startActivity(intent);
			}
		});
		buttonShare.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				shareToQQ(FinishActivity.this);
			}
		});


		//设置分享用的QQ实例初始化
		mTencent = Tencent.createInstance("1106810223", getApplicationContext());
		myIUiListener = new MyIUiListener();

	}

	//获得任务完成信息
	private void getTaskFinishInfo(){
		Intent intent = getIntent();
		taskTotalTimeUsed = intent.getIntExtra("task_total_time_used", 0);
		taskTimeUsed = intent.getIntExtra("task_time_used", 0);
		timeUsedToday = MyDatabaseOperation.getTotalTimeUsedToday(this);
		breakCount = intent.getIntExtra("break_count", 0);
		taskTimeUsedWeek = MyDatabaseOperation.getThisWeekPerDayTimeUsed(this);
	}

	private void shareToQQ(Context context){
		Calendar calendar = new GregorianCalendar();
		SimpleDateFormat format = new SimpleDateFormat("yyMMddHHmmss", Locale.getDefault());
		String fileName = format.format(calendar.getTime());

		TempPicStorageManager storageManager = new TempPicStorageManager(context, "tempPicToShare");
		String dirPath = storageManager.getDirPath();

		BackgroundCollection backgroundCollection = new BackgroundCollection();
		BitmapFactory.Options options = new BitmapFactory.Options();
		Bitmap bitmapToShare = BitmapFactory.decodeResource(getResources(), backgroundCollection.getTodayBackground(), options);
		int height = bitmapToShare.getHeight();
		int width = bitmapToShare.getWidth();
		int density = bitmapToShare.getDensity();

		Bitmap.Config config = bitmapToShare.getConfig();
		if(config == null) config = Bitmap.Config.ARGB_8888;
		bitmapToShare = bitmapToShare.copy(config, true);

		Canvas canvas = new Canvas(bitmapToShare);
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

		//绘制表层模板
		Bitmap bitmapRefUpper = BitmapFactory.decodeResource(getResources(), R.drawable.share_ref_upper, options);
		canvas.drawBitmap(bitmapRefUpper, 0.0f, 0.0f, paint);

		//绘制弧形，时间占比
		paint.setColor(backgroundCollection.getTodayColor());
		paint.setAlpha(128);
		paint.setShadowLayer(1f, 0f, 1f, Color.DKGRAY);
		int rad = 250;
		RectF rectF = new RectF((int)(0.3f * width) - rad, (int)(0.52f * height) - rad,
				(int)(0.3f * width) + rad, (int)(0.52f * height) + rad);
		canvas.drawArc(rectF, 270.0f, 360.0f * taskTimeUsed / taskTotalTimeUsed, true, paint);
		paint.setColor(Color.WHITE);
		paint.setAlpha(64);
		int radL = 280;
		rectF.set((int)(0.3f * width) - radL, (int)(0.52f * height) - radL,
				(int)(0.3f * width) + radL, (int)(0.52f * height) + radL);
		canvas.drawArc(rectF, 0, 360, true, paint);

		//绘制评分
		paint.setTextSize(392);
		paint.setTextAlign(Paint.Align.CENTER);
		paint.setAlpha(255);
		canvas.drawText(Integer.toString(intGoal), width / 2, height / 4, paint);

		//绘制此次有效时长
		paint.setTextSize(192);
		canvas.drawText(secToHourMin(taskTimeUsed), (int)(0.3f * width), (int)(0.535 * height), paint);
		//绘制此次总时长，今日有效时长
		paint.setTextSize(144);
		canvas.drawText(secToHourMin(taskTotalTimeUsed), (int)(0.65f * width), (int)(0.47 * height), paint);
		canvas.drawText(secToHourMin(timeUsedToday), (int)(0.65f * width), (int)(0.58 * height), paint);

		String filePicStoredPath = dirPath + File.separator + fileName + ".jpg";
		File filePicStored = new File(filePicStoredPath);
		try{
			FileOutputStream fileOutputStream = new FileOutputStream(filePicStored);
			bitmapToShare.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
			fileOutputStream.flush();
			fileOutputStream.close();
		}catch (FileNotFoundException e){
			e.printStackTrace();
		}catch (IOException e){
			e.printStackTrace();
		}

		shareImgToQQ(filePicStoredPath);
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Tencent.onActivityResultData(requestCode, resultCode, data, myIUiListener);
		if (requestCode == Constants.REQUEST_API) {
			if (resultCode == Constants.REQUEST_QQ_SHARE || resultCode == Constants.REQUEST_QZONE_SHARE || resultCode == Constants.REQUEST_OLD_SHARE) {
				Tencent.handleResultData(data, myIUiListener);
			}
		}
	}

	private void shareImgToQQ(String imgUrl){
		params = new Bundle();
		params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);// 设置分享类型为纯图片分享
		params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, imgUrl);// 需要分享的本地图片URL
		params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);//默认分享到空间
		mTencent.shareToQQ(FinishActivity.this, params, myIUiListener);
	}

	private String secToHourMin(int sec){
		int min = (sec + 1) / 60;
		int hor = min / 60;
		min = min - hor * 60;
		return String.format(Locale.getDefault(), "%02d:%02d", hor, min);
	}

}
