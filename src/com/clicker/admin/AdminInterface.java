package com.clicker.admin;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TableLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

// TODO: Auto-generated Javadoc
/**
 * The Class AdminInterface.
 */
public class AdminInterface extends Activity implements OnClickListener{
    
    /** The server socket. */
    private Socket serverSocket;
    
    /** The in. */
    private BufferedReader in;
    
    /** The out. */
    private PrintWriter out;
    
    /** The inflater. */
    private LayoutInflater inflater;
    
    /** The clients. */
    private Map<String, ToggleButton> clients;
    
    /** The layout. */
    private TableLayout layout;
    
    /** The client lock. */
    private final Semaphore clientLock = new Semaphore(1);
    
    /** The client change handler. */
    private Handler clientChangeHandler;
    
    /** The questions. */
    private Map<String, Question> questions;
    
    /** The ordered questions. */
    private ArrayList<Question> orderedQuestions;
    
    /** The current q index. */
    private int currentQIndex;
    
    /** The my app. */
    private AdminApplication myApp;
    
    /** The activity handler. */
    private Handler activityHandler;
    
    
    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adminui);
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        clients = new HashMap<String, ToggleButton>(50);
        
        myApp = ((AdminApplication)getApplication());
        /*
		try {
			in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
			out = new PrintWriter(serverSocket.getOutputStream(), true);
		} catch (IOException e) {}
         */
        
        View openMCButton = findViewById(R.id.openmc_button);
        openMCButton.setOnClickListener(this);
        
        View openTogButton = findViewById(R.id.opentog_button);
        openTogButton.setOnClickListener(this);
        
        View openSlidesButton = findViewById(R.id.openslides_button);
        openSlidesButton.setOnClickListener(this);
        
        View openCombosButton = findViewById(R.id.opencombos_button);
        openCombosButton.setOnClickListener(this);
        
        View openTextboxButton = findViewById(R.id.opentextbox_button);
        openTextboxButton.setOnClickListener(this);
        
        View openMouseControlButton = findViewById(R.id.openmousecontrol_button);
        openMouseControlButton.setOnClickListener(this);
        
        View closeButton = findViewById(R.id.close_button);
        closeButton.setOnClickListener(this);
        
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
        
        
    }
    
    /**
     * Gets the this.
     *
     * @return the this
     */
    public AdminInterface getThis() {
        return this;
    }
    
    /* (non-Javadoc)
     * @see android.app.Activity#onResume()
     */
    protected void onResume() {
        super.onResume();
        if (!myApp.amConnected()) {
            Log.d("hb","Finishing activity");
            finish();
        }
    }
    
    /* (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.openmc_button:
                myApp.openQuestion("Open`/;34`/;`/;B`/:A`/:0`/,B`/:B`/:0`/,B`/:C`/:0`/,B`/:D`/:0", this);
                break;
            case R.id.opentog_button:
                myApp.openQuestion("Open`/;35`/;`/;TOG`/:A`/:0`/,TOG`/:B`/:0`/,TOG`/:C`/:0`/,TOG`/:D`/:0", this);
                break;
            case R.id.openslides_button:
                myApp.openQuestion("Open`/;36`/;P`/;SLIDE`/:Slider 1`/:0`/:50`/:25", this);
                break;
            case R.id.opencombos_button:
                myApp.openQuestion("Open`/;37`/;A`/;COMBO`/:Combo 1`/:a`/~b`/~c`/~d`/:0", this);
                break;
            case R.id.opentextbox_button:
                myApp.openQuestion("Open`/;38`/;`/;TEXTBOX`/:Enter text`/: ", this);
                Log.d("SOCKET", "sending msg to server");
                break;
            case R.id.openmousecontrol_button:
                Log.d("SOCKET", "sending msg to server");
                myApp.openQuestion("OpenClickPad`/;39`/;`/;TEXTBOX`/:Enter text`/: ", this);
                
                break;
            case R.id.close_button:
                myApp.closeQuestion(this);
                break;
        }
    }
    
    
    
    
}
