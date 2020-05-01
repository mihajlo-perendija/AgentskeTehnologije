package beans;

import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jms.ConnectionFactory;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import messages.AgentManager;
import model.Message;
import model.Node;
import model.User;
import servers.ServersRestLocal;
import ws.WSEndpoint;

@Stateless
@Path("/chat")
@LocalBean
@Remote(ChatBeanRemote.class)
public class ChatBean implements ChatBeanRemote{
	
	@Resource(mappedName = "java:/ConnectionFactory")
	private ConnectionFactory connectionFactory;
	@Resource(mappedName = "java:jboss/exported/jms/queue/mojQueue")
	private Queue queue;
	
	@EJB
	private DataBean data;
	@EJB
	private AgentManager agents;
	@EJB
	private ServersRestLocal serversRest;
	@EJB
	private WSEndpoint ws;

	
	@POST
	@Path("/users/register")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response register(User user) {
		User check = data.getRegisteredUsers().get(user.getUsername());
		if (check != null) {
			return Response.status(Response.Status.BAD_REQUEST).entity("Username already exists").build();
		}

		Node node = serversRest.getNode();
		User registered = new User( user.getUsername(), node.getAddress(), user.getPassword());
		data.getRegisteredUsers().put(registered.getUsername(), registered);
		agents.createNewAgent(registered.getUsername(), registered);
		
		serversRest.informNodesNewUserRegistered(registered);
		
		// Log ------------
		System.out.println("User { " + registered.getUsername() + " } registered");
		// -----------------
		return Response.status(Response.Status.OK).build();
	}
	
	@POST
	@Path("/users/login")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response login(User user) {
		User checkRegistered = data.getRegisteredUsers().get(user.getUsername());
		if (checkRegistered == null || !checkRegistered.getPassword().equals(user.getPassword())) {
			return Response.status(Response.Status.BAD_REQUEST).entity("Invalid username or password").build();
		}
		
		data.getLoggedInUsers().put(checkRegistered.getUsername(), checkRegistered);
		
		serversRest.informNodesAboutLoggedInUsers();
		
		// WebSocket
		ObjectMapper mapper = new ObjectMapper();
        try {
			String jsonMessage = mapper.writeValueAsString(data.getLoggedInUsers().values());
			ws.updateLoggedInUsers(jsonMessage);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
        
		
		// Log ------------
		System.out.println("User { " + checkRegistered.getUsername() + " } logged IN");
		// -----------------

		return Response.status(Response.Status.OK).entity(checkRegistered).build();
	}
	
	@DELETE
	@Path("/users/loggedIn/{username}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response logout(@PathParam("username") String username) {
		User checkLoggedIn = data.getLoggedInUsers().get(username);
		if (checkLoggedIn == null ) {
			return Response.status(Response.Status.BAD_REQUEST).entity("Not logged in").build();
		}
		
		data.getLoggedInUsers().remove(username);
		
		serversRest.informNodesAboutLoggedInUsers();
		
		// WebSocket
		ObjectMapper mapper = new ObjectMapper();
        try {
			String jsonMessage = mapper.writeValueAsString(data.getLoggedInUsers().values());
			ws.updateLoggedInUsers(jsonMessage);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		// Log ------------
		System.out.println("User { " + username + " } logged OUT");
		// -----------------

		return Response.status(Response.Status.OK).build();
	}

	@GET
	@Path("/users/loggedIn")
	@Produces(MediaType.APPLICATION_JSON)
	public Response loggedIn() {
		Collection<User> retVal = (Collection<User>) data.getLoggedInUsers().values();
		
		// Log ------------
		System.out.println("----------------");
		System.out.println("Logged in: ");
		for(User u: data.getLoggedInUsers().values()) {
			System.out.println(u.getId() + " " + u.getUsername() + " " + u.getPassword());
		}
		System.out.println("----------------");
		// -----------------
		
		return Response.status(Response.Status.OK).entity(retVal).build();
	}
	
	@GET
	@Path("/users/registered")
	@Produces(MediaType.APPLICATION_JSON)
	public Response registered() {
		Collection<User> retVal = (Collection<User>) data.getRegisteredUsers().values();
		
		// Log ------------
		System.out.println("----------------");
		System.out.println("Registered: ");
		for(User u: data.getRegisteredUsers().values()) {
			System.out.println(u.getId() + " " +u.getUsername() + " " + u.getPassword());
		}
		System.out.println("----------------");
		// -----------------
		
		return Response.status(Response.Status.OK).entity(retVal).build();
	}
	
	@Override
	public Response sendMessageAll(Message message) {
		if (message == null || message.getSender() == null || !message.getReceiver().equals("ALL")) {
			return Response.status(Response.Status.BAD_REQUEST).entity("Invalid message").build();
		}
		if (!data.getLoggedInUsers().containsKey(message.getSender())){
			return Response.status(Response.Status.BAD_REQUEST).entity("Not logged in").build();
		}
		
		try {
			QueueConnection connection = (QueueConnection) connectionFactory.createConnection("guest", "guest.guest.1");
			QueueSession session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
			QueueSender msgSender = session.createSender(queue);
			
			ObjectMessage msg = session.createObjectMessage();
			msg.setObject(message);
			msgSender.send(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String type = message.isSentOverNetwork() ? " / over network }" : "/ same host }";

		// Log ------------
		System.out.println("Sent message { sender: " + message.getSender() + " text: " + message.getText()
		+ " time: " + message.getTimeStamp() + " TO: " + message.getReceiver() +  type);
		// -----------------
		
		return Response.status(Response.Status.OK).build();
	}
	
	@Override
	public Response sendMessageUser(Message message) {
		if (message == null || message.getSender() == null || message.getReceiver() == null) {
			return Response.status(Response.Status.BAD_REQUEST).entity("Invalid message").build();
		}
		User sender = data.getLoggedInUsers().get(message.getSender());
		if (sender == null){
			return Response.status(Response.Status.BAD_REQUEST).entity("Not logged in").build();
		}

		User reciever = data.getRegisteredUsers().get(message.getReceiver());
		if (reciever == null) {
			return Response.status(Response.Status.BAD_REQUEST).entity("Invalid reciever").build();
		}
		
		String type = message.isSentOverNetwork() ? " / over network }" : "/ same host }";
		// Log ------------
		System.out.println("Sent message { sender: " + message.getSender() + " text: " + message.getText() +
				" time: " + message.getTimeStamp() + " TO: " + message.getReceiver() + type);
		// -----------------
		
		try {
			QueueConnection connection = (QueueConnection) connectionFactory.createConnection("guest", "guest.guest.1");
			QueueSession session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
			QueueSender msgSender = session.createSender(queue);
			
			ObjectMessage msg = session.createObjectMessage();
			msg.setObject(message);
			msgSender.send(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return Response.status(Response.Status.OK).build();
	}
	
	@GET
	@Path("/messages/{username}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response messages(@PathParam("username") String username) {
		User user = data.getRegisteredUsers().get(username);
		if (user == null){
			return Response.status(Response.Status.BAD_REQUEST).entity("Invalid user").build();
		}
		ArrayList<Message> messages = user.getMessages();
		// Log ------------
		System.out.println( username + " messages: ");
		for(Message message: messages) {
			System.out.println("{ sender:" + message.getSender() + " text: " + message.getText() + " time: " + message.getTimeStamp() + " TO: " + message.getReceiver() + " }");
		}
		// -----------------
		
		return Response.status(Response.Status.OK).entity(messages).build();
	}
	
}
