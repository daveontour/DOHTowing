package au.com.quaysystems.arrivalaware.web.util;

public class AirportEntry {

	public String IATA;
	public String ICAO;
	public String Name;

	public AirportEntry(String ICAO, String IATA, String Name) {
		this.ICAO = ICAO;
		this.IATA = IATA;
		this.Name = Name;
	}

	public AirportEntry(String x) {

		String [] v = x.split(":");

		this.ICAO = v[0];
		this.IATA = v[1];
		this.Name = v[2].replaceAll("  ", " ");
	}

	public String toString() {
		String text = String.format("\"%s\": \"%s\",\"%s\": \"%s\"", ICAO,Name,IATA,Name);
		text = text.replaceAll("[^\\x00-\\x7F]", "");
		text = text.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "");
		text = text.replaceAll("\\p{C}", "");

		return text.trim();
	}
}
