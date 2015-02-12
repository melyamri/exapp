package com.mvm.exapp.useful;



import java.util.ArrayList;

import com.mvm.exapp.R;
import com.mvm.exapp.model.ExappAppointment;



import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;

public class ExpandListAdapter extends BaseExpandableListAdapter {

	private Context context;
	private ArrayList<ExappAppointment> appointments;
	public ExpandListAdapter(Context context, ArrayList<ExappAppointment> groups) {
		this.context = context;
		this.appointments = groups;
	}
	

	public Object getChild(int appointmentPos, int childPosition) {
		
		return appointments.get(appointmentPos);
		
	}

	public long getChildId(int groupPosition, int childPosition) {
		
		return childPosition;
	}

	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view,
			ViewGroup parent) {
		ExappAppointment child = (ExappAppointment) getChild(groupPosition, childPosition);
		if (view == null) {
			LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
			view = infalInflater.inflate(R.layout.expandlist_child_item, null);
		}
		TextView tv_group = (TextView) view.findViewById(R.id.tv_group);
		tv_group.setText(child.getGroup().toString());
		
		TextView tv_place = (TextView) view.findViewById(R.id.tv_place);
		tv_place.setText(child.getPlace().toString());
		
		TextView tv_date = (TextView) view.findViewById(R.id.tv_date);
		tv_date.setText(child.getDate().toString());
		
		TextView tv_time = (TextView) view.findViewById(R.id.tv_time);
		tv_time.setText(child.getTime().toString());
		
		TextView tv_description = (TextView) view.findViewById(R.id.tv_description);
		tv_description.setText(child.getDescription().toString());
		
		TextView tv_id = (TextView) view.findViewById(R.id.tv_app_id);
		tv_id.setText(child.getId() + "");
		
		ToggleButton toggle = (ToggleButton) view.findViewById(R.id.tg_accept);
		if(child.isHasCurrentUserAccepted()){
			toggle.setChecked(true);
		}
		
		return view;
	}

	public int getChildrenCount(int groupPosition) {
		
		

		return 1;

	}

	public Object getGroup(int appointmentPos) {
		
		return appointments.get(appointmentPos);
	}

	public int getGroupCount() {
		
		return appointments.size();
	}

	public long getGroupId(int groupPosition) {
		
		return groupPosition;
	}

	public View getGroupView(int groupPosition, boolean isLastChild, View view,
			ViewGroup parent) {
		ExappAppointment  group = (ExappAppointment) getGroup(groupPosition);
		if (view == null) {
			LayoutInflater inf = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
			view = inf.inflate(R.layout.expandlist_group_item, null);
		}
		TextView tv = (TextView) view.findViewById(R.id.appointment_title);
		tv.setText(group.getTitle());
		
		return view;
	}

	public boolean hasStableIds() {
		
		return true;
	}

	public boolean isChildSelectable(int arg0, int arg1) {
		
		return true;
	}

}




