package com.mvm.exapp.model.statistics;

import java.util.ArrayList;

import com.mvm.exapp.model.ExappAppointment;

public class StatisticsAppointment {
	private String groupID;	//name of the group
	private ExappAppointment appointment;	//appointment
	private ArrayList<String> listParticipants;		//list of users nicknames that assisted to the appointment
	
	public StatisticsAppointment(String groupID, ExappAppointment appointment) {
		super();
		this.groupID = groupID;
		this.appointment = appointment;
		this.listParticipants = new ArrayList<String>();
	}
	
	public String getGroupID() {
		return this.groupID;
	}

	public ExappAppointment getAppointment() {
		return this.appointment;
	}

	public ArrayList<String> getListParticipants() {
		return this.listParticipants;
	}
	
}
