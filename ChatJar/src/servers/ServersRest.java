package servers;

import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.LocalBean;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ws.rs.Path;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import beans.DataBean;
import model.Node;
import model.User;
import ws.WSEndpoint;

@Stateless
@Remote(ServersRestRemote.class)
@Local(ServersRestLocal.class)
@Path("/connection")
@LocalBean
public class ServersRest implements ServersRestRemote, ServersRestLocal {
	
	@EJB
	private NodeManager nodeManager;
	
	@EJB
	private DataBean data;
	
	@EJB
	private WSEndpoint ws;

	@Override
	public List<Node> registerNode(Node node) {
		try {
			System.out.println("New node registered: " + node.getAlias() + ", address: " + node.getAddress());
			ResteasyClient client = new ResteasyClientBuilder().build();

			for (Node n : nodeManager.getNodes()) {
				ResteasyWebTarget rtarget = client.target("http://" + n.getAddress() + "/ChatWar/rest/connection");
				ServersRestRemote rest = rtarget.proxy(ServersRestRemote.class);
				rest.newNodeConnected(node);
			}
			nodeManager.getNodes().add(new Node(node.getAlias(), node.getAddress()));
			return nodeManager.getNodes();
		} catch (Exception e) {
			System.out.println("Error while registerning new node... Deleting node from cluster...");

			ResteasyClient client = new ResteasyClientBuilder().build();
			for (Node n : nodeManager.getNodes()) {
				if (!n.getAlias().equals(node.getAlias())) {
					try {
						ResteasyWebTarget rtarget = client.target("http://" + n.getAddress() + "/ChatWar/rest/connection");
						ServersRestRemote rest = rtarget.proxy(ServersRestRemote.class);
						rest.deleteNode(node.getAlias());
					} catch (Exception e1) {
						System.out.println(n.getAlias() + " not responding...");
						e1.printStackTrace();
					}
				}
			}
			this.deleteNode(node.getAlias());
			return null;
		}
	}

	@Override
	public void newNodeConnected(Node node) {
		System.out.println("New node connected: " + node.getAlias() + ", address: " + node.getAddress());
		nodeManager.getNodes().add(new Node(node.getAlias(), node.getAddress()));
	}

	@Override
	public void setNodes(List<Node> nodes) {
		nodeManager.setNodes(nodes);
	}
	
	@Override
	public void setLoggedIn(HashMap<String, User> loggedIn) {
		System.out.println("Logged in users updated");
		data.setLoggedInUsers(loggedIn);
		// WebSocket
		ObjectMapper mapper = new ObjectMapper();
        try {
			String jsonMessage = mapper.writeValueAsString(data.getLoggedInUsers().values());
			ws.updateLoggedInUsers(jsonMessage);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public HashMap<String, User> getLoggedIn() {
		if (!nodeManager.getNode().getAddress().equals(nodeManager.getMaster())) {
			return null;
		}
		return data.getLoggedInUsers();
	}
	
	@Override
	public HashMap<String, User> getRegistered() {
		if (!nodeManager.getNode().getAddress().equals(nodeManager.getMaster())) {
			return null;
		}
		return data.getRegisteredUsers();
	}

	@Override
	public void deleteNode(String alias) {
		int index = IntStream.range(0, nodeManager.getNodes().size())
			     .filter(i -> nodeManager.getNodes().get(i).getAlias().equals(alias))
			     .findFirst()
			     .orElse(-1);
		// Deleting all information, no persistence implemented
		if (index != -1) {
			System.out.println("Deleting node: " + alias + " from cluster");
			Node node = nodeManager.getNodes().get(index);
			data.getLoggedInUsers().values().removeIf( isUserFromHost(node.getAddress()) );
			data.getRegisteredUsers().values().removeIf( isUserFromHost(node.getAddress()) );
			nodeManager.getNodes().remove(index);
			System.out.println("Node: " + alias + " deleted from cluster");
			// WebSocket
			ObjectMapper mapper = new ObjectMapper();
	        try {
				String jsonMessage = mapper.writeValueAsString(data.getLoggedInUsers().values());
				ws.updateLoggedInUsers(jsonMessage);
			} catch (JsonProcessingException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	public static Predicate<User> isUserFromHost(String address) 
	{
	    return user -> user.getHost().equals(address);
	}

	@Override
	public String getNodeHealth() {
		System.out.println("Node health check");
		return nodeManager.getNode().getAlias();
	}
	
	public void informNodesAboutLoggedInUsers() {
		ResteasyClient client = new ResteasyClientBuilder().build();
		for (Node node: nodeManager.getNodes()) {
			if (node.getAlias().equals(nodeManager.getNode().getAlias())) {
        		continue;
        	}
			ResteasyWebTarget rtarget = client.target("http://" + node.getAddress() + "/ChatWar/rest/connection");
			ServersRestRemote rest = rtarget.proxy(ServersRestRemote.class);
			rest.setLoggedIn(data.getLoggedInUsers());
		}
		if (!nodeManager.getNode().getAddress().equals(nodeManager.getMaster())) {
			ResteasyWebTarget rtarget = client.target("http://" + nodeManager.getMaster() + "/ChatWar/rest/connection");
			ServersRestRemote rest = rtarget.proxy(ServersRestRemote.class);
			rest.setLoggedIn(data.getLoggedInUsers());
		}
	}
	
	public void informNodesNewUserRegistered(User user) {
		ResteasyClient client = new ResteasyClientBuilder().build();
		for (Node node: nodeManager.getNodes()) {
			if (node.getAlias().equals(nodeManager.getNode().getAlias())) {
        		continue;
        	}
			ResteasyWebTarget rtarget = client.target("http://" + node.getAddress() + "/ChatWar/rest/connection");
			ServersRestRemote rest = rtarget.proxy(ServersRestRemote.class);
			rest.newUserRegistered(user);
		}
		if (!nodeManager.getNode().getAddress().equals(nodeManager.getMaster())) {
			ResteasyWebTarget rtarget = client.target("http://" + nodeManager.getMaster() + "/ChatWar/rest/connection");
			ServersRestRemote rest = rtarget.proxy(ServersRestRemote.class);
			rest.newUserRegistered(user);
		}
	}

	@Override
	public void newUserRegistered(User user) {
		System.out.println("New user: " + user.getUsername() + " registered on: " + user.getHost());
		// Saves username and his host, not password. User can sign in only on his main host
		data.getRegisteredUsers().put(user.getUsername(), user);
	}
	
	public Node getNode() {
		return nodeManager.getNode();
	}
}
