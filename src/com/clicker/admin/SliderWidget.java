package com.clicker.admin;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;


public class SliderWidget extends LinearLayout implements BuilderWidget{
	
	private Dialog editorDialog;
	private RelativeLayout editingLayout;
	private LinearLayout editFieldLayout;
	private Button saveWidgeButton;
	private QuestionEditor seContext;
	private boolean wasCreated;
	
	private TextView sliderLabel;
	private SeekBar sliderBar;
	
	private Integer sliderMax;
	private Integer sliderMin;
	private Integer sliderDefault;
	
	private EditText labelText;
	private EditText minVal;
	private EditText maxVal;
	private EditText defaultVal;

	
	public SliderWidget(QuestionEditor context) {
		super(context);
		seContext = context;
		setOrientation(LinearLayout.VERTICAL);
		editorDialog = new Dialog(context);
		editingLayout = new RelativeLayout(context);
		editFieldLayout = new LinearLayout(context);
		editFieldLayout.setOrientation(LinearLayout.VERTICAL);
		saveWidgeButton = new Button(context);
		saveWidgeButton.setText("Save values");
		RelativeLayout.LayoutParams buttonParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		buttonParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		
		sliderLabel = new TextView(context);
		sliderBar = new SeekBar(context);
		this.addView(sliderLabel, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		this.addView(sliderBar, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		
		sliderMax = 100;
		sliderMin = 0;
		sliderDefault = 0;
		
		TextView labelLabel = new TextView(context);
		labelLabel.setText("Label");
		labelText = new EditText(context);
		TextView minLabel = new TextView(context);
		minLabel.setText("Min");
		minVal = new EditText(context);
		TextView maxLabel = new TextView(context);
		maxLabel.setText("Max");
		maxVal = new EditText(context);
		TextView defaultLabel = new TextView(context);
		defaultLabel.setText("Default");
		defaultVal = new EditText(context);
		
		editFieldLayout.addView(labelLabel, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		editFieldLayout.addView(labelText, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		editFieldLayout.addView(minLabel, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		editFieldLayout.addView(minVal, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		editFieldLayout.addView(maxLabel, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		editFieldLayout.addView(maxVal, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		editFieldLayout.addView(defaultLabel, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		editFieldLayout.addView(defaultVal, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		
		editingLayout.addView(editFieldLayout, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 1f));
		editingLayout.addView(saveWidgeButton, buttonParams);
		saveWidgeButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				saveValues();
			}
		});
		editorDialog.setContentView(editingLayout);
		wasCreated = true;
	}
	
	public void doneCreating() {
		wasCreated = false;
	}
	
	public void setValues(String qString) {
		String[] widgetParts = qString.split("`/:");
		sliderLabel.setText(widgetParts[1]);
		sliderMin = Integer.parseInt(widgetParts[2]);
		sliderMax = Integer.parseInt(widgetParts[3]);
		sliderDefault = Integer.parseInt(widgetParts[4]);
		sliderBar.setMax(sliderMax.intValue() - sliderMin.intValue());
		sliderBar.setProgress(sliderDefault);
		seContext.widgetEdited(this, wasCreated);
		
	}
	
	public String getValue() {
		return "SLIDE`/:" + sliderLabel.getText() + "`/:" + ((Integer) sliderMin).toString() + 
		"`/:" + ((Integer) sliderMax).toString() + "`/:" + ((Integer) sliderDefault).toString();
	}
	
	public void showEditingDialog() {	
		labelText.setText(sliderLabel.getText());
		minVal.setText(sliderMin.toString());
		maxVal.setText(sliderMax.toString());
		defaultVal.setText(sliderDefault.toString());
		editorDialog.show();
	}
	
	public void saveValues() {
		sliderMin = Integer.parseInt(minVal.getText().toString());
		Log.d("BW","" + sliderMin);
		sliderMax = Integer.parseInt(maxVal.getText().toString());
		Log.d("BW","" + sliderMax);
		sliderDefault = Integer.parseInt(defaultVal.getText().toString());
		sliderLabel.setText(labelText.getText());
		Log.d("BW","" + (sliderMax.intValue() - sliderMin.intValue()));
		sliderBar.setMax(sliderMax.intValue() - sliderMin.intValue());
		sliderBar.setProgress(sliderDefault);
		seContext.widgetEdited(this, wasCreated);
		editorDialog.dismiss();
	}
	
}
