package com.mvm.exapp.activities;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

import com.mvm.exapp.R;
import com.mvm.exapp.externalDB.DBManager;
import com.mvm.exapp.model.ExappGroup;
import com.mvm.exapp.model.ExappUser;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ProfileActivity extends Activity{
	private TextView userID;
	private TextView userName;
	private TextView age;
	private TextView description;
	private ListView groups; 
	private ImageView img;
	private ExappUser currentUser;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile);
		this.currentUser = (ExappUser) getIntent().getSerializableExtra("user");
		this.userID = (TextView) findViewById(R.id.pr_user);
		this.userName = (TextView) findViewById(R.id.pr_username);
		this.age = (TextView) findViewById(R.id.pr_age);
		this.description = (TextView) findViewById(R.id.pr_description);
		this.groups = (ListView) findViewById(R.id.pr_list);
		this.img = (ImageView) findViewById(R.id.pr_img);
		this.loadProfile();
	}
	public boolean onCreateOptionsMenu(Menu menu) {
        
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.edit_profile, menu);
	    
	    return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		
		Intent i = new Intent(this, RegisterActivity.class);
		i.putExtra("user", currentUser);
		startActivity(i);
		return true;
	 
	}
	private void loadProfile(){
		this.userID.setText(this.currentUser.getUserID());
		this.userName.setText(this.currentUser.getName());
		this.description.setText(this.currentUser.getDescription());
		Date birthdate = currentUser.getBirthday();
		java.util.Date now =  Calendar.getInstance().getTime();
		int age =  (now.getYear() - birthdate.getYear());
		this.age.setText(age + "");
		
		this.description.setText(this.currentUser.getDescription());
		this.handleListView();
		//TODO load image from db
		Bitmap b = null;
		try {
			 b = (Bitmap) DBManager.getInstance(getActivity()).execute(new Object[]{"getPicture", currentUser.getImageID()}).get();
			
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while(b == null){}
		this.img.setImageBitmap(b);
		
	}
	
	private void handleListView(){
		ArrayList<ExappGroup> g = null;
		try {

			String id =  this.currentUser.getUserID();
			g = (ArrayList<ExappGroup>)DBManager.getInstance(getActivity()).execute(new String[]{"getAllGroups", id}).get();
		} catch (InterruptedException | ExecutionException e) {
			
			e.printStackTrace();
		}
		//TODO ¿add RWTH groups?
		ArrayList<String> values = new ArrayList<String>();
		for(int i = 0; i < g.size(); i++){
			values.add(g.get(i).getGroupID());
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, 
				android.R.id.text1, values);
		groups.setAdapter(adapter);
		
	}
	public Activity getActivity(){
		return this;
	}
}
