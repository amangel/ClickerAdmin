package com.clicker.admin;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.ProgressDialog;
import android.util.Log;
import android.widget.Toast;
import android.os.Handler;
import android.os.Message;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;

import java.util.Timer;
import java.util.TimerTask;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.FileReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

public class AdminApplication extends Application{
	
	private boolean waitingForHB = false;

	private Socket serverSocket;
	private Map<String, Question> questions;
	private ArrayList<Question> orderedQuestions;
	private InputStream questionInput;
	private BufferedReader in;
	private PrintWriter out;
	private Handler subHandler;
	private Set displaySet;
	private String goingToBeOpened;
	private boolean[] dSelects;
	private String[] groupOptions;
	private boolean[] groupSelects;
	private String[] dOptions;
	private String[] groupNames = {};
	private Map<String, String> groupQuestions;
	private Activity activityForDialog;
	private String groupsToSend;
	private Timer heartbeatTimer;
	private boolean everyoneOnQuestion;
	
	private String username;
	private String password;
	private InetAddress ip;
	private ProgressDialog reconnectingDialog;
	
	private boolean isConnected;
	
	public static final int CLIENT_LIST_RECEIVED = 100;
	public static final int ALL_SETS_RECEIVED = 200;
	public static final int QUESTION_SET_RECEIVED = 300;
	public static final int GOT_DISCONNECTED = 400;
	public static final int RECONNECT_SUCCESS = 500;
	public static final int RECONNECT_FAILED = 600;
	private static final int heartbeatSeconds = 15;
	
	public AdminApplication() {
		super();
		//questions = Collections.synchronizedMap(new HashMap<String, Question>(50));
		//orderedQuestions = new ArrayList<Question>();
		questionInput = null;
		serverSocket = null;
		in = null;
		out = null;
		subHandler = null;
		displaySet = new HashSet();
		goingToBeOpened = "";
		dOptions = null;
		dSelects = null;
		groupOptions = null;
		groupSelects = null;
		groupQuestions = Collections.synchronizedMap(new HashMap<String, String>(50));
		//groupQuestions.put("Ungrouped","");
		activityForDialog = null;
		groupsToSend = "";
		heartbeatTimer = new Timer();
		everyoneOnQuestion = false;
		isConnected = false;
		
		username = null;
		password = null;
		ip = null;
		reconnectingDialog = null;
	}
	
	public void setSocket(Socket serverSocket) {
		this.isConnected = true;
		this.serverSocket = serverSocket;
		try {
			this.in = new BufferedReader(new InputStreamReader(this.serverSocket.getInputStream()));
			this.out = new PrintWriter(this.serverSocket.getOutputStream());
		} catch (IOException e) {System.out.println("Unable to create input and output streams");}	
	}
	
	public void setSubHandler(Handler h) {
		this.subHandler = h;
	}
	
	public void sendMessage(String message) {
		while (!isConnected) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		out.println(message);
		out.flush();
	}
	
	public void gotDisconnected() {
		this.waitingForHB = false;
		this.heartbeatTimer.cancel();
		this.isConnected = false;
		this.subHandler.sendEmptyMessage(GOT_DISCONNECTED);
		
		
	}
	
	public boolean amConnected() {
		return this.isConnected;
		
	}
	
	public void reconnect(Activity runningActivity) {
		reconnectingDialog = ProgressDialog.show(runningActivity, "", 
                "Connecting to server...", true);
		new Thread(new Reconnecter()).start();
	}
	
	public void setConnectionInfo(String username, String password, InetAddress ip) {
		this.username = username;
		this.password = password;
		this.ip = ip;
	}
	
	
	//This should maybe be called from reconnect thread if successful?
	public void startListening() {
		new Thread(new ServerMessageHandler()).start();
		heartbeatTimer = new Timer();
		heartbeatTimer.scheduleAtFixedRate(new HeartbeatTask(), 15000, heartbeatSeconds * 1000);
	}
	
