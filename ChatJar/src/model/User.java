package model;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String username;
	private String password;
	private ArrayList<Message> recievedMessages = new ArrayList<Message>();

	public User() {
		super();
	}

	public User(String username, String password, ArrayList<Message> recievedMessages) {
		super();
		this.username = username;
		this.password = password;
		this.recievedMessages = recievedMessages;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public ArrayList<Message> getRecievedMessages() {
		return recievedMessages;
	}

	public void setRecievedMessages(ArrayList<Message> recievedMessages) {
		this.recievedMessages = recievedMessages;
	}
	
	
	
}
