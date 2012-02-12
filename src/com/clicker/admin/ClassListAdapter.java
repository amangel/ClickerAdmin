package com.clicker.admin;

import java.util.ArrayList;
import java.util.Collections;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class ClassListAdapter extends BaseExpandableListAdapter {
	
	private Context context;
	private ArrayList<String> groups;
    private ArrayList<ArrayList<String>> children;
    
    public ClassListAdapter(Context context, ArrayList<String> groups, ArrayList<ArrayList<String>> children) {
        this.context = context;
        this.groups = groups;
        this.children = children;
    }
    
    public void addGroup(String groupName) {
    	groups.add(groupName);
    	children.add(new ArrayList<String>());
    	Log.d("GROUPING", "Adding group: " + groupName + " and now have " + groups.size() + " groups");
    }
    
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
    
    public ArrayList<String> getOtherGroups(int groupPosition) {
    	ArrayList<String> results = new ArrayList<String>();
    	for (int i=0; i<groups.size(); i++) {
    		if (i != groupPosition) {
    			results.add(groups.get(i));
    		}
    	}
    	return results;
    }
    
    public ArrayList<String> getAllStudents() {
    	ArrayList<String> studentNames = new ArrayList<String>();
    	for (int i=0; i<children.size(); i++) {
    		for (int j=0; j<children.get(i).size(); j++) {
    			studentNames.add(children.get(i).get(j));
    		}
    	}
    	return studentNames;
    }
    
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
    
    public void randomizeByGroupSize(int groupSize) {
    	ArrayList<String> studentNames = getAllStudents();
    	int numOfStudents = studentNames.size();
    	int numOfGroups = (int) Math.ceil((float)numOfStudents/(float)groupSize);
    	randomizeGroups(numOfGroups, studentNames);
    }
    
    public void randomizeByNumOfGroups(int numOfGroups) {
    	ArrayList<String> studentNames = getAllStudents();
    	randomizeGroups(numOfGroups, studentNames);
    }
    
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
    
    public void moveStudent(String studentName, String previousGroup, String newGroup) {
    	children.get(groups.indexOf(previousGroup)).remove(studentName);
    	children.get(groups.indexOf(newGroup)).add(studentName);
    }
    
    public void addStudent(String studentName, String groupName) {
    	children.get(groups.indexOf(groupName)).add(studentName);
    	Log.d("GROUPING", "Adding student: " + studentName + " and now have " + children.get(groups.indexOf(groupName)).size() + " students in the group");
    }

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return children.get(groupPosition).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}
	
    // Return a child view. You can load your custom layout here.
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

	@Override
	public int getChildrenCount(int groupPosition) {
		return children.get(groupPosition).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return groups.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return groups.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

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

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

}
