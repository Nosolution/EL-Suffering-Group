package com.example.el_project;

public class Task implements Comparable<Task>{
	private String name;
	private String remark;
	private int backgroundId;
	private int selectedBackgroundId;
	private int defaultBackground;
	private int id;
	//以下是为了实现排序，与Task功能无关
	private Integer eDgree;
	private Integer dateDiffWeight;
	private Integer weight;

	public Task(String name,String remark,int backgroundId,int checkedbackgroundId){
		this.name=name;
		this.remark=remark;
		this.backgroundId=backgroundId;
		this.selectedBackgroundId =checkedbackgroundId;
		this.defaultBackground=backgroundId;
	}

	public Task(int id,Integer eDgree,int dateDiff){
		this.id=id;
		this.eDgree=eDgree;
		this.dateDiffWeight=dateDiff<5? 5-dateDiff:0;
		this.weight=3*this.eDgree+4*this.dateDiffWeight;//权重模型
	}

	public Task(int id){
		this.id=id;
		this.weight=18;
	}


	public int compareTo(Task arg0){
		return arg0.getWeight().compareTo(this.getWeight());
	}

	public String getName(){
		return name;
	}

	public String getRemark(){return remark;}

	public void setName(String name){this.name=name;}

	public int getId(){return id;}

	public Integer getWeight(){
		return this.weight;
	}

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
