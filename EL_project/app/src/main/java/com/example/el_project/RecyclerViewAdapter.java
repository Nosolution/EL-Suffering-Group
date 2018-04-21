package com.example.el_project;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

//Adapter作用是将数据与每一个item的界面进行绑定

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
	private List<Task> mTaskList;
	private SparseBooleanArray mSelectedPositions = new SparseBooleanArray();//储存被选中的Item的位置
	private boolean mIsSelectable = false; //是否可被编辑

	//提供一个合适的construction（由dataset类型决定）//构造方法上移，看起来更美观
	public RecyclerViewAdapter(List<Task> taskList){
		mTaskList=taskList;  //要展示的数据源
	}

	//自定义的ViewHolder，持有每个Item的的所有界面元素
	public static class ViewHolder extends RecyclerView.ViewHolder{
		TextView taskName;
//		ImageView taskImage;
		LinearLayout layoutBackground;

		//构造函数
		public ViewHolder(View view){
			super(view);
			taskName=view.findViewById(R.id.task_name);
//			taskImage=view.findViewById(R.id.task_image);
			layoutBackground=view.findViewById(R.id.layout_item);
		}
	}



	//重写三个方法
	//创建ViewHolder实例，新的view被LayoutManager调用
	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item,parent,false);
		ViewHolder holder=new ViewHolder(view);  //把加载出来的布局view传入构造函数ViewHolder
		return holder;
	}

	//将数据与界面进行绑定，对RecyclerView子项赋值（滚动到屏幕内的部分）
	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
		Task task=mTaskList.get(position);  //得到当前项的实例
//		holder.taskImage.setImageResource(task.getImageId());
		holder.taskName.setText(task.getName());
		holder.layoutBackground.setBackgroundResource(task.getBackgroundId());
	}

	@Override
	public int getItemCount() {
		return mTaskList.size();
	}
}

