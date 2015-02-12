package com.mvm.exapp.activities;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import com.mvm.exapp.R;
import com.mvm.exapp.externalDB.DBManager;
import com.mvm.exapp.model.ExappGroup;
import com.mvm.exapp.model.ExappUser;

import android.app.Activity;
import android.content.Intent;
import android.content.ClipData.Item;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class ViewGroupActivity extends ViewItemActivity{
	private ListView list;
	private ExappUser currentUser;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		list = (ListView) findViewById(R.id.group_members_view);
		
		//group = (ExappGroup) getIntent().getSerializableExtra("group");
		String type = getIntent().getStringExtra("type");
		if(type.equals("group")){
			if(getIntent().getSerializableExtra("exappGroup") != null){
				group = (ExappGroup) getIntent().getSerializableExtra("exappGroup");
				Log.i("chosen group", this.group.getGroupID());
			}
		}
		currentUser = (ExappUser) getIntent().getSerializableExtra("user");
		findViewById(R.id.layout_viewgroup).setVisibility(View.INVISIBLE);
		this.handleListView();
		
		this.title = "Group Name: " + group.getGroupID();
		this.body = group.getDescription();
		this.from = "Subject: " + group.getSubject();
		
		this.updateFields();
		
		
	}
	public boolean onPrepareOptionsMenu(Menu menu){
		ArrayList<String> members = new ArrayList<String> ();
	    try {
			members = (ArrayList<String>) DBManager.getInstance(getActivity()).execute(new Object[]{"getAllMembers", group.getGroupID()}).get();
		} catch (InterruptedException | ExecutionException e) {
			
			e.printStackTrace();
		}
	   
	    Log.i("Memebers", members.size() + "");
	    boolean found = false;
		for(String user: members){
				if(user.equals(this.currentUser.getUserID())){
					found = true;
				
				}
			}
		if(found){
			menu.findItem(R.id.join_or_quit).setTitle("Quit from group");
		}
		return true;
	}
	public boolean onCreateOptionsMenu(Menu menu) {
        
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.group_options_menu, menu);
	    
	    return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		
		if(item.getTitle().equals("Join group")){
			DBManager.getInstance(getActivity()).execute(new Object[]{"joinGroup", this.currentUser.getUserID(), this.group.getGroupID()});
		}else{
			DBManager.getInstance(getActivity()).execute(new Object[]{"quitGroup", this.currentUser.getUserID(), this.group.getGroupID()});
		}
		
		return true;
	 
	}
	private void handleListView(){
		ArrayList<String> members = new ArrayList<String>();
		
		try {
			members = (ArrayList<String>) DBManager.getInstance(getActivity()).execute(new Object[]{"getAllMembers", group.getGroupID()}).get();
		} catch (InterruptedException | ExecutionException e) {
			
			e.printStackTrace();
		}
		
		ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, android.R.id.text1, members);
		
		this.list.setAdapter(adapter);
		
		this.list.setOnItemClickListener(new OnItemClickListener() {

			

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String userID = (String) list.getAdapter().getItem(position);
				
				ExappUser u = null;
				try {
					u = (ExappUser) DBManager.getInstance(getActivity()).execute(new Object[]{"getProfile", userID}).get();
				} catch (InterruptedException | ExecutionException e) {
					
					e.printStackTrace();
				}
				list.setItemChecked(list.getCheckedItemPosition(), false);
				Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
				
				while(u == null) {}
				
				i.putExtra("user", u );
				startActivity(i);
			}
	    });
	}
	public ListView getList() {
		return list;
	}
	public void setList(ListView list) {
		this.list = list;
	}
	public ExappGroup getGroup() {
		return group;
	}
	public void setGroup(ExappGroup group) {
		this.group = group;
	}
	public ExappUser getCurrentUser() {
		return currentUser;
	}
	public void setCurrentUser(ExappUser currentUser) {
		this.currentUser = currentUser;
	}
	public Activity getActivity(){
		return this;
	}
	}