	public void openQuestion(String qString, Activity runningActivity) {
		goingToBeOpened = qString;
		activityForDialog = runningActivity;
		if (groupNames.length != 0) {
			groupOptions = new String[groupNames.length + 1];
			groupOptions[0] = "Everyone";
			for (int i=0; i<groupNames.length; i++) {
				groupOptions[i + 1] = groupNames[i];
			}
			groupSelects = new boolean[groupOptions.length];
			AlertDialog.Builder coBuilder = new AlertDialog.Builder(runningActivity);
			coBuilder.setTitle("Select Group");
			coBuilder.setMultiChoiceItems(groupOptions, groupSelects, new DialogSelectionClickHandler());
			coBuilder.setPositiveButton("OK", new GroupButtonClickHandler());
			coBuilder.create();
			coBuilder.show();
		} else {
			groupsToSend = "Everyone";
			selectDisplayPlugins();
		}
	}
	
	public void selectDisplayPlugins() {
		if (displaySet.size() == 0) {
			out.println("ClientCommand`/`" + goingToBeOpened + "`/&`/&" + groupsToSend);
			Log.d("SOCKET", "ClientCommand`/`" + goingToBeOpened + "`/&`/&" + groupsToSend);
			everyoneOnQuestion = groupsToSend.equals("Everyone");			
			out.flush();
			String[] openedParts = goingToBeOpened.split("`/;");
			String[] openedGroups = groupsToSend.split("`/,");
			for (int i=0; i<openedGroups.length; i++) {
				groupQuestions.put(openedGroups[i], openedParts[1]);
			}
			goingToBeOpened = "";
			groupsToSend = "";
		} else {
			Object[] displayArray = displaySet.toArray();
			dOptions = new String[displayArray.length];
			for (int i=0; i<displayArray.length; i++) {
				dOptions[i] = (String)displayArray[i];
			}
			dSelects = new boolean[dOptions.length];
			AlertDialog.Builder coBuilder = new AlertDialog.Builder(activityForDialog);
			coBuilder.setTitle("Select Consumers");
			coBuilder.setMultiChoiceItems(dOptions, dSelects, new DialogSelectionClickHandler());
			coBuilder.setPositiveButton("OK", new DisplayButtonClickHandler());
			coBuilder.create();
			coBuilder.show();
		}
	}
	
	public void closeQuestion(Activity runningActivity) {
		activityForDialog = runningActivity;
		if (everyoneOnQuestion) {
			out.println("ClientCommand`/`Close`/;Everyone`/,");
            out.flush();
            everyoneOnQuestion = false;
		}
		else if (groupQuestions.size() > 0) {
			groupOptions = new String[groupQuestions.size()];
			int groupOptCount = 0;
			Iterator<String> gqIter = groupQuestions.keySet().iterator();
			while (gqIter.hasNext()) {
				groupOptions[groupOptCount] = gqIter.next();
				groupOptCount++;
			}
			for (int i=0; i<groupOptions.length; i++) {
				Log.d("OPTION",groupOptions[i]);
			}
			groupSelects = new boolean[groupOptions.length];
			AlertDialog.Builder coBuilder = new AlertDialog.Builder(runningActivity);
			coBuilder.setTitle("Select Group");
			coBuilder.setMultiChoiceItems(groupOptions, groupSelects, new DialogSelectionClickHandler());
			coBuilder.setPositiveButton("OK", new GroupCloseButtonClickHandler());
			coBuilder.create();
			coBuilder.show();
		} else {
			out.println("ClientCommand`/`Close`/;Everyone`/,");
			Log.d("QC", "Not sure how we could get here");
            out.flush();
		}
	}
	
	public class DisplayButtonClickHandler implements DialogInterface.OnClickListener
	{
		public void onClick( DialogInterface dialog, int clicked ) {
			//Log.d("Balls", "inOnClick");
			switch( clicked ) {
				case DialogInterface.BUTTON_POSITIVE:
					Log.d("Balls", "inButtonPositive");
					Log.d("Balls", "q: " + goingToBeOpened);
					goingToBeOpened += "`/&";
					for (int i=0; i<dSelects.length; i++) {
						if (dSelects[i]) {
							goingToBeOpened += dOptions[i] + "`/,";
						}
					}
					Log.d("Balls", "qafter: " + goingToBeOpened);
					if (groupsToSend.equals("Everyone")) {
						String[] newQParts = goingToBeOpened.split("`/;");
						newQParts[2] = "e";
						goingToBeOpened = "";
						for (int i=0; i<newQParts.length; i++) {
							goingToBeOpened += newQParts[i] + "`/;";
						}
					}
					out.println("ClientCommand`/`" + goingToBeOpened + "`/&" + groupsToSend);
					Log.d("SOCKET", "ClientCommand`/`" + goingToBeOpened + "`/&" + groupsToSend);
					everyoneOnQuestion = groupsToSend.equals("Everyone");
					out.flush();
					dOptions = null;
					dSelects = null;
					Log.d("Balls", groupsToSend);
					String[] openedParts = goingToBeOpened.split("`/;");
					String[] openedGroups = groupsToSend.split("`/,");
					for (int i=0; i<openedGroups.length; i++) {
						groupQuestions.put(openedGroups[i], openedParts[1]);
					}
					goingToBeOpened = "";
					groupsToSend = "";
			}
		}
	}
	
