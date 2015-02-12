package com.mvm.exapp.fragments;

import java.util.ArrayList;
import java.util.HashMap;

import com.mvm.exapp.R;
import com.mvm.exapp.R.id;
import com.mvm.exapp.R.layout;
import com.mvm.exapp.activities.ViewItemActivity;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.TextView;

public class ListFragment extends Fragment {
	ArrayList<String> values;
	ListView lv;
	public static Object dataFromAsyncTask;
	public static boolean error;
	
	public ArrayList<String> getValues() {
		return values;
	}

	public void setValues(ArrayList<String> values) {
		this.values = values;
	}

	public ListView getLv() {
		return lv;
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
		
		return inflater.inflate(R.layout.simple_list, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		
		super.onActivityCreated(savedInstanceState);
		
		
		this.lv = (ListView)getActivity().findViewById(R.id.list_updates);
		lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		values = new ArrayList<String>();

			    lv.setLongClickable(true);
			    
			    lv.setOnItemLongClickListener(new OnItemLongClickListener() {
					@Override
					public boolean onItemLongClick(AdapterView<?> parent,
							View view, int position, long id) {
							lv.setItemChecked(lv.getCheckedItemPosition(), false);
							lv.setItemChecked(position, true);
							
						return true;
					}
			    });
			    

	}

	@Override
	public void onResume() {
		
		super.onResume();
	}

	@Override
	public void onPause() {
	
		super.onPause();
	}

	@Override
	public void onDestroy() {
		
		super.onDestroy();
	}
	
}
