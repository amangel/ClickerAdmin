package com.clicker.admin;

public interface BuilderWidget {

	public void showEditingDialog();
	
	public void saveValues();
	
	public void doneCreating();
	
	public void setValues(String qString);
	
	public String getValue();
	
}
