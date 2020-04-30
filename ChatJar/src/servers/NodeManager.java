package servers;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import beans.DataBean;
import model.Node;
import model.User;

@Singleton
@Startup
@LocalBean
public class NodeManager {
	@Inject
	DataBean data;
	
	private String master = null;
	private Node node = new Node();
	private List<Node> nodes = new ArrayList<Node>();
	
	private Timer hearthBeat = new Timer();
	
	@PostConstruct
	private void init() {
		try {
	        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
            ObjectName http = new ObjectName("jboss.as:socket-binding-group=standard-sockets,socket-binding=http");
			

			this.node.setAddress((String) mBeanServer.getAttribute(http,"boundAddress") + ":8080");
			this.node.setAlias(System.getProperty("jboss.node.name") + ":8080");
			
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
				for (Node n: this.nodes) {
					System.out.println("Alias: " + n.getAlias() + ", Address: " + n.getAddress());
				}
				System.out.println("Getting logged in users...");
				HashMap<String, User> users = rest.getLoggedIn();
				this.data.setLoggedInUsers(users);
				for (User user: this.data.getLoggedInUsers().values()) {

					System.out.println("Username: " + user.getUsername() + " Node: " + user.getHost());
				} // PRINT USER NODE
				System.out.println("Handshake completed");
			}
			
			hearthBeat.scheduleAtFixedRate(new TimerTask() {
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
			        		try {
				        		ResteasyWebTarget rtarget = client.target("http://" + n.getAddress() + "/ChatWar/rest/connection");
								ServersRestRemote rest = rtarget.proxy(ServersRestRemote.class);
								String alias = rest.getNodeHealth();
								System.out.println(alias + " - Alive");
				        	} catch (Exception e2) {
				    			System.out.println("Node: " + n.getAlias() + " not responding... Deleting node from cluster...");
				        		for (Node n1: nodes) {
				        			ResteasyWebTarget rtarget = client.target("http://" + n1.getAddress() + "/ChatWar/rest/connection");
				    				ServersRestRemote rest = rtarget.proxy(ServersRestRemote.class);
				    				rest.deleteNode(n.getAlias());
				        		}
				        		getNodes().remove(n);
							}
						}
			        }
			    }
			}, 50000, 50000);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
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

