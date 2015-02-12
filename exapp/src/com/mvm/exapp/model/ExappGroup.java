package com.mvm.exapp.model;

import java.io.Serializable;
import java.util.ArrayList;

public class ExappGroup implements Serializable{
	private String founder;
	private String groupID;
	private String subject;
	private String description;
	private ArrayList<String> members;
	private int type; // 0 --> Normal Exapp Group, 1--> RWTH Group

	public ExappGroup(String groupID, String founder, 
			String subject, String description) {
		this.founder=founder;
		this.groupID = groupID;
		this.subject = subject;
		this.description = description;
		this.members = new ArrayList<String>();
		this.type = 0;

	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setMembers(ArrayList<String> members) {
		this.members = members;
	}

	public ExappGroup() {
		// TODO Auto-generated constructor stub


	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFounder() {
		return founder;
	}

	public String getGroupID() {
		return groupID;
	}

	public ArrayList<String> getMembers() {
		return members;
	}

	public void setGroupID(String name) {
		this.groupID = name;
		
	}

	public void setFounder(String currentUser) {
		this.founder = currentUser;
		
	}

	
}
