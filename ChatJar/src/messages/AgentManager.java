package messages;

import java.util.HashMap;

import javax.ejb.LocalBean;
import javax.ejb.Singleton;

import model.User;

@Singleton
@LocalBean
public class AgentManager {

	private HashMap<String, Agent> agents;

	public AgentManager() {
		agents = new HashMap<String, Agent>();
	}

	public void createNewAgent(String username, User user) {
		this.agents.put(username, new Agent(username, user));
	}
	
	public Agent getAgentByUsername(String username) {
		return this.agents.get(username);
	}
	
	public HashMap<String, Agent> getAgents() {
		return agents;
	}

	public void setAgents(HashMap<String, Agent> agents) {
		this.agents = agents;
	}
	
	
}
