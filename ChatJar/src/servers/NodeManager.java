package servers;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import beans.DataBean;
import model.Node;
import model.User;
import ws.WSEndpoint;

@Singleton
@Startup
@LocalBean
public class NodeManager {
	@EJB
	private DataBean data;
	@EJB
	private WSEndpoint ws;
	
	private String master = null;
	private Node node = new Node();
	
	// Master is not in the "nodes", its IP hardcoded is in all nodes
	// Preventing deleting master and health checking it
	private List<Node> nodes = new ArrayList<Node>();
	
	private Timer heartBeat = new Timer();
	
	@PostConstruct
	private void init() {
		try {
	        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
            ObjectName http = new ObjectName("jboss.as:socket-binding-group=standard-sockets,socket-binding=http");

			this.node.setAddress((String) mBeanServer.getAttribute(http,"boundAddress") + ":8080");
			this.node.setAlias(System.getProperty("jboss.node.name") + ":8080");
			
			// Hardcoded, could use .properties file
			this.master = "192.168.0.15:8080";
			System.out.println("MASTER ADDR: " + master + ", node name: " + this.node.getAlias() + ", node address: " + this.node.getAddress());
			
			if (master != null && !master.equals("") && !master.equals(this.node.getAddress())) {
				System.out.println("Connecting to master...");
				ResteasyClient client = new ResteasyClientBuilder().build();
				ResteasyWebTarget rtarget = client.target("http://" + master + "/ChatWar/rest/connection");
				ServersRestRemote rest = rtarget.proxy(ServersRestRemote.class);
				this.nodes = rest.registerNode(this.node);
				System.out.println("Node registered");
				System.out.println("Nodes in cluster:");
				System.out.println("MASTER: " + master);
				for (Node n: this.nodes) {
					System.out.println("Alias: " + n.getAlias() + ", Address: " + n.getAddress());
				}
				System.out.println("Getting logged in users...");
				// Calling getLoggedIn and getRegistered only in handshake instead of master setting them by himself, problems with locking threads
				HashMap<String, User> loggedIn = rest.getLoggedIn();
				this.data.setLoggedInUsers(loggedIn);
				// Prevents new user registering with the same username
				HashMap<String, User> registered = rest.getRegistered();
				this.data.setRegisteredUsers(registered);
				for (User user: this.data.getLoggedInUsers().values()) {
					System.out.println("Username: " + user.getUsername() + " Node: " + user.getHost());
				}
				System.out.println("Handshake completed");
			}
			
			heartBeat.scheduleAtFixedRate(new TimerTask() {
				ResteasyClient client = new ResteasyClientBuilder().build();

			    @Override
			    public void run() {
			    	System.out.println("Hearthbeat started");
			        for (Node n: nodes) {
			        	if (n.getAlias().equals(node.getAlias())) {
			        		continue;
			        	}
			        	try {
			        		ResteasyWebTarget rtarget = client.target("http://" + n.getAddress() + "/ChatWar/rest/connection");
							ServersRestRemote rest = rtarget.proxy(ServersRestRemote.class);
							String alias = rest.getNodeHealth();
							System.out.println(alias + " - Alive");
			        	} catch (Exception e) {
			        		// Try again if error or timeout
			        		try {
								Thread.sleep(300); // The check could happen during the handshake (server is not yet fully started)
							} catch (InterruptedException e3) {
								e3.printStackTrace();
							}
			        		try {
				        		ResteasyWebTarget rtarget = client.target("http://" + n.getAddress() + "/ChatWar/rest/connection");
								ServersRestRemote rest = rtarget.proxy(ServersRestRemote.class);
								String alias = rest.getNodeHealth();
								System.out.println(alias + " - Alive");
				        	} catch (Exception e2) {
				    			System.out.println("Node: " + n.getAlias() + " not responding... Deleting node from cluster...");
				        		for (Node n1: nodes) {
				        			if (n1.getAlias().equals(n.getAlias())) {
				        				// Inform all but the one that is not responding
						        		continue;
						        	}
				        			ResteasyWebTarget rtarget = client.target("http://" + n1.getAddress() + "/ChatWar/rest/connection");
				    				ServersRestRemote rest = rtarget.proxy(ServersRestRemote.class);
				    				rest.deleteNode(n.getAlias());
				        		}
				        		if (!getNode().getAddress().equals(getMaster())) {
				        			ResteasyWebTarget rtarget = client.target("http://" + getMaster() + "/ChatWar/rest/connection");
				        			ServersRestRemote rest = rtarget.proxy(ServersRestRemote.class);
				        			rest.deleteNode(n.getAlias());
				        		}
				        		
				    			data.getLoggedInUsers().values().removeIf( ServersRest.isUserFromHost(n.getAddress()) );
				    			data.getRegisteredUsers().values().removeIf( ServersRest.isUserFromHost(n.getAddress()) );
				    			// WebSocket
				    			ObjectMapper mapper = new ObjectMapper();
				    	        try {
				    				String jsonMessage = mapper.writeValueAsString(data.getLoggedInUsers().values());
				    				ws.updateLoggedInUsers(jsonMessage);
				    			} catch (JsonProcessingException e1) {
				    				e1.printStackTrace();
				    			}
				        		getNodes().remove(n);
				    			System.out.println("Node: " + n.getAlias() + " deleted from cluster");
				    			// Escape deleting in for loop
				    			// Not great, not terrible
				    			return;
							}
						}
			        }
			    }
			}, 40000, 40000); // Every 40 seconds check the health of all nodes except master
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@PreDestroy
	private void informNodeShuttingDown() {
		System.out.println("Shutting down... Informing all nodes in cluster...");
		ResteasyClient client = new ResteasyClientBuilder().build();
		for (Node n: nodes) {
			if (n.getAlias().equals(this.node.getAlias())) {
        		continue;
        	}
			ResteasyWebTarget rtarget = client.target("http://" + n.getAddress() + "/ChatWar/rest/connection");
			ServersRestRemote rest = rtarget.proxy(ServersRestRemote.class);
			rest.deleteNode(this.node.getAlias());
		}
		if (!getNode().getAddress().equals(getMaster())) {
			ResteasyWebTarget rtarget = client.target("http://" + getMaster() + "/ChatWar/rest/connection");
			ServersRestRemote rest = rtarget.proxy(ServersRestRemote.class);
			rest.deleteNode(getNode().getAlias());
		}
		System.out.println("Node manager: shutdown completed");
	}
	
	public Node getNode() {
		return this.node;
	}
	
	public List<Node> getNodes() {
		return this.nodes;
	}
	
	public void setNodes(List<Node> nodes) {
		this.nodes = nodes;
	}
	
	public String getMaster() {
		return this.master;
	}
}

