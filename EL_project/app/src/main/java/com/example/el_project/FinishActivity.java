package com.example.el_project;

import android.animation.Animator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 我不确定完成界面的专注度的得分下面要显示什么，就把它们都放上去了
 */

public class FinishActivity extends AppCompatActivity {
	private TextView tvGoal;  // 用于显示专注度的分数
	private TextView tvSingleTaskConsumeTime;  // 单个任务的耗时
	private TextView tvSingleTaskConcentrateTime;  // 单个任务的专注时间
	private TextView tvWeekConcentrateTime;  // 一周的专注时间
	private Button buttonReturnMain;  // 返回主界面按钮
	private Button buttonNextTask;  // 开始下一项任务的按钮


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

		buttonReturnMain.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(),"点击返回主界面按钮",Toast.LENGTH_SHORT).show();
			}
		});

	}
}
