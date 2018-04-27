package com.example.el_project;

public class Task {
	private String name;
	private int backgroundId;

	public Task(String name,int backgroundId){
		this.name=name;
		this.backgroundId=backgroundId;
	}

	public String getName(){
		return name;
	}

	public int getBackgroundId(){
		return backgroundId;
	}

}
