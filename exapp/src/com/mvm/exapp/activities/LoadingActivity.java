package com.mvm.exapp.activities;

import com.mvm.exapp.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class LoadingActivity extends Activity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loading);
		
	}

	public  static LoadingActivity getInstance(){
		
		
		return new LoadingActivity();
		
	}
	
}
