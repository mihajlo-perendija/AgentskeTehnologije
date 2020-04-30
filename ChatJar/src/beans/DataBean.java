package beans;

import java.util.HashMap;

import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import model.User;


@Singleton
@LocalBean
@Startup
public class DataBean {

	private HashMap<String, User> registeredUsers;
	private HashMap<String, User> loggedInUsers;

	public DataBean() {
		registeredUsers = new HashMap<String, User>();
		loggedInUsers = new HashMap<String, User>();
	}

	public HashMap<String, User> getRegisteredUsers() {
		return registeredUsers;
	}

	public HashMap<String, User> getLoggedInUsers() {
		return loggedInUsers;
	}

	public void setLoggedInUsers(HashMap<String, User> loggedInUsers) {
		this.loggedInUsers = loggedInUsers;
	}
}