	public class GroupButtonClickHandler implements DialogInterface.OnClickListener
	{
		
		public void onClick( DialogInterface dialog, int clicked ) {
			//Log.d("Balls", "inOnClick");
			switch( clicked ) {
				case DialogInterface.BUTTON_POSITIVE:
					for (int i=0; i<groupSelects.length; i++) {
						if (groupSelects[i]) {
							groupsToSend += groupOptions[i] + "`/,";
						}
					}
					groupsToSend = groupsToSend.substring(0, groupsToSend.length() - 3);
					Log.d("Balls", groupsToSend);
					groupOptions = null;
					groupSelects = null;
					selectDisplayPlugins();
			}
		}
	}
	
	public class GroupCloseButtonClickHandler implements DialogInterface.OnClickListener
	{
		
		public void onClick( DialogInterface dialog, int clicked ) {
			//Log.d("Balls", "inOnClick");
			switch( clicked ) {
				case DialogInterface.BUTTON_POSITIVE:
					for (int i=0; i<groupSelects.length; i++) {
						if (groupSelects[i]) {
							Log.d("OPTIONS", "adding " + groupOptions[i] + " to gorups to send");
							groupsToSend += groupOptions[i] + "`/,";
							groupQuestions.remove(groupOptions[i]);
						}
					}
					Log.d("OPTIONS", groupsToSend);
					out.println("ClientCommand`/`Close`/;" + groupsToSend);
					out.flush();
					groupOptions = null;
					groupSelects = null;
					groupsToSend = "";
			}
		}
	}
	
	
	public class DialogSelectionClickHandler implements DialogInterface.OnMultiChoiceClickListener
	{
		public void onClick( DialogInterface dialog, int clicked, boolean isChecked ) {}
	}
	
/*	public void setQuestionInput(InputStream qIn) {
		if (questionInput == null) {
			questionInput = qIn;
			try {
				Log.d("AA","Starting file load");		
				BufferedReader in = new BufferedReader(new InputStreamReader(questionInput));
				String str;
				while ((str = in.readLine()) != null) {
					Log.d("AA","Loading a question");
					Log.d("AA",str);
					String[] questionParts = str.split("~");
					Log.d("AA","" + questionParts.length);
					Question newQuestion = new Question(questionParts[0]);
					questions.put(newQuestion.getID(), newQuestion);
					orderedQuestions.add(newQuestion);
					if (questionParts.length > 1) {
						String previous = newQuestion.getID();
						for (int i=1; i<questionParts.length; i++) {
							newQuestion = new Question(questionParts[i]);
							String newID = newQuestion.getID();
							questions.get(previous).setFollowUp(newID);
							questions.put(newID, newQuestion);
							previous = newID;
						}
					}
				}
				in.close();
			} catch (IOException e) {
				Log.d("AA","In file exception");
				Log.d("AA",e.getMessage());
			}
		}
	}*/
	
	
	public Socket getSocket() {
		return serverSocket;
	}
	
	public void updateGroups(String groupList) {
		groupNames = groupList.split("`/;");
		updateGroupQuestions();
	}
	
	public void updateGroupQuestions() {
		Iterator<String> gqIter = groupQuestions.keySet().iterator();
		while (gqIter.hasNext()) {
			String next = gqIter.next();
			boolean found = false;
			for (int i=0; i<groupNames.length; i++) {
				if (groupNames[i].equals(next)) {
					found = true;
					break;
				}
			}
			if (!found) {
				groupQuestions.remove(next);
			}
		}

	}
	
