package com.mvm.exapp.fragments;

import com.mvm.exapp.R;
import com.mvm.exapp.R.id;
import com.mvm.exapp.R.layout;
import com.mvm.exapp.activities.MainActivity;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class GroupsFragment extends Fragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.groups, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
		//Deactivate bottom bar
		((MainActivity)getActivity()).getBtn_add().setEnabled(false);
		((MainActivity)getActivity()).getBtn_delete().setEnabled(false);
		((MainActivity)getActivity()).getBtn_edit().setEnabled(false);
		((MainActivity)getActivity()).getFrgt_title().setText("Group Workspace");
		
		this.handleButtons();
		  
		 
	}
	public void handleButtons(){
		 Button b1;
		  Button b2;
		  Button b3;
		 
		    
		    b1 = (Button) this.getActivity().findViewById(R.id.btn_discussions);
		    b2 = (Button) this.getActivity().findViewById(R.id.btn_appointments);
		    b3 = (Button) this.getActivity().findViewById(R.id.btn_docs);
		    
		  
		    
		  
		  View.OnClickListener myhandler1 = new View.OnClickListener() {
		    public void onClick(View v) {
		      // it was the 1st button
		    	((MainActivity) getActivity()).getTabs().setCurrentTab(1);
		    	((MainActivity) getActivity()).changeFragment(1);
		    }
		  };
		  View.OnClickListener myhandler2 = new View.OnClickListener() {
		    public void onClick(View v) {
		      // it was the 2nd button
		    	((MainActivity) getActivity()).changeFragment(3);
		    }
		  };
		  View.OnClickListener myhandler3 = new View.OnClickListener() {
			    public void onClick(View v) {
			      // it was the 2nd button
			    	((MainActivity) getActivity()).changeFragment(4);
			    }
			  };
		  
		  b1.setOnClickListener(myhandler1);
		    b2.setOnClickListener(myhandler2);
		    b3.setOnClickListener(myhandler3);
	}
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
}
