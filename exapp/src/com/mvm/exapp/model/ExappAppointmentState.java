package com.mvm.exapp.model;

public class ExappAppointmentState {
	private String user;
	private boolean state;
	
	public ExappAppointmentState(String user, boolean state) {
		super();
		this.user = user;
		this.state = state;
	}

	public String getUser() {
		return user;
	}

	public boolean getState() {
		return state;
	}
	
	
}
