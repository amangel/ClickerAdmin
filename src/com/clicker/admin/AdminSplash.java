package com.clicker.admin;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
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

public class AdminSplash extends Activity{
	
	private int splashTime = 3000;
	
	private Handler handler;
	private Runnable runnable;
	
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
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            handler.removeCallbacks(runnable);
            launchAdmin();
        }
        return true;
    }
    
    @Override
    public void onBackPressed() {
       handler.removeCallbacks(runnable);
       finish();
    }
    
    public void launchAdmin() {
    	finish();
        startActivity(new Intent("com.clicker.admin.splashscreen.ClickerAdmin"));
    }
    
}