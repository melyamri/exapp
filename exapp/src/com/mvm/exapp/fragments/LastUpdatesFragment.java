package com.mvm.exapp.fragments;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mvm.exapp.activities.MainActivity;
import com.mvm.exapp.activities.ViewItemActivity;
import com.mvm.exapp.externalDB.DBManager;
import com.mvm.exapp.model.ExappAnnouncement;
import com.mvm.exapp.model.ExappAppointment;
import com.mvm.exapp.model.ExappGroup;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class LastUpdatesFragment extends ListFragment {
	private ArrayList<ExappAnnouncement> announcements;
	private ArrayList<ExappAnnouncement> otherAnnouncements;
	private ArrayAdapter<String> adapter;
	private APIMethods api;
	private HashMap<Integer, ExappGroup> aux;
	ArrayList<ExappGroup> groups;
	ArrayList<ExappAppointment> app;
	@Override
	
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
	}

	public void setAPIAnnouncements(){


		if(error){
			//Log.i("Error",(String) this.dataFromAsyncTask);
			
			this.error = false;
		}else{
			
			this.handleListView();
			
		}
		this.loadExappAnnouncements();
	}
	
	@SuppressWarnings("unchecked")
	public void loadExappAnnouncements(){
		
		this.aux = new HashMap<Integer, ExappGroup>();
		this.otherAnnouncements = new ArrayList<ExappAnnouncement>();
		//TODO get all future appointments with just one variable Date
		 app = new ArrayList<ExappAppointment>();
		 groups = new ArrayList<ExappGroup>();
		 try {
			app = (ArrayList<ExappAppointment>) DBManager.getInstance(getActivity()).execute(new Object[]{"getFutureAppointments", ((MainActivity) getActivity()).getCurrent_user().getUserID(), new java.sql.Date(Calendar.getInstance().getTime().getTime())}).get();
			groups = (ArrayList<ExappGroup>) DBManager.getInstance(getActivity()).execute(new Object[]{"getInvitations", ((MainActivity) getActivity()).getCurrent_user().getUserID()}).get();
		} catch (InterruptedException | ExecutionException e) {
			
		e.printStackTrace();
		}
		 ArrayList<String> g = new ArrayList<String>();
		 for(int i = 0; i < groups.size(); i++){
			 g.add(groups.get(i).getGroupID());
			 this.aux.put(g.size() - 1, groups.get(i));
			 Log.i("group", groups.get(i).getGroupID() + " " + (g.size() - 1));
		 }
		 
		 for (String group : g){
			 //TODO make something to access the group and accept
			 String body = "Group " + group + " would like to have you as a member. Whould you like to join them?";
			 ExappAnnouncement a = new ExappAnnouncement("Group Invitation", body, null, 0, 0, "group_invite");
			 this.otherAnnouncements.add(a);
			 
			 
			
		 }
		 
		 for (ExappAppointment appointment : app){
			 String body = "A new appointment of group " + appointment.getGroup() + " has been programmed for the day: " 
					 						+ appointment.getDate().toString() + " at " + appointment.getTime().toString() 
					 						+ " in " + appointment.getPlace() + ". \n" + "If you plan to attend, please accept on your appointments list.";
			 ExappAnnouncement a = new ExappAnnouncement("New Appointment: " + appointment.getTitle(), body, null, 0, 0, "appointment");
			 this.otherAnnouncements.add(a);
		 }
		// adapter.clear();
		 for(int i = 0; i < otherAnnouncements.size();  i++){
			
				values.add(i , otherAnnouncements.get(i).getTitle());
			}
		 adapter.notifyDataSetChanged();
		 
	}
	public void handleListView(){
		
		adapter.clear();
		for(int i = 0; i < announcements.size();  i++){
			values.add(0, announcements.get(i).getTitle());
			
		}
		 adapter.notifyDataSetChanged();
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		
		super.onActivityCreated(savedInstanceState);
		
		((MainActivity)getActivity()).getFrgt_title().setText("Last Updates");
		this.api = new APIMethods((MainActivity) getActivity());
		this.values = new ArrayList<String>();
		this.announcements = new ArrayList<ExappAnnouncement>();
		 adapter = new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_1, 
					android.R.id.text1, values);
			lv.setAdapter(adapter);
		lv.setOnItemClickListener(new OnItemClickListener() {

			

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				lv.setItemChecked(lv.getCheckedItemPosition(), false);
				Intent i = new Intent(getActivity(), ViewItemActivity.class);
				
				
				if(lv.getItemAtPosition(position).equals("Group Invitation")){
					i.putExtra("type", "announcement");
					i.putExtra("item", otherAnnouncements.get(position + announcements.size()));
					i.putExtra("group", aux.get(position));
					i.putExtra("hide", "false");
					i.putExtra("user", "null");
					i.putExtra("currentUser", ((MainActivity) getActivity()).getCurrent_user() );
				}else if(((String) lv.getItemAtPosition(position)).contains("New Appointment")){
					i.putExtra("item", otherAnnouncements.get(position - announcements.size() ) );
					i.putExtra("type", "announcement");
					i.putExtra("user", "null" );
					i.putExtra("hide", "true");
				}else{
					i.putExtra("item", announcements.get(position ));
					i.putExtra("type", "announcement");
					i.putExtra("user", "null" );
					i.putExtra("hide", "true");
				}
				
				startActivity(i);
			}
	    });

		if(((MainActivity) getActivity()).isRWTH()){
			api.execute(new String[]{"getAllAnnouncements", ((MainActivity) getActivity()).l2pUri, "viewAllAnnouncements?" 
					, ((MainActivity) getActivity()).getToken(), ((MainActivity) getActivity()).l2pCourseId});
		}else{
			this.loadExappAnnouncements();
		}
		
		
		
	}
	private class APIMethods extends AsyncTask<String, Void, String[]> {
		
		private ProgressDialog dialog;
       
        private MainActivity activity;
        private Context context;
        //private List<Message> messages;
        public APIMethods(MainActivity activity) {
            this.activity = activity;
            context = activity;
            dialog = new ProgressDialog(context);
        }
        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Loading your data");
            this.dialog.show();
        }
		
		
		
		
		HttpResponse response;
		
		
		@Override
		protected String[] doInBackground(String... params) {
			HttpClient httpclient = new DefaultHttpClient();

			String[] result = {};

			HttpGet get = new HttpGet(params[1]+ params[2] + "accessToken=" + params[3] + "&cid=" + params[4]);

			try {
				response = httpclient.execute(get);
				result = new String[] {
						inputStreamToString(
								response.getEntity().getContent())
								.toString(), params[0] };
			} catch (IOException e) {
				
				e.printStackTrace();
			}


			//Log.i("res", result[0]);
			return result;

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
				e.printStackTrace();
				Toast.makeText(getActivity(),
						"Error..." + e.toString(), Toast.LENGTH_LONG).show();
			}
			return answer;
		}

		@Override
		protected void onPostExecute(String[] result) {
			parseJSON(result[0], result[1]);
			if (dialog.isShowing()) {
                dialog.dismiss();
            }

		}

	}

	private void parseJSON(String jsonResult, String type) {
		Object result = new Object();
		try {

			JSONObject jsonResponse = new JSONObject(jsonResult);

			switch (type) {
			case "getAllAnnouncements":

				
				String status = jsonResponse.getString("Status");
				Log.i("status", status);
				if(status.equals("true")){
					JSONArray jsonMainNode = jsonResponse.optJSONArray("dataSet");
					for (int i = 0; i < jsonMainNode.length(); i++) {
						JSONObject item = jsonMainNode.getJSONObject(i);
						String title = item.getString("title");
						String att = item.getString("attachment");
						int id = item.getInt("itemId");
						int exp = item.getInt("expireTime");
						String body = item.getString("body");
						
						ExappAnnouncement a = new ExappAnnouncement(title, body, att, id, exp, "general");
						announcements.add(a);
						result = announcements;
					}
					
				}else{
					result = "error: " + jsonResponse.getString("errorDescription");
					this.error = true;
					
				}
				this.setAPIAnnouncements();

				break;

			}

		} catch (JSONException e) {
			Log.i("JSON error", "Error parsing");
			
		}

		this.dataFromAsyncTask = result;
		

	}

	private void getInfo() {
		Log.i("course", (String) this.dataFromAsyncTask);

	}

	@Override
	public void onPause() {
		
		super.onPause();
		
	}

	@Override
	public void onDestroy() {
		
		
		super.onDestroy();
		
		if(((MainActivity) getActivity()).isRWTH()){
			api.cancel(true);
		}
	}
	
	
}