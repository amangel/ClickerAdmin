package com.clicker.admin;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

// TODO: Auto-generated Javadoc
/**
 * The Class QuestionEditor.
 */
public class QuestionEditor extends Activity implements OnClickListener{
    
    /** The widget options. */
    private String[] widgetOptions = {"Button", "Toggle Button", "Textfield", "Slider", "Textview"};
    
    /** The widget selects. */
    private boolean[] widgetSelects = new boolean[widgetOptions.length];
    
    /** The widget pick dialog. */
    private AlertDialog widgetPickDialog;
    
    /** The widget click dialog. */
    private Dialog widgetClickDialog;
    
    /** The widget clicked. */
    private int widgetClicked;
    
    /** The widget to add. */
    private int widgetToAdd;
    
    /** The selected widgets. */
    private ArrayList<BuilderWidget> selectedWidgets;
    
    /** The se widget list. */
    private LinearLayout seWidgetList;
    
    /** The ques string. */
    private String quesString;
    
    /** The ques index. */
    private String quesIndex;
    
    /** The loading dialog. */
    private ProgressDialog loadingDialog;
    
    /** The ID. */
    private String ID;
    
    /** The q id edit. */
    private EditText qIdEdit;
    
    /** The activity handler. */
    private Handler activityHandler;
    
    /** The my app. */
    private AdminApplication myApp;
    
    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.questioneditor);
        
        quesString = getIntent().getStringExtra("QUESSTRING");
        quesIndex = getIntent().getStringExtra("QUESINDEX");
        
        qIdEdit = (EditText) findViewById(R.id.qidedit);
        qIdEdit.setText(quesString.split("`/;")[0]);
        
        Button addWidgetButton = (Button) findViewById(R.id.addwidgetbutton);
        addWidgetButton.setOnClickListener(this);
        Button saveQuestionButton = (Button) findViewById(R.id.savequestionbutton);
        saveQuestionButton.setOnClickListener(this);
        Button deleteQuestionButton = (Button) findViewById(R.id.deletequestionbutton);
        deleteQuestionButton.setOnClickListener(this);
        
        AlertDialog.Builder coBuilder = new AlertDialog.Builder(this);
        coBuilder.setTitle("Select Widget");
        coBuilder.setSingleChoiceItems(widgetOptions, -1, new DialogSelectionClickHandler());
        coBuilder.setPositiveButton("OK", new DialogOKClickHandler());
        coBuilder.setOnCancelListener(new DialogCancelHandler());
        
        widgetClicked = -1;
        widgetClickDialog = new Dialog(this);
        widgetClickDialog.setCancelable(false);
        widgetClickDialog.setContentView(R.layout.widgetclickdialog);
        Button editWidgetButton = (Button) widgetClickDialog.findViewById(R.id.editwidgetbutton);
        Button deleteWidgetButton = (Button) widgetClickDialog.findViewById(R.id.deletewidgetbutton);
        Button cancelWidgetButton = (Button) widgetClickDialog.findViewById(R.id.cancelwidgetbutton);
        editWidgetButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                editWidget((View)selectedWidgets.get(widgetClicked));
                widgetClicked = -1;
                widgetClickDialog.dismiss();
            }
        });
        deleteWidgetButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                seWidgetList.removeView((View)selectedWidgets.get(widgetClicked));
                selectedWidgets.remove(widgetClicked);
                widgetClicked = -1;
                widgetClickDialog.dismiss();
            }
        });
        cancelWidgetButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                widgetClicked = -1;
                widgetClickDialog.dismiss();
            }
        });
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
        
        
        seWidgetList = (LinearLayout) findViewById(R.id.sewidgetlist);
        selectedWidgets = new ArrayList<BuilderWidget>();
        widgetPickDialog = coBuilder.create();
        widgetToAdd = -1;
        if (!quesString.equals("")) {
            Log.d("BALLSAC","BUILDING QUESTION: " + quesString);
            loadingDialog = ProgressDialog.show(this, "", 
                    "Loading question. Please wait...", true);
            String[] questionParts = quesString.split("`/;");
            ID = questionParts[0];
            qIdEdit.setText(ID);
            
            if (questionParts.length > 2) {
                String[] questionWidgets = questionParts[2].split("`/,");
                for (int i=0; i<questionWidgets.length; i++) {
                    String[] widgetParts = questionWidgets[i].split("`/:");
                    String widgetType = widgetParts[0];
                    
                    if (widgetType.equals("B")) {
                        ButtonWidget newButton = new ButtonWidget(this);
                        newButton.setValues(questionWidgets[i]);
                    } else if (widgetType.equals("TOG")) {
                        ToggleButtonWidget newTog = new ToggleButtonWidget(this);
                        newTog.setValues(questionWidgets[i]);
                    } else if (widgetType.equals("TEXTBOX")) {
                        TextfieldWidget newTextfield = new TextfieldWidget(this);
                        newTextfield.setValues(questionWidgets[i]);
                    } else if (widgetType.equals("SLIDE")) {
                        SliderWidget newSlider = new SliderWidget(this);
                        newSlider.setValues(questionWidgets[i]);
                    } else if (widgetType.equals("TEXTVIEW")){
                        TextviewWidget newTextview = new TextviewWidget(this);
                        newTextview.setValues(questionWidgets[i]);
                    }
                }
            }
            loadingDialog.dismiss();
        }
    }
    
    /* (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addwidgetbutton:
                widgetPickDialog.show();
                break;
            case R.id.savequestionbutton:
                String qVal = qIdEdit.getText().toString();
                Log.d("QS", "" + selectedWidgets.size());
                if (selectedWidgets.size() > 0) {
                    qVal += "`/;`/;" + selectedWidgets.get(0).getValue();
                    for (int i=1; i<selectedWidgets.size(); i++) {
                        qVal += "`/," + selectedWidgets.get(i).getValue();
                    }
                }
                Log.d("QS", qVal);
                Intent intent = getIntent();
                intent.putExtra("QSTRING", qVal);
                intent.putExtra("QINDEX", quesIndex);
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.deletequestionbutton:
                Intent dIntent = getIntent();
                dIntent.putExtra("QSTRING", "DELETE");
                dIntent.putExtra("QINDEX", quesIndex);
                setResult(RESULT_OK, dIntent);
                finish();
                break;
        }
    }
    
    /**
     * Gets the this.
     *
     * @return the this
     */
    public QuestionEditor getThis() {
        return this;
    }
    
    /**
     * Widget edited.
     *
     * @param bw the bw
     * @param wasCreated the was created
     */
    public void widgetEdited(BuilderWidget bw, boolean wasCreated) {
        if (wasCreated) {
            selectedWidgets.add(bw);
            //LinearLayout widgetRow = new LinearLayout(this);
            //widgetRow.setOrientation(LinearLayout.HORIZONTAL);
            //Button wEditButton = new Button(this);
            //wEditButton.setText("Edit");
            //wEditButton.setId((selectedWidgets.size() - 1) * 105 + 48);
            //wEditButton.setOnClickListener(new View.OnClickListener() {
            //     public void onClick(View v) {
            //         editWidget(v.getId());
            //     }
            // });
            //widgetRow.addView((View) v);
            //widgetRow.addView(wEditButton);
            //seWidgetList.addView(widgetRow);
            //LinearLayout widgetRow = new LinearLayout(this);
            //widgetRow.setOrientation(LinearLayout.HORIZONTAL);
            View bwView= (View) bw;
            bwView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    for (int i=0; i<seWidgetList.getChildCount(); i++) {
                        if (seWidgetList.getChildAt(i) == v) {
                            Log.d("WIDGEDIT", "SET THE ID TO: " + i);
                            widgetClicked = i;
                            break;
                        }
                    }
                    widgetClickDialog.show();
                    
                }
            });
            seWidgetList.addView(bwView);
            bw.doneCreating();
        }
    }
    
    //public void editWidget(int wId) {
    //	BuilderWidget theWidget = selectedWidgets.get((wId - 48) / 105);
    //	theWidget.showEditingDialog();
    //}
    
    /**
     * Edits the widget.
     *
     * @param v the v
     */
    public void editWidget(View v) {
        BuilderWidget theWidget = (BuilderWidget) v;
        theWidget.showEditingDialog();
    }
    
    /**
     * The Class DialogSelectionClickHandler.
     */
    public class DialogSelectionClickHandler implements DialogInterface.OnClickListener
    {
        
        /* (non-Javadoc)
         * @see android.content.DialogInterface.OnClickListener#onClick(android.content.DialogInterface, int)
         */
        public void onClick( DialogInterface dialog, int clicked) {
            widgetToAdd = clicked;
        }
    }
    
    /**
     * The Class DialogOKClickHandler.
     */
    public class DialogOKClickHandler implements DialogInterface.OnClickListener
    {
        
        /* (non-Javadoc)
         * @see android.content.DialogInterface.OnClickListener#onClick(android.content.DialogInterface, int)
         */
        public void onClick( DialogInterface dialog, int clicked) {
            switch (clicked) {
                case DialogInterface.BUTTON_POSITIVE:
                    switch (widgetToAdd) {
                        case 0:
                            ButtonWidget newButton = new ButtonWidget(getThis());
                            newButton.showEditingDialog();
                            break;
                        case 1:
                            ToggleButtonWidget newTog = new ToggleButtonWidget(getThis());
                            newTog.showEditingDialog();
                            break;
                        case 2:
                            TextfieldWidget newTextfield = new TextfieldWidget(getThis());
                            newTextfield.showEditingDialog();
                            break;
                        case 3:
                            SliderWidget newSlider = new SliderWidget(getThis());
                            newSlider.showEditingDialog();
                            break;
                        case 4:
                            TextviewWidget newTextview = new TextviewWidget(getThis());
                            newTextview.showEditingDialog();
                            break;
                    }
            }
            widgetPickDialog.cancel();
        }
    }
    
    /**
     * The Class DialogCancelHandler.
     */
    public class DialogCancelHandler implements DialogInterface.OnCancelListener {
        
        /* (non-Javadoc)
         * @see android.content.DialogInterface.OnCancelListener#onCancel(android.content.DialogInterface)
         */
        public void onCancel(DialogInterface dialog) {
            if (widgetToAdd != -1) {
                widgetPickDialog.getListView().setItemChecked(widgetToAdd, false);
                widgetToAdd = -1;
                Log.d("CLEEK","Unchecked item");
            }
        }
    }
    
    
    
}
