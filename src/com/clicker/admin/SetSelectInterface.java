package com.clicker.admin;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.ViewFlipper;

// TODO: Auto-generated Javadoc
/**
 * The Class SetSelectInterface.
 */
public class SetSelectInterface extends Activity implements OnClickListener {
    
    /** The server socket. */
    private Socket serverSocket;
    
    /** The my app. */
    private AdminApplication myApp;
    
    /** The in. */
    private BufferedReader in;
    
    /** The out. */
    private PrintWriter out;
    
    /** The set string. */
    private String setString;
    
    /** The all question string. */
    private String allQuestionString;
    
    /** The all q array. */
    private String[] allQArray;
    
    /** The selection counter. */
    private int selectionCounter;
    
    /** Spinner */
    private Spinner s;
    
    /** The open close button. */
    private ToggleButton openCloseButton;
    
    /** The activity handler. */
    private Handler activityHandler;
    
    /** The loading dialog. */
    private ProgressDialog loadingDialog;
    //private ProgressDialog reconnectingDialog;
    
    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.setselect);
        myApp = ((AdminApplication)getApplication());
        
        Button b = (Button) findViewById(R.id.loadset_button);
        b.setOnClickListener(this);
        allQuestionString = "";
        allQArray = null;
        
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
                    case AdminApplication.ALL_SETS_RECEIVED:
                        String setString = (String)msg.obj;
                        String[] qParts = setString.split("`/&");
                        s = (Spinner) findViewById(R.id.setselect_spinner);
                        ArrayAdapter adapter = new ArrayAdapter(getThis(), android.R.layout.simple_spinner_item, qParts);
                        selectionCounter = 0;
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        //s.setOnItemSelectedListener(this);
                        s.setAdapter(adapter);
                        loadingDialog.dismiss();
                        break;
                    case AdminApplication.QUESTION_SET_RECEIVED:
                        String allQuestionString = (String)msg.obj;
                        allQArray = allQuestionString.split("`/&");
                        setContentView(R.layout.lectureset);
                        ViewFlipper vf = (ViewFlipper) findViewById(R.id.lecture_flipper);
                        for (int i=0; i<allQArray.length; i++) {
                            String[] qTextParts = allQArray[i].split("`/;");
                            String[] qWidgets = qTextParts[2].split("`/,");
                            LinearLayout insideLL = new LinearLayout(getThis());
                            insideLL.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
                                    LayoutParams.FILL_PARENT));
                            insideLL.setOrientation(LinearLayout.VERTICAL);
                            for (int j=0; j<qWidgets.length; j++) {
                                LinearLayout sll = new LinearLayout(getThis());
                                sll.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
                                        LayoutParams.FILL_PARENT));
                                sll.setOrientation(LinearLayout.VERTICAL);
                                sll.setBackgroundColor(Color.parseColor("#000000"));
                                String[] widgetParts = qWidgets[j].split("`/:");
                                Log.d("QS",widgetParts[0]);
                                if (widgetParts[0].equals("B") || widgetParts[0].equals("JEO")) {
                                    Button tb = new Button(getThis());
                                    tb.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 
                                            LayoutParams.WRAP_CONTENT, 1f));
                                    tb.setText(widgetParts[1]);
                                    sll.addView(tb);
                                } else if (widgetParts[0].equals("TOG")) {
                                    ToggleButton ttb = new ToggleButton(getThis());
                                    ttb.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
                                            LayoutParams.WRAP_CONTENT, 1f));
                                    ttb.setTextOn(widgetParts[1]);
                                    ttb.setTextOff(widgetParts[1]);
                                    sll.addView(ttb);
                                } else if (widgetParts[0].equals("TEXTVIEW") || widgetParts[0].equals("TVBUTTON")) {
                                    TextView ttv = new TextView(getThis());
                                    ttv.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
                                            LayoutParams.WRAP_CONTENT));
                                    ttv.setText(widgetParts[1]);
                                    sll.addView(ttv);
                                    if (widgetParts[0].equals("TVBUTTON")) {
                                        Button conBut = new Button(getThis());
                                        conBut.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                                                LayoutParams.WRAP_CONTENT));
                                        conBut.setText("Continue");
                                        sll.addView(conBut);
                                    }
                                } else if (widgetParts[0].equals("SLIDE")) {
                                    SeekBar tsb = new SeekBar(getThis());
                                    tsb.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
                                            LayoutParams.WRAP_CONTENT)); 			
                                    tsb.setMax(Integer.parseInt(widgetParts[3]) - Integer.parseInt(widgetParts[2]));
                                    TextView stv = new TextView(getThis());
                                    stv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                                            LayoutParams.WRAP_CONTENT));
                                    stv.setText(widgetParts[1]);
                                    sll.addView(stv);
                                    sll.addView(tsb);
                                } else if (widgetParts[0].equals("COMBO")) {
                                    TextView ctv = new TextView(getThis());
                                    ctv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                                            LayoutParams.WRAP_CONTENT));
                                    ctv.setText(widgetParts[1]);
                                    String[] copts = widgetParts[2].split("`/~");
                                    Spinner tcs = new Spinner(getThis());
                                    tcs.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
                                            LayoutParams.WRAP_CONTENT));
                                    ArrayAdapter qAdapter = new ArrayAdapter(getThis(), android.R.layout.simple_spinner_item, copts);
                                    qAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    tcs.setAdapter(qAdapter);
                                    sll.addView(ctv);
                                    sll.addView(tcs);
                                } else if (widgetParts[0].equals("TEXTBOX")) {
                                    EditText tet = new EditText(getThis());
                                    tet.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
                                            LayoutParams.WRAP_CONTENT));
                                    sll.addView(tet);
                                } else if (widgetParts[0].equals("TEXTQ") || widgetParts[0].equals("QRTEXT")) {
                                    EditText tet = new EditText(getThis());
                                    tet.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
                                            LayoutParams.WRAP_CONTENT));
                                    Button sbut = new Button(getThis());
                                    sbut.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                                            LayoutParams.WRAP_CONTENT));
                                    if (widgetParts[0].equals("QRTEXT")) {
                                        sbut.setText("Scan");
                                    } else {
                                        sbut.setText("Submit");
                                    }
                                    sll.addView(tet);
                                    sll.addView(sbut);
                                } else {
                                    TextView tv = new TextView(getThis());
                                    tv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                                            LayoutParams.WRAP_CONTENT));
                                    tv.setText("Some other widget type");
                                    sll.addView(tv);
                                }
                                insideLL.addView(sll);
                            }
                            
                            vf.addView(insideLL);
                        }
                        Button buttonNext = (Button) findViewById(R.id.next_button);
                        Button buttonPrev = (Button) findViewById(R.id.prev_button);
                        Button setQOpen = (Button) findViewById(R.id.setq_open_button);
                        Button setQClose = (Button) findViewById(R.id.setq_close_button);
                        buttonNext.setOnClickListener(getThis());
                        buttonPrev.setOnClickListener(getThis());
                        setQOpen.setOnClickListener(getThis());
                        setQClose.setOnClickListener(getThis());
                        loadingDialog.dismiss();
                }
            }
        };
        myApp.setSubHandler(activityHandler);
        myApp.sendMessage("GetQuestionSets`/`");
        //reconnectingDialog = null;
        loadingDialog = ProgressDialog.show(this, "", 
                "Loading question sets. Please wait...", true);
        
    }
    
    /* (non-Javadoc)
     * @see android.app.Activity#onResume()
     */
    protected void onResume() {
        super.onResume();
        if (!myApp.amConnected()) {
            finish();
        }
    }
    
    /**
     * Gets this.
     *
     * @return the this
     */
    public SetSelectInterface getThis() {
        return this;
    }
    
    /* (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    public void onClick(View v) {
        if (v.getId() == R.id.loadset_button) {
            myApp.sendMessage("GetAllQuestions`/`" + s.getSelectedItem().toString());
            loadingDialog = ProgressDialog.show(this, "", 
                    "Loading questions. Please wait...", true);
        } 
        else {
            ViewFlipper vf = (ViewFlipper) findViewById(R.id.lecture_flipper);
            switch (v.getId()) {
                case R.id.next_button:
                    vf.setAnimation(AnimationUtils.loadAnimation(v.getContext(), R.anim.slide_in_right));
                    vf.showNext();
                    break;
                case R.id.prev_button:
                    vf.setAnimation(AnimationUtils.loadAnimation(v.getContext(), R.anim.slide_in_left));
                    vf.showPrevious();
                    break;
                case R.id.setq_open_button:
                    myApp.openQuestion("Open`/;" + allQArray[vf.indexOfChild(vf.getCurrentView())], this);
                    break;
                case R.id.setq_close_button:
                    myApp.closeQuestion(this);
                    break;
            }
        }
    }
}
