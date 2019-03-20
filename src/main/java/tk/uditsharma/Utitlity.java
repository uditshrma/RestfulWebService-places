package tk.uditsharma;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class Utitlity {
	/**
	 * Null check Method
	 * 
	 * @param txt
	 * @return
	 */
	public static boolean isNotNull(String txt) {
		// System.out.println("Inside isNotNull");
		return txt != null && txt.trim().length() >= 0 ? true : false;
	}

	/**
	 * Method to construct JSON
	 * 
	 * @param tag
	 * @param status
	 * @return
	 */
	public static String constructJSON(String tag, boolean status) {
		JSONObject obj = new JSONObject();
		try {
			obj.put("tag", tag);
			obj.put("status", new Boolean(status));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return obj.toString();
	}

	/**
	 * Method to construct JSON with Error Msg
	 * 
	 * @param tag
	 * @param status
	 * @param err_msg
	 * @return
	 */
	public static String constructJSON(String tag, boolean status,String err_msg) {
		JSONObject obj = new JSONObject();
		try {
			obj.put("tag", tag);
			obj.put("status", new Boolean(status));
			obj.put("error_msg", err_msg);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return obj.toString();
	}
	
	public static String constructJSON(String name, String time, String token) {
		JSONObject obj = new JSONObject();
		try {
			obj.put("name", name);
			obj.put("reg_time", time);
			obj.put("encoded", token);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return obj.toString();
	}
	
	public static String constructJSON(String tag, String value) {
		JSONObject obj = new JSONObject();
		try {
			obj.put(tag, value);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return obj.toString();
	}
	public static String constructJSON(int tag, String value) {
		JSONObject obj = new JSONObject();
		try {
			obj.put("status", value);
			obj.put("comment_id", tag);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return obj.toString();
	}

}