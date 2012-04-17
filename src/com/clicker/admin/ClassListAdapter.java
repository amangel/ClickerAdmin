package com.clicker.admin;

import java.util.ArrayList;
import java.util.Collections;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

// TODO: Auto-generated Javadoc
/**
 * The Class ClassListAdapter.
 */
public class ClassListAdapter extends BaseExpandableListAdapter {
    
    /** The context. */
    private Context context;
    
    /** The groups. */
    private ArrayList<String> groups;
    
    /** The children. */
    private ArrayList<ArrayList<String>> children;
    
    /**
     * Instantiates a new class list adapter.
     *
     * @param context the context
     * @param groups the groups
     * @param children the children
     */
    public ClassListAdapter(Context context, ArrayList<String> groups, ArrayList<ArrayList<String>> children) {
        this.context = context;
        this.groups = groups;
        this.children = children;
    }
    
    /**
     * Adds the group.
     *
     * @param groupName the group name
     */
    public void addGroup(String groupName) {
        groups.add(groupName);
        children.add(new ArrayList<String>());
        Log.d("GROUPING", "Adding group: " + groupName + " and now have " + groups.size() + " groups");
    }
    
    /**
     * Gets the group names.
     *
     * @return the group names
     */
    public String getGroupNames() {
        String output = "";
        for (int i=0; i<groups.size(); i++) {
            String groupName = groups.get(i);
            if (!groupName.equals("Not grouped")) {
                output += groupName + "`/;";
            }	
        }
        return output;
    }
    
    /**
     * Gets the group update string.
     *
     * @return the group update string
     */
    public String getGroupUpdateString() {
        String groupOutput = "UpdateClientList`/`";
        for (int i=0; i<groups.size(); i++) {
            groupOutput += groups.get(i) + "`/;";
            ArrayList<String> groupChildren = children.get(i);
            if (groupChildren.size() > 0) {
                groupOutput += groupChildren.get(0);
                for (int j=1; j<groupChildren.size(); j++) {
                    groupOutput += "`/," + groupChildren.get(j);
                }
            }
            groupOutput += "`/&";
        }
        return groupOutput;
    }
    
    /**
     * Gets the other groups.
     *
     * @param groupPosition the group position
     * @return the other groups
     */
    public ArrayList<String> getOtherGroups(int groupPosition) {
        ArrayList<String> results = new ArrayList<String>();
        for (int i=0; i<groups.size(); i++) {
            if (i != groupPosition) {
                results.add(groups.get(i));
            }
        }
        return results;
    }
    
    /**
     * Gets the all students.
     *
     * @return the all students
     */
    public ArrayList<String> getAllStudents() {
        ArrayList<String> studentNames = new ArrayList<String>();
        for (int i=0; i<children.size(); i++) {
            for (int j=0; j<children.get(i).size(); j++) {
                studentNames.add(children.get(i).get(j));
            }
        }
        return studentNames;
    }
    
    /**
     * Ungroup all students.
     *
     * @param studentNames the student names
     */
    public void ungroupAllStudents(ArrayList<String> studentNames) {
        //Move everyone to ungrouped and clear other groups
        groups = new ArrayList<String>();
        groups.add("Not grouped");
        children = new ArrayList<ArrayList<String>>();
        children.add(new ArrayList<String>());
        for (int i=0; i<studentNames.size(); i++) {
            children.get(0).add(studentNames.get(i));
        }
    }
    
    /**
     * Randomize by group size.
     *
     * @param groupSize the group size
     */
    public void randomizeByGroupSize(int groupSize) {
        ArrayList<String> studentNames = getAllStudents();
        int numOfStudents = studentNames.size();
        int numOfGroups = (int) Math.ceil((float)numOfStudents/(float)groupSize);
        randomizeGroups(numOfGroups, studentNames);
    }
    
    /**
     * Randomize by num of groups.
     *
     * @param numOfGroups the num of groups
     */
    public void randomizeByNumOfGroups(int numOfGroups) {
        ArrayList<String> studentNames = getAllStudents();
        randomizeGroups(numOfGroups, studentNames);
    }
    
    /**
     * Randomize groups.
     *
     * @param numOfGroups the num of groups
     * @param studentNames the student names
     */
    public void randomizeGroups(int numOfGroups, ArrayList<String> studentNames) {
        ungroupAllStudents(studentNames);
        Collections.shuffle(studentNames);
        for (int i=0; i<numOfGroups; i++) {
            String newGroupName = "Group " + (i + 1);
            Log.d("Grouping","Adding Group " + (i + 1));
            addGroup(newGroupName);
        }
        int groupNum = 0;
        for (int i=0; i<studentNames.size(); i++) {
            Log.d("GROUPING", "Moving " + studentNames.get(i) + " to Group " + (groupNum + 1));
            moveStudent(studentNames.get(i), "Not grouped", "Group " + (groupNum + 1));
            groupNum = (groupNum + 1) % numOfGroups;
        }
        
    }
    
    /**
     * Move student.
     *
     * @param studentName the student name
     * @param previousGroup the previous group
     * @param newGroup the new group
     */
    public void moveStudent(String studentName, String previousGroup, String newGroup) {
        children.get(groups.indexOf(previousGroup)).remove(studentName);
        children.get(groups.indexOf(newGroup)).add(studentName);
    }
    
    /**
     * Adds the student.
     *
     * @param studentName the student name
     * @param groupName the group name
     */
    public void addStudent(String studentName, String groupName) {
        children.get(groups.indexOf(groupName)).add(studentName);
        Log.d("GROUPING", "Adding student: " + studentName + " and now have " + children.get(groups.indexOf(groupName)).size() + " students in the group");
    }
    
    /* (non-Javadoc)
     * @see android.widget.ExpandableListAdapter#getChild(int, int)
     */
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return children.get(groupPosition).get(childPosition);
    }
    
    /* (non-Javadoc)
     * @see android.widget.ExpandableListAdapter#getChildId(int, int)
     */
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }
    
    // Return a child view. You can load your custom layout here.
    /* (non-Javadoc)
     * @see android.widget.ExpandableListAdapter#getChildView(int, int, boolean, android.view.View, android.view.ViewGroup)
     */
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
            View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.child_layout, null);
        }
        TextView tv = (TextView) convertView.findViewById(R.id.tvChild);
        Log.d("GROUPING", "In getChildView, setting tv text to: " + getChild(groupPosition, childPosition).toString());
        tv.setText(getChild(groupPosition, childPosition).toString());
        
        return convertView;
    }
    
    /* (non-Javadoc)
     * @see android.widget.ExpandableListAdapter#getChildrenCount(int)
     */
    @Override
    public int getChildrenCount(int groupPosition) {
        return children.get(groupPosition).size();
    }
    
    /* (non-Javadoc)
     * @see android.widget.ExpandableListAdapter#getGroup(int)
     */
    @Override
    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }
    
    /* (non-Javadoc)
     * @see android.widget.ExpandableListAdapter#getGroupCount()
     */
    @Override
    public int getGroupCount() {
        return groups.size();
    }
    
    /* (non-Javadoc)
     * @see android.widget.ExpandableListAdapter#getGroupId(int)
     */
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }
    
    /* (non-Javadoc)
     * @see android.widget.ExpandableListAdapter#getGroupView(int, boolean, android.view.View, android.view.ViewGroup)
     */
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String group = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.group_layout, null);
        }
        TextView tv = (TextView) convertView.findViewById(R.id.tvGroup);
        tv.setText(group);
        return convertView;
    }
    
    /* (non-Javadoc)
     * @see android.widget.ExpandableListAdapter#hasStableIds()
     */
    @Override
    public boolean hasStableIds() {
        return true;
    }
    
    /* (non-Javadoc)
     * @see android.widget.ExpandableListAdapter#isChildSelectable(int, int)
     */
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
    
}
