package com.clicker.admin;

import android.widget.Button;
import android.widget.TextView;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.app.Activity;
import android.widget.TextView;

public class SetQuestionDisplay {
	
	private LinearLayout mainPanel;

	public SetQuestionDisplay(String questionString, Activity ctx) {
		mainPanel = new LinearLayout(ctx);
		mainPanel.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				                                   LayoutParams.FILL_PARENT));
		LinearLayout questionPanel = new LinearLayout(ctx);
		questionPanel.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                                                       LayoutParams.WRAP_CONTENT));
		questionPanel.setOrientation(LinearLayout.VERTICAL);
		TextView tv = new TextView(ctx);
		tv.setText(questionString);
		questionPanel.addView(tv);
		
		LinearLayout navPanel = new LinearLayout(ctx);
		navPanel.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
                                                  LayoutParams.WRAP_CONTENT));
		navPanel.setOrientation(LinearLayout.HORIZONTAL);
		navPanel.setGravity(Gravity.BOTTOM);
		//navPanel.addView(prevButton);
		//navPanel.addView(nextButton);
		mainPanel.addView(questionPanel);
		mainPanel.addView(navPanel);
	}
	
	public LinearLayout getLayout() {
		return mainPanel;
	}
	
}
