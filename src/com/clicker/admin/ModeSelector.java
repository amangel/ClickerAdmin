package com.clicker.admin;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class ModeSelector extends Activity implements OnClickListener {
	
	private AdminApplication myApp;
	private Handler activityHandler;
	//private ProgressDialog reconnectingDialog;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modeselector);
        
        View classModeButton = findViewById(R.id.classmodebutton);
        classModeButton.setOnClickListener(this);
        
        View buildModeButton = findViewById(R.id.buildmodebutton);
        buildModeButton.setOnClickListener(this);
        
        myApp = ((AdminApplication)getApplication());
        
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
        		}
        	}
        };
        myApp.setSubHandler(activityHandler);
        //reconnectingDialog = null;
	}
	
	protected void onResume() {
		super.onResume();
		if (!myApp.amConnected()) {
			Log.d("hb","Finishing activity");
			finish();
		}
	}
	
	
	public ModeSelector getThis() {
		return this;
	}
	
	public void onClick(View v) {
		Intent i;
		switch (v.getId()) {
		case R.id.classmodebutton:
		    i = new Intent(this, AdminTabs.class);
		    startActivity(i);
		    break;
		case R.id.buildmodebutton:
		    i = new Intent(this, QuestionBuilder.class);
		    startActivity(i);
		    break;
		}
	}
	
}
