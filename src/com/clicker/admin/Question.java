package com.clicker.admin;

// TODO: Auto-generated Javadoc
/**
 * The Class Question.
 */
public class Question {
    
    /** The id. */
    private String id;
    
    /** The follow up. */
    private String followUp;
    
    /** The widgets. */
    private String widgets;
    
    /**
     * Instantiates a new question.
     *
     * @param questionString the question string
     */
    public Question(String questionString) {
        String[] parts = questionString.split(";");
        id = parts[0];
        widgets = parts[1];
        followUp = "Close";
    }
    
    /**
     * Gets the iD.
     *
     * @return the iD
     */
    public String getID() {
        return id;
    }
    
    /**
     * Gets the follow up.
     *
     * @return the follow up
     */
    public String getFollowUp() {
        return followUp;
    }
    
    /**
     * Gets the widgets.
     *
     * @return the widgets
     */
    public String getWidgets() {
        return widgets;
    }
    
    /**
     * Sets the follow up.
     *
     * @param next the new follow up
     */
    public void setFollowUp(String next) {
        followUp = next;
    }
}
