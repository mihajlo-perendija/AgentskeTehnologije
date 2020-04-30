package servers;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.IntStream;

import javax.annotation.PostConstruct;
import javax.ejb.DependsOn;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.ws.rs.Path;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import beans.DataBean;
import model.Node;
import model.User;

@Singleton
@Startup
@DependsOn("DataBean")
@Remote(ServersRestRemote.class)
@Path("/connection")
public class ServersRest implements ServersRestRemote {
	
	@Inject
	DataBean data;
	
	public String master = null;
	public Node node = new Node();
	public List<Node> nodes = new ArrayList<Node>();
	
	public Timer hearthBeat = new Timer();
	
	@PostConstruct
	private void init() {
		try {
			Thread.sleep(10000);
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
				boolean test = rest.registerNode(this.node);
				System.out.println("Node registered");
				System.out.println("Nodes in cluster:");
				for (Node n: this.nodes) {
					System.out.println("Alias: " + n.getAlias() + ", Address: " + n.getAddress());
				}
				//this.connections.remove(this.nodeName);
				//this.connections.add(this.master);
				
				hearthBeat.scheduleAtFixedRate(new TimerTask() {
					ResteasyClient client = new ResteasyClientBuilder().build();

				    @Override
				    public void run() {
				        for (Node n: nodes) {
				        	try {
				        		ResteasyWebTarget rtarget = client.target("http://" + n.getAddress() + "/ChatWar/rest/connection");
								ServersRestRemote rest = rtarget.proxy(ServersRestRemote.class);
								String alias = rest.getNodeHealth();
				        	} catch (Exception e) {
				        		try {
					        		ResteasyWebTarget rtarget = client.target("http://" + n.getAddress() + "/ChatWar/rest/connection");
									ServersRestRemote rest = rtarget.proxy(ServersRestRemote.class);
									String alias = rest.getNodeHealth();
					        	} catch (Exception e2) {
					    			System.out.println("Node: " + n.getAlias() + " not responding... Deleting node from cluster...");
					        		for (Node n1: nodes) {
					        			ResteasyWebTarget rtarget = client.target("http://" + n1.getAddress() + "/ChatWar/rest/connection");
					    				ServersRestRemote rest = rtarget.proxy(ServersRestRemote.class);
					    				rest.deleteNode(n.getAlias());
					        		}
								}
							}
				        }
				    }
				}, 20000, 30000);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean registerNode(Node node) {
		try {
			System.out.println("New node registered: " + node.getAlias() + ", address: " + node.getAddress());
			ResteasyClient client = new ResteasyClientBuilder().build();

			for (Node n : this.nodes) {
				ResteasyWebTarget rtarget = client.target("http://" + n.getAddress() + "/ChatWar/rest/connection");
				ServersRestRemote rest = rtarget.proxy(ServersRestRemote.class);
				boolean test = rest.newNodeConnected(node);
			}
			this.nodes.add(new Node(node.getAlias(), node.getAddress()));
			
			try {
				ResteasyWebTarget rtarget = client.target("http://" + node.getAddress() + "/ChatWar/rest/connection");
				ServersRestRemote rest = rtarget.proxy(ServersRestRemote.class);
				boolean test = rest.setNodes(this.nodes);
				test = rest.setLoggedIn(this.data.getLoggedInUsers());
			} catch (Exception e) {
				System.out.println("New node not responding... Trying again...");
				try {
					Thread.sleep(200);
					ResteasyWebTarget rtarget = client.target("http://" + node.getAddress() + "/ChatWar/rest/connection");
					ServersRestRemote rest = rtarget.proxy(ServersRestRemote.class);
					boolean test = rest.setNodes(this.nodes);
					test = rest.setLoggedIn(this.data.getLoggedInUsers());
				} catch (Exception e1) {
					throw e1;
				}
			}
			return true;
			
		} catch (Exception e) {
			System.out.println("New node not responding... Deleting node from cluster...");

			ResteasyClient client = new ResteasyClientBuilder().build();
			for (Node n : this.nodes) {
				if (!n.getAlias().equals(node.getAlias())) {
					ResteasyWebTarget rtarget = client.target("http://" + n.getAddress() + "/ChatWar/rest/connection");
					ServersRestRemote rest = rtarget.proxy(ServersRestRemote.class);
					rest.deleteNode(node.getAlias());
				}
			}
			this.deleteNode(node.getAlias());
			return false;
		}
		
	}

	@Override
	public boolean newNodeConnected(Node node) {
		System.out.println("New node connected: " + node.getAlias() + ", address: " + node.getAddress());
		this.nodes.add(new Node(node.getAlias(), node.getAddress()));
		return true;
	}

	@Override
	public boolean setNodes(List<Node> nodes) {
		this.nodes = nodes;
		return true;
	}
	
	@Override
	public boolean setLoggedIn(HashMap<String, User> loggedIn) {
		this.data.setLoggedInUsers(loggedIn);
		return true;
	}

	@Override
	public boolean deleteNode(String alias) {
		System.out.println("Deleting node: " + alias + " from cluster");
		int index = IntStream.range(0, this.nodes.size())
			     .filter(i -> this.nodes.get(i).getAlias().equals(alias))
			     .findFirst()
			     .orElse(-1);
		if (index != -1) {
			this.nodes.remove(index);
		}
		return true;
	}

	@Override
	public String getNodeHealth() {
		return this.node.getAlias();
	}
}
