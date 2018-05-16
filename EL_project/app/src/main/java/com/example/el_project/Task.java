package com.example.el_project;

public class Task {
	private String name;
	private String remark;
	private int backgroundId;
	private int selectedBackgroundId;
	private int defaultBackground;

	public Task(String name,String remark,int backgroundId,int checkedbackgroundId){
		this.name=name;
		this.remark=remark;
		this.backgroundId=backgroundId;
		this.selectedBackgroundId =checkedbackgroundId;
		this.defaultBackground=backgroundId;
	}

	public String getName(){
		return name;
	}

	public String getRemark(){return remark;}

	public void setName(String name){this.name=name;}

//	public int getImageId(){
//		return imageId;
//	}

	public int getBackgroundId(){
		return backgroundId;
	}

	public int getSelectedBackgroundId(){return selectedBackgroundId;}

	public void switchBackground(){
		int temp=backgroundId;
		backgroundId= selectedBackgroundId;
		selectedBackgroundId =temp;
	}

	public void initBackground(){
		this.backgroundId=defaultBackground;
	}
}
