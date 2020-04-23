package model;

import java.io.Serializable;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import beans.IdGenerator;

public class User implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private long id = IdGenerator.getNextId();
	private String username;
	@JsonProperty(access = Access.WRITE_ONLY)
	private String password;
	@JsonProperty(access = Access.WRITE_ONLY)
	private ArrayList<Message> messages = new ArrayList<Message>();

	public User() {
		super();
	}

	public User(String username, String password, ArrayList<Message> messages) {
		super();
		this.username = username;
		this.password = password;
		this.messages = messages;
	}
	
	public long getId() {
		return id;
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

	public ArrayList<Message> getMessages() {
		return messages;
	}

	public void setMessages(ArrayList<Message> messages) {
		this.messages = messages;
	}

}
