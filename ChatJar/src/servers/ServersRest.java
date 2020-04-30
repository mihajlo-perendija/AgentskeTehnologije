package servers;

import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;

import javax.ejb.Local;
import javax.ejb.LocalBean;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Path;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import beans.DataBean;
import model.Node;
import model.User;

@Stateless
//@DependsOn("DataBean")
@Remote(ServersRestRemote.class)
@Local(ServersRestLocal.class)
@Path("/connection")
@LocalBean
public class ServersRest implements ServersRestRemote, ServersRestLocal {
	
	@Inject
	NodeManager nodeManager;
	
	@Inject
	DataBean data;

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
			System.out.println("New node not responding... Deleting node from cluster...");

			ResteasyClient client = new ResteasyClientBuilder().build();
			for (Node n : nodeManager.getNodes()) {
				if (!n.getAlias().equals(node.getAlias())) {
					ResteasyWebTarget rtarget = client.target("http://" + n.getAddress() + "/ChatWar/rest/connection");
					ServersRestRemote rest = rtarget.proxy(ServersRestRemote.class);
					rest.deleteNode(node.getAlias());
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
	public boolean setNodes(List<Node> nodes) {
		nodeManager.setNodes(nodes);
		return true;
	}
	
	@Override
	public boolean setLoggedIn(HashMap<String, User> loggedIn) {
		System.out.println("Logged in users updated");
		data.setLoggedInUsers(loggedIn);
		return true;
	}
	
	@Override
	public HashMap<String, User> getLoggedIn() {
		return data.getLoggedInUsers();
	}

	@Override
	public boolean deleteNode(String alias) {
		System.out.println("Deleting node: " + alias + " from cluster");
		int index = IntStream.range(0, nodeManager.getNodes().size())
			     .filter(i -> nodeManager.getNodes().get(i).getAlias().equals(alias))
			     .findFirst()
			     .orElse(-1);
		if (index != -1) {
			nodeManager.getNodes().remove(index);
		}
		return true;
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
		data.getRegisteredUsers().put(user.getUsername(), user);
	}
	
	public Node getNode() {
		return nodeManager.getNode();
	}
}
