package com.clicker.admin;

import android.app.Dialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

// TODO: Auto-generated Javadoc
/**
 * The Class TextfieldWidget.
 */
public class TextfieldWidget extends LinearLayout implements BuilderWidget {
    
    /** The editor dialog. */
    private Dialog editorDialog;
    
    /** The label text. */
    private EditText labelText;
    
    /** The default value. */
    private EditText defaultValue;
    
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
    
    /** The tf label. */
    private TextView tfLabel;
    
    /** The tf edit. */
    private EditText tfEdit;
    
    /**
     * Instantiates a new textfield widget.
     *
     * @param context the context
     */
    public TextfieldWidget(QuestionEditor context) {
        
        //Initialization
        super(context);
        seContext = context;
        editorDialog = new Dialog(context);
        setOrientation(LinearLayout.VERTICAL);
        
        //Create object widgets
        tfLabel = new TextView(context);
        tfEdit = new EditText(context);
        this.addView(tfLabel, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        this.addView(tfEdit, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
        
        //Create editing widgets
        
        TextView labelLabel = new TextView(context);
        labelLabel.setText("Label");
        labelText = new EditText(context);
        TextView defaultLabel = new TextView(context);
        defaultLabel.setText("Default");
        defaultValue = new EditText(context);
        
        editingLayout = new RelativeLayout(context);
        editFieldLayout = new LinearLayout(context);
        editFieldLayout.setOrientation(LinearLayout.VERTICAL);
        saveWidgeButton = new Button(context);
        saveWidgeButton.setText("Save values");
        RelativeLayout.LayoutParams buttonParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        buttonParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        
        editFieldLayout.addView(labelLabel, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        editFieldLayout.addView(labelText, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
        editFieldLayout.addView(defaultLabel, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        editFieldLayout.addView(defaultValue,new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
        
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
        labelText.setText(tfLabel.getText());
        defaultValue.setText(tfEdit.getText().toString());
        editorDialog.show();
        
    }
    
    /* (non-Javadoc)
     * @see com.clicker.admin.BuilderWidget#saveValues()
     */
    @Override
    public void saveValues() {
        tfLabel.setText(labelText.getText().toString());
        tfEdit.setText(defaultValue.getText().toString());
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
        tfLabel.setText(widgetParts[1]);
        if (widgetParts.length > 2) {
            tfEdit.setText(widgetParts[2]);
        } else {
            tfEdit.setText("");
        }
        seContext.widgetEdited(this, wasCreated);
    }
    
    /* (non-Javadoc)
     * @see com.clicker.admin.BuilderWidget#getValue()
     */
    @Override
    public String getValue() {
        return "TEXTBOX`/:" + tfLabel.getText().toString() + "`/:" + tfEdit.getText().toString();
    }
    
}
