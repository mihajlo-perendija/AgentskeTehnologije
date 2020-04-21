package beans;

import java.util.ArrayList;
import java.util.Collection;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import model.Message;
import model.User;

@Stateless
@Path("/chat")
@LocalBean
public class ChatBean {
	
//	@Resource(mappedName = "java:/ConnectionFactory")
//	private ConnectionFactory connectionFactory;
//	@Resource(mappedName = "java:jboss/exported/jms/queue/mojQueue")
//	private Queue queue;
//	
//	@GET
//	@Path("/test")
//	@Produces(MediaType.TEXT_PLAIN)
//	public String test() {
//		return "OK";
//	}
//	
//	@POST
//	@Path("/post/{text}")
//	@Produces(MediaType.TEXT_PLAIN)
//	public String post(@PathParam("text") String text) {
//		try {
//			QueueConnection connection = (QueueConnection) connectionFactory.createConnection("guest", "guest.guest.1");
//			QueueSession session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
//			QueueSender sender = session.createSender(queue);
//			
//			TextMessage msg = session.createTextMessage();
//			msg.setText(text);
//			sender.send(msg);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return "OK";
//	}
	
	@EJB
	private DataBean data;
	
	@POST
	@Path("/users/register")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response register(User user) {
		User check = data.getRegisteredUsers().get(user.getUsername());
		if (check != null) {
			return Response.status(Response.Status.BAD_REQUEST).entity("Username already exists").build();
		}
		
		data.getRegisteredUsers().put(user.getUsername(), user);
		
		// Log ------------
		System.out.println("User { " + user.getUsername() + " } registered");
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
		
		// Log ------------
		System.out.println("User { " + checkRegistered.getUsername() + " } logged IN");
		// -----------------

		return Response.status(Response.Status.OK).entity(checkRegistered).build();
	}
	
	@DELETE
	@Path("/users/loggedIn/{username}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response logout(@PathParam("username") String username) {
		System.out.println(username);
		User checkLoggedIn = data.getLoggedInUsers().get(username);
		if (checkLoggedIn == null ) {
			return Response.status(Response.Status.BAD_REQUEST).entity("Not logged in").build();
		}
		
		data.getLoggedInUsers().remove(username);
		
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
	
	@POST
	@Path("/messages/all")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response sendMessageAll(Message message) {
		if (message == null || message.getSender() == null || !message.getReciever().equals("ALL")) {
			return Response.status(Response.Status.BAD_REQUEST).entity("Invalid message").build();
		}
		if (!data.getLoggedInUsers().containsKey(message.getSender())){
			return Response.status(Response.Status.BAD_REQUEST).entity("Not logged in").build();
		}		
		for (User user: data.getRegisteredUsers().values()) {
			user.getMessages().add(message);
		}
		
		// Log ------------
		System.out.println("Sent message { sender: " + message.getSender() + " text: " + message.getText() + " time: " + message.getTimeStamp() + " TO: " + message.getReciever() + " }");
		// -----------------
		
		return Response.status(Response.Status.OK).build();
	}
	
	@POST
	@Path("/messages/user")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response sendMessageUser(Message message) {
		if (message == null || message.getSender() == null || message.getReciever() == null) {
			return Response.status(Response.Status.BAD_REQUEST).entity("Invalid message").build();
		}
		User sender = data.getLoggedInUsers().get(message.getSender());
		if (sender == null){
			return Response.status(Response.Status.BAD_REQUEST).entity("Not logged in").build();
		}
		sender.getMessages().add(message);

		User reciever = data.getRegisteredUsers().get(message.getReciever());
		if (reciever == null) {
			return Response.status(Response.Status.BAD_REQUEST).entity("Invalid reciever").build();
		}
		reciever.getMessages().add(message);
		
		// Log ------------
		System.out.println("Sent message { sender: " + message.getSender() + " text: " + message.getText() + " time: " + message.getTimeStamp() + " TO: " + message.getReciever() + " }");
		// -----------------
		
		return Response.status(Response.Status.OK).build();
	}
	
	@GET
	@Path("/messages/{username}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response messages(@PathParam("username") String username) {
		User user = data.getRegisteredUsers().get(username);
		if (user == null){
			return Response.status(Response.Status.BAD_REQUEST).entity("Invalid user").build();
		}
		ArrayList<Message> messages = user.getMessages();
		// Log ------------
		System.out.println( username + " messages: ");
		for(Message message: messages) {
			System.out.println("{ sender:" + message.getSender() + " text: " + message.getText() + " time: " + message.getTimeStamp() + " TO: " + message.getReciever() + " }");
		}
		// -----------------
		
		return Response.status(Response.Status.OK).build();
	}
	
}
