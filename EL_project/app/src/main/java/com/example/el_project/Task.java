package com.example.el_project;

public class Task {
	private String name;
	private int imageId;
	private int backgroundId;

	public Task(String name,int imageId,int backgroundId){
		this.name=name;
		this.imageId=imageId;
		this.backgroundId=backgroundId;
	}

	public String getName(){
		return name;
	}

	public int getImageId(){
		return imageId;
	}

	public int getBackgroundId(){
		return backgroundId;
	}

}
