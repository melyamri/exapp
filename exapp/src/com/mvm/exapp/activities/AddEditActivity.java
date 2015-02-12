package com.mvm.exapp.activities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mvm.exapp.R;
import com.mvm.exapp.externalDB.DBManager;
import com.mvm.exapp.fragments.DiscussionsFragment;
import com.mvm.exapp.model.ExappDiscussion;
import com.mvm.exapp.model.ExappUser;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AddEditActivity extends Activity {

	private Object dataFromAsyncTask;
	private APIMethods api;

	private Button save;
	private Button discard;

	private EditText title;
	private EditText body;

	private ExappDiscussion discussion;
	private ExappUser user;
	private String groupID;
	private String token;

	private String type;
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_edit);

		this.save = (Button) findViewById(R.id.btn_send);
		this.discard = (Button) findViewById(R.id.btn_discard);
		this.title = (EditText) findViewById(R.id.tv_item_title);
		this.body = (EditText) findViewById(R.id.text_input);
		this.handleButtons();
		this.api = new APIMethods(this);
		this.user = (ExappUser) getIntent().getSerializableExtra("user");
		this.token = getIntent().getStringExtra("token");
		this.type = getIntent().getStringExtra("type");
		this.groupID = getIntent().getStringExtra("group");

		//TODO get type 
		//		if(getIntent().getStringExtra("type").equals("edit")){
		//			if(){
		//				
		//			}
		//		}

		if(getIntent().getStringExtra("type").equals("edit")){
			this.discussion = (ExappDiscussion) getIntent().getSerializableExtra("item");
			this.type = "deleteDiscussion";
			this.loadDiscussion();
		}else{


			this.discussion = new ExappDiscussion();
			this.type = "postDiscussion";

		}




	}

	private void loadDiscussion() {
		this.title.setText(Html.fromHtml(this.discussion.getTitle()));
		Log.i("title", this.discussion.getTitle());
		this.body.setText(Html.fromHtml(this.discussion.getBody()));


	}
	public ExappDiscussion getDiscussion() {
		return discussion;
	}

	public void setDiscussion(ExappDiscussion discussion) {
		this.discussion = discussion;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public void addDiscussion(){
		this.discussion.setTitle(this.title.getText().toString());
		this.discussion.setBody(this.body.getText().toString());
		//this.discussion.setFrom(user.getUserID());
		this.discussion.setUserID(user.getUserID());
		long d = Calendar.getInstance().getTime().getTime();
		java.sql.Date date = new java.sql.Date(d);
		this.discussion.setDate(date );

	}
	public void handleButtons(){
		View.OnClickListener handle_save = new View.OnClickListener() {

			public void onClick(View v) {
				addDiscussion();
				if(groupID.equalsIgnoreCase("RWTH")){
					try {
						switch(getType()){
						case "postDiscussion":  api.execute(new String[]{ getType(),MainActivity.l2pUri, 
								"addDiscussionThread?", 
								getToken(), MainActivity.l2pCourseId, 
								getDiscussion().getTitle(), 
								getDiscussion().getBody()}).get();
						break;
						case "postReplyDiscussion": api.execute(new String[]{ getType(),MainActivity.l2pUri, 
								"addDiscussionThreadReply?", 
								getToken(), MainActivity.l2pCourseId, 
								getDiscussion().getTitle(), 
								getDiscussion().getBody(), getDiscussion().getReplyToId() + ""}).get();
						break;
						case "updateDiscussion": api.execute(new String[]{ getType(),MainActivity.l2pUri, 
								"updateDiscussionThread?", 
								getToken(), MainActivity.l2pCourseId, 
								getDiscussion().getTitle(), 
								getDiscussion().getBody(), getDiscussion().getSelfId() + ""}).get();
						break;
						case "updateReplyDiscussion": api.execute(new String[]{ getType(),MainActivity.l2pUri, 
								"updateDiscussionThreadReply?", 
								getToken(), MainActivity.l2pCourseId, 
								getDiscussion().getTitle(), 
								getDiscussion().getBody(), getDiscussion().getSelfId() + ""}).get();
						break;

						}

					} catch (InterruptedException | ExecutionException e) {

						e.printStackTrace();
					}
					
				}else{
					try {
						if(!(boolean) DBManager.getInstance(getActivity()).execute(new Object[]{"insertDiscussion", groupID, getDiscussion()}).get()){
							Toast.makeText(getApplicationContext(), "Error when creating thread", Toast.LENGTH_LONG).show();
						}else{
							finish(); 
						}
					} catch (InterruptedException | ExecutionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};

		this.save.setOnClickListener(handle_save);
		View.OnClickListener handle_discard = new View.OnClickListener() {

			public void onClick(View v) {
				finish();
			}	
		};

		this.discard.setOnClickListener(handle_discard);

	}
	private class APIMethods extends AsyncTask<String, Void, String[]> {
		private ProgressDialog dialog;

		private AddEditActivity activity;
		private Context context;
		//private List<Message> messages;
		public APIMethods(AddEditActivity activity) {
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
			HttpGet get = new HttpGet();//new HttpPost(params[1] + params[2]);
			String[] result = {};
			switch(params[0]){

			case "postDiscussion":
				get = new HttpGet(params[1]+ params[2] + "accessToken=" + params[3] + "&cid=" + params[4] + "&title=" + URLEncoder.encode(params[5]) + "&contents=" + URLEncoder.encode(params[6]) );


				break;
			case "updateDiscussion":
				get = new HttpGet(params[1]+ params[2] + "accessToken=" + params[3] + "&cid=" + params[4] + "&title=" + URLEncoder.encode(params[5]) + "&contents=" + URLEncoder.encode(params[6]) + "&selfid=" + params[7] );

				break;
			case "deleteDiscussion":
				get = new HttpGet(params[1]+ params[2] + "accessToken=" + params[3] + "&cid=" + params[4] + "&selfid=" + params[5] );

			case "postReplyDiscussion":
				get = new HttpGet(params[1]+ params[2] + "accessToken=" + params[3] + "&cid=" + params[4] + "&title=" + URLEncoder.encode(params[5]) + "&contents=" + URLEncoder.encode(params[6])  + "&replyToId=" + params[7]);


				break;
			case "updateReplyDiscussion":
				get = new HttpGet(params[1]+ params[2] + "accessToken=" + params[3] + "&cid=" + params[4] + "&title=" + URLEncoder.encode(params[5]) + "&contents=" + URLEncoder.encode(params[6]) + "&itemid=" + params[7] );


				break;

			}


			try {
				response = httpclient.execute(get);
				result = new String[] {
						inputStreamToString(
								response.getEntity().getContent())
								.toString(), params[0] };
			} catch (IOException e) {

				e.printStackTrace();
			}
			Log.i("discussion_res", result[0]);
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

			case "updateDiscussion":
			case "updateReplyDiscussion":
			case "deleteDiscussion":
			case "postReplyDiscussion":
			case "postDiscussion":
				String st = jsonResponse.getString("Status");
				Log.i("status", st);
				if(st.equals("true")){
					DiscussionsFragment.error = false;

					Toast.makeText(this, "Discussion was succesfully added", Toast.LENGTH_LONG);
					finish();
				}else{
					result = "error: " + jsonResponse.getString("errorDescription");
					DiscussionsFragment.error = true;
				}
			}

		} catch (JSONException e) {
			Log.i("JSON error", "Error parsing discussion");
		}

		this.dataFromAsyncTask = result;
		
		//this.getAllDiscussions();

	}



	public void onPause() {

		super.onPause();

	}
	public Activity getActivity(){
		return this;
	}
	@Override
	public void onDestroy() {


		super.onDestroy();



	}

}
