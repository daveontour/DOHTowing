package au.com.quaysystems.arrivalaware.web.util;

import java.util.ArrayList;
import java.util.HashMap;

import org.joda.time.DateTime;

@SuppressWarnings("serial")
public class FlightEntryMap extends HashMap<String, FlightEntry>{
	
	public static enum TimeType {STO,MCO}
	public static enum MvtType {ARR,DEP,ALL	}

	public boolean put(String xml) {
		if (xml == null || xml.length() < 1) {
			return false;
		}
		FlightEntry entry = new  FlightEntry(xml);
		return this.put(entry);
	}
	public boolean put(FlightEntry entry) {
		if (entry == null || entry.key == null) {
			return false;
		}
		super.put(entry.key, entry);
		return true;
	}
	public void remove(FlightEntry entry) {
		super.remove(entry.key);
	}

	public FlightEntry getLinkedFlight(String id) {
		FlightEntry entry = this.get(id);
		return get(entry.linkedID);
	}
	public boolean update(FlightEntry entry) {
		if (entry == null || entry.key == null) {
			return false;
		}
		super.put(entry.key, entry);
		return true;
	}

	public FlightMovement getFlightMovement(String id) {
		
		// In case it is an unlinked flight
		if (id.contains("UNLINKED")) {
			id = id.replace("UNLINKED", "");
		}
		
		// Simplify it when the full MVTID is supplied
		if (id.contains("ARR") && id.contains("DEP")) {
			id = id.replace("ARR_", "");
			id = id.replaceAll("^[0-9]*","");
		}
		
		FlightEntry entry = get(id);

		if (entry == null) {
			return null;
		}

		FlightMovement mvt = null;

		if (entry.isArrival) {
			mvt = new FlightMovement(entry,this.getLinkedFlight(entry.key));
		} else {
			mvt = new FlightMovement(this.getLinkedFlight(entry.key), entry);
		}
		return mvt;		
	}

	public ArrayList<FlightMovement> getFlightMovements() {

		ArrayList<FlightMovement> mvts = new ArrayList<FlightMovement>();
		HashMap<String, FlightMovement> set = new HashMap<>();

		for (String key: this.keySet()) {
			FlightMovement m = this.getFlightMovement(key);
			set.put(m.mvtID, m);
		}

		for (String key: set.keySet()) {
			mvts.add(set.get(key));
		}

		return mvts;

	}

	public ArrayList<FlightMovement> getFlightMovements(DateTime from, DateTime to, TimeType type) {

		ArrayList<FlightMovement> mvts = new ArrayList<FlightMovement>();
		HashMap<String, FlightMovement> set = new HashMap<>();

		for (String key: this.keySet()) {
			FlightEntry e = this.get(key);
			
			DateTime t = e.STO;
			if (type == TimeType.MCO) {
				t = e.MCO;
			}
			
			if (t.isAfter(from) && t.isBefore(to)) {
				FlightMovement m = this.getFlightMovement(key);
				set.put(m.mvtID, m);
			}
		}

		for (String key: set.keySet()) {
			mvts.add(set.get(key));
		}

		return mvts;

	}

	public ArrayList<FlightEntry> getFlightEntries(DateTime from, DateTime to, TimeType type, MvtType mvtType) {

		ArrayList<FlightEntry> es = new ArrayList<>();

		
		for (String key: this.keySet()) {
			FlightEntry e = this.get(key);
			
			DateTime t = e.STO;
			if (type == TimeType.MCO) {
				t = e.MCO;
			}
			if (t.isAfter(from) && t.isBefore(to)) {
				if (mvtType == MvtType.ALL) {
					es.add(e);
				} else if (mvtType == MvtType.ARR && e.isArrival) {
					es.add(e);					
				} else if (mvtType == MvtType.DEP && !e.isArrival) {
					es.add(e);
				}
			}
		}

		return es;

	}
	public ArrayList<FlightEntry> getAllFlightEntries(MvtType mvtType) {

		ArrayList<FlightEntry> es = new ArrayList<>();

		for (String key: this.keySet()) {
			FlightEntry e = this.get(key);
			if (mvtType == MvtType.ALL) {
				es.add(e);
			} else if (mvtType == MvtType.ARR && e.isArrival) {
				es.add(e);					
			} else if (mvtType == MvtType.DEP && !e.isArrival) {
				es.add(e);
			}
		}

		return es;

	}


	public String serialize() {
		String xml = "";
		for (String key: this.keySet()) {
			FlightEntry entry = this.get(key);
			xml = xml+entry;
		}
		return xml;
	}

	public String info() {
		String xml = "";
		for (String key: this.keySet()) {
			FlightEntry entry = this.get(key);
			xml = xml+String.format("\nFlight ID: %s, Is Arrival: %s, Linked ID: %s, MCO: %s", entry.key, entry.isArrival, entry.linkedID, entry.MCO);
		}
		return xml;
	} 
	
	public String serializeArr() {
		String xml = "";
		for (String key: this.keySet()) {
			FlightEntry entry = this.get(key);
			if (entry.isArrival) {
				xml = xml+entry;
			}
		}
		return xml;
	}
	public String serializeDep() {
		String xml = "";
		for (String key: this.keySet()) {
			FlightEntry entry = this.get(key);
			if (!entry.isArrival) {
				xml = xml+entry;
			}
		}
		return xml;
	}	

	@Override
	public String toString() {
		return serialize();
	}
}
