package com.clicker.admin;

public class Question {
	
	private String id;
	private String followUp;
	private String widgets;
	
	public Question(String questionString) {
		String[] parts = questionString.split(";");
		id = parts[0];
		widgets = parts[1];
		followUp = "Close";
	}
	
	public String getID() {
		return id;
	}
	
	public String getFollowUp() {
		return followUp;
	}
	
	public String getWidgets() {
		return widgets;
	}
	
	public void setFollowUp(String next) {
		followUp = next;
	}
}
