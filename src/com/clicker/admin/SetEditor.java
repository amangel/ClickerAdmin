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

public class SetEditor extends Activity implements OnClickListener{
	
	private static final int QSRESULT = 1000;
	
	private Handler activityHandler;
	private AdminApplication myApp;
	private Socket serverSocket;
	private BufferedReader in;
	private PrintWriter out;
	private ProgressDialog loadingDialog;
	private ListView quesDisplay;
	private String[] allQArray;
	private String setName;
	private EditText setNameEdit;
	//private ProgressDialog reconnectingDialog;
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setName = getIntent().getStringExtra("SETNAME");
        setContentView(R.layout.seteditor);
        quesDisplay = (ListView) findViewById(R.id.quesdisplay);
        myApp = ((AdminApplication)getApplication());
        
		setNameEdit = (EditText) findViewById(R.id.setnameedit);
		setNameEdit.setText(setName);
		
        Button addQuesButton = (Button) findViewById(R.id.addquesbutton);
        addQuesButton.setOnClickListener(this);
        Button saveSetButton = (Button) findViewById(R.id.savesetbutton);
        saveSetButton.setOnClickListener(this);
        Button deleteSetButton = (Button) findViewById(R.id.deletesetbutton);
        deleteSetButton.setOnClickListener(this);
        
        
        //If selecting an existing set to edit, launch seteditor but do
        // putExtra("QSet", "setname") first
        
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
        		case AdminApplication.QUESTION_SET_RECEIVED:
        			String setString = (String)msg.obj;
        			allQArray = setString.split("`/&");
        			buildQuesDisplay();
        			loadingDialog.dismiss();
        			break;
        		}
        	}
        };
        
        myApp.setSubHandler(activityHandler);
        if (!setName.equals("")) {
        	myApp.sendMessage("GetAllQuestions`/`" + setName);
        	loadingDialog = ProgressDialog.show(this, "", 
                "Loading question sets. Please wait...", true);
        }
        else {
        	allQArray = new String[0];
        }
        //reconnectingDialog = null;
	}
	
	public void buildQuesDisplay() {
		String[] qIds = new String[allQArray.length];
		for (int i=0; i<allQArray.length; i++) {
			String[] quesParts = allQArray[i].split("`/;");
			qIds[i] = quesParts[0];
		}
		quesDisplay.setAdapter(new ArrayAdapter<String>(getThis(), R.layout.qblistitem, qIds));

		quesDisplay.setOnItemClickListener(new OnItemClickListener() {
		    public void onItemClick(AdapterView<?> parent, View view,
		        int position, long id) {
		       Intent i = new Intent(getThis(), QuestionEditor.class);
		    	   i.putExtra("QUESSTRING", allQArray[position]);
		    	   i.putExtra("QUESINDEX", ((Integer)position).toString());
			       startActivityForResult(i, QSRESULT);
		    }
		  });
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.d("QBTEST", "In activity result");
		if (resultCode == RESULT_OK && requestCode == QSRESULT) {
			Log.d("QBTEST", "result ok set");
			String qString = data.getStringExtra("QSTRING");
			String qIndex = data.getStringExtra("QINDEX");
			if (qIndex.equals("NEW")) {
				Log.d("QBTEST", "qindex was new");
				String[] newAllQ = new String[allQArray.length + 1];
				for (int i=0; i<allQArray.length; i++) {
					newAllQ[i] = allQArray[i];
				}
				newAllQ[allQArray.length] = qString;
				allQArray = newAllQ;
			} else {
				if (qString.equals("DELETE")) {
					String[] newDQArray = new String[allQArray.length - 1];
					int dqIndex = Integer.parseInt(qIndex);
					for (int i=0; i<dqIndex; i++) {
						newDQArray[i] = allQArray[i];
					}
					for (int i=dqIndex+1; i<allQArray.length; i++) {
						newDQArray[i-1] = allQArray[i];
					}
					allQArray = newDQArray;		
				} else {
					Log.d("QBTEST", "qindex was not new");
					int eqIndex = Integer.parseInt(qIndex);
					allQArray[eqIndex] = qString;
				}
			}
			buildQuesDisplay();
		}
	}
	
	public SetEditor getThis() {
		return this;
	}
	
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.addquesbutton:
		    Intent i = new Intent(this, QuestionEditor.class);
		    i.putExtra("QUESSTRING", "");
		    i.putExtra("QUESINDEX", "NEW");
		    startActivityForResult(i, QSRESULT);
		    break;
		case R.id.savesetbutton:
			String output = setNameEdit.getText().toString() + "`/@";
			if (allQArray.length > 0) {
				output += allQArray[0];
				for (int j=1; j<allQArray.length; j++) {
					output += "`/&" + allQArray[j];
				}
			}
			myApp.sendMessage("AddQuestionSet`/`" + setName + "`/`" + output);
			finish();
			break;
		case R.id.deletesetbutton:
			myApp.sendMessage("DeleteQuestionSet`/`" + setName);
			finish();
			break;
		}
	}
	
	
}

