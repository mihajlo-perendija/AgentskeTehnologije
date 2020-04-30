package beans;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import model.Message;

public interface ChatBeanRemote {

	@POST
	@Path("/messages/all")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response sendMessageAll(Message message);
	
	@POST
	@Path("/messages/user")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response sendMessageUser(Message message);
}
