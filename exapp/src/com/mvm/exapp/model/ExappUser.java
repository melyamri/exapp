package com.mvm.exapp.model;

import java.io.Serializable;
import java.sql.Date;

public class ExappUser implements Serializable{
	private String userID;
	private String password;
	private String name;
	private Date birthday;
	private int imageID;
	private boolean isRWTHstudent;
	private String description;
	
	public ExappUser(String user, String password, String name, Date birthday,
			int imageID, boolean isRWTHstudent, String description) {
		super();
		this.userID = user;
		this.password = password;
		this.name = name;
		this.birthday = birthday;
		this.imageID = imageID;
		this.isRWTHstudent = isRWTHstudent;
		this.description = description;
	}

	public String getUserID() {
		return userID;
	}

	public void setUser(String user) {
		this.userID = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public int getImageID() {
		return imageID;
	}

	public void setImageID(int imageID) {
		this.imageID = imageID;
	}
	
	public boolean getIsRWTHstudent(){
		return isRWTHstudent;
	}
	
	public void setisRWTHstudent(boolean isRWTHstudent){
		this.isRWTHstudent = isRWTHstudent;
	}
	
	public String getDescription(){
		return this.description;
	}
	
	public void setDescription(String description){
		this.description = description;
	}
}
