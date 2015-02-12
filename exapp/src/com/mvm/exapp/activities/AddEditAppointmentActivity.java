package com.mvm.exapp.activities;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import com.mvm.exapp.R;
import com.mvm.exapp.externalDB.DBManager;
import com.mvm.exapp.fragments.GroupListFragment;
import com.mvm.exapp.model.ExappAppointment;
import com.mvm.exapp.model.ExappGroup;
import com.mvm.exapp.model.ExappUser;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TimePicker;
import android.widget.Toast;

@SuppressLint("ShowToast")
public class AddEditAppointmentActivity extends Activity{
private Button save;
private Button discard;

private EditText title;
private EditText place;
private EditText description;
private DatePicker date;
private TimePicker time;
private AutoCompleteTextView group;

private ExappAppointment appointment;
private ExappUser currentUser;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_appointment);
		
		this.save = (Button) findViewById(R.id.btn_save_app);
		this.discard = (Button) findViewById(R.id.btn_discard_app);
		this.handleButtons();
		
		this.title = (EditText) findViewById(R.id.et_appointment_title);
		this.place = (EditText) findViewById(R.id.et_app_place);
		this.description = (EditText) findViewById(R.id.et_app_description);
		this.date = (DatePicker) findViewById(R.id.datePicker1);
		this.time = (TimePicker) findViewById(R.id.timePicker1);
		this.group = (AutoCompleteTextView) findViewById(R.id.et_app_group);
		
		this.currentUser = (ExappUser) getIntent().getSerializableExtra("user");
		
		this.handleListView();
		if(getIntent().getSerializableExtra("item") == null) {
			this.appointment = new ExappAppointment();
			
		}else{
			
			this.appointment = (ExappAppointment) getIntent().getSerializableExtra("item");
			this.loadAppointmentData();
			
			
		}
	}
	private void loadAppointmentData() {
		this.title.setText(this.appointment.getTitle());
		this.place.setText(this.appointment.getPlace());
		this.description.setText(this.appointment.getDescription());
		java.util.Date d = new java.util.Date(this.appointment.getDate().getTime());
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		this.date.init(c.get(Calendar.YEAR), 
				c.get(Calendar.MONTH), 
				c.get(Calendar.DAY_OF_MONTH), null);
		this.time.setCurrentHour(this.appointment.getTime().getHours());
		this.time.setCurrentMinute(this.appointment.getTime().getMinutes());
		this.group.setText(this.appointment.getGroup());
		this.group.setEnabled(false);
		
	}
	
	public void addAppointment(){
		if(title.getText().toString() != null){
			appointment.setTitle(title.getText().toString());
		}else{
			Toast.makeText(getApplicationContext(), "Please fill in a title", Toast.LENGTH_SHORT);
		}

		if(place.getText().toString() != null){
			appointment.setPlace(place.getText().toString());
		}else{
			Toast.makeText(getApplicationContext(), "Please fill in a place", Toast.LENGTH_SHORT);
		}

		if(description.getText().toString() != null){
			appointment.setDescription(description.getText().toString());
		}else{
			Toast.makeText(getApplicationContext(), "Please fill in a description", Toast.LENGTH_SHORT);
		}

		Date d = new Date(date.getYear() - 1900, date.getMonth(), date.getDayOfMonth());

		Calendar cal = Calendar.getInstance(); 
		Date now = cal.getTime();
		
		
		cal.setTime(d);
		Calendar calTime = Calendar.getInstance();
		calTime.setTime(new Time(time.getCurrentHour(), time.getCurrentMinute(), 0));
		cal.set(Calendar.HOUR_OF_DAY, calTime.get(Calendar.HOUR_OF_DAY));
		cal.set(Calendar.MINUTE, calTime.get(Calendar.MINUTE));
		
		if( now.before(cal.getTime()) )	{
			appointment.setDate(new java.sql.Date(d.getTime()));
		}else{
			Toast.makeText(getApplicationContext(), "Please set a date in the future", Toast.LENGTH_SHORT);
		}

		appointment.setTime(new Time(time.getCurrentHour(), time.getCurrentMinute(), 0));
		
		if(group.getText().toString() != null){
			appointment.setGroup(group.getText().toString());
		}else{
			Toast.makeText(getApplicationContext(), "Please fill in a description", Toast.LENGTH_SHORT);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void handleListView(){
		
		ArrayList<ExappGroup> groups = null;
		try {
			groups = (ArrayList<ExappGroup>) DBManager.getInstance(getActivity()).execute(new String[]{"getAllGroups", this.currentUser.getUserID() }).get();
		} catch (InterruptedException | ExecutionException e) {
			
			e.printStackTrace();
		}
		
		
		ArrayList<String> values = new ArrayList<String>();
		for (int i = 0; i < groups.size(); i++){
			values.add(groups.get(i).getGroupID());
		}
		
		ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_dropdown_item_1line, values);
				   group.setAdapter(adapter);
				  // actv.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
				   group.setThreshold(1);

	}
	
	private void handleButtons() {
		View.OnClickListener handle_save = new View.OnClickListener() {
			
			public void onClick(View v) {
				addAppointment();
				boolean success = false;
				
				try {
					 success = (boolean) DBManager.getInstance(getActivity()).execute(new Object[]{"setAppointment", appointment}).get();
					
				} catch (InterruptedException | ExecutionException e) {
					
					e.printStackTrace();
				}
				
				if(!success){
					Toast.makeText(getApplicationContext(), "Error, check your data", Toast.LENGTH_LONG).show();
				}else{
					Toast.makeText(getApplicationContext(), "Appointment created successfully", Toast.LENGTH_LONG).show();
					Intent resultIntent = new Intent();
					resultIntent.putExtra("appointment", appointment);
					setResult(Activity.RESULT_OK, resultIntent);
					finish();
					
				}
				
			}
			};

		this.save.setOnClickListener(handle_save);
		  View.OnClickListener handle_discard = new View.OnClickListener() {
		   		 
			    public void onClick(View v) {
			    	finish();			    	
			    	}    
			  };
			 
			  this.discard.setOnClickListener(handle_discard);
			
	}
		public Activity getActivity(){
			return this;
		}

}
