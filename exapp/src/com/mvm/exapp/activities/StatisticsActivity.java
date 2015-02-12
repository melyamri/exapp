package com.mvm.exapp.activities;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import com.mvm.exapp.R;
import com.mvm.exapp.externalDB.DBManager;
import com.mvm.exapp.externalDB.Statistics;
import com.mvm.exapp.model.ExappAppointment;
import com.mvm.exapp.model.ExappDiscussion;
import com.mvm.exapp.model.ExappDiscussionBoard;
import com.mvm.exapp.model.ExappGroup;
import com.mvm.exapp.model.ExappUser;
import com.mvm.exapp.model.statistics.StatisticsSubject;
import com.mvm.exapp.useful.ExpandListAdapterForSubjects;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class StatisticsActivity extends Activity{

	private ListView lv_chooseGroup;
	private TextView tv_percentageAppointmentsAssisted;
	private ProgressBar progressBar1;
	private TextView tv_totalMeetingsPerGroup;
	private TextView tv_st_discussionsPerGroup;
	private TextView tv_st_discussionsPerUserPerGroup;
	private TextView tv_discussionsOfTheUser1;
	private TextView tv_st_groups;
	private TextView tv_st_groups_founder;
	private ExpandableListView expandablelv_subjects;
	
	private ArrayList<StatisticsSubject> listSubjects;
	private ArrayList<ExappGroup> listGroups;
	private ArrayList<String> listGroupNames;
	
	private ExappUser user;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.statistics);

		user = (ExappUser) getIntent().getSerializableExtra("user");
		
		this.lv_chooseGroup = (ListView) findViewById(R.id.lv_chooseGroup);	//done
		this.progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);
		this.tv_percentageAppointmentsAssisted = (TextView) findViewById(R.id.tv_percentageAppointmentsAssisted);
		this.tv_totalMeetingsPerGroup = (TextView) findViewById(R.id.tv_group_total_meetings);	
		this.tv_st_discussionsPerGroup = (TextView) findViewById(R.id.tv_st_discussionsPerGroup);	
		this.tv_st_discussionsPerUserPerGroup = (TextView) findViewById(R.id.tv_st_discussionsPerUserPerGroup);	
		this.tv_discussionsOfTheUser1 = (TextView) findViewById(R.id.tv_discussionsOfTheUser1);	//done
		this.tv_st_groups = (TextView) findViewById(R.id.tv_st_groups);	//done
		this.tv_st_groups_founder = (TextView) findViewById(R.id.tv_st_groups_founder);	//done
		this.expandablelv_subjects = (ExpandableListView) findViewById(R.id.expandablelv_subjects);	//done
//
		
		this.loadData();
	}
	
	private void loadData(){
		/// On Click listener to select group ///
		this.lv_chooseGroup.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				lv_chooseGroup.setItemChecked(position, true);
				
				try {
					
					int attendedAppointments = (int)((ArrayList<ExappAppointment>) Statistics.getInstance(getActivity()).execute(new Object[]{"getMeetingsByGroupByUser", user.getUserID(), lv_chooseGroup.getItemAtPosition(position)}).get()).size();
					int totalMeetingsByGroup = (int) ((ArrayList<ExappAppointment>) Statistics.getInstance(getActivity()).execute(new Object[]{"getMeetingsByGroup", lv_chooseGroup.getItemAtPosition(position)}).get()).size();
					tv_totalMeetingsPerGroup.setText(totalMeetingsByGroup + "");
					int percentage = 0;
					if(totalMeetingsByGroup !=0) {
						 percentage = attendedAppointments / totalMeetingsByGroup * 100;
					}
					tv_percentageAppointmentsAssisted.setText(percentage + " %");
					progressBar1.setProgress(percentage);
					
					int discussions = (int)((ExappDiscussionBoard)Statistics.getInstance(getActivity()).execute(new Object[]{"getDiscussionsByGroup", lv_chooseGroup.getItemAtPosition(position)}).get()).getList().size();
					tv_st_discussionsPerGroup.setText(discussions + "");
					
					int discPerUser = (int)((ExappDiscussionBoard)Statistics.getInstance(getActivity()).execute(new Object[]{"getDiscussionsByUserPerGroup", user.getUserID(), lv_chooseGroup.getItemAtPosition(position)}).get()).getList().size();
					tv_st_discussionsPerUserPerGroup.setText(discPerUser + "");
					
				} catch (InterruptedException | ExecutionException e) {
					
					e.printStackTrace();
				}
				
			}
			
		});
		
		/// Other loaders ///
		//Number of groups that the user is in.
		this.listGroups = new ArrayList<ExappGroup>();
		try {
			this.listGroups = (ArrayList<ExappGroup>) DBManager.getInstance(getApplicationContext()).execute(new Object[]{"getAllGroups", this.user.getUserID()}).get();
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.listGroupNames = new ArrayList<String>();
		for(ExappGroup g: this.listGroups)
			this.listGroupNames.add(g.getGroupID());
		this.lv_chooseGroup.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, this.listGroupNames));
		
		listSubjects = setSubjects();
		ExpandListAdapterForSubjects adapter = new ExpandListAdapterForSubjects(this, this.listSubjects);
		this.expandablelv_subjects.setAdapter(adapter);
		
		
		int numPostsOfTheUser;
		try {
			numPostsOfTheUser = (int) ((ArrayList<ExappDiscussion>) Statistics.getInstance(this).execute(new Object[]{"getAllDiscussions", this.user.getUserID()}).get()).size();
			this.tv_discussionsOfTheUser1.setText(numPostsOfTheUser +"");
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		//int groups = ((ArrayList<ExappGroup>) DBManager.getInstance().execute(new Object[]{"getAllGroups", this.user.getUserID()}).get()).size();
		int groups = this.listGroups.size();

		this.tv_st_groups_founder.setText(groups +"");
		
		int groupsIFounded;
		try {
			groupsIFounded = (int) Statistics.getInstance(this).execute(new Object[]{"groupsIFounded", this.user.getUserID()}).get();
			this.tv_st_groups_founder.setText(groupsIFounded+"");
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public ArrayList<StatisticsSubject> setSubjects() {
    	
		 
		ArrayList<StatisticsSubject> list = new ArrayList<StatisticsSubject>();
		try {
			list = (ArrayList<StatisticsSubject>) Statistics.getInstance(getApplicationContext()).execute(new Object[]{"preferedSubjectsByUser", this.user.getUserID()}).get();
		} catch (InterruptedException | ExecutionException e) {
			System.out.println("List of subjects: " + list.size());
			e.printStackTrace();
		}
		 	
	        return list;
	 }
	
	public Activity getActivity(){
		return this;
	}

}
