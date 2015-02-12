package com.mvm.exapp.activities;

import java.util.concurrent.ExecutionException;

import com.mvm.exapp.R;
import com.mvm.exapp.externalDB.DBManager;
import com.mvm.exapp.model.ExappAnnouncement;
import com.mvm.exapp.model.ExappDiscussion;
import com.mvm.exapp.model.ExappGroup;
import com.mvm.exapp.model.ExappUser;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ViewItemActivity extends Activity{
 protected ExappAnnouncement announcement;
 protected ExappDiscussion discussion;
 protected String body;
 protected String title;
 protected String from;
 protected ExappUser user;
 protected ExappGroup group;
 protected Button viewgroup;
 protected Button back;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_item);
		//findViewById(R.id.layout_viewgroup).setVisibility(View.INVISIBLE);
		String type = getIntent().getStringExtra("type");

		this.user = (ExappUser) getIntent().getSerializableExtra("currentUser");
		this.viewgroup = (Button) findViewById(R.id.btn_view_group);
		this.back = (Button) findViewById(R.id.btn_go_back);
		String hide = getIntent().getStringExtra("hide");
		if(hide.equals("false")){
			findViewById(R.id.layout_viewgroup).setVisibility(View.VISIBLE);
		}else{
			findViewById(R.id.layout_viewgroup).setVisibility(View.GONE);
		}
		if(type.equals("discussion")){
			this.discussion = (ExappDiscussion) getIntent().getSerializableExtra("item");
			this.body = this.discussion.getBody();
			this.title = this.discussion.getTitle();
			this.from = "From: " + getIntent().getStringExtra("user");
			this.updateFields();
		}else if(type.equals("announcement")){
			this.announcement = (ExappAnnouncement) getIntent().getSerializableExtra("item");
			this.body = this.announcement.getBody();
			this.title = announcement.getTitle();
			this.from = getIntent().getStringExtra("user");
			this.group = (ExappGroup) getIntent().getSerializableExtra("group");
			this.updateFields();
		}

		this.handleButtons();
	}
	private void handleButtons(){
		View.OnClickListener handle_back = new View.OnClickListener() {

			public void onClick(View v) {
				finish();
			}	
		};

		this.back.setOnClickListener(handle_back);
		
		View.OnClickListener handle_view_group = new View.OnClickListener() {

			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), ViewGroupActivity.class);
				i.putExtra("exappGroup", group );
				i.putExtra("user", user);
				i.putExtra("type", "group");
				i.putExtra("hide", "true");
				startActivity(i);
			}	
		};

		this.viewgroup.setOnClickListener(handle_view_group);
	}
	protected void updateFields(){
		TextView title_tv = (TextView) findViewById(R.id.item_title);
		TextView body_tv = (TextView) findViewById(R.id.body);
		TextView user_tv = (TextView) findViewById(R.id.item_user);
		
		title_tv.setText(this.title);
		body_tv.setText(Html.fromHtml(this.body));
		if (this.from.equals("null")){
			user_tv.setVisibility(View.INVISIBLE);
		}else{
			user_tv.setText(this.from);
		}
	}

	

}
