package com.mvm.exapp.activities;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import com.mvm.exapp.R;
import com.mvm.exapp.externalDB.DBManager;
import com.mvm.exapp.model.ExappGroup;
import com.mvm.exapp.model.ExappUser;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class SearchGroupsActivity extends Activity{
	private ListView lv;
	private ArrayList<ExappGroup> groups;
	private ExappUser user;
	private Button go;
	private ArrayList<String> subjects;
	private AutoCompleteTextView subject;
	private ArrayAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_groups);
		this.lv = (ListView) findViewById(R.id.search_list);
		this.go = (Button) findViewById(R.id.btn_go);
		this.subject = (AutoCompleteTextView) findViewById(R.id.ac_subject);
		this.user = (ExappUser) getIntent().getSerializableExtra("user");
		this.subjects = null;
		try {
			this.subjects = (ArrayList<String>) DBManager.getInstance(getActivity()).execute(new Object[]{"getAllSubjects"}).get();
		} catch (InterruptedException | ExecutionException e) {
			
			e.printStackTrace();
		}
		
			ArrayAdapter ad = new ArrayAdapter(this,android.R.layout.simple_dropdown_item_1line,this.subjects);
				   subject.setAdapter(ad);
		this.handleListView();
		this.handleGo();
	}

	private void handleListView(){
		
		try {
			groups = (ArrayList<ExappGroup>)DBManager.getInstance(getActivity()).execute(new String[]{"getAllGroupsInDB"}).get();
		} catch (InterruptedException | ExecutionException e) {

			e.printStackTrace();
		}
		ArrayList<String> values = new ArrayList<String>();
		for(int i = 0; i < groups.size(); i++){
			values.add(groups.get(i).getGroupID());
		}
		 adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_2, 
				android.R.id.text1, values);
		lv.setAdapter(adapter);


		lv.setOnItemClickListener(new OnItemClickListener() {



			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				lv.setItemChecked(lv.getCheckedItemPosition(), false);
				Intent i = new Intent(getApplicationContext(), ViewGroupActivity.class);
				i.putExtra("exappGroup", groups.get(position) );
				i.putExtra("user", user);
				i.putExtra("type", "group");
				i.putExtra("hide", "true");
				startActivity(i);
			}
		});
	}
	
	public void handleGo(){
		View.OnClickListener handle_go = new View.OnClickListener() {
			public void onClick(View v) {
				try {
					groups = (ArrayList<ExappGroup>)DBManager.getInstance(getActivity()).execute(new String[]{"getGroupsBySubject", subject.getText().toString()}).get();
				} catch (InterruptedException | ExecutionException e) {

					e.printStackTrace();
				}
				ArrayList<String> values = new ArrayList<String>();
				for(int i = 0; i < groups.size(); i++){
					values.add(groups.get(i).getGroupID());
				}
				
//				adapter = new ArrayAdapter<String>(getApplicationContext(),
//						android.R.layout.simple_list_item_1, 
//						android.R.id.text1, values);
				adapter.clear();
				adapter.addAll(values);
				adapter.notifyDataSetChanged();
				//lv.setAdapter(adapter);
				

			}
		};

		this.go.setOnClickListener(handle_go);

	}
	public Activity getActivity(){
		return this;
	}
}
