package com.clicker.admin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;

// TODO: Auto-generated Javadoc
/**
 * The Class AdminSplash.
 */
public class AdminSplash extends Activity{
	
	/** The splash time. */
	private int splashTime = 3000;
	
	/** The handler. */
	private Handler handler;
	
	/** The runnable. */
	private Runnable runnable;
	
    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        runnable = new Runnable(){
            @Override
            public void run() {
              launchAdmin();
            }
        };
        handler = new Handler();
        handler.postDelayed(runnable, splashTime);
        //Add code to kill handler if this activity is backed out of   
    }
    
    /* (non-Javadoc)
     * @see android.app.Activity#onTouchEvent(android.view.MotionEvent)
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            handler.removeCallbacks(runnable);
            launchAdmin();
        }
        return true;
    }
    
    /* (non-Javadoc)
     * @see android.app.Activity#onBackPressed()
     */
    @Override
    public void onBackPressed() {
       handler.removeCallbacks(runnable);
       finish();
    }
    
    /**
     * Launch admin.
     */
    public void launchAdmin() {
    	finish();
        startActivity(new Intent("com.clicker.admin.splashscreen.ClickerAdmin"));
    }
    
}