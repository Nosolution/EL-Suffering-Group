package com.example.el_project;

public class Task {
	private String name;
//	private int imageId;
	private int backgroundId;
	private int checkedBackgroundId;
	private int defaultBackground;

	public Task(String name,int backgroundId,int checkedbackgroundId){
		this.name=name;
//		this.imageId=imageId;
		this.backgroundId=backgroundId;
		this.checkedBackgroundId=checkedbackgroundId;
		this.defaultBackground=backgroundId;
	}

	public String getName(){
		return name;
	}

//	public int getImageId(){
//		return imageId;
//	}

	public int getBackgroundId(){
		return backgroundId;
	}

	public int getCheckedBackgroundId(){return checkedBackgroundId;}

	public void switchBackground(){
		int temp=backgroundId;
		backgroundId=checkedBackgroundId;
		checkedBackgroundId=temp;
	}

	public void initBackground(){
		this.backgroundId=defaultBackground;
	}
}
