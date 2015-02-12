package com.mvm.exapp.fragments;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import com.mvm.exapp.R;
import com.mvm.exapp.R.layout;
import com.mvm.exapp.activities.MainActivity;
import com.mvm.exapp.externalDB.DBManager;
import com.mvm.exapp.model.ExappAppointment;
import com.mvm.exapp.model.ExappAppointmentState;
import com.mvm.exapp.model.ExappUser;
import com.mvm.exapp.useful.ExpandListAdapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.AdapterView.OnItemLongClickListener;

public class AppointmentsFragment extends Fragment{
	private ExpandableListView lv;
	private ArrayList<ExappAppointment> apps;
	private ExappUser current_user;
	//private boolean [] acceptedOrNot;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
	}
	
	public ArrayList<ExappAppointment> getApps() {
		return apps;
	}

	public void setApps(ArrayList<ExappAppointment> apps) {
		this.apps = apps;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		return inflater.inflate(R.layout.appointments, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		
		super.onActivityCreated(savedInstanceState);
		this.lv = (ExpandableListView) getActivity().findViewById(R.id.list_appointments);
		((MainActivity)getActivity()).getFrgt_title().setText("Your Appointments");
		
		Display newDisplay = getActivity().getWindowManager().getDefaultDisplay(); 
		int width = newDisplay.getWidth();
		this.lv.setIndicatorBounds(width-50, width);
		
        this.apps = setStandardGroups();
        ExpandListAdapter adapter = new ExpandListAdapter(getActivity(), this.apps);
        this.lv.setAdapter(adapter);
        
        //////////// SHOW GROUP MEMBERS AND IF THEY ACCEPTED APPOINTMENT OR NOT //////////
        //////////Works on long click////////////////////////////////////////////////////
        
        lv.setLongClickable(true);
	    
	    lv.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent,
					View view, int position, long id) {
					//getActivity().setContentView(R.layout.simple_list);
					int newPos =  ExpandableListView.getPackedPositionGroup(id);
					
					int appointmentID =	((ExappAppointment)	lv.getExpandableListAdapter().getChild(newPos, 0)).getId();
					//Log.i("ID", appointmentID + "");
					ListView attending = new ListView(getActivity());
					
					ArrayList<ExappAppointmentState> showUsers = new ArrayList<ExappAppointmentState>();
					
					
					 try {
						showUsers = (ArrayList<ExappAppointmentState>) DBManager.getInstance(getActivity()).execute(new Object[]{"getUsersAppointmentState", appointmentID}).get();
					} catch (InterruptedException | ExecutionException e) {
						
						e.printStackTrace();
					}
					 
					 ArrayList<String> str = new ArrayList<String>();
					 for(int i = 0; i < showUsers.size(); i++){
						 if(showUsers.get(i).getState()){
							 str.add(showUsers.get(i).getUser() + " [Joined Appointment]");
						 }else{
							 str.add(showUsers.get(i).getUser() + " [Not answered]");
						 }
					 }

					
					ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(getActivity(), R.layout.my_list_item, str);
					attending.setAdapter(modeAdapter);
					
					
					AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
					builder.setTitle("Users");

					
					
					

					builder.setView(attending);
					
					final Dialog dialog = builder.create();
					

					dialog.show();
					
				return true;
			}
	    });
	  //  this.handleToggles();
	
	   // this.handleAcceptAppointment();
	    /////////
	}
	
		
		
	
	 public ArrayList<ExappAppointment> setStandardGroups() {
	    	
		 
		ArrayList<ExappAppointment> list = new ArrayList<ExappAppointment>();
		try {
			list = (ArrayList<ExappAppointment>) DBManager.getInstance(getActivity()).execute(new Object[]{"getAllAppointmentsByUser", ((MainActivity) getActivity()).getCurrent_user().getUserID()}).get();
		} catch (InterruptedException | ExecutionException e) {
			
			e.printStackTrace();
		}

		Log.i("appointments size", "" + list.size());
	        return list;
	 }



	 public ExpandableListView getLv() {
		 return lv;
	 }

	public void setLv(ExpandableListView lv) {
		this.lv = lv;
	}

	@Override
	public void onResume() {
		
		super.onResume();
	}

	@Override
	public void onPause() {
		
		super.onPause();
	}

	@Override
	public void onDestroy() {
		
		super.onDestroy();
	}

	public void refresh(ExappAppointment app) {
		this.apps.add(app);
		((BaseExpandableListAdapter) lv.getExpandableListAdapter()).notifyDataSetChanged();
		
	}

}

