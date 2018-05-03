package com.example.el_project;

import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.DrawableWrapper;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DrawerSettingActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener{
	private DrawerLayout mDrawerLayout;
	private Switch switch_clock_status;
	private Switch switch_music_status;
	private TextView text_clock_on;
	private TextView text_chosen_time;
	private TextView text_time;
	private Spinner spinner_choose_time;
	private List<Map<String,Object>> data_time;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_drawer_setting);
		Toolbar toolbar=findViewById(R.id.setting_toolbar);
		setSupportActionBar(toolbar);
		mDrawerLayout=findViewById(R.id.drawer_layout);
		ActionBar actionBar=getSupportActionBar();

		spinner_choose_time=findViewById(R.id.spinner_choose_time);
		text_clock_on=findViewById(R.id.text_clock_on);
		text_chosen_time=findViewById(R.id.text_chosen_time);
		text_time=findViewById(R.id.text_time);
		text_chosen_time.setText("20分钟");

		switch_clock_status=findViewById(R.id.switch_if_tomato_clock_on);
		switch_music_status=findViewById(R.id.switch_if_music_on);
		switch_clock_status.setChecked(GeneralSetting.getTomatoClockEnable(this));
		switch_music_status.setChecked(GeneralSetting.getMusicOn(this));
		switch_clock_status.setOnCheckedChangeListener(this);
		switch_music_status.setOnCheckedChangeListener(this);

		TextView tv=(TextView)findViewById(R.id.time_action);
		AnimationDrawable ad=(AnimationDrawable)tv.getBackground();
		ad.start();


		if(actionBar!=null){
			actionBar.setDisplayHomeAsUpEnabled(true);  //显示导航按钮
//			actionBar.setHomeAsUpIndicator(R.drawable.category_white_31);  //设置导航按钮图标
		}

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

//	绑定Menu布局
	public boolean onCreateOptionsMenu(Menu menu){
		getMenuInflater().inflate(R.menu.toolbar,menu);
		return true;
	}


	//对HomeAsUp按钮点击事件进行处理
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
//			case android.R.id.home:
//				mDrawerLayout.openDrawer(GravityCompat.END);  //展示滑动菜单，传入Gravity参数
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
				    GeneralSetting.setMusicOn(DrawerSettingActivity.this, true);
					Toast.makeText(this,"音乐已打开",Toast.LENGTH_SHORT).show();
				}
				else {
				    GeneralSetting.setMusicOn(DrawerSettingActivity.this, false);
				    Toast.makeText(this,"音乐已关闭",Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.switch_if_tomato_clock_on:
				if(compoundButton.isChecked()) {
				    GeneralSetting.setTomatoClockEnable(DrawerSettingActivity.this, true);
				    Toast.makeText(this,"番茄钟已打开",Toast.LENGTH_SHORT).show();
				    text_clock_on.setVisibility(View.VISIBLE);
				    text_chosen_time.setVisibility(View.VISIBLE);
				    spinner_choose_time.setVisibility(View.VISIBLE);
				}
				else {
				    GeneralSetting.setTomatoClockEnable(DrawerSettingActivity.this, false);
				    Toast.makeText(this,"番茄钟已关闭",Toast.LENGTH_SHORT).show();
					text_clock_on.setVisibility(View.GONE);
					text_chosen_time.setVisibility(View.GONE);
					spinner_choose_time.setVisibility(View.GONE);
				}
				break;
		}
	}
}
