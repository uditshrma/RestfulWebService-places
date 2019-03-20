package tk.uditsharma;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

@Path("/users")
public class Userdao {
	
	@Context
	SecurityContext securityContext;
	
	@GET
	@Secured
	@Path("/userlist")
	@Produces(MediaType.APPLICATION_JSON)
	public List<User> displayUsers() {
		List<User> allUsers = null;
		try {
			allUsers = DBConnection.getAllUserList(securityContext);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return allUsers;		
	}
	
	@GET
	@Secured
	@Path("/userid")
	@Produces(MediaType.APPLICATION_JSON)
	public User getUser(@QueryParam("username") String uname) {
		User mUser = null;
		
		try {
			mUser = DBConnection.getUser(securityContext, uname);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mUser;
		
	}
	
}
