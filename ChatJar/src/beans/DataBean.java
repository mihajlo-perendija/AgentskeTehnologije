package beans;

import java.util.HashMap;

import javax.ejb.LocalBean;
import javax.ejb.Singleton;

import model.User;


@Singleton
@LocalBean
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
}
