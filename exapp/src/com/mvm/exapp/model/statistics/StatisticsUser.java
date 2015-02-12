package com.mvm.exapp.model.statistics;

public class StatisticsUser {
	private String userID;
	private int numAppointmentsAssited;
	
	public StatisticsUser(String userID, int numAppointmentsAssited) {
		super();
		this.userID = userID;
		this.numAppointmentsAssited = numAppointmentsAssited;
	}
	
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public int getNumAppointmentsAssited() {
		return numAppointmentsAssited;
	}
	public void setNumAppointmentsAssited(int numAppointmentsAssited) {
		this.numAppointmentsAssited = numAppointmentsAssited;
	}
		
}
