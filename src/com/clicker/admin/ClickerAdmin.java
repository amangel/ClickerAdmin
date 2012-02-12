package com.clicker.admin;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.content.Intent;
import android.view.View.OnClickListener;
import android.widget.Toast;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.InetAddress;
import java.net.UnknownHostException;


public class ClickerAdmin extends Activity implements OnClickListener{
	
	private Handler activityHandler;
	private AdminApplication myApp;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // Set up click listeners for all the buttons
        View connectButton = findViewById(R.id.connect_button);
        connectButton.setOnClickListener(this);
        myApp = ((AdminApplication)getApplication());
    }
    
    protected void onResume() {
    	super.onResume();
        activityHandler = new Handler() {
        	public void handleMessage(android.os.Message msg) {
        		switch (msg.what){
        		case AdminApplication.RECONNECT_SUCCESS:
        			Log.d("RECONNECT", "Got reconnect Success");
        			Toast.makeText(getApplicationContext(), "Connected to server", Toast.LENGTH_SHORT).show();
        		    Intent i = new Intent(getThis(), ModeSelector.class);
        		    startActivity(i);
        			break;
        		case AdminApplication.RECONNECT_FAILED:
        			Log.d("RECONNECT", "Got reconnect Failed");
        			Toast.makeText(getApplicationContext(), "Unable to connect to server", Toast.LENGTH_SHORT).show();
        			break;
        		}
        	}
        };
        myApp.setSubHandler(activityHandler); 
    }
    
    public ClickerAdmin getThis() {
    	return this;
    }
    
    public void onClick(View v) {
    	switch (v.getId()) {
    	case R.id.connect_button:
    		
    		//Get values out of login fields
    		//Pass through socket info and user info to admin
    		//Call "reconnect"
    		//On success message, start next activity
    		//On failure display toast
            EditText usernameField = (EditText) findViewById(R.id.username_field);
            EditText passwordField = (EditText) findViewById(R.id.password_field);
            EditText ipField = (EditText) findViewById(R.id.ip_field);
            try {
            	InetAddress ip = InetAddress.getByName(ipField.getText().toString());
            	myApp.setConnectionInfo(usernameField.getText().toString(), passwordField.getText().toString(), ip);
            	myApp.reconnect(this);
            } catch (UnknownHostException e) {}
            /*
    		try {
    			
        		Socket adminSocket = new Socket(ip, 7700);
        		adminSocket.setKeepAlive(true);
        		AdminApplication myApp = ((AdminApplication)getApplication());
        		myApp.setSocket(adminSocket);
        		myApp.setConnectionInfo(usernameField.getText().toString(), passwordField.getText().toString(), ip);
        		myApp.startListening();
        		myApp.sendMessage(usernameField.getText().toString());
        		myApp.sendMessage(passwordField.getText().toString());
        		
        		try {
        			BufferedReader in = new BufferedReader(new InputStreamReader(adminSocket.getInputStream()));
        			PrintWriter out = new PrintWriter(adminSocket.getOutputStream(), true);
            		out.println(usernameField.getText());
            		out.println(passwordField.getText());
        		} catch (IOException e) {}
        		
        		
        		//I think this should be moved to a message handler for a confirmation message upon login
    		    Intent i = new Intent(this, ModeSelector.class);
    		    startActivity(i);
    		} catch (Exception e) {}
    		break;
    		*/
    	// More buttons go here (if any) ...
    	}
    }
}