package com.mvm.exapp.activities;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Date;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

import com.mvm.exapp.R;
import com.mvm.exapp.externalDB.DBManager;
import com.mvm.exapp.model.ExappUser;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class RegisterActivity extends Activity{
	private EditText userID;
	private EditText userName;
	private EditText password;
	private EditText passwordRepeat;
	private DatePicker date;
	private ImageButton addImg;
	private ImageView img;
	private EditText aboutUser;
	private Bitmap b;
	private ExappUser user;
	private boolean editingProfile;
	private Button register;
	private Button back;

	private Bitmap mBitmap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);

		this.userID = (EditText) findViewById(R.id.et_username);
		this.userName = (EditText) findViewById(R.id.et_complete_name);
		this.password = (EditText) findViewById(R.id.et_password);
		this.passwordRepeat = (EditText) findViewById(R.id.et_password_repeat);
		this.date = (DatePicker) findViewById(R.id.birth);
		this.addImg = (ImageButton) findViewById(R.id.btn_add_picture);
		this.img = (ImageView) findViewById(R.id.img_profile);
		this.aboutUser = (EditText) findViewById(R.id.about_user);
		this.register = (Button) findViewById(R.id.btn_register);
		this.back = (Button) findViewById(R.id.btn_back);

		this.user = (ExappUser) getIntent().getSerializableExtra("user");
		if(this.user != null){
			this.loadUserData();
			this.editingProfile = true;
		}else{
			this.editingProfile = false;
		}
		this.handleButtons();
		this.handleImage();


	}

	private void loadUserData() {
		this.userID.setText(user.getUserID());
		this.userID.setEnabled(false);
		this.userName.setText(user.getName());

		java.util.Date d = new java.util.Date(this.user.getBirthday().getTime());
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		this.date.init(c.get(Calendar.YEAR), 
				c.get(Calendar.MONTH), 
				c.get(Calendar.DAY_OF_MONTH), null);

		this.aboutUser.setText(user.getDescription());
		Bitmap b = null;
		
		try {
			b = (Bitmap) DBManager.getInstance(getActivity()).execute(new Object[]{"getPicture", user.getImageID()}).get();
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.img.setImageBitmap(b);
		
	}

	private void handleImage(){
		View.OnClickListener handle_addimage = new View.OnClickListener() {

			public void onClick(View v) {
				Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
				photoPickerIntent.setType("image/*");
				startActivityForResult(photoPickerIntent, 322);



			}

		};

		addImg.setOnClickListener(handle_addimage);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 322 && resultCode == RESULT_OK)
		{
			Uri chosenImageUri = data.getData();

			mBitmap = null;
			try {
				mBitmap = Media.getBitmap(this.getContentResolver(), chosenImageUri);
			} catch (IOException e) {

				e.printStackTrace();
			}

			this.img.setImageBitmap(mBitmap);
		}
	}
	private boolean loadUser(){
		boolean success = true;
		String user = userID.getText().toString();
		if(!editingProfile){
			try {
				if((boolean) DBManager.getInstance(getActivity()).execute(new Object[]{"checkUserExists", user}).get()){
	
					Toast.makeText(getApplicationContext(),"Username already taken, try another", Toast.LENGTH_LONG).show();
					success = false;
				}else{
					String p1 = this.password.getText().toString();
					String p2 = this.passwordRepeat.getText().toString();
					if(!p1.equals(p2)) success = false;
					Date d = new Date(date.getYear() - 1900, date.getMonth(), date.getDayOfMonth());
					String name = this.userName.getText().toString();
					String description = this.aboutUser.getText().toString();
					 b = ((BitmapDrawable) this.img.getDrawable()).getBitmap();
					
					
					this.user = new ExappUser(user, p1, name, d,0,false, description);
				}
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}else{
			String p1 = this.password.getText().toString();
			String p2 = this.passwordRepeat.getText().toString();
			if(!p1.equals(p2)) success = false;
			if(p1.equals("")) p1 = this.user.getPassword();
			Date d = new Date(date.getYear() - 1900, date.getMonth(), date.getDayOfMonth());
			String name = this.userName.getText().toString();
			String description = this.aboutUser.getText().toString();
			 b = ((BitmapDrawable) this.img.getDrawable()).getBitmap();
			 this.user = new ExappUser(user, p1, name, d,this.user.getImageID(),false, description);
		}



		return success;
	}

	private void handleButtons(){
		View.OnClickListener handle_reg = new View.OnClickListener() {

			public void onClick(View v) {
				boolean doo = loadUser();
				if(doo){

					ExappUser u = null;
					try {
						u = (ExappUser) DBManager.getInstance(getActivity()).execute(new Object[]{"insertUser", user, true, b}).get();
					} catch (InterruptedException | ExecutionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(u != null){
						Toast.makeText(getApplicationContext(), "You were registered succesfully", Toast.LENGTH_LONG).show();
						finish();
					}
					

				}else{
					Toast.makeText(getApplicationContext(), "Error when registering", Toast.LENGTH_LONG).show();
				}
			}

		};

		register.setOnClickListener(handle_reg);

		View.OnClickListener handle_back = new View.OnClickListener() {

			public void onClick(View v) {


				finish();
			}

		};

		back.setOnClickListener(handle_back);
	}

	public Activity getActivity(){
		return this;
	}

}
