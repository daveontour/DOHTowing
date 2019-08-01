package au.com.quaysystems.arrivalaware.web.util;

import org.basex.core.BaseXException;
import org.basex.core.Context;
import org.basex.core.cmd.XQuery;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import au.com.quaysystems.arrivalaware.web.services.XSLTransformService;

public class FlightEntry {
	
	public String key;
	public String rawXML;
	public DateTime MCO;
	public DateTime STO;
	public boolean isArrival;
	public String linkedID;

	
	public FlightEntry(String xml) {

		String idQuery = 
				"declare variable $var1 as xs:string external;\n"+
						"for $x in fn:parse-xml($var1)//Flight\r\n" +
						"return $x";

		Context ctx = new Context();
		XQuery q = new XQuery(idQuery);
		q.bind("var1", stripNS(xml));
		
		try {
			rawXML = q.execute(ctx);
		} catch (BaseXException e) {
			rawXML = null;
			return;
		}

		if (this.rawXML != null && this.rawXML.length() > 0 ) {
			this.rawXML = stripNS(rawXML);
			this.setFlightID();
			this.setFlightMCO();
			this.setLinkedFlightID();
			this.setFlightSTO();
		}
	}
	
	
	private void setFlightMCO() {


		String idQuery = 
				"declare variable $var1 as xs:string external;\n"+
						"for $x in fn:parse-xml($var1)//Value[@propertyName=\"de-G_MostConfidentArrivalTime\"]\r\n" + 
						"return $x/text()";

		if (!this.isArrival) {
			idQuery = 
					"declare variable $var1 as xs:string external;\n"+
							"for $x in fn:parse-xml($var1)//Value[@propertyName=\"de-G_MostConfidentDepartureTime\"]\r\n" + 
							"return $x/text()";
		}

		Context ctx = new Context();
		XQuery q = new XQuery(idQuery);
		q.bind("var1", this.rawXML);
		try {
			String t = q.execute(ctx);
			this.MCO =  new DateTime(t);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void setFlightSTO() {


		String idQuery = 
				"declare variable $var1 as xs:string external;\n"+
						"for $x in fn:parse-xml($var1)//Flight/FlightState/ScheduledTime\r\n" + 
						"return $x/text()";


		Context ctx = new Context();
		XQuery q = new XQuery(idQuery);
		q.bind("var1", this.rawXML);
		try {
			String t = q.execute(ctx);
			this.STO =  new DateTime(t);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private void setFlightID() {

		String idQuery = 
				"declare variable $var1 as xs:string external;\n"+
						"for $x in fn:parse-xml($var1)//FlightState/Value[@propertyName=\"FlightUniqueID\"]\r\n" + 
						"return $x/text()";

		Context ctx = new Context();
		XQuery q = new XQuery(idQuery);
		q.bind("var1", this.rawXML);
		try {
			this.key =  q.execute(ctx);
		} catch (BaseXException e) {
			e.printStackTrace();
		}


		this.isArrival = key.contains("ARR");

	}
	private void setLinkedFlightID() {

		String idQuery = 
						"declare variable $var1 as xs:string external;\n"+
						"for $x in fn:parse-xml($var1)//LinkedFlight/Value[@propertyName=\"FlightUniqueID\"]\r\n" +
						"return $x/text()";

		Context ctx = new Context();
		XQuery q = new XQuery(idQuery);
		q.bind("var1", rawXML);
		try {
			this.linkedID =   q.execute(ctx);
		} catch (Exception e) {
			this.linkedID = null;
		}
	}

	private String stripNS(String xml) {
		return xml.replaceAll("xmlns(.*?)=(\".*?\")", "");
	}

	public String toString() {
		return this.rawXML;
	}
	
	public String info() {
		return String.format("\nFlight ID: %s, Is Arrival: %s, Linked ID: %s, MCO: %s, STO: %s", this.key, this.isArrival, this.linkedID, this.MCO, this.STO);
	}
	
	public String info2() {
		return String.format("Flight ID: %s, Is Arrival: %s, Linked ID: %s, MCO: %s, STO: %s", this.key, this.isArrival, this.linkedID, this.MCO, this.STO);
	}
}