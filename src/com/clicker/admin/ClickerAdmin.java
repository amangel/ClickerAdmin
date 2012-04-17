package com.clicker.admin;

import java.net.InetAddress;
import java.net.UnknownHostException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;


// TODO: Auto-generated Javadoc
/**
 * The Class ClickerAdmin.
 */
public class ClickerAdmin extends Activity implements OnClickListener{
    
    /** The activity handler. */
    private Handler activityHandler;
    
    /** The my app. */
    private AdminApplication myApp;
    
    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState the saved instance state
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // Set up click listeners for all the buttons
        View connectButton = findViewById(R.id.connect_button);
        connectButton.setOnClickListener(this);
        myApp = ((AdminApplication)getApplication());
    }
    
    /* (non-Javadoc)
     * @see android.app.Activity#onResume()
     */
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
    
    /**
     * Gets the this.
     *
     * @return the this
     */
    public ClickerAdmin getThis() {
        return this;
    }
    
    /* (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
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