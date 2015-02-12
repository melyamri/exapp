package com.mvm.exapp.activities;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import com.mvm.exapp.R;
import com.mvm.exapp.externalDB.DBManager;
import com.mvm.exapp.externalDB.Statistics;
import com.mvm.exapp.model.ExappDiscussionBoard;
import com.mvm.exapp.model.ExappGroup;
import com.mvm.exapp.model.ExappUser;
import com.mvm.exapp.model.statistics.StatisticsAppointment;
import com.mvm.exapp.model.statistics.StatisticsSubject;
import com.mvm.exapp.model.statistics.StatisticsUser;
import com.mvm.exapp.useful.ExpandListAdapterForSubjects;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

public class StatisticsGlobalActivity extends Activity{

	private TextView tv_numUsers;
	private TextView tv_numGroups;
	private TextView tv_numDiscussion;
	private ExpandableListView expandablelvGlobal_subjects;
	private ListView lv_famousGroups;
	private ListView lv_activeUsers;
	private ListView lv_popularMeetings;	
	
	private ArrayList<String> listUsersID;
	private ArrayList<ExappGroup> listGroups;
	private ArrayList<ExappDiscussionBoard> listDiscussions;
	
	private ArrayList<StatisticsSubject> listSubjects;
	
	private ArrayList<ExappGroup> famousGroups;
	private ArrayList<StatisticsUser> famousUsers;
	private ArrayList<StatisticsAppointment> famousMeetings;
	
	private ExappUser user;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.statistics_global);

		user = (ExappUser) getIntent().getSerializableExtra("user");
		
		this.tv_numUsers = (TextView) findViewById(R.id.tv_numUsers);
		this.tv_numGroups = (TextView) findViewById(R.id.tv_numGroups);
		this.tv_numDiscussion = (TextView) findViewById(R.id.tv_numDiscussion);
		this.expandablelvGlobal_subjects = (ExpandableListView) findViewById(R.id.expandablelv_all_subjects);
		this.lv_famousGroups = (ListView) findViewById(R.id.lv_famousGroups);
		this.lv_activeUsers = (ListView) findViewById(R.id.lv_activeUsers);
		this.lv_popularMeetings = (ListView) findViewById(R.id.lv_popularMeetings);
		
		this.loadData();
		final ScrollView sc = (ScrollView) findViewById(R.id.scrollView_global);
		
		sc.setOnTouchListener(new View.OnTouchListener() {

		   

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				 lv_famousGroups.getParent().requestDisallowInterceptTouchEvent(false);
				return false;
			}
		    });
		
		lv_famousGroups.setOnTouchListener(new View.OnTouchListener() {
			@Override
		    public boolean onTouch(View v, MotionEvent event) {
		        // TODO Auto-generated method stub
		         //Log.v(TAG,"PARENT TOUCH");
		    	lv_famousGroups.getParent().requestDisallowInterceptTouchEvent(true);
		        return false;
		    }
		});
		
	}
	
	private void loadData(){
		try {
			//Number of users.
			this.listUsersID = new ArrayList<String>();
			this.listUsersID = (ArrayList<String>) DBManager.getInstance(this).execute(new Object[]{"getAllUsers"}).get();
			
			
			this.tv_numUsers.setText(this.listUsersID.size() +" users.");
			
			
			//Number of groups.
			this.listGroups = new ArrayList<ExappGroup>();
			this.listGroups = (ArrayList<ExappGroup>) DBManager.getInstance(this).execute(new Object[]{"getAllGroupsInDB"}).get();
			this.tv_numGroups.setText(this.listGroups.size() +" groups.");
			
			//Number of discussions.
			this.listDiscussions = new ArrayList<ExappDiscussionBoard>();
			this.listDiscussions = ((ArrayList<ExappDiscussionBoard>) Statistics.getInstance(this).execute(new Object[]{"getAllDiscussions"}).get());
			this.tv_numDiscussion.setText(this.listDiscussions.size() +" posts.");
			
			// Popular subjects
			Display newDisplay = getWindowManager().getDefaultDisplay(); 
			int width = newDisplay.getWidth();
			this.expandablelvGlobal_subjects.setIndicatorBounds(width-50, width);
			setSubjects();
			
			ExpandListAdapterForSubjects ad = new ExpandListAdapterForSubjects(this, this.listSubjects);
			this.expandablelvGlobal_subjects.setAdapter(ad);
			this.expandablelvGlobal_subjects.getLayoutParams().height = 100 * this.listSubjects.size();
//			expandablelvGlobal_subjects.setOnTouchListener(new OnTouchListener() {
//
//			    public boolean onTouch(View v, MotionEvent event) {
//			        return (event.getAction() == MotionEvent.ACTION_MOVE);
//			    }
//			});
//			
			//Popular groups
			this.famousGroups = ((ArrayList<ExappGroup>) Statistics.getInstance(this).execute(new Object[]{"groupsWithMoreMembers"}).get());
			ArrayList<String> famousGroupsID = new ArrayList<String>();
			Log.i("famousGroups", this.famousGroups.size() + "");
			for (ExappGroup g:this.famousGroups)
				famousGroupsID.add(g.getGroupID());
			this.lv_famousGroups.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, famousGroupsID));
//			this.lv_famousGroups.getLayoutParams().height = 100 * this.famousGroups.size();
//			lv_famousGroups.setOnTouchListener(new OnTouchListener() {
//
//			    public boolean onTouch(View v, MotionEvent event) {
//			        return (event.getAction() == MotionEvent.ACTION_MOVE);
//			    }
//			});
			//Popular users
			this.famousUsers = ((ArrayList<StatisticsUser>) Statistics.getInstance(this).execute(new Object[]{"peopleWhoAttendedMoreAppointments"}).get());
			ArrayList<String> famousUsersID = new ArrayList<String>();
			for (StatisticsUser u:this.famousUsers)
				famousUsersID.add(u.getUserID()+" has assisted "+u.getNumAppointmentsAssited()+" meetings.");
			this.lv_activeUsers.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, famousUsersID));
//			this.lv_activeUsers.getLayoutParams().height = 100 * this.famousUsers.size();
//			lv_activeUsers.setOnTouchListener(new OnTouchListener() {
//
//			    public boolean onTouch(View v, MotionEvent event) {
//			        return (event.getAction() == MotionEvent.ACTION_MOVE);
//			    }
//			});
			//Popular meetings
			this.famousMeetings = ((ArrayList<StatisticsAppointment>) Statistics.getInstance(this).execute(new Object[]{"appointmentsWithMoreParticipants"}).get());
			ArrayList<String> famousMeetingsID = new ArrayList<String>();
			while (this.famousMeetings == null){}
			for (StatisticsAppointment a:this.famousMeetings)
				famousMeetingsID.add("Appointment: "+a.getAppointment().getTitle()+" for group "+a.getGroupID()+" with "+a.getListParticipants().size()+" participants.");
			this.lv_popularMeetings.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, famousMeetingsID));
			this.lv_popularMeetings.getLayoutParams().height = 200 * this.famousMeetings.size();
//			lv_popularMeetings.setOnTouchListener(new OnTouchListener() {
//
//			    public boolean onTouch(View v, MotionEvent event) {
//			        return (event.getAction() == MotionEvent.ACTION_MOVE);
//			    }
//			});
		} catch (InterruptedException | ExecutionException e) {
			
			e.printStackTrace();
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.statistics_menu, menu);
		return true;
	}
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i = new Intent(this, StatisticsActivity.class);
		i.putExtra("user", this.user);
		startActivity(i);
		return true;
		
	}
	public ArrayList<StatisticsSubject> setSubjects() {
    	
		 
		ArrayList<StatisticsSubject> list = new ArrayList<StatisticsSubject>();
		try {
			this.listSubjects = (ArrayList<StatisticsSubject>) Statistics.getInstance(getApplicationContext()).execute(new Object[]{"preferedSubjects"}).get();
		} catch (InterruptedException | ExecutionException e) {
			Log.e("error", e.getMessage());
			e.printStackTrace();
		}
		 	
	        return listSubjects;
	 }

}
