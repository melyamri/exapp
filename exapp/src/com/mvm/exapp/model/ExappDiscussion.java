package com.mvm.exapp.model;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;

public class ExappDiscussion implements Serializable{
	private int discussionID;
	private String userID;
	private String title;
	private String body;
	private Time time;
	private Date date;
	
	///RWTH discussions attributes and methods //////
	private int selfId;
	private int replyToId;
	//private String from;
	
//	public String getFrom() {
//		return from;
//	}
//	public void setFrom(String from) {
//		this.from = from;
//	}
	public ExappDiscussion(){
		
	}
	public int getSelfId() {
		return selfId;
	}
	public void setSelfId(int selfId) {
		this.selfId = selfId;
	}
	public int getReplyToId() {
		return replyToId;
	}
	public void setReplyToId(int replyToId) {
		this.replyToId = replyToId;
	}
	
	public ExappDiscussion(String userID, String title, String body, Time time, Date date){
		this.discussionID = -1;
		this.userID = userID;
		this.title = title;
		this.body = body;
		this.time = time;
		this.date = date;
	}
	
	public ExappDiscussion(int discussionID, String userID, String title, String body, Time time, Date date){
		this.discussionID = discussionID;
		this.userID = userID;
		this.title = title;
		this.body = body;
		this.time = time;
		this.date = date;
	}

	public ExappDiscussion(String title2, String body2, String from2,
			int modifiedTimeStamp, int selfId2, int replyToId2) {
		this.title = title2;
		this.body = body2;
		this.userID = from2;
		this.selfId = selfId2;
		this.replyToId = replyToId2;
	}

	public int getDiscussionID() {
		return discussionID;
	}

	public void setDiscussionID(int discussionID) {
		this.discussionID = discussionID;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public Time getTime() {
		return time;
	}

	public void setTime(Time time) {
		this.time = time;
	}
	
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
}
