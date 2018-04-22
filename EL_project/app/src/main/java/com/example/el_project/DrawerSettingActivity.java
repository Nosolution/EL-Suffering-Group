package com.example.el_project;

import android.app.Activity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.ViewDragHelper;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
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

		switch_clock_status=findViewById(R.id.switch_clock);
		switch_music_status=findViewById(R.id.switch_music);
		switch_clock_status.setOnCheckedChangeListener(this);
		switch_music_status.setOnCheckedChangeListener(this);


		if(actionBar!=null){
			actionBar.setDisplayHomeAsUpEnabled(true);  //显示导航按钮
//			actionBar.setHomeAsUpIndicator(R.drawable.category_white_31);  //设置导航按钮图标
		}
	}

//	绑定Menu布局
	public boolean onCreateOptionsMenu(Menu menu){
		getMenuInflater().inflate(R.menu.toolbar,menu);
		return true;
	}


	//对设置按钮点击事件进行处理
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
			case R.id.switch_music:
				if(compoundButton.isChecked()) Toast.makeText(this,"音乐已打开",Toast.LENGTH_SHORT).show();
				else Toast.makeText(this,"音乐已关闭",Toast.LENGTH_SHORT).show();
				break;
			case R.id.switch_clock:
				if(compoundButton.isChecked()) Toast.makeText(this,"番茄钟已打开",Toast.LENGTH_SHORT).show();
				else Toast.makeText(this,"番茄钟已关闭",Toast.LENGTH_SHORT).show();
				break;
		}
	}
}
