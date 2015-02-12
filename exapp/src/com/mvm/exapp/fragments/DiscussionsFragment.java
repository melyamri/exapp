package com.mvm.exapp.fragments;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mvm.exapp.R;
import com.mvm.exapp.activities.MainActivity;
import com.mvm.exapp.activities.ViewItemActivity;
import com.mvm.exapp.externalDB.DBManager;
import com.mvm.exapp.model.ExappAnnouncement;
import com.mvm.exapp.model.ExappDiscussion;
import com.mvm.exapp.model.ExappDiscussionBoard;
import com.mvm.exapp.model.ExappGroup;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

public class DiscussionsFragment extends Fragment{
	private APIMethods api;
	private ArrayList<ExappDiscussion> discussions;
	private ExappDiscussionBoard groupDiscussions;
	private ListView lv;
	public static boolean error;
	public static Object dataFromAsyncTask;
	private ArrayList<String> values;
	private AutoCompleteTextView group;
	private Button go;
	
	
	private ArrayAdapter adapter;
	public ListView getLv() {
		return lv;
	}

	public ArrayList<ExappDiscussion> getDiscussions() {
		return discussions;
	}

	public void setDiscussions(ArrayList<ExappDiscussion> discussions) {
		this.discussions = discussions;
	}

	public void setLv(ListView lv) {
		this.lv = lv;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		


	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		return inflater.inflate(R.layout.discussions, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);
		((MainActivity)getActivity()).getFrgt_title().setText("Shared Domain");
		this.lv = (ListView) getActivity().findViewById(R.id.lv_group_discussions);
		this.group = (AutoCompleteTextView) getActivity().findViewById(R.id.ac_group_discussions);
		this.go = (Button)getActivity().findViewById(R.id.btn_search_discussion);
		this.values = new ArrayList<String>();
		this.discussions = new ArrayList<ExappDiscussion>();
		adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, 
				android.R.id.text1, values);
		lv.setAdapter(adapter);
		//TODO make item checked
		lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		lv.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent,
					View view, int position, long id) {
					lv.setItemChecked(lv.getCheckedItemPosition(), false);
					lv.setItemChecked(position, true);
					
				return true;
			}
	    });
		lv.setOnItemClickListener(new OnItemClickListener() {



			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				lv.setItemChecked(lv.getCheckedItemPosition(), false);
				
				Intent i = new Intent(getActivity(), ViewItemActivity.class);
				
					i.putExtra("item", discussions.get(position) );
					i.putExtra("user", discussions.get(position).getUserID());
					
					i.putExtra("type", "discussion");
					i.putExtra("hide", "true");
					startActivity(i);
				
			}
		});
		
		
		
		this.handleAutoComplete();
		this.handleButton();
	}
	private void handleAutoComplete(){
		ArrayList<String> groups = new ArrayList<String>();
		ArrayList<ExappGroup> g = null;
		try {
			g = (ArrayList<ExappGroup>) DBManager.getInstance(getActivity()).execute(new String[]{"getAllGroups", ((MainActivity) getActivity()).getCurrent_user().getUserID()}).get();
		} catch (InterruptedException | ExecutionException e) {
			
			e.printStackTrace();
		}
		for(int i = 0; i < g.size(); i++){
			groups.add(g.get(i).getGroupID());
		}
		if(((MainActivity) getActivity()).isRWTH()){
			groups.add("RWTH");
		}
		@SuppressWarnings("unchecked")
		ArrayAdapter a = new ArrayAdapter(getActivity(),android.R.layout.simple_dropdown_item_1line,groups);
				   this.group.setAdapter(a);
			   
				  
	}
	private void loadDiscussions(String groupID){
		if(groupID.equalsIgnoreCase("rwth")){
			this.api = new APIMethods((MainActivity) getActivity());
			
			api.execute(new String[]{"getAllDiscussions", ((MainActivity) getActivity()).l2pUri, 
					"viewAllDiscussionItems?", ((MainActivity) getActivity()).getToken(), ((MainActivity) getActivity()).l2pCourseId});
		}else{
		this.groupDiscussions = null;
		try {
			this.groupDiscussions = (ExappDiscussionBoard) DBManager.getInstance(getActivity()).execute(new Object[]{"getExappDiscussion", groupID}).get();
		} catch (InterruptedException | ExecutionException e) {
			
			e.printStackTrace();
			
		}
		Log.i("discussions", groupDiscussions.getList().size() + "");
		this.discussions = groupDiscussions.getList();
		
			
		}
	}
	private void handleButton(){
		View.OnClickListener handle_go = new View.OnClickListener() {
			public void onClick(View v) {
				loadDiscussions(group.getText().toString());	
				handleListView();
			}
		};

		this.go.setOnClickListener(handle_go);

	}
	private void handleListView(){
		//this.loadDiscussions();
		adapter.clear();
		values = new ArrayList<String>();
			for(int i = 0; i < discussions.size(); i++){
				values.add(discussions.get(i).getTitle());
			}
		adapter.addAll(values);
		adapter.notifyDataSetChanged();
		
	}
	public ExappDiscussionBoard getGroupDiscussions() {
		return groupDiscussions;
	}

	public AutoCompleteTextView getGroup() {
		return group;
	}

	public void setGroup(AutoCompleteTextView group) {
		this.group = group;
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
			switch(params[0]){
			case "getAllDiscussions":
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
				break;
			
			
			}



			Log.i("res", result[0]);
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
				// e.printStackTrace();
				//Toast.makeText(getApplicationContext(),
				//		"Error..." + e.toString(), Toast.LENGTH_LONG).show();
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
			case "getAllDiscussions":

				this.discussions = new ArrayList<ExappDiscussion>();
				String status = jsonResponse.getString("Status");
				Log.i("status", status);
				if(status.equals("true")){
					JSONArray jsonMainNode = jsonResponse.optJSONArray("listOfDiscussionThreadAndReplies");
					for (int i = 0; i < jsonMainNode.length(); i++) {
						JSONObject item = jsonMainNode.getJSONObject(i);
						String title = item.getString("title");
						String body = item.getString("body");
						String from = item.getString("from");
						int selfId = item.getInt("selfId");
						int modifiedTimeStamp = item.getInt("modifiedTimestamp");

						int replyToId = item.getInt("replyToId");
						int parentDiscussionId = item.getInt("parentDiscussionId");

						if(title == "null"){
							for(int k = 0; k < discussions.size(); k++){
								if(replyToId == discussions.get(k).getSelfId()){
									title = "Re: " + discussions.get(k).getTitle();
								}
							}
						}
						ExappDiscussion d = new ExappDiscussion(title, body, from, modifiedTimeStamp, selfId, replyToId);
						discussions.add(0, d);
						result = discussions;
					}
				}else{
					result = "error: " + jsonResponse.getString("errorDescription");
					this.error = true;
				}

				break;
			}

		} catch (JSONException e) {
			Log.i("JSON error", "Error parsing");
		}

		this.dataFromAsyncTask = result;
		this.handleListView();

	}

	

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