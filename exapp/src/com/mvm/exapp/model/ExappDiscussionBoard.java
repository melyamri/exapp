package com.mvm.exapp.model;

import java.util.ArrayList;

public class ExappDiscussionBoard {
	private String groupID;
	private ArrayList<ExappDiscussion> list;
	
	public ExappDiscussionBoard(String groupID) {
		super();
		this.groupID = groupID;
		this.list = new ArrayList<ExappDiscussion>();
	}

	public String getGroupID() {
		return groupID;
	}

	public void setGroupID(String groupID) {
		this.groupID = groupID;
	}

	public ArrayList<ExappDiscussion> getList() {
		return list;
	}
}
