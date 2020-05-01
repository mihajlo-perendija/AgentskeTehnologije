package servers;

import java.util.HashMap;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import model.Node;
import model.User;

public interface ServersRestRemote {

	@POST
	@Path("/register")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<Node> registerNode(Node node);
	
	@POST
	@Path("/node")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public void newNodeConnected(Node node);
	
	@POST
	@Path("/nodes")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public void setNodes(List<Node> nodes);
	
	@POST
	@Path("/users/loggedIn")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public void setLoggedIn(HashMap<String, User> loggedIn);
	
	@GET // Called only in handshake by new registered node
	@Path("/users/loggedIn")
	@Produces(MediaType.APPLICATION_JSON)
	public HashMap<String, User> getLoggedIn();
	
	@GET // Called only in handshake by new registered node
	@Path("/users/registered")
	@Produces(MediaType.APPLICATION_JSON)
	public HashMap<String, User> getRegistered();
	
	@POST
	@Path("/users/register")
	@Consumes(MediaType.APPLICATION_JSON)
	public void newUserRegistered(User user);
	
	@DELETE
	@Path("/node/{alias}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public void deleteNode(@PathParam("alias") String alias);
	
	@GET
	@Path("/node")
	@Produces(MediaType.APPLICATION_JSON)
	public String getNodeHealth();
	
}
