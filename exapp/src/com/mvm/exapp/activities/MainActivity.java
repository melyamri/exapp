package com.mvm.exapp.activities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.mvm.exapp.R;
import com.mvm.exapp.externalDB.DBManager;
import com.mvm.exapp.fragments.AppointmentsFragment;
import com.mvm.exapp.fragments.DiscussionsFragment;
import com.mvm.exapp.fragments.DocumentsFragment;
import com.mvm.exapp.fragments.GroupListFragment;
import com.mvm.exapp.fragments.GroupsFragment;
import com.mvm.exapp.fragments.LastUpdatesFragment;
import com.mvm.exapp.model.ExappAppointment;
import com.mvm.exapp.model.ExappDiscussion;
import com.mvm.exapp.model.ExappGroup;
import com.mvm.exapp.model.ExappUser;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.ToggleButton;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ArrayAdapter;

public class MainActivity extends Activity {
	private TabHost tabs;
	private FragmentTransaction fragmentTransaction;
	private FragmentManager fragmentManager;
	private ExappUser current_user;

	private Fragment current_fragment;



	/*****CONSTANTS*******/

	final public static String l2pUri = "https://www.elearning.rwth-aachen.de/_vti_bin/l2pservices/api.svc/v1/";
	final public static String l2pCourseId = "[course id]";
	final static public String clientId1 = "[client id]";
	final static public String oAuthUri = new String("https://oauth.campus.rwth-aachen.de/oauth2waitress/oauth2.svc/");
	final static public String tokenEndpoint = new String(oAuthUri + "token?");
	/********************************/

	private ImageButton btn_delete;
	private ImageButton btn_add;
	private ImageButton btn_edit;

	private TextView frgt_title;

	private ImageButton btn_groups;
	private String token;
	private String refresh_token;
	
	// Boolean to execute api or not //
	public boolean isRWTH;


	public ImageButton getBtn_delete() {
		return btn_delete;
	}

	public void setBtn_delete(ImageButton btn_delete) {
		this.btn_delete = btn_delete;
	}

	public ImageButton getBtn_add() {
		return btn_add;
	}

	public void setBtn_add(ImageButton btn_add) {
		this.btn_add = btn_add;
	}

	public ImageButton getBtn_edit() {
		return btn_edit;
	}

	public void setBtn_edit(ImageButton btn_edit) {
		this.btn_edit = btn_edit;
	}

	public ImageButton getBtn_groups() {
		return btn_groups;
	}

	public void setBtn_groups(ImageButton btn_groups) {
		this.btn_groups = btn_groups;
	}


	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Get Access Token
		this.token = getIntent().getStringExtra("token");
		this.refresh_token = getIntent().getStringExtra("refresh");
		this.current_user = (ExappUser) getIntent().getSerializableExtra("user");
		this.isRWTH = (getIntent().getStringExtra("isRWTH").equals("true"));
		
		getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		setContentView(R.layout.activity_main);
		/*** Main Menu Set Up ***/		
		tabs = (TabHost)findViewById(R.id.main_menu);
		tabs.setup();
		//tabs.getTabWidget().setDividerDrawable(R.drawable.tab_divider);

		setupTab(new TextView(this), "Home");
		setupTab(new TextView(this), "Discussions");
		setupTab(new TextView(this), "Meetings");
		/**********************************/

		fragmentManager = getFragmentManager();
		fragmentTransaction = fragmentManager.beginTransaction();
		this.setTabListener();
		LastUpdatesFragment lu_frgt = new LastUpdatesFragment();
		fragmentTransaction.add(R.id.fragment_holder, lu_frgt);
		fragmentTransaction.commit(); 
		/*****************************************/

		this.btn_delete = (ImageButton) findViewById(R.id.btn_delete);
		this.btn_add = (ImageButton)findViewById(R.id.btn_add);
		this.btn_edit = (ImageButton)findViewById(R.id.btn_edit);
		this.handleBottomBar();
		this.frgt_title = (TextView) findViewById(R.id.title);

		this.btn_groups = (ImageButton) findViewById(R.id.btn_change_group);
		
		this.handleButtonGroups();
		
		//TODO do refresh when expire_time finishes
		RefreshTask task = new RefreshTask();
		
