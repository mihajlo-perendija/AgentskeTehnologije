package model;

import java.io.Serializable;

public class Message implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String sender;
	private String reciever;
	private long timeStamp;
	private String text;
	
	public Message() {
		super();
	}
	
	public Message(String sender, String reciever, long timeStamp, String text) {
		super();
		this.sender = sender;
		this.reciever = reciever;
		this.timeStamp = timeStamp;
		this.text = text;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getReciever() {
		return reciever;
	}

	public void setReciever(String reciever) {
		this.reciever = reciever;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
