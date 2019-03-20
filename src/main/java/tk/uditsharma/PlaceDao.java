package tk.uditsharma;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

@Path("/places")
public class PlaceDao {
	
	@Context
	SecurityContext securityContext;
	
	@GET
	@Secured
	@Path("/getplace")
	@Produces(MediaType.APPLICATION_JSON)
	public String getPlaceId(@QueryParam("userid") String uID, @QueryParam("datestring") String dString) {
		String idResponse = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date jDate = new Date();
		try {
			jDate = sdf.parse(dString);
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		java.sql.Date sqlDate = new java.sql.Date(jDate.getTime());
		try {
			idResponse = DBConnection.getPlace(uID, sqlDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (idResponse != null) ? Utitlity.constructJSON("status", idResponse) : Utitlity.constructJSON("status", "not_found");
		
	}
	
	@GET
	@Secured
	@Path("/userplaces")
	@Produces(MediaType.APPLICATION_JSON)
	public List<AllPlacesResponse> getPlaces(@QueryParam("userid") String userId) {
		List<AllPlacesResponse> allPlaces = null;
		try {
			allPlaces = DBConnection.getPlacesbyUser(userId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return allPlaces;		
	}
	
	@GET
	@Secured
	@Path("/addplace")
	@Produces(MediaType.APPLICATION_JSON)
	public String addPlaceId(@QueryParam("userid") String uId, @QueryParam("date") String cData, @QueryParam("placeid") String place) {
		boolean status = false;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date jDate = new Date();
		try {
			jDate = sdf.parse(cData);
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		java.sql.Date sqlDate = new java.sql.Date(jDate.getTime());
		try {
			status = DBConnection.insertPlace(securityContext, uId, sqlDate, place);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (status == true) ? Utitlity.constructJSON("status", "Success") : Utitlity.constructJSON("status", "Failed");
		
	}
	
	@GET
	@Secured
	@Path("/updateplace")
	@Produces(MediaType.APPLICATION_JSON)
	public String updatePlaceId(@QueryParam("userid") String uId, @QueryParam("date") String cData, @QueryParam("placeid") String place) {
		int status = 0;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date jDate = new Date();
		try {
			jDate = sdf.parse(cData);
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		java.sql.Date sqlDate = new java.sql.Date(jDate.getTime());
		try {
			status = DBConnection.updatePlace(securityContext, uId, sqlDate, place);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return (status > 0) ? Utitlity.constructJSON("status", "Success") : Utitlity.constructJSON("status", "Failed");
		
	}
	
	@GET
	@Secured
	@Path("/deleteplace")
	@Produces(MediaType.APPLICATION_JSON)
	public String deletePlaceId(@QueryParam("userid") String uId, @QueryParam("date") String cData, @QueryParam("placeid") String place) {
		int status = 0;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date jDate = new Date();
		try {
			jDate = sdf.parse(cData);
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		java.sql.Date sqlDate = new java.sql.Date(jDate.getTime());
		try {
			status = DBConnection.deletePlace(securityContext, uId, sqlDate, place);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (status > 0) ? Utitlity.constructJSON("status", "Success") : Utitlity.constructJSON("status", "Failed");
	}
	
	@GET
	@Secured
	@Path("/getcomments")
	@Produces(MediaType.APPLICATION_JSON)
	public List<CommentResponse> getComments(@QueryParam("placeid") String pID) {
		List<CommentResponse> comList = null;
		try {
			comList = DBConnection.getComments(pID);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return comList;
		
	}
	
	@POST
	@Secured
	@Path("/addcomment")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public String addComment(@FormParam("userid") String uId, @FormParam("comment") String uComm, @FormParam("placeid") String place) {
		int status = 0;
		
		try {
			status = DBConnection.insertComment(securityContext, uId, uComm, place);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (status != 0) ? Utitlity.constructJSON(status, "Success") : Utitlity.constructJSON(status, "Failed");
		
	}
	
	@POST
	@Secured
	@Path("/updatecomment")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public String updateComm(@FormParam("userid") String uId, @FormParam("comment") String cText, @FormParam("commentid") int cId) {
		int status = 0;
		
		try {
			status = DBConnection.updateComment(securityContext, uId, cText, cId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return (status > 0) ? Utitlity.constructJSON("status", "Success") : Utitlity.constructJSON("status", "Failed");
		
	}
	
	@GET
	@Secured
	@Path("/deletecomment")
	@Produces(MediaType.APPLICATION_JSON)
	public String deletecomnt(@QueryParam("userid") String uId, @QueryParam("commentid") int cId) {
		int status = 0;
		
		try {
			status = DBConnection.deleteComment(securityContext, uId, cId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (status > 0) ? Utitlity.constructJSON("status", "Success") : Utitlity.constructJSON("status", "Failed");
	}

}
