package tk.uditsharma;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

//Change these parameters according to your DB
public class Constants {
	public static String dbClass = null;
	//public static String dbClass = "com.mysql.cj.jdbc.Driver";
	//private static String dbName= null;
	public static String dbUrl = null;
	public static String dbUser = null;
	public static String dbPwd = null;
	public static void setDbValues() {
		
		//Constants.dbPwd = dbPwd;
		//System.out.println("inside setDbValue");
		Properties prop = new Properties();
		InputStream input = null;
		try {

		    input = Constants.class.getClassLoader().getResourceAsStream("config.properties");
		    // load a properties file
		    prop.load(input);

		    // get the property value
		    Constants.dbClass = prop.getProperty("dbdriver");
		    Constants.dbUrl = prop.getProperty("dburl");
		    Constants.dbUser = prop.getProperty("dbusername");
		    Constants.dbPwd = prop.getProperty("dbpassword");

		} catch (IOException ex) {
		    ex.printStackTrace();
		} finally {
		    if (input != null) {
		        try {
		            input.close();
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
		    }
		}		
	}	
}