package com.mvm.exapp.activities;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import com.mvm.exapp.R;
import com.mvm.exapp.externalDB.DBManager;
import com.mvm.exapp.fragments.GroupListFragment;
import com.mvm.exapp.model.ExappGroup;
import com.mvm.exapp.model.ExappUser;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

public class AddEditGroupActivity extends Activity {
private ExappGroup group;
private ExappUser currentUser;
private Button save;
private Button discard;
private ArrayList<String> members;
private MultiAutoCompleteTextView actv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		//Initialize buttons
		setContentView(R.layout.add_group);
		findViewById(R.id.et_group_name).setEnabled(true);
		this.save = (Button) findViewById(R.id.btn_group_save);
		this.discard = (Button) findViewById(R.id.btn_group_discard);
		this.actv = (MultiAutoCompleteTextView) findViewById(R.id.et_members);
		this.members = new ArrayList<String>();
		this.handleButtons();
		this.handleListView();
		
		this.currentUser = (ExappUser) getIntent().getSerializableExtra("founder");
		
		if(getIntent().getStringExtra("item") == null) {
			this.group = new ExappGroup();
			
		}else{
			
			try {
				this.group = (ExappGroup) DBManager.getInstance(getActivity()).execute(new Object[]{"getGroupById", getIntent().getStringExtra("item")}).get();
			} catch (InterruptedException | ExecutionException e) {
				
				e.printStackTrace();
			}
			this.loadGroupData();
			
			
		}
		
		
	}
	
	private void handleListView(){
		
		ArrayList<String> users = null;
		try {
			users = (ArrayList<String>) DBManager.getInstance(getActivity()).execute(new String[]{"getAllUsers"}).get();
		} catch (InterruptedException | ExecutionException e) {
			
			e.printStackTrace();
		}
		
		@SuppressWarnings("unchecked")
		ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_dropdown_item_1line,users);
				   actv.setAdapter(adapter);
				   actv.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
				   actv.setThreshold(1);

	}
	
	private void handleButtons() {
		View.OnClickListener handle_save = new View.OnClickListener() {
	   		 
		    public void onClick(View v) {
		    	addGroup();
		    	
		    	
		    	String msg = "";
				try {
					Log.i("Length", members.size() + "");
					msg = (String) DBManager.getInstance(getActivity()).execute(new Object[]{"changeOrAddGroup", currentUser, group}).get();
					
				} catch (InterruptedException | ExecutionException e) {
					
					e.printStackTrace();
				}
		    	 
		    		 Log.i("DBMSG", msg);
		    		 Toast.makeText(getApplicationContext(), msg,Toast.LENGTH_LONG).show();
		    	 
		    	getActivity().finish(); 
		    	}
		  };
		 
		  this.save.setOnClickListener(handle_save);
		  View.OnClickListener handle_discard = new View.OnClickListener() {
		   		 
			    public void onClick(View v) {
			    	getActivity().finish();
			    }	
			  };
			 
			  this.discard.setOnClickListener(handle_discard);
		
	}
	private void addGroup() {
		String name =  ((EditText) findViewById(R.id.et_group_name)).getText().toString();
		String subject =  ((EditText) findViewById(R.id.et_subject)).getText().toString();
		String description = ((EditText) findViewById(R.id.et_group_description)).getText().toString();
		
		
		
		this.group.setGroupID(name);
		this.group.setDescription(description);
		this.group.setSubject(subject);
		this.group.setFounder(this.currentUser.getUserID());
		
		String[] str = actv.getText().toString().split(",");
		
		for(int i=0; i<str.length - 1; i++) {
			
			members.add(str[i]);
		}
		this.group.setMembers(this.members);
	}
	private void loadGroupData() {
		EditText et_name = (EditText) findViewById(R.id.et_group_name);
		et_name.setEnabled(false);
		EditText et_subject = (EditText) findViewById(R.id.et_subject);
		EditText et_description = (EditText) findViewById(R.id.et_group_description);
		
		et_name.setText(this.group.getGroupID());
		et_subject.setText(this.group.getSubject());
		et_description.setText(this.group.getDescription());
		
		
	}
	private Activity getActivity() {
		
		return this;
	}

}
