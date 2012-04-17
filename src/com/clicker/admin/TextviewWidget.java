package com.clicker.admin;

import android.app.Dialog;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

// TODO: Auto-generated Javadoc
/**
 * The Class TextviewWidget.
 */
public class TextviewWidget extends TextView implements BuilderWidget {
    
    /** The editor dialog. */
    private Dialog editorDialog;
    
    /** The editing layout. */
    private RelativeLayout editingLayout;
    
    /** The edit field layout. */
    private LinearLayout editFieldLayout;
    
    /** The save widge button. */
    private Button saveWidgeButton;
    
    /** The se context. */
    private QuestionEditor seContext;
    
    /** The was created. */
    private boolean wasCreated;
    
    /** The label text. */
    private EditText labelText;
    
    /**
     * Instantiates a new textview widget.
     *
     * @param context the context
     */
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
    
    /* (non-Javadoc)
     * @see com.clicker.admin.BuilderWidget#showEditingDialog()
     */
    @Override
    public void showEditingDialog() {
        labelText.setText(getText());
        editorDialog.show();
    }
    
    /* (non-Javadoc)
     * @see com.clicker.admin.BuilderWidget#saveValues()
     */
    @Override
    public void saveValues() {
        setText(labelText.getText());
        seContext.widgetEdited(this, wasCreated);
        editorDialog.dismiss();
        
    }
    
    /* (non-Javadoc)
     * @see com.clicker.admin.BuilderWidget#doneCreating()
     */
    @Override
    public void doneCreating() {
        wasCreated = false;
        
    }
    
    /* (non-Javadoc)
     * @see com.clicker.admin.BuilderWidget#setValues(java.lang.String)
     */
    @Override
    public void setValues(String qString) {
        String[] widgetParts = qString.split("`/:");
        setText(widgetParts[1]);
        seContext.widgetEdited(this, wasCreated);
        
    }
    
    /* (non-Javadoc)
     * @see com.clicker.admin.BuilderWidget#getValue()
     */
    @Override
    public String getValue() {
        return "TEXTVIEW`/:" + getText();
    }
    
}
