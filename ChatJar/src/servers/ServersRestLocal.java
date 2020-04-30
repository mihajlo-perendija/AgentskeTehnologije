package servers;

import model.Node;
import model.User;

public interface ServersRestLocal {

	public Node getNode();

	public void informNodesAboutLoggedInUsers();
	
	public void informNodesNewUserRegistered(User user);
}
