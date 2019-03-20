package tk.uditsharma;

import java.security.Principal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;


public class DBConnection {
	/**
	 * Method to create DB Connection
	 * 
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("finally")
	public static Connection createConnection() throws Exception {
		Connection con = null;
		try {
			//System.out.println("before setDbValue");
			Constants.setDbValues();
			Class.forName(Constants.dbClass);
			con = DriverManager.getConnection(Constants.dbUrl, Constants.dbUser, Constants.dbPwd);
		} catch (Exception e) {
			throw e;
		} finally {
			return con;
		}
	}
    /**
     * Method to check whether uname and pwd combination are correct
     * 
     * @param uname
     * @param pwd
     * @return
     * @throws Exception
     */
	public static ArrayList<Object> checkLogin(String uname, String pwd) throws Exception {
		boolean isUserAvailable = false;
		ArrayList<Object> userData = new ArrayList<Object>();
		Connection dbConn = null;
		userData.add(0, isUserAvailable);
		try {
			try {
				//System.out.println("before create conn");
				dbConn = DBConnection.createConnection();
			} catch (Exception e) {
				e.printStackTrace();
			}
			PreparedStatement ps=dbConn.prepareStatement("select * from user where username=? "
					+ "and password=?");  
            ps.setString(1,uname);
            ps.setString(2,pwd);
            ResultSet rs=ps.executeQuery();
			while (rs.next()) {
				//System.out.println(rs.getString(1) + rs.getString(2) + rs.getString(3));
				isUserAvailable = true;
				userData.add(0, isUserAvailable);
				userData.add(1, rs.getString(1));
				Date date = new Date();
				date.setTime(rs.getTimestamp(4).getTime());
				String formattedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
				userData.add(2, formattedDate);
			}
		} catch (SQLException sqle) {
			throw sqle;
		} catch (Exception e) {
			if (dbConn != null) {
				dbConn.close();
			}
			throw e;
		} finally {
			if (dbConn != null) {
				dbConn.close();
			}
		}
		return userData;
	}
	/**
	 * Method to insert uname and pwd in DB
	 * 
	 * @param name
	 * @param uname
	 * @param pwd
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public static boolean insertUser(String name, String uname, String pwd) throws SQLException, Exception {
		boolean insertStatus = false;
		Connection dbConn = null;
		try {
			try {
				dbConn = DBConnection.createConnection();
			} catch (Exception e) {
				e.printStackTrace();
			}
			//Statement stmt = dbConn.createStatement();
			//String query = "INSERT into user(name, username, password) values('"+name+ "',"+"'"
			//		+ uname + "','" + pwd + "')";
			//System.out.println(query);
			//int records = stmt.executeUpdate(query);
			//System.out.println(records);
			//When record is successfully inserted
			PreparedStatement ps=dbConn.prepareStatement(  
                    "insert into user(name,username,password) values (?,?,?)");
	       ps.setString(1,name);
	       ps.setString(2,uname);  
	       ps.setString(3,pwd);
	         
	       int records = ps.executeUpdate();
			if (records > 0) {
				insertStatus = true;
			}
		} catch (SQLException sqle) {
			//sqle.printStackTrace();
			throw sqle;
		} catch (Exception e) {
			e.printStackTrace();
			if (dbConn != null) {
				dbConn.close();
			}
			throw e;
		} finally {
			if (dbConn != null) {
				dbConn.close();
			}
		}
		return insertStatus;
	}
	
	public static void updateToken(String token, String uname){
		
		int status = 0;
        try{
            Connection dbConn = DBConnection.createConnection();
            PreparedStatement ps = dbConn.prepareStatement(
                         "update user set token=? where username=?");  
            ps.setString(1,token);
            ps.setString(2,uname);
            status=ps.executeUpdate();
            dbConn.close();
		
	}catch(Exception ex){ex.printStackTrace();}
	
        System.out.println(status);
	}
	
	public static void checkToken(String token) throws Exception {
		
		boolean isTokenValid = false;
		Connection dbConn = null;
		try {
			try {
				dbConn = DBConnection.createConnection();
			} catch (Exception e) {
				e.printStackTrace();
			}
			//System.out.println("inside checkToken");
			PreparedStatement ps=dbConn.prepareStatement("select * from user where token=?");
            ps.setString(1,token);
			ResultSet rs=ps.executeQuery();
			while (rs.next()) {
				//System.out.println(rs.getString(1) + rs.getString(2) + rs.getString(3));
				isTokenValid = true;
				System.out.println("returned true");
			}
		} catch (SQLException sqle) {
			throw sqle;
		} catch (Exception e) {
			
			if (dbConn != null) {
				dbConn.close();
			}
			throw e;
		} finally {
			if (dbConn != null) {
				dbConn.close();
			}
		}
		if(!isTokenValid) {
			throw new Exception();
		}
	}
	
	public static List<User> getAllUserList(@Context SecurityContext securityContext) throws Exception {
			
			List<User> userList = null;
			Connection dbConn = null;
			try {
				try {
					dbConn = DBConnection.createConnection();
				} catch (Exception e) {
					e.printStackTrace();
				}
				PreparedStatement ps=dbConn.prepareStatement("select * from user order by name");
				ResultSet rs=ps.executeQuery();
				userList = new ArrayList<>();
				while (rs.next()) {
					String name = rs.getString(1);
					String uname = rs.getString(2);
					String pwd = rs.getString(3);
					Principal principal = securityContext.getUserPrincipal();
					if(!principal.getName().equals(uname)) {
						pwd = "Access Denied";
					}
					
					Timestamp regD = rs.getTimestamp(4);
					userList.add(new User(name, uname, pwd, regD));
				}
			} catch (SQLException sqle) {
				throw sqle;
			} catch (Exception e) {
				
				if (dbConn != null) {
					dbConn.close();
				}
				throw e;
			} finally {
				if (dbConn != null) {
					dbConn.close();
				}
			}
			
			return userList;
			
		}
	
	public static User getUser(@Context SecurityContext securityContext, String username) throws Exception {
		
		User userV = new User();
		Connection dbConn = null;
		
		try {
			try {
				dbConn = DBConnection.createConnection();
			} catch (Exception e) {
				e.printStackTrace();
			}
			PreparedStatement ps=dbConn.prepareStatement("select * from user where username=?");
			ps.setString(1,username);
			ResultSet rs=ps.executeQuery();
			if(rs.next()) {
				
				String pwd = rs.getString(3);
				Principal principal = securityContext.getUserPrincipal();
				if(!principal.getName().equals(rs.getString(2))) {
					pwd = "Access Denied";
				}
				userV.setName(rs.getString(1));
				userV.setUserName(rs.getString(2));
				userV.setPassword(pwd);
				userV.setRegDate(rs.getTimestamp(4));
			}else {
				
				throw new Exception();
			}
				


		} catch (SQLException sqle) {
			throw sqle;
		} catch (Exception e) {
			
			if (dbConn != null) {
				dbConn.close();
			}
			throw e;
		} finally {
			if (dbConn != null) {
				dbConn.close();
			}
		}
		
		return userV;
		
	}
	
	public static boolean insertPlace(@Context SecurityContext securityContext, String userID, java.sql.Date uDate, String placeId) throws SQLException, Exception {
		boolean insertStatus = false;
		Connection dbConn = null;
		if (securityContext.getUserPrincipal().getName().equals(userID)) {
			try {
				try {
					dbConn = DBConnection.createConnection();
				} catch (Exception e) {
					e.printStackTrace();
				}
				PreparedStatement ps1 = dbConn.prepareStatement("select * from visited_places where "
						+ "user_id=? and date=?");
				ps1.setString(1, userID);
				ps1.setDate(2, uDate);
				ResultSet rs = ps1.executeQuery();
				while (rs.next()) {
					return (updatePlace(securityContext, userID, uDate, placeId) > 0) ? true : false;
				}
				
				PreparedStatement ps = dbConn
						.prepareStatement("insert into visited_places(user_id,date,place_id) values (?,?,?)");
				ps.setString(1, userID);
				ps.setDate(2, uDate);
				ps.setString(3, placeId);

				int records = ps.executeUpdate();
				if (records > 0) {
					insertStatus = true;
				}
			} catch (SQLException sqle) {
				//sqle.printStackTrace();
				throw sqle;
			} catch (Exception e) {
				e.printStackTrace();
				if (dbConn != null) {
					dbConn.close();
				}
				throw e;
			} finally {
				if (dbConn != null) {
					dbConn.close();
				}
			} 
		}
		return insertStatus;
	}

	public static int updatePlace(@Context SecurityContext securityContext, String userId, java.sql.Date cDate, String placeID) throws Exception {

		int status = 0;
		if (securityContext.getUserPrincipal().getName().equals(userId)) {
			try {
				Connection dbConn = DBConnection.createConnection();
				PreparedStatement ps = dbConn
						.prepareStatement("update visited_places set place_id=? " + "where user_id=? and date=?");
				ps.setString(1, placeID);
				ps.setString(2, userId);
				ps.setDate(3, cDate);
				status = ps.executeUpdate();
				dbConn.close();

			} catch (Exception ex) {
				ex.printStackTrace();
				throw ex;
			} 
		}
		//System.out.println(status);
		return status;
	}
	
	public static String getPlace(String cUser, java.sql.Date uDate) throws Exception {

		String placeId = null;
		Connection dbConn = null;
		try {
			try {
				dbConn = DBConnection.createConnection();
			} catch (Exception e) {
				e.printStackTrace();
			}

			//System.out.println("inside getPlace");
			PreparedStatement ps = dbConn.prepareStatement("select place_id from visited_places where "
					+ "user_id=? and date=?");
			ps.setString(1, cUser);
			ps.setDate(2, uDate);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				// System.out.println(rs.getString(1));
				placeId = rs.getString(1);
			}
		} catch (SQLException sqle) {
			throw sqle;
		} catch (Exception e) {

			if (dbConn != null) {
				dbConn.close();
			}
			throw e;
		} finally {
			if (dbConn != null) {
				dbConn.close();
			}
		}
		return placeId;
	}
	
	public static int deletePlace(@Context SecurityContext securityContext, String userId, java.sql.Date cDate, String placeID){  
        int status=0;  
        if (securityContext.getUserPrincipal().getName().equals(userId)) {
			try {
				Connection dbConn = DBConnection.createConnection();
				PreparedStatement ps = dbConn
						.prepareStatement("delete from visited_places where " + "user_id=? and date=? and place_id=?");
				ps.setString(1, userId);
				ps.setDate(2, cDate);
				ps.setString(3, placeID);
				status = ps.executeUpdate();

				dbConn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return status;  
    }
	
	public static List<AllPlacesResponse> getPlacesbyUser(String cUser) throws Exception {

		List<AllPlacesResponse> placesList = null;
		Connection dbConn = null;
		try {
			try {
				dbConn = DBConnection.createConnection();
			} catch (Exception e) {
				e.printStackTrace();
			}

			PreparedStatement ps = dbConn.prepareStatement("select place_id, date from "
					+ "visited_places where user_id=?");
			ps.setString(1, cUser);
			ResultSet rs = ps.executeQuery();
			placesList = new ArrayList<>();
			while (rs.next()) {
				//System.out.println(rs.getString(1));
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
				placesList.add(new AllPlacesResponse(sdf.format(rs.getDate(2)), rs.getString(1)));
			}
		} catch (SQLException sqle) {
			throw sqle;
		} catch (Exception e) {

			if (dbConn != null) {
				dbConn.close();
			}
			throw e;
		} finally {
			if (dbConn != null) {
				dbConn.close();
			}
		}
		return placesList;
	}
	
	public static int insertComment(@Context SecurityContext securityContext, String userID, String commnt, String placeId) throws SQLException, Exception {
		int auto_id = 0; 
		Connection dbConn = null;
		if (securityContext.getUserPrincipal().getName().equals(userID)) {
			try {
				try {
					dbConn = DBConnection.createConnection();
				} catch (Exception e) {
					e.printStackTrace();
				}
				PreparedStatement ps1 = dbConn.prepareStatement("select name from "
						+ "user where username=?");
				ps1.setString(1, userID);
				ResultSet rs1 = ps1.executeQuery();
				if (rs1.next()) {
					String sql = "insert into places_comments(user_id,name,place_id,comment) values (?,?,?,?)";
					PreparedStatement ps = dbConn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
					ps.setString(1, userID);
					ps.setString(2, rs1.getString(1));
					ps.setString(3, placeId);
					ps.setString(4, commnt);

					int records = ps.executeUpdate();
					if (records > 0) {
						ResultSet rs = ps.getGeneratedKeys();
					    rs.next();
					    auto_id = rs.getInt(1);
					}
				}
				
			} catch (SQLException sqle) {
				//sqle.printStackTrace();
				throw sqle;
			} catch (Exception e) {
				e.printStackTrace();
				if (dbConn != null) {
					dbConn.close();
				}
				throw e;
			} finally {
				if (dbConn != null) {
					dbConn.close();
				}
			} 
		}
		return auto_id;
	}

	public static int updateComment(@Context SecurityContext securityContext, String userId, String newCommnt, int id) throws Exception {

		int status = 0;
		Connection dbConn = null;
		if (securityContext.getUserPrincipal().getName().equals(userId)) {
			try {
				dbConn = DBConnection.createConnection();
				PreparedStatement ps = dbConn.prepareStatement("update places_comments set comment=? where id=? and user_id=?");
				ps.setString(1, newCommnt);
				ps.setInt(2, id);
				ps.setString(3, userId);
				status = ps.executeUpdate();
					
			} catch (Exception ex) {
				ex.printStackTrace();
				throw ex;
			} finally {
				if (dbConn != null) {
					dbConn.close();
				}
			}
		}
		//System.out.println(status);
		return status;
	}
	
	public static List<CommentResponse> getComments(String placeId) throws Exception {

		List<CommentResponse> commentList = null;
		Connection dbConn = null;
		try {
			try {
				dbConn = DBConnection.createConnection();
			} catch (Exception e) {
				e.printStackTrace();
			}

			PreparedStatement ps = dbConn.prepareStatement("select id, user_id, name, comment from "
					+ "places_comments where place_id=?");
			ps.setString(1, placeId);
			ResultSet rs = ps.executeQuery();
			commentList = new ArrayList<>();
			while (rs.next()) {
				commentList.add(new CommentResponse(rs.getInt(1), rs.getString(3), rs.getString(2), rs.getString(4)));
			}
		} catch (SQLException sqle) {
			throw sqle;
		} catch (Exception e) {

			if (dbConn != null) {
				dbConn.close();
			}
			throw e;
		} finally {
			if (dbConn != null) {
				dbConn.close();
			}
		}
		return commentList;
	}
	
	public static int deleteComment(@Context SecurityContext securityContext, String userId, int id){  
        int status=0;
        Connection dbConn = null;
        if (securityContext.getUserPrincipal().getName().equals(userId)) {
			try {
				dbConn = DBConnection.createConnection();
				PreparedStatement ps = dbConn
						.prepareStatement("delete from places_comments where id=? and user_id=?");
				ps.setInt(1, id);
				ps.setString(2, userId);
				status = ps.executeUpdate();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (dbConn != null) {
					try {
						dbConn.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return status;
    }
}