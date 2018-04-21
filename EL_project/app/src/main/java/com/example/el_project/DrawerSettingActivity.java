package com.example.el_project;

import android.graphics.drawable.DrawableWrapper;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

public class DrawerSettingActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener{
	private DrawerLayout mDrawerLayout;
	private Switch switch_clock_status;
	private Switch switch_music_status;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_drawer_setting);
		Toolbar toolbar=findViewById(R.id.setting_toolbar);
		setSupportActionBar(toolbar);
		mDrawerLayout=findViewById(R.id.drawer_layout);
		ActionBar actionBar=getSupportActionBar();

		switch_clock_status=findViewById(R.id.switch_if_tomato_clock_on);
		switch_music_status=findViewById(R.id.switch_if_music_on);
		switch_clock_status.setChecked(GeneralSetting.getTomatoClockEnable(this));
		switch_music_status.setChecked(GeneralSetting.getMusicOn(this));
		switch_clock_status.setOnCheckedChangeListener(this);
		switch_music_status.setOnCheckedChangeListener(this);


		if(actionBar!=null){
			actionBar.setDisplayHomeAsUpEnabled(true);  //显示导航按钮
			actionBar.setHomeAsUpIndicator(R.drawable.category_white_31);  //设置导航按钮图标
		}
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
				}
				else {
				    GeneralSetting.setTomatoClockEnable(DrawerSettingActivity.this, false);
				    Toast.makeText(this,"番茄钟已关闭",Toast.LENGTH_SHORT).show();
				}
				break;
		}
	}
}
