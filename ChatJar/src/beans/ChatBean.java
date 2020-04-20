package beans;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.websocket.server.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.Body;

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
			return Response.status(Response.Status.BAD_REQUEST).header("Access-Control-Allow-Origin", "*").entity("Username already exists").build();
		}
		
		data.getRegisteredUsers().put(user.getUsername(), user);		
		return Response.status(Response.Status.OK).header("Access-Control-Allow-Origin", "*").build();
	}
	
	@POST
	@Path("/users/login")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response login(User user) {
		User check = data.getRegisteredUsers().get(user.getUsername());
		if (check == null || !check.getPassword().equals(user.getPassword())) {
			return Response.status(Response.Status.BAD_REQUEST).entity("Invalid username or password").build();
		}
		
		data.getLoggedInUsers().put(user.getUsername(), user);		
		user.setPassword(null);
		return Response.status(Response.Status.OK).entity(user).header("Access-Control-Allow-Origin", "*").build();
	}
	
	@OPTIONS
	@Path("{path : .*}")
	public Response options() {
	    return Response.ok("")
	            .header("Access-Control-Allow-Origin", "*")
	            .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
	            .header("Access-Control-Allow-Credentials", "true")
	            .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
	            .header("Access-Control-Max-Age", "1209600")
	            .build();
	}
}
