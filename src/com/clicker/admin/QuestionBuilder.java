package com.clicker.admin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.ViewFlipper;
import android.widget.LinearLayout.LayoutParams;

public class QuestionBuilder extends Activity implements OnClickListener{
	
	private Handler activityHandler;
	private AdminApplication myApp;
	private Socket serverSocket;
	private BufferedReader in;
	private PrintWriter out;
	private ProgressDialog loadingDialog;
	private ListView qSetDisplay;
	private boolean needToRequest;
	//private ProgressDialog reconnectingDialog;
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		needToRequest = true;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.questionbuilder);
        qSetDisplay = (ListView) findViewById(R.id.qsetdisplay);
        myApp = ((AdminApplication)getApplication());
        
        Button addQSetButton = (Button) findViewById(R.id.addqsetbutton);
        addQSetButton.setOnClickListener(this);
        
        activityHandler = new Handler() {
        	public void handleMessage(android.os.Message msg) {
        		switch (msg.what){
        		case AdminApplication.GOT_DISCONNECTED:
        			Log.d("RECONNECT", "Showing dialog then calling reconnect");
        			if (loadingDialog.isShowing()) {
        				Log.d("RECONNECT", "Got disconnect and closing loading dialog");
        				loadingDialog.dismiss();
        			}
        			myApp.reconnect(getThis());
        			break;
        		case AdminApplication.RECONNECT_SUCCESS:
        			Log.d("RECONNECT", "Got reconnect Success");
        			if (needToRequest) {
        				Log.d("RECONNECT", "Got reconnect and rerequesting data");
        				myApp.sendMessage("GetQuestionSets`/`");
        				loadingDialog = ProgressDialog.show(getThis(), "", 
        		                "Loading question sets. Please wait", true);
        			}
        			Toast.makeText(getApplicationContext(), "Reconnected to server", Toast.LENGTH_SHORT).show();
        			break;
        		case AdminApplication.RECONNECT_FAILED:
        			Log.d("RECONNECT", "Got reconnect Failed");
        			Toast.makeText(getApplicationContext(), "Failed to reconnect to server", Toast.LENGTH_SHORT).show();
        			finish();
        			break;
        		case AdminApplication.ALL_SETS_RECEIVED:
        			needToRequest = false;
        			Log.d("RECONNECT", "Set needToRequest to false");
        			String setString = (String)msg.obj;
        			String[] qParts = setString.split("`/&");
        			qSetDisplay.setAdapter(new ArrayAdapter<String>(getThis(), R.layout.qblistitem, qParts));

        			qSetDisplay.setOnItemClickListener(new OnItemClickListener() {
        			    public void onItemClick(AdapterView<?> parent, View view,
        			        int position, long id) {
        			      // When clicked, show a toast with the TextView text
        			       Intent i = new Intent(getThis(), SetEditor.class);
      			    	   i.putExtra("SETNAME", ((TextView) view).getText());
      				       startActivity(i);
        			    }
        			  });
        			loadingDialog.dismiss();
        			break;
        		}
        	}
        };
        //reconnectingDialog = null;
	}
	
	protected void onResume() {
		super.onResume();
		myApp.setSubHandler(activityHandler);
		myApp.sendMessage("GetQuestionSets`/`");
		loadingDialog = ProgressDialog.show(this, "", 
                "Loading question sets. Please wait", true);
	}
	
	public QuestionBuilder getThis() {
		return this;
	}
	
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.addqsetbutton:
			loadingDialog.dismiss();
		    Intent i = new Intent(this, SetEditor.class);
		    i.putExtra("SETNAME", "");
		    startActivity(i);
		}
	}
	
	
}
