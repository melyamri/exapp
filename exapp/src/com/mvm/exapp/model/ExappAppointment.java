package com.mvm.exapp.model;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;

public class ExappAppointment implements Serializable{
	private int id;
	private String title;
	private String place;
	private Date date;
	private Time time;
	private String description;
	private String group;
	
	private boolean hasCurrentUserAccepted;
	
	public boolean isHasCurrentUserAccepted() {
		return hasCurrentUserAccepted;
	}

	public void setHasCurrentUserAccepted(boolean hasCurrentUserAccepted) {
		this.hasCurrentUserAccepted = hasCurrentUserAccepted;
	}

	public ExappAppointment(String title, String place, Date date, Time time, String description, String group) {
		super();
		this.title = title;
		this.place = place;
		this.date = date;
		this.time = time;
		this.description = description;
		this.group = group;
	}
	
	// Im only using the constructor below, and those parameters bellow.
	public ExappAppointment(int id, String title, String place, Date date, Time time, String description, String group) {
		super();
		this.id = id;
		this.title = title;
		this.place = place;
		this.date = date;
		this.time = time;
		this.description = description;
		this.group = group;
	}

	public ExappAppointment() {
		// TODO Auto-generated constructor stub
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public Time getTime() {
		return this.time;
	}

	public void setTime(Time time) {
		this.time = time;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
	
}