		//task.execute(new String[]{"refreshToken", this.tokenEndpoint, this.clientId1, this.refresh_token});

	} 

	public boolean isRWTH() {
		return isRWTH;
	}

	public void setRWTH(boolean isRWTH) {
		this.isRWTH = isRWTH;
	}

	public TextView getFrgt_title() {
		return frgt_title;
	}

	public void setFrgt_title(TextView frgt_title) {
		this.frgt_title = frgt_title;
	}

	public ExappUser getCurrent_user() {
		return current_user;
	}

	public void setCurrent_user(ExappUser current_user) {
		this.current_user = current_user;
	}

	public void setToken(String token) {
		this.token = token;
	}

	private void setupTab(final View view, final String tag) {
		View tabview = createTabView(tabs.getContext(), tag);
		TabSpec setContent = tabs.newTabSpec(tag).setIndicator(tabview).setContent(new TabHost.TabContentFactory() {
			public View createTabContent(String tag) {return view;}
		});
		tabs.addTab(setContent);
	}

	private static View createTabView(final Context context, final String text) {
		View view = LayoutInflater.from(context).inflate(R.drawable.actionbar_tab_bg, null);
		TextView tv = (TextView) view.findViewById(R.id.tabsText);
		tv.setText(text);
		return view;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == (R.id.action_statistics)){
			Intent i = new Intent(this, StatisticsGlobalActivity.class);
			i.putExtra("user", this.current_user);
			this.startActivity(i);
		}else if(item.getItemId() == (R.id.action_profile)){
			Intent i = new Intent(this, ProfileActivity.class);
			i.putExtra("user", this.current_user);
			this.startActivity(i);
		}else if(item.getItemId() == (R.id.action_search)){
			Intent i = new Intent(this, SearchGroupsActivity.class);
			i.putExtra("user", this.current_user);
			this.startActivity(i);
		}
		return true;
		
		
	}

	public void changeFragment(int frgt){
		fragmentManager = getFragmentManager();
		fragmentTransaction = fragmentManager.beginTransaction();

		//Enable bottom bar
		this.btn_add.setEnabled(true);
		this.btn_delete.setEnabled(true);
		this.btn_edit.setEnabled(true);


		switch(frgt){
		case 0: LastUpdatesFragment lu_frgt = new LastUpdatesFragment();
		fragmentTransaction.replace(R.id.fragment_holder, lu_frgt, "last_updates_frgt");
		fragmentTransaction.commit(); break;
		case 1: DiscussionsFragment dis_frg = new DiscussionsFragment();
		fragmentTransaction.replace(R.id.fragment_holder, dis_frg, "discussion_frgt");
		fragmentTransaction.commit(); break;
		case 3: GroupsFragment g_frgt = new GroupsFragment();
		fragmentTransaction.replace(R.id.fragment_holder, g_frgt, "groups_frgt");
		fragmentTransaction.commit(); break;
		case 2: AppointmentsFragment app_frgt = new AppointmentsFragment();
		fragmentTransaction.replace(R.id.fragment_holder, app_frgt, "appointments_frgt");
		fragmentTransaction.commit(); break;
		
		default: LastUpdatesFragment def_frgt = new LastUpdatesFragment();
		fragmentTransaction.replace(R.id.fragment_holder, def_frgt, "last_updates_frgt");
		fragmentTransaction.commit(); break;
		}
	}

	public TabHost getTabs() {
		return tabs;
	}


	public void setTabs(TabHost tabs) {
		this.tabs = tabs;
	}


	public void setTabListener(){
		tabs.setOnTabChangedListener(new OnTabChangeListener() {

			public void onTabChanged(String tabId) {
				//Log.d(debugTag, "onTabChanged: tab number=" + mTabHost.getCurrentTab());

				changeFragment(tabs.getCurrentTab());
			}
		});
	}
	 public void onToggleClicked(View view){
		 int id = Integer.parseInt( ((TextView) ((View) view.getParent()).findViewById(R.id.tv_app_id)).getText().toString());
		 ToggleButton b = (ToggleButton) view;
		 
		 if(b.isChecked()){
			 
			 DBManager.getInstance(getActivity()).execute(new Object[]{"acceptAppointment", id, ((MainActivity) getActivity()).getCurrent_user().getUserID(),true});
		 }else{
			 DBManager.getInstance(getActivity()).execute(new Object[]{"acceptAppointment", id, ((MainActivity) getActivity()).getCurrent_user().getUserID(),false});
		 }
	 }
	public void handleButtonGroups(){
		View.OnClickListener handle_groups = new View.OnClickListener() {

			public void onClick(View v) {

				fragmentManager = getFragmentManager();
				fragmentTransaction = fragmentManager.beginTransaction();

				GroupListFragment group_list_frgt = new GroupListFragment();
				fragmentTransaction.replace(R.id.fragment_holder, group_list_frgt, "group_list_frgt");

				fragmentTransaction.commit();

			}    
		};

		this.btn_groups.setOnClickListener(handle_groups);
	}
	public Activity getActivity() {

		return this;
	}
	public FragmentManager getFrgtManager() {
		return fragmentManager;
	}

	

	public void handleBottomBar(){
		View.OnClickListener handle_delete = new View.OnClickListener() {

			@SuppressWarnings("unchecked")
			public void onClick(View v) {

				Fragment df = fragmentManager.findFragmentById(R.id.fragment_holder);
				//Log.i("Index", "Fragment Tag " + df.getTag()  );
				if (df.getTag().equals("discussion_frgt")) {
					// Check selected items

					int index = ((DiscussionsFragment)df).getLv().getCheckedItemPosition();
					if(index != -1){
						((ArrayAdapter <String>) ((DiscussionsFragment)df).getLv().getAdapter()).remove((String) ((DiscussionsFragment)df).getLv().getAdapter().getItem(index));
						ExappDiscussion ed = ((DiscussionsFragment)df).getDiscussions().get(index);
						Dialog accept = createDialog("Delete " + ((DiscussionsFragment) df).getLv().getItemIdAtPosition(index));
						accept.show();
						//if(((DiscussionsFragment)df).getGroup().getText().equasIgnoreCase("rwth")){
						DBManager.getInstance(getActivity()).execute(new Object[]{"deleteDiscussion",  ((DiscussionsFragment)df).getDiscussions().get(index).getDiscussionID()});


						((ArrayAdapter <String>) ((DiscussionsFragment)df).getLv().getAdapter()).notifyDataSetChanged();
						((DiscussionsFragment)df).getLv().setItemChecked(index, false);
					}else{
						Toast.makeText(getApplicationContext(), "Please, select a thread that you want to delete by pressing and holding on it.", Toast.LENGTH_SHORT).show();
					}
				}else if(df.getTag().equals("group_list_frgt")){
					int index = ((GroupListFragment)df).getLv().getCheckedItemPosition();
					if(index != -1){
						
						
						Dialog accept = createDialog("Delete " + ((GroupListFragment) df).getLv().getItemIdAtPosition(index));
						accept.show();
						try {
							if((boolean) DBManager.getInstance(getActivity()).execute(new Object[]{"deleteGroup", current_user.getUserID(), (String) ((GroupListFragment)df).getLv().getAdapter().getItem(index)}).get()){
								((ArrayAdapter <String>) ((GroupListFragment)df).getLv().getAdapter()).remove((String) ((GroupListFragment)df).getLv().getAdapter().getItem(index));
								
								Toast.makeText(getApplicationContext(),"Group Deleted Succesfully", Toast.LENGTH_LONG).show();
							}
						} catch (InterruptedException | ExecutionException e) {
							
							e.printStackTrace();
						}

						((ArrayAdapter <String>) ((GroupListFragment)df).getLv().getAdapter()).notifyDataSetChanged();
						((GroupListFragment)df).getLv().setItemChecked(index, false);
					}else{
						Toast.makeText(getApplicationContext(), "Please, select a group that you want to delete by pressing and holding on it.", Toast.LENGTH_SHORT).show();
					}
				}else if(df.getTag().equals("last_updates_frgt")){
					int index = ((LastUpdatesFragment)df).getLv().getCheckedItemPosition();
					if(index != -1){
						((ArrayAdapter <String>) ((LastUpdatesFragment)df).getLv().getAdapter()).remove((String) ((LastUpdatesFragment)df).getLv().getAdapter().getItem(index));

						((ArrayAdapter <String>) ((LastUpdatesFragment)df).getLv().getAdapter()).notifyDataSetChanged();
						((LastUpdatesFragment)df).getLv().setItemChecked(index, false);
					}else{
						Toast.makeText(getApplicationContext(), "Please, select an update that you want to discard by pressing and holding on it.", Toast.LENGTH_SHORT).show();
					}
				}else if(df.getTag().equals("appointments_frgt")){
					ListView appointments = new ListView(getActivity());
					final ArrayList<String> str = new ArrayList<String>();
					for(int i = 0; i < ((AppointmentsFragment)df).getApps().size(); i++){
						str.add(((AppointmentsFragment)df).getApps().get(i).getTitle());
					}
					ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(getActivity(), R.layout.my_list_item, str);
					appointments.setAdapter(modeAdapter);
					appointments.setOnItemLongClickListener(new OnItemLongClickListener() {
						
						@Override
						public boolean onItemLongClick(AdapterView<?> parent,
								View view, int position, long id) {
							//TODO make dialog to ensure delete
							if(deleteAppointment(position, getFrgtManager().findFragmentById(R.id.fragment_holder))){
								str.remove(position);
								Toast.makeText(getApplicationContext(), "Appointment deleted successfully", Toast.LENGTH_LONG).show();
							}
							return true;
							
						}
						
					});
					AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
					builder.setTitle("Delete appointment");
					builder.setView(appointments);
					
					final Dialog dialog = builder.create();
					dialog.show();
					
				}

			}
		};

		this.btn_delete.setOnClickListener(handle_delete);

		View.OnClickListener handle_add = new View.OnClickListener() {
			public void onClick(View v) {
				Fragment df = fragmentManager.findFragmentById(R.id.fragment_holder);
				if (df.getTag().equals("discussion_frgt")) {
					Intent myIntent = new Intent(getApplicationContext(), AddEditActivity.class);
					myIntent.putExtra("item", "null");
					myIntent.putExtra("user", getCurrent_user());
					myIntent.putExtra("token", token);
					myIntent.putExtra("type", "add");
					myIntent.putExtra("group", ((DiscussionsFragment) df).getGroup().getText().toString());
					startActivityForResult(myIntent, 0);
					// TODO add discussion to L2P
				}else if(df.getTag().equals("last_updates_frgt")){
					Toast.makeText(getApplicationContext(), "Sorry, you are not allowed to add announcements", Toast.LENGTH_SHORT);
				}else if(df.getTag().equals("group_list_frgt")){
					Intent myIntent = new Intent(getApplicationContext(), AddEditGroupActivity.class);
					myIntent.putExtra("founder", current_user);
					startActivityForResult(myIntent, 0);
				}else if(df.getTag().equals("appointments_frgt")){
					Intent myIntent = new Intent(getApplicationContext(), AddEditAppointmentActivity.class);
					myIntent.putExtra("user", current_user);
					startActivityForResult(myIntent, 522);
					
				}
			}
		};
		
		
		this.btn_add.setOnClickListener(handle_add);
		View.OnClickListener handle_edit = new View.OnClickListener() {
			public void onClick(View v) {
				Fragment df = fragmentManager.findFragmentById(R.id.fragment_holder);
				if (df.getTag().equals("discussion_frgt")) {
					int index = ((DiscussionsFragment)df).getLv().getCheckedItemPosition();
					if(index != -1){
						Intent myIntent = new Intent(getApplicationContext(), AddEditActivity.class);
						myIntent.putExtra("item", (ExappDiscussion)((DiscussionsFragment) df).getDiscussions().get(index));
						myIntent.putExtra("user", getCurrent_user());
						myIntent.putExtra("token", token);
						myIntent.putExtra("type", "edit");
						myIntent.putExtra("group", ((DiscussionsFragment) df).getGroup().getText().toString());
						startActivityForResult(myIntent, 0);
					}else{
						Toast.makeText(getApplicationContext(), "Please, select the discussion thread you want to edit", Toast.LENGTH_SHORT);
					}
				}else if (df.getTag().equals("group_list_frgt")) {
					int index = ((GroupListFragment)df).getLv().getCheckedItemPosition();
					if(index != -1){
						Intent myIntent = new Intent(getApplicationContext(), AddEditGroupActivity.class);
						myIntent.putExtra("item", ((GroupListFragment) df).getLv().getItemAtPosition(index).toString());
						myIntent.putExtra("founder", current_user);
						startActivityForResult(myIntent, 0);
					}else{
						Toast.makeText(getApplicationContext(), "Please, select the group you want to edit", Toast.LENGTH_SHORT);
					}
				}else if (df.getTag().equals("appointments_frgt")) {
					ListView appointments = new ListView(getActivity());
					final ArrayList<String> str = new ArrayList<String>();
					for(int i = 0; i < ((AppointmentsFragment)df).getApps().size(); i++){
						str.add(((AppointmentsFragment)df).getApps().get(i).getTitle());
					}
					ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(getActivity(), R.layout.my_list_item, str);
					appointments.setAdapter(modeAdapter);
					appointments.setOnItemLongClickListener(new OnItemLongClickListener() {
						
						@Override
						public boolean onItemLongClick(AdapterView<?> parent,
								View view, int position, long id) {
							//TODO make dialog to ensure delete
							
							Intent myIntent = new Intent(getApplicationContext(), AddEditAppointmentActivity.class);
							myIntent.putExtra("item", ((AppointmentsFragment) getFrgtManager().findFragmentById(R.id.fragment_holder)).getApps().get(position));
							myIntent.putExtra("user", current_user);
							startActivity(myIntent);
							return true;
							
						}
						
					});
					AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
					builder.setTitle("Delete appointment");
					builder.setView(appointments);
					
					final Dialog dialog = builder.create();
					dialog.show();
				}
			}
		};
		this.btn_edit.setOnClickListener(handle_edit);


	}
	
	public boolean deleteAppointment(int position, Fragment fragment){
		int id = ((AppointmentsFragment)fragment).getApps().get(position).getId();
		try {
			if((boolean)DBManager.getInstance(getActivity()).execute(new Object[]{"deleteAppointment", id}).get()){
				((AppointmentsFragment) fragment).getApps().remove(position);
				
				((BaseExpandableListAdapter) ((AppointmentsFragment) fragment).getLv().getExpandableListAdapter()).notifyDataSetChanged();
				return true;
			}
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		return false;
		
	}
	@Override 
	public void onActivityResult(int requestCode, int resultCode, Intent data) {     
	  super.onActivityResult(requestCode, resultCode, data); 
	  switch(requestCode) { 
	    case (522) : { 
	      if (resultCode == Activity.RESULT_OK) { 
	      ExappAppointment app = (ExappAppointment) data.getSerializableExtra("appointment");
	      Fragment df = fragmentManager.findFragmentById(R.id.fragment_holder);
			if (df.getTag().equals("appointments_frgt")) {
				((AppointmentsFragment) df).refresh(app);
			}
	      } 
	      break; 
	    } 
	  } 
	}
	public String getToken() {
		
		return this.token;
	}

	public Dialog createDialog(String message){
		/***/
		AlertDialog dialog;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(message).setTitle("Please confirm");
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				
			}
		});
		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {

			}	

		});
		dialog = builder.create();
		return dialog;
	}

	private class RefreshTask extends AsyncTask<String, Void, String[]> {
		HttpResponse response;


		@Override
		protected String[] doInBackground(String... params) {
			HttpClient httpclient = new DefaultHttpClient();
			String[] result = {};
			HttpPost httppost = new HttpPost(params[1]);
			try {
				// Add your data
				switch (params[0]) {
				case "refreshToken":

					List<NameValuePair> pairs_refresh_token = new ArrayList<NameValuePair>(
							2);
					pairs_refresh_token.add(new BasicNameValuePair("client_id",
							params[2]));
					pairs_refresh_token.add(new BasicNameValuePair("refresh_token",
							params[3]));
					pairs_refresh_token.add(new BasicNameValuePair("grant_type",
							"refresh_token"));
					httppost.setEntity(new UrlEncodedFormEntity(pairs_refresh_token));
					break;

				}
				String status = "";
				response = httpclient.execute(httppost);

				String res = inputStreamToString(
						response.getEntity().getContent()).toString();
				result = new String[] { res, params[0] };
				JSONObject jsonResponse;
				try {
					jsonResponse = new JSONObject(res);
					status = (String) jsonResponse.get("status");

				} catch (JSONException e) {
				
					e.printStackTrace();
				}




				Log.i("token", result[0]);
				return result;

			}

			catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		private StringBuilder inputStreamToString(InputStream is) {
			String rLine = "";
			StringBuilder answer = new StringBuilder();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));

			try {
				while ((rLine = rd.readLine()) != null) {
					answer.append(rLine);
				}
			}

			catch (IOException e) {
				// e.printStackTrace();
				Toast.makeText(getApplicationContext(),
						"Error..." + e.toString(), Toast.LENGTH_LONG).show();
			}
			return answer;
		}

		@Override
		protected void onPostExecute(String[] result) {

			parseJSON(result[0], result[1]);

		}
	}

	public void parseJSON(String jsonResult, String type) {


		try {
			JSONObject jsonResponse = new JSONObject(jsonResult);
			switch (type) {
			case "refreshToken":
				String stat2 = (String) jsonResponse.get("status");
				if (stat2.equals("ok")) {
					String token = (String) jsonResponse.get("access_token");
					String token_type = (String) jsonResponse.get("token_type");
					String expires = new String(""
							+ jsonResponse.getInt("expires_in"));

					this.token = token;

					break;
				} else {
					Log.i("error", "Refresh token did not work");
				}
			}

		} catch (JSONException e) {
			Toast.makeText(getApplicationContext(), "Error" + e.toString(),
					Toast.LENGTH_LONG).show();
		}

	}


}


