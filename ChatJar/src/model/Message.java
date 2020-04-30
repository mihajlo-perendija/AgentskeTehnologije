package model;

import java.io.Serializable;

public class Message implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String sender;
	private String receiver;
	private long timeStamp;
	private String text;
	private boolean sentOverNetwork = false;
	
	public Message() {
		super();
	}
	
	public Message(String sender, String receiver, long timeStamp, String text) {
		super();
		this.sender = sender;
		this.receiver = receiver;
		this.timeStamp = timeStamp;
		this.text = text;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
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

	public boolean isSentOverNetwork() {
		return sentOverNetwork;
	}

	public void setSentOverNetwork(boolean sentOverNetwork) {
		this.sentOverNetwork = sentOverNetwork;
	}
}
