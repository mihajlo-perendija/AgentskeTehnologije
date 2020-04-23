package messages;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;

import model.User;

public class Agent{
	
	private User user;
	private String username;
	
	public Agent(String username, User user) {
		this.username = username;
		this.user = user;
	}

	public void onMessage(Message msg) {
		
		try {
	        ObjectMessage objectMessage = (ObjectMessage) msg;
	        model.Message message = (model.Message) objectMessage.getObject();
			
			if (message.getSender().equals(this.username)) {
				this.user.getMessages().add(message);
			} else if(message.getReceiver().equals(this.username) || message.getReceiver().equals("ALL")) {
				this.user.getMessages().add(message);
			}
		} catch (JMSException e) {
			e.printStackTrace();
		} 
		
	}

}
