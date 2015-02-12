package com.mvm.exapp.model.statistics;

import java.util.ArrayList;

public class StatisticsSubject {
	private String subject;
	private ArrayList<String> listGroups;
	private int numMembers;
	
	public StatisticsSubject(String subject, int students) {
		super();
		this.subject = subject;
		this.listGroups = new ArrayList<String>();
		this.numMembers = students;
	}
	public String getSubject() {
		return subject;
	}
	public ArrayList<String> getListGroups() {
		return listGroups;
	}
	public int getNumMembers() {
		return numMembers;
	}
	public void setNumMembers(int numMembers) {
		this.numMembers = numMembers;
	}
	
	@Override
	public boolean equals(Object obj){
		if (obj instanceof StatisticsSubject){
			StatisticsSubject newObject = (StatisticsSubject)obj;
			if(newObject.getSubject().equals(this.getSubject()))
				return true;
		}
		return false;
	}
	
	@Override
    public int hashCode(){
        return 1;
    }
}
