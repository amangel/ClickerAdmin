package com.clicker.admin;

import android.app.Dialog;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class TextviewWidget extends TextView implements BuilderWidget {
   
	private Dialog editorDialog;
	private RelativeLayout editingLayout;
	private LinearLayout editFieldLayout;
	private Button saveWidgeButton;
	private QuestionEditor seContext;
	private boolean wasCreated;
	
	private EditText labelText;
	
	public TextviewWidget(QuestionEditor context) {
		super(context);
		seContext = context;
		this.setTextColor(Color.parseColor("#000000"));
		editorDialog = new Dialog(context);
		
		TextView labelLabel = new TextView(context);
		labelLabel.setText("Text");
		labelText = new EditText(context);
		labelText.setSingleLine(false);
		
		editingLayout = new RelativeLayout(context);
		editFieldLayout = new LinearLayout(context);
		editFieldLayout.setOrientation(LinearLayout.VERTICAL);
		saveWidgeButton = new Button(context);
		saveWidgeButton.setText("Save values");
		RelativeLayout.LayoutParams buttonParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		buttonParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

		editFieldLayout.addView(labelLabel, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		editFieldLayout.addView(labelText, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
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

	@Override
	public void showEditingDialog() {
		labelText.setText(getText());
		editorDialog.show();
	}

	@Override
	public void saveValues() {
		setText(labelText.getText());
		seContext.widgetEdited(this, wasCreated);
		editorDialog.dismiss();
		
	}

	@Override
	public void doneCreating() {
		wasCreated = false;
		
	}

	@Override
	public void setValues(String qString) {
		String[] widgetParts = qString.split("`/:");
		setText(widgetParts[1]);
		seContext.widgetEdited(this, wasCreated);
		
	}

	@Override
	public String getValue() {
		return "TEXTVIEW`/:" + getText();
	}

}
