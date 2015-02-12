package com.mvm.exapp.model;

import java.io.Serializable;

public class ExappAnnouncement implements Serializable{
private String title;
private String body;
private String attachment;
private int id;
private int expireTime;
//Type of announcement: general, discussion, appointment, group_invite
private String type; 

	public ExappAnnouncement(String title, String body, String attachment, int id, int exp, String type){
		
		this.title = title;
		this.body = body;
		this.attachment = attachment;
		this.id = id;
		this.expireTime = exp;
		this.type = type;
		
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

	public String getAttachment() {
		return attachment;
	}

	public void setAttachment(String attachment) {
		this.attachment = attachment;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(int expireTime) {
		this.expireTime = expireTime;
	}

	public String getType() {
		// TODO Auto-generated method stub
		return this.type;
	}

}
