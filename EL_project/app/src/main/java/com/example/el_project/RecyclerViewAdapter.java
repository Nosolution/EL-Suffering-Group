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

import java.util.ArrayList;
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
		LinearLayout layoutBackground;
//		View taskView;

		//构造函数
		public ViewHolder(View view){
			super(view);
			taskName=view.findViewById(R.id.task_name);
//			taskImage=view.findViewById(R.id.task_image);
			layoutBackground=view.findViewById(R.id.layout_item);
//			taskView = view;
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
	public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
		Task task=mTaskList.get(position);  //得到当前项的实例
//		holder.taskImage.setImageResource(task.getImageId());
		holder.taskName.setText(task.getName());
		holder.layoutBackground.setBackgroundResource(task.getBackgroundId());

		if (onItemClickListener != null) {
			holder.itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					onItemClickListener.onItemClick(v, position);
				}
			});
			holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					onItemClickListener.onItemLongClick(v, position);
					return true;
				}
			});
		}
	}

	@Override
	public int getItemCount() {
		return mTaskList.size();
	}

	//动态添加Item
	public void addItem(Task task){
		mTaskList.add(mTaskList.size(),task);
		notifyItemInserted(mTaskList.size()-1);
	}

	public void modifyItem(int i,Task task){
		mTaskList.set(i,task);
		notifyItemChanged(i);
	}

	//修改Item的选择状态，改变背景
	public void changeItemBackGround(int position){
		notifyItemChanged(position);
		setItemSelected(position,!isItemSelected(position));
	}

	//更新adpter的数据和选择状态
	public void updateDataSet(ArrayList<Task> list) {
		this.mTaskList = list;
		mSelectedPositions = new SparseBooleanArray();
	}

	//获得选中条目的动态数组
	public ArrayList<Task> getSelectedItem() {
		ArrayList<Task> selectedList = new ArrayList<>();
		for (int i = 0; i < mTaskList.size(); i++) {
			if (isItemSelected(i)) {
				selectedList.add(mTaskList.get(i));
			}
		}
		return selectedList;
	}

	public void initSelectPositions(){

	}

	//设置给定位置条目的选择状态
	public void setItemSelected(int position,boolean ischecked){
		mSelectedPositions.put(position,ischecked);
	}

	//根据位置判断条目是否选中
	public boolean isItemSelected(int position){
		return mSelectedPositions.get(position);
	}

	//根据位置判断条目是否可选
	public boolean isSelectable() {
		return mIsSelectable;
	}

	//设置给定位置条目的可选与否的状态
	public void setSelectable(boolean selectable) {
		mIsSelectable = selectable;
	}

	//清除选择状态
	public void cleanSelected(){
		for(int i=0;i<mTaskList.size();i++){
			if (isItemSelected(i)){
				Task task=mTaskList.get(i);
				task.switchBackground();
				notifyItemChanged(i);
			}
		}
		mSelectedPositions.clear();
	}


	//自定义Item点击接口
	public interface OnItemClickListener {
		void onItemClick(View view, int position);

		void onItemLongClick(View view, int position);
	}

	private OnItemClickListener onItemClickListener;

	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}
}

