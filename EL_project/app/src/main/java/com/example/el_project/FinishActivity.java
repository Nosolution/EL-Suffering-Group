package com.example.el_project;

import android.animation.Animator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_finish);

		tvGoal = findViewById(R.id.tv_goal);
		tvSingleTaskConsumeTime = findViewById(R.id.tv_single_task_consume_time);
		tvSingleTaskConcentrateTime = findViewById(R.id.tv_single_task_concentrate_time);
		tvWeekConcentrateTime = findViewById(R.id.tv_week_concentrate_time);
		buttonReturnMain = findViewById(R.id.button_return_main);
		buttonNextTask = findViewById(R.id.button_next_task);

		// 设置TextView的显示格式
		strGoal = intGoal + "分";
		strSingleTaskConsumeTime = "本次任务共耗时 " + intSingleTaskConsumeTime + " 分钟";
		strSingleTaskConcentrateTime = "专注 " + intSingleTaskConcentrateTime + " 分钟";
		strWeekConcentrateTime = "本周已专注 " +intWeekConcentrateTime + " 分钟";


		// 设置各个TextView显示的值
		SpannableStringBuilder ssbGoal = new SpannableStringBuilder(strGoal);
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



		buttonReturnMain.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(FinishActivity.this, MainActivity.class);
				startActivity(intent);
			}
		});

	}
}
