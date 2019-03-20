package tk.uditsharma;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.StringTokenizer;

public class AuthenticationService {
	private String uname;
	private String name;
	private String regTime;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRegTime() {
		return regTime;
	}

	public void setRegTime(String regTime) {
		this.regTime = regTime;
	}

	public boolean authenticate(String authCredentials) {

		if (null == authCredentials)
			return false;
		// header value format will be "Basic encodedstring" for Basic
		// authentication. Example "Basic YWRtaW46YWRtaW4="
		final String encodedUserPassword = authCredentials.replaceFirst("Basic"
				+ " ", "");
		String usernameAndPassword = null;
		try {
			byte[] decodedBytes = Base64.getDecoder().decode(
					encodedUserPassword);
			usernameAndPassword = new String(decodedBytes, "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		final StringTokenizer tokenizer = new StringTokenizer(
				usernameAndPassword, ":");
		final String username = tokenizer.nextToken();
		final String password = tokenizer.nextToken();
		this.uname = username;

		// call some UserService/LDAP here
		//System.out.println("Inside login authenticate");
		boolean result = false;
		if(Utitlity.isNotNull(username) && Utitlity.isNotNull(password)){
			try {
				ArrayList<Object> uData = DBConnection.checkLogin(username, password);
				result = (boolean) uData.get(0);
				//System.out.println("Inside authenticate try "+result);
				setName((String) uData.get(1));
				setRegTime((String) uData.get(2));
				System.out.println(result);
			} catch (Exception e) {
				System.out.println("Inside first authentcate catch");
				e.printStackTrace();
				result = false;
			}
		}else{
			System.out.println("Inside authenticate else");
			result = false;
		}
		
		return result;
	}
	
	public String getUname() {
		return uname;
	}
}