	/*public Map<String, Question> getQuestions() {
		return questions;
	}
	
	public ArrayList<Question> getOrdered() {
		return orderedQuestions;
	}*/
	
	private class HeartbeatTask extends TimerTask {
		public void run() {
			Log.d("hb","In heartbeattask");
			if (waitingForHB) {
				Log.d("hb","Got no heartbeat response");
				try {
					serverSocket.close();
				} catch (IOException e) {}
				gotDisconnected();
			} else {
				waitingForHB = true;
				Log.d("hb","Sending heartbeat message");
				out.println("AreYouStillThere");
				out.flush();
			}
		}
	}
	
	
	private class ServerMessageHandler implements Runnable {
		
		public void run() {
			while (isConnected) {
				try {
					String str = in.readLine();
					if (str == null) {
						Log.d("hb", "Read null in message Handler");
						gotDisconnected();
						//Thread.sleep(200);
						break;
					} else if (str.equals("YesImHere")){
						Log.d("hb", "Read hb response in message Handler");
						waitingForHB = false;
					} else {
						Log.d("hb", "Read something else in message Handler");
						String[] parts = str.split("`/`");
						if (parts[0].equals("Response")) {
							//process response
						} else if (parts[0].equals("AllSets")) {
							Log.d("GOTMESSAGE",str);
							Message mess;
							if (parts.length > 0) {
								mess = Message.obtain(subHandler, ALL_SETS_RECEIVED, parts[1]);
							} else {
								mess = Message.obtain(subHandler, ALL_SETS_RECEIVED, "");
							}
							subHandler.sendMessage(mess);
						} else if (parts[0].equals("QuestionSet")) {
							Log.d("BALLS", "Got questions in set");
							Message mess;
							if (parts.length > 1) {
								mess = Message.obtain(subHandler, QUESTION_SET_RECEIVED, parts[1]);
							} else {
								mess = Message.obtain(subHandler, QUESTION_SET_RECEIVED, "");
							}
							Log.d("BALLS", "Message is: " + mess);
							subHandler.sendMessage(mess);
						} else if (parts[0].equals("ClientList")) {
							Message mess = Message.obtain(subHandler, CLIENT_LIST_RECEIVED, parts[1]);
							subHandler.sendMessage(mess);
						} else if (parts[0].equals("GroupList")) {
							groupNames = parts[1].split("`/;");
							updateGroupQuestions();
						} else if (parts[0].equals("DisplayConnected")) {
							Log.d("BALLS", str);
							String[] consumptionTypes = parts[1].split("`/,");
							for (int i=0; i<consumptionTypes.length; i++) {
								displaySet.add(consumptionTypes[i]);
							}
						}
					}
					Thread.sleep(100);
				} catch (IOException e) {
				} catch (InterruptedException e) {}
				System.out.println("Server message thread should be dying");
			}
		}
	}
	
	private class Reconnecter implements Runnable {
		
		public void run() {
			int retryCount = 0;
			while (!isConnected && retryCount < 2) {
				Log.d("RECONNECT", "In reconnection loop");
				try {				
					SocketAddress sockaddr = new InetSocketAddress(ip, 7700);
					Socket newSocket = new Socket();
					newSocket.connect(sockaddr, 5000);
					newSocket.setKeepAlive(true);
					setSocket(newSocket);
					Log.d("RECONNECT", "Passed setsocket!");
					sendMessage(username);
					sendMessage(password);
				} catch (Exception e) {
					try {
						Log.d("RECONNECT", "Failed to connect, waiting and trying again");
						retryCount++;
						Thread.sleep(100);
					} catch (InterruptedException ie) {}
				}
			}
			Log.d("RECONNECT", "Out of reconnection loop");
			reconnectingDialog.dismiss();
			if (isConnected) {
				Log.d("RECONNECT", "Calling success message");
				startListening();
				subHandler.sendEmptyMessage(RECONNECT_SUCCESS);		
			} else {
				Log.d("RECONNECT", "Calling failed message");
				subHandler.sendEmptyMessage(RECONNECT_FAILED);
			}
		}
		
	}
	
}
