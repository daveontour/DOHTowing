package au.com.quaysystems.arrivalaware.web.util;

public class FlightMovement {
	public FlightEntry arrival;
	public FlightEntry departure;
	public String mvtID;

	public FlightMovement(	FlightEntry arrival, FlightEntry departure) {
		this.arrival = arrival;
		this.departure = departure;

		if (arrival != null && departure != null) {
			this.mvtID = arrival.key+departure.key;
		} else if (arrival != null && departure == null) {
			this.mvtID = "UNLINKED"+arrival.key;
		} else if (arrival == null && departure != null) {
			this.mvtID = "UNLINKED"+departure.key;
		} 
	}

	public String info() {

		if (arrival != null && departure != null) {
			return String.format("\nMVT ID: %s  %s %s \n", mvtID, arrival.info(), departure.info());
		} else if (arrival != null && departure == null) {
			return String.format("\nMVT ID: %s %s \n%s \n", mvtID, arrival.info(), "UNLINKED");
		} else if (arrival == null && departure != null) {
			return String.format("\nMVT ID: %s \n %s %s \n\n", mvtID, "UNLINKED", departure.info());
		} 
		return null;
	}
}
