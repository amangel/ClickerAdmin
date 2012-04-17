package com.clicker.admin;

// TODO: Auto-generated Javadoc
/**
 * The Interface BuilderWidget.
 */
public interface BuilderWidget {
    
    /**
     * Show editing dialog.
     */
    public void showEditingDialog();
    
    /**
     * Save values.
     */
    public void saveValues();
    
    /**
     * Done creating.
     */
    public void doneCreating();
    
    /**
     * Sets the values.
     *
     * @param qString the new values
     */
    public void setValues(String qString);
    
    /**
     * Gets the value.
     *
     * @return the value
     */
    public String getValue();
    
}
