package tk.uditsharma;

import java.io.Serializable;

public class AllPlacesResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7906447027790770576L;
	
	private String date;
	private String placeId;
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getPlaceId() {
		return placeId;
	}
	public void setPlaceId(String placeId) {
		this.placeId = placeId;
	}
	public AllPlacesResponse(String date, String placeId) {
		super();
		this.date = date;
		this.placeId = placeId;
	}

}
