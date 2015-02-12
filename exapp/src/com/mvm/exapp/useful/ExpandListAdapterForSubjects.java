package com.mvm.exapp.useful;

import java.util.ArrayList;

import com.mvm.exapp.R;

import com.mvm.exapp.model.statistics.StatisticsSubject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ExpandListAdapterForSubjects extends BaseExpandableListAdapter {

	private Context context;
	private ArrayList<StatisticsSubject> list;
	public ExpandListAdapterForSubjects(Context context, ArrayList<StatisticsSubject> list) {
		this.context = context;
		this.list = list;
	}
	

	public Object getChild(int pos, int childPosition) {
		
		return list.get(pos).getListGroups().get(childPosition);
		
	}

	public long getChildId(int groupPosition, int childPosition) {
		
		return childPosition;
	}

	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view,
			ViewGroup parent) {
		String child = (String) getChild(groupPosition, childPosition);
		if (view == null) {
			LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
			view = infalInflater.inflate(R.layout.expandlist_subject, null);
		}
//		TextView tv_numStudents = (TextView) view.findViewById(R.id.tv_numStudents);
//		tv_numStudents.setText(((Integer)child.getNumMembers()).toString());
		
		TextView tv_child = (TextView) view.findViewById(R.id.tv_child);
		tv_child.setText(child);
		return view;
	}

	public int getChildrenCount(int groupPosition) {
		return list.get(groupPosition).getListGroups().size();
	}

	public Object getGroup(int pos) {	
		return this.list.get(pos);
	}

	public int getGroupCount() {
		return this.list.size();
	}

	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	public View getGroupView(int groupPosition, boolean isLastChild, View view,
			ViewGroup parent) {
		StatisticsSubject  ssubject = (StatisticsSubject) getGroup(groupPosition);
		if (view == null) {
			LayoutInflater inf = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
			view = inf.inflate(R.layout.expandlist_subject_group, null);
		}
		TextView tv = (TextView) view.findViewById(R.id.group_subject);
		tv.setText(ssubject.getSubject());
		
		TextView people = (TextView) view.findViewById(R.id.subject_people);
		people.setText("People: "+ ssubject.getNumMembers());
		
		return view;
	}

	public boolean hasStableIds() {	
		return true;
	}

	public boolean isChildSelectable(int arg0, int arg1) {	
		return true;
	}

}




