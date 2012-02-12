package com.clicker.admin;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.EditText;
import android.widget.ToggleButton;
import android.widget.TextView;
import android.widget.ViewFlipper;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;
import android.widget.SeekBar;
import android.widget.ListView;
import android.app.Dialog;

import java.net.Socket;
import java.net.InetAddress;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Map;
import java.util.Arrays;
import java.util.Iterator;
import java.util.HashMap;
import java.util.concurrent.Semaphore;
import java.util.ArrayList;

public class ClassManagement extends Activity implements OnClickListener, OnItemClickListener {
	
	private Map<String, ArrayList<String>> groupMembers;
	private ArrayList<ListView> groupLists;
	private Handler activityHandler;
	private AdminApplication myApp;
	//private Socket serverSocket;
	//private BufferedReader in;
	//private PrintWriter out;
	private int groupCount;
	private EditText addGroupEditText;
	private LinearLayout groupScroll;
	private Dialog addgroup_dialog;
	private Dialog moveclient_dialog;
	private Dialog rgroup_method_dialog;
	private Dialog rgroup_size_dialog;
	private String currentGroup;
	private String currentClient;
	private ClassListAdapter adapter;
	//private ProgressDialog reconnectingDialog;
	
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.classmanagement);
		
		myApp = ((AdminApplication)getApplication());
		//serverSocket = myApp.getSocket();
		/*
		try {
			in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
			out = new PrintWriter(serverSocket.getOutputStream(), true);
		} catch (IOException e) {}
		*/
		groupMembers = Collections.synchronizedMap(new HashMap<String, ArrayList<String>>(50));
		groupCount = 0;
		addGroupEditText = null;
		groupScroll = null;
		addgroup_dialog = null;
		moveclient_dialog = null;
		rgroup_method_dialog = null;
		rgroup_size_dialog = null;
		
		Button addGroupButton = (Button) findViewById(R.id.addgroup_button);
		Button randomGroupButton = (Button) findViewById(R.id.randomgroup_button);
		Button saveGroupButton = (Button) findViewById(R.id.savegroup_button);
		
		
		addGroupButton.setOnClickListener(this);
		randomGroupButton.setOnClickListener(this);
		saveGroupButton.setOnClickListener(this);
		
		activityHandler = new Handler() {
        	public void handleMessage(android.os.Message msg) {
        		switch (msg.what){
        		case AdminApplication.GOT_DISCONNECTED:
        			Log.d("RECONNECT", "Showing dialog then calling reconnect");
        			myApp.reconnect(getThis());
        			break;
        		case AdminApplication.RECONNECT_SUCCESS:
        			Log.d("RECONNECT", "Got reconnect Success");
        			Toast.makeText(getApplicationContext(), "Reconnected to server", Toast.LENGTH_SHORT).show();
        			break;
        		case AdminApplication.RECONNECT_FAILED:
        			Log.d("RECONNECT", "Got reconnect Failed");
        			Toast.makeText(getApplicationContext(), "Failed to reconnect to server", Toast.LENGTH_SHORT).show();
        			finish();
        			break;
        		case AdminApplication.CLIENT_LIST_RECEIVED:
        			String setString = (String)msg.obj;
        			String[] groupStrings = setString.split("`/&");
        			for (int i=0; i<groupStrings.length; i++) {
        				String[] groupParts = groupStrings[i].split("`/;");
        				ArrayList<String> newGroupListing = new ArrayList<String>();
        				if (groupParts.length > 1) {
        					String[] newMembers = groupParts[1].split("`/,");
        					Collections.addAll(newGroupListing, newMembers);
        				}
    					groupMembers.put(groupParts[0], newGroupListing);
    					groupCount++;
        			}
        			redrawGroups();
        			adapter.notifyDataSetChanged();
        			break;
        		}
        	}
		};
		myApp.setSubHandler(activityHandler);
		//reconnectingDialog = null;
	}
	
	
	/*
	public void redrawGroups() {
		groupLists.clear();
		Iterator<Map.Entry<String, ArrayList<String>>> iter = groupMembers.entrySet().iterator();
		int gCount = 1;
		fullGroupLL = (LinearLayout) findViewById(R.id.groups_ll);
		fullGroupLL.removeAllViews();
		while (iter.hasNext()) {
			Map.Entry<String, ArrayList<String>> next = iter.next();
			Log.d("GROUPING", "Redrawing group " + next.getKey());
			Button b = new Button(getThis());
			b.setText(next.getKey());
			b.setId((85 * gCount) + 236);
			b.setOnClickListener(getThis());
			ListView groupLV = new ListView(getThis());
			groupLV.setAdapter(new ArrayAdapter<String>(getThis(), R.layout.grouplistitem, next.getValue()));
			groupLV.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			groupLV.setTextFilterEnabled(true);
			groupLV.setOnItemClickListener(getThis());
			groupLV.setVisibility(ListView.GONE);
			groupLists.add(groupLV);
			
			fullGroupLL.addView(b);
			fullGroupLL.addView(groupLV);
			
			gCount++;
			
		}
	}
	*/
	
	
	public void redrawGroups() {
		ExpandableListView listView = new ExpandableListView(this);
        
        listView.setOnChildClickListener(new OnChildClickListener()
        {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id)
            {
            	//Create dialog for selecting new group
            	moveclient_dialog = new Dialog(getThis());
    			moveclient_dialog.setContentView(R.layout.moveclientdialog);
    			LinearLayout moveClientLL = (LinearLayout) moveclient_dialog.findViewById(R.id.moveclientll);
    			moveClientLL.removeAllViews();
    			moveclient_dialog.setTitle("Switch groups");
    			moveclient_dialog.setCancelable(true);
    			
    			//Setup currentGroup, currentClient, and build list of valid groups to switch to
    			ListView moveGroupLV = new ListView(getThis());
    			ArrayList<String> moveableGroups = adapter.getOtherGroups(groupPosition);
    			currentGroup = adapter.getGroup(groupPosition).toString();
    			currentClient = adapter.getChild(groupPosition, childPosition).toString();
    			
    			//Add components to dialog and show
    			moveGroupLV.setAdapter(new ArrayAdapter<String>(getThis(), R.layout.grouplistitem, moveableGroups));
    			moveGroupLV.setTextFilterEnabled(true);
    			moveGroupLV.setOnItemClickListener(getThis());
    			moveClientLL.addView(moveGroupLV);
    			moveclient_dialog.show();
    			
    			return false;
            }
        });
		
		
		
		listView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		adapter = new ClassListAdapter(this, new ArrayList<String>(),
                new ArrayList<ArrayList<String>>());
		listView.setAdapter(adapter);
		Iterator<Map.Entry<String, ArrayList<String>>> iter = groupMembers.entrySet().iterator();
		int gCount = 1;
		
		groupScroll = (LinearLayout) findViewById(R.id.group_scroll_ll);
	    groupScroll.removeAllViews();
		while (iter.hasNext()) {
			Map.Entry<String, ArrayList<String>> next = iter.next();
			Log.d("GROUPING", "Redrawing group " + next.getKey());
			String groupName = next.getKey();
			adapter.addGroup(groupName);
			ArrayList<String> groupMembers = next.getValue();
			for (int i=0; i<groupMembers.size(); i++) {
				Log.d("GROUPING", "Adding client: " + groupMembers.get(i) + " to group: " + groupName);
				adapter.addStudent(groupMembers.get(i), groupName);
			}
			gCount++;
		}
		groupScroll.addView(listView);
	}
	
	protected void onResume() {
		super.onResume();
		if (!myApp.amConnected()) {
			finish();
		} else {
			myApp.sendMessage("GetClientList`/`");
		}
	}
	
	public void onItemClick(AdapterView<?> parent, View view,
        int position, long id) {
			String destinationGroup = ((TextView) view).getText().toString();
			adapter.moveStudent(currentClient, currentGroup, destinationGroup);
			currentGroup = "";
			currentClient = "";
			adapter.notifyDataSetChanged();
			moveclient_dialog.dismiss();
	}
	
	public ClassManagement getThis() {
		return this;
	}
	
	
	public void addGroup(String newGroupName) {
		/*
		ArrayList<String> newGroupListing = new ArrayList<String>();
		groupMembers.put(newGroupName, newGroupListing);
		groupCount++;
		Button b = new Button(this);
		b.setText(newGroupName);
		b.setId((85 * groupCount) + 236);
		b.setOnClickListener(this);
		ListView groupLV = new ListView(this);
		groupLV.setAdapter(new ArrayAdapter<String>(this, R.layout.grouplistitem, newGroupListing));
		groupLV.setTextFilterEnabled(true);
		groupLV.setOnItemClickListener(getThis());
		groupLV.setVisibility(ListView.GONE);
		groupLists.add(groupLV);	
        */
		adapter.addGroup(newGroupName);
		adapter.notifyDataSetChanged();
	}
	
	
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.addgroup_button:
			addgroup_dialog = new Dialog(this);
			addgroup_dialog.setContentView(R.layout.addgroupdialog);
			addgroup_dialog.setTitle("Add a group");
			addgroup_dialog.setCancelable(true);
			addGroupEditText = (EditText) addgroup_dialog.findViewById(R.id.addgroup_name);
			Button addgroup_submit = (Button) addgroup_dialog.findViewById(R.id.addgroup_submit);
			addgroup_submit.setOnClickListener(this);
			addgroup_dialog.show();
			break;
		case R.id.savegroup_button:
			/*
			Iterator<String> iter = groupMembers.keySet().iterator();
			String groupOutput = "UpdateClientList`/`";
			String groupNameUpdate = "";
			while (iter.hasNext()) {
				String currentKey = iter.next();
				if (!currentKey.equals("Not grouped")) {
					groupNameUpdate += currentKey + "`/;";
				}
				groupOutput = groupOutput + currentKey + "`/;";
				ArrayList<String> groupClients = groupMembers.get(currentKey);
				if (groupClients.size() > 0)
					groupOutput = groupOutput + groupClients.get(0);
				for (int i=1; i<groupClients.size(); i++) {
					groupOutput = groupOutput + "`/," + groupClients.get(i);
				}
				groupOutput = groupOutput + "`/&";
			}
			*/
			String groupOutput = adapter.getGroupUpdateString();
			String groupNameUpdate = adapter.getGroupNames();
			if (!groupNameUpdate.equals("")) {
				myApp.updateGroups(groupNameUpdate);
			}
			myApp.sendMessage(groupOutput);
			break;
		case R.id.randomgroup_button:
			rgroup_method_dialog = new Dialog(this);
			rgroup_method_dialog.setContentView(R.layout.random_group_method);
			ListView rmethodLV = (ListView) rgroup_method_dialog.findViewById(R.id.rg_method_list);
			String[] rgMethods = {"By group size", "By # of groups"};
			rmethodLV.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, rgMethods));
			rmethodLV.setOnItemClickListener(new OnItemClickListener()
		        {
		            @Override
		            public void onItemClick(AdapterView<?> parent, View view,
		                    int position, long id)
		            {
		            	rgroup_size_dialog = new Dialog(getThis());
		            	rgroup_size_dialog.setCancelable(true);
		            	if (position == 0) {
		            		rgroup_size_dialog.setContentView(R.layout.rg_bygroupsize);
		            		Button rgsdSubmit = (Button) rgroup_size_dialog.findViewById(R.id.rg_bygroupsize_submit);
		            		rgsdSubmit.setOnClickListener(getThis());
		            	} else {
		            		rgroup_size_dialog.setContentView(R.layout.rg_bynumofgroups);
		            		Button rgbnSubmit = (Button) rgroup_size_dialog.findViewById(R.id.rg_bynumofgroups_submit);
		            		rgbnSubmit.setOnClickListener(getThis());
		            	}
		            	rgroup_method_dialog.dismiss();
	            		rgroup_size_dialog.show();
		            }
		        });
			rgroup_method_dialog.show();
			break;
		case R.id.addgroup_submit:
			addGroup(addGroupEditText.getText().toString());
			addgroup_dialog.dismiss();
			break;
		case R.id.rg_bygroupsize_submit:
			EditText rg_groupSizeET = (EditText) rgroup_size_dialog.findViewById(R.id.rg_bygroupsize_et);
			adapter.randomizeByGroupSize(Integer.parseInt(rg_groupSizeET.getText().toString()));
			adapter.notifyDataSetChanged();
			rgroup_size_dialog.dismiss();
			break;
		case R.id.rg_bynumofgroups_submit:
			EditText rg_groupNumET = (EditText) rgroup_size_dialog.findViewById(R.id.rg_bynumofgroups_et);
			adapter.randomizeByNumOfGroups(Integer.parseInt(rg_groupNumET.getText().toString()));
			adapter.notifyDataSetChanged();
			rgroup_size_dialog.dismiss();
			break;
		default:
			break;
		}
	}
	
}
