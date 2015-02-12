package com.mvm.exapp.fragments;
import com.mvm.exapp.externalDB.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mvm.exapp.activities.MainActivity;
import com.mvm.exapp.activities.ViewGroupActivity;
import com.mvm.exapp.activities.ViewItemActivity;
import com.mvm.exapp.model.ExappAnnouncement;
import com.mvm.exapp.model.ExappGroup;
import com.mvm.exapp.model.ExappUser;




import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class GroupListFragment extends ListFragment{
	private ArrayList<ExappGroup> groups;
	private ArrayList<ExappGroup> rwthGroups;
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		
		super.onActivityCreated(savedInstanceState);
		((MainActivity)getActivity()).getFrgt_title().setText("Your Groups");
		this.rwthGroups = new ArrayList<ExappGroup>();
		APIMethods api = new APIMethods((MainActivity) getActivity());
		api.execute(new String[]{"getAllGroups", MainActivity.l2pUri, "viewMyGroupWorkspace?", ((MainActivity) getActivity()).getToken(), MainActivity.l2pCourseId});
		//Reactivate bottom bar
				((MainActivity)getActivity()).getBtn_add().setEnabled(true);
				((MainActivity)getActivity()).getBtn_delete().setEnabled(true);
				((MainActivity)getActivity()).getBtn_edit().setEnabled(true);
				
		this.getAllGroups(((MainActivity) getActivity()).getCurrent_user());
		
		lv.setOnItemClickListener(new OnItemClickListener() {

			

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//Toast.makeText(getActivity(), "onitem", Toast.LENGTH_LONG).show();
				lv.setItemChecked(lv.getCheckedItemPosition(), false);
				Intent i = new Intent(getActivity(), ViewGroupActivity.class);
				i.putExtra("exappGroup", groups.get(position) );
				i.putExtra("user", ((MainActivity) getActivity()).getCurrent_user());
				i.putExtra("type", "group");
				i.putExtra("hide", "true");
				startActivity(i);
			}
	    });

	}
	
	@SuppressWarnings("unchecked")
	public void getAllGroups(ExappUser user){
		
		
		try {
			
			
			String id =  user.getUserID();
			groups = (ArrayList<ExappGroup>)DBManager.getInstance(getActivity()).execute(new String[]{"getAllGroups", id}).get();
		} catch (InterruptedException | ExecutionException e) {
			
			e.printStackTrace();
		}
		groups.addAll(rwthGroups);
		for(int i = 0; i < groups.size(); i++){
			values.add(groups.get(i).getGroupID());
//			Log.i("groups", groups.get(i).getGroupID());
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, 
				android.R.id.text1, values);
		lv.setAdapter(adapter);
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
			case "getAllGroups":

				
				String status = jsonResponse.getString("Status");
				Log.i("status", status);
				if(status.equals("true")){
					JSONArray jsonMainNode = jsonResponse.optJSONArray("listOfGWSs");
					for (int i = 0; i < jsonMainNode.length(); i++) {
						JSONObject item = jsonMainNode.getJSONObject(i);
						String id = item.getString("name");
						String description = item.getString("description");
						
						JSONArray array = item.optJSONArray("members");
						ArrayList<String> members = new ArrayList<String>();
						for(int j = 0; j < array.length(); j++){
							members.add(array.getString(j));
						}
						
						ExappGroup a = new ExappGroup(id, null, "RWTH group", description);
						a.setType(1);
						a.setMembers(members);
						rwthGroups.add(a);
						result = rwthGroups;
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
		

	}


public void onResume(){
	super.onResume();
	//this.getAllGroups(((MainActivity) getActivity()).getCurrent_user());
}

}
