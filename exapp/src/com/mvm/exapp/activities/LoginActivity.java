package com.mvm.exapp.activities;
import com.mvm.exapp.externalDB.*;

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
import com.mvm.exapp.model.ExappUser;





import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {
	/**
	 * The default email to populate the email field with.
	 */
	public ExappUser currentuser;

	// Values for email and password at the time of the login attempt.
	private String mUser;
	private String mPassword;

	// UI references.
	private EditText mUserView;
	private EditText mPasswordView;

	
	private Button mSignIn;
	private Button mRegister;

	
	private String access_token;
	
	private String user_code;

	private boolean check_token;

	private String device_code;

	private String refresh_token;

	private CheckBox isRWTHView;
	

	private class OauthTask extends AsyncTask<String, Void, String[]> {
		HttpResponse response;

		@Override
		protected String[] doInBackground(String... params) {
			HttpClient httpclient = new DefaultHttpClient();
			String[] result = {};
			HttpPost httppost = new HttpPost(params[1]);
			try {
				// Add your data
				switch (params[0]) {
				case "getCode":

					List<NameValuePair> pairs_get_code = new ArrayList<NameValuePair>(
							2);
					pairs_get_code.add(new BasicNameValuePair("client_id",
							params[2]));
					pairs_get_code.add(new BasicNameValuePair("scope",
							params[3]));
					httppost.setEntity(new UrlEncodedFormEntity(pairs_get_code));
					break;
				case "getToken":
				
					
					List<NameValuePair> pairs_get_token = new ArrayList<NameValuePair>(
							3);
					pairs_get_token.add(new BasicNameValuePair("client_id",
							params[2]));
					pairs_get_token.add(new BasicNameValuePair("code",
							device_code));

					pairs_get_token.add(new BasicNameValuePair("grant_type",
							"device"));
					httppost.setEntity(new UrlEncodedFormEntity(pairs_get_token));
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
				
					break;

				}
				if (!params[0].equals("getToken")) {
					response = httpclient.execute(httppost);
					result = new String[] {
							inputStreamToString(
									response.getEntity().getContent())
									.toString(), params[0] };
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
	}// end async task

	public void parseJSON(String jsonResult, String type) {
		

		try {
			JSONObject jsonResponse = new JSONObject(jsonResult);
			switch (type) {
			case "getCode":
				String stat1 = (String) jsonResponse.get("status");
				String device_code = (String) jsonResponse.get("device_code");
				String user_code = (String) jsonResponse.get("user_code");
				String verification_url = (String) jsonResponse
						.get("verification_url");
				String expires_in = new String(""
						+ jsonResponse.getInt("expires_in"));
				String interval = new String(""
						+ jsonResponse.getInt("interval"));
				this.user_code = user_code;
				this.device_code = device_code;
				accessWebService(new String[] { "openBrowser", stat1,
						device_code, user_code, verification_url, expires_in,
						interval });
				break;

			case "getToken":
				String stat2 = (String) jsonResponse.get("status");
				if (stat2.equals("ok")) {
					String token = (String) jsonResponse.get("access_token");
					String token_type = (String) jsonResponse.get("token_type");
					String expires = new String(""
							+ jsonResponse.getInt("expires_in"));
					String refresh_token = new String(jsonResponse.getString("refresh_token"));
					this.access_token = token;
					this.refresh_token = refresh_token;
					accessWebService(new String[] { "callApi", stat2, token,
							token_type, expires, refresh_token });
					break;
				} else {
					accessWebService(new String[] { "callApi", stat2 });
				}
			}

		} catch (JSONException e) {
			Toast.makeText(getApplicationContext(), "Error" + e.toString(),
					Toast.LENGTH_LONG).show();
		}

	}

	public void accessWebService(String[] args) {
		String oAuthUri = new String(
				"https://oauth.campus.rwth-aachen.de/oauth2waitress/oauth2.svc/");
		String codeEndpoint = new String(oAuthUri + "code?");
		String tokenEndpoint = new String(oAuthUri + "token?");
		String tokenInfoEndpoint = new String(oAuthUri + "tokeninfo?");


		String clientId1 = "kzaEa04S2f9S85o8BFZadFrZZFF4ptN5TbYNPDk06E8o9OAJHLf4pyIUdsli8Zkn.apps.rwth-aachen.de";
		String scopes = "l2p2013.rwth";

		// Type of request
		String type = args[0];

		// Prepare a request object

		OauthTask task = new OauthTask();
		// passes values for the urls string array
		switch (type) {
		case "getCode":
			task.execute(new String[] { type, codeEndpoint, clientId1, scopes });
			break;
		case "getToken":
			Log.i("deviceCode", this.device_code);
			task.execute(new String[] { type, tokenEndpoint, clientId1,
					this.device_code });

			break;
		case "openBrowser":
			this.check_token = true;
			Intent browserIntent = new Intent(Intent.ACTION_VIEW,
					Uri.parse(args[4] + "/?q=verify&d=" + this.user_code));
			startActivity(browserIntent);

			break;
		case "callApi":
			
			Intent i = new Intent(getActivity(), MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.putExtra("token", access_token); 
            i.putExtra("refresh", refresh_token);
            i.putExtra("user", currentuser);
            i.putExtra("isRWTH", "true");
           
            startActivity(i);
            getActivity().finish();
            
            break;
		

		}

	}

	@Override
	protected void onResume() {

		super.onResume();
		if (this.check_token) {
			accessWebService(new String[] { "getToken",
					"error: authorization pending" });
			this.check_token = false;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		 try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException e1) {
			
			
		}
		
		this.mSignIn = (Button) findViewById(R.id.sign_in_button);
		this.mRegister = (Button) findViewById(R.id.register_button);
		this.mUserView = (EditText) findViewById(R.id.user);
		this.mPasswordView = (EditText) findViewById(R.id.password);
		this.isRWTHView = (CheckBox) findViewById(R.id.cb_isRWTH);
		this.handleButtons();
		
		
		
}
	private void handleButtons(){
		View.OnClickListener handle_sign = new View.OnClickListener() {
	   		 
		    public void onClick(View v) {
		    	mUser = mUserView.getText().toString();
		    	mPassword = mPasswordView.getText().toString();
		    	boolean isRWTH = isRWTHView.isChecked();
		    	
		    	
				try {
					currentuser = (ExappUser)DBManager.getInstance(getActivity()).execute(new Object[]{"login", mUser, mPassword}).get();
				} catch (InterruptedException | ExecutionException e) {
					
					e.printStackTrace();
				}
			
		    	if(currentuser != null){
		    		
		    		Log.i("username", currentuser.getName());
		    		Log.i("rwth", isRWTH + "");
		    		if(isRWTH){
		    			accessWebService(new String[] { "getCode" });
		    		}else{
			    	Intent i = new Intent(getActivity(), MainActivity.class);
		               i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		               i.putExtra("token", access_token); 
		               i.putExtra("refresh", refresh_token);
		               i.putExtra("user", currentuser);
		               i.putExtra("isRWTH", "false");
		              
		               startActivity(i);
		               getActivity().finish();
		    		}
			    		
		    	}else{
		    		Toast.makeText(getApplicationContext(), "Wrong password, try again", Toast.LENGTH_SHORT).show();
		    	}
			    		
			    	}
		    
		  };
		 
		  mSignIn.setOnClickListener(handle_sign);
		  
		  View.OnClickListener handle_register = new View.OnClickListener() {
		   		 
			    public void onClick(View v) {

				    	Intent i = new Intent(getActivity(), RegisterActivity.class); 
			    	//Intent i = new Intent(getActivity(), StatisticsGlobalActivity.class); 
			               startActivity(i);
			              // getActivity().finish();
			    		}
		
			  };
			 
			  mRegister.setOnClickListener(handle_register);
	}
	private Activity getActivity() {
		
		return this;
	}
}
