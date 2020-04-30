package messages;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import beans.ChatBeanRemote;
import beans.DataBean;
import model.Node;
import model.User;
import servers.NodeManager;
import ws.WSEndpoint;


@MessageDriven(activationConfig = {
	@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
	@ActivationConfigProperty(propertyName = "destination", propertyValue = "jms/queue/mojQueue")
})
public class MDBConsumer implements MessageListener{
	
	@EJB
	AgentManager agents;
	
	@EJB
	WSEndpoint ws;
	
	@EJB
	NodeManager nodeManager;
	
	@EJB
	DataBean data;

	@Override
	public void onMessage(Message msg) {
		try {
	        ObjectMessage objectMessage = (ObjectMessage) msg;
	        model.Message message = (model.Message) objectMessage.getObject();
	        
			ObjectMapper mapper = new ObjectMapper();
	        String jsonMessage = mapper.writeValueAsString(message);
	        
	        if (!message.getReceiver().equals("ALL")) {
	        	Agent sender = this.agents.getAgentByUsername(message.getSender());
	        	
	        	// Sender is always on its host, this check ensures there is no error when sending message to the other host
	        	if (sender != null) {
	        		sender.onMessage(msg);
			        ws.sendToOne(message.getSender(), jsonMessage);
	        	}
		        
		        Agent reciever = this.agents.getAgentByUsername(message.getReceiver());
		        if (reciever != null) {
		        	reciever.onMessage(msg);
			        ws.sendToOne(message.getReceiver(), jsonMessage);
		        } else {
		        	User user = data.getRegisteredUsers().get(message.getReceiver());
		        	ResteasyClient client = new ResteasyClientBuilder().build();
					ResteasyWebTarget rtarget = client.target("http://" + user.getHost() + "/ChatWar/rest/chat");
					ChatBeanRemote rest = rtarget.proxy(ChatBeanRemote.class);
					rest.sendMessageUser(message);
		        }
	        } else {
	        	for(Agent agent: agents.getAgents().values()) {
	        		agent.onMessage(msg);
	        	}
	        	ws.broadcast(jsonMessage);
	        	// Preventing loop, message gets redirected back to originating host
	        	if (message.isSentOverNetwork()) {
	        		return;
	        	}
	        	message.setSentOverNetwork(true);
	        	// Send to all hosts over the network except this one
	        	ResteasyClient client = new ResteasyClientBuilder().build();
	        	for(Node node: nodeManager.getNodes()) {
	        		if (node.getAddress().equals(nodeManager.getNode().getAddress())) {
	        			continue;
	        		}
	        		
	        		ResteasyWebTarget rtarget = client.target("http://" + node.getAddress() + "/ChatWar/rest/chat");
					ChatBeanRemote rest = rtarget.proxy(ChatBeanRemote.class);
					rest.sendMessageAll(message);
	        	}
	        	// Send to master, it is not saved in hosts
	        	if (!nodeManager.getMaster().equals(nodeManager.getNode().getAddress())) {
	        		ResteasyWebTarget rtarget = client.target("http://" + nodeManager.getMaster() + "/ChatWar/rest/chat");
					ChatBeanRemote rest = rtarget.proxy(ChatBeanRemote.class);
					rest.sendMessageAll(message);
	        	}
	        }
		} catch (JMSException | JsonProcessingException e) {
			e.printStackTrace();
		}
}

}
