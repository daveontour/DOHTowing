package au.com.quaysystems.arrivalaware.web.services;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Service;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.SoapMessage;

import au.com.quaysystems.arrivalaware.web.ws.AirportIdentifierType;
import au.com.quaysystems.arrivalaware.web.ws.ArrayOfLookupCode;
import au.com.quaysystems.arrivalaware.web.ws.CodeContext;
import au.com.quaysystems.arrivalaware.web.ws.FlightId;
import au.com.quaysystems.arrivalaware.web.ws.FlightKind;
import au.com.quaysystems.arrivalaware.web.ws.GetAirports;
import au.com.quaysystems.arrivalaware.web.ws.GetFlight;
import au.com.quaysystems.arrivalaware.web.ws.GetFlights;
import au.com.quaysystems.arrivalaware.web.ws.LookupCode;
import au.com.quaysystems.arrivalaware.web.ws.ObjectFactory;


@Service
public class AMSServices {

	private final Logger log = LoggerFactory.getLogger(AMSServices.class);
	private Jaxb2Marshaller marshaller;
	private WebServiceTemplate ws;
	private JAXBContext context;
	private Marshaller m;
	private ObjectFactory fact;
	private JAXBElement<String> token;
	private JAXBElement<String> airport;
	private AirportIdentifierType apType = AirportIdentifierType.IATA_CODE;

	public AMSServices(
			@Value("${token}")String toke,
			@Value("${airport}")String ap,
			@Value("${wsurl:http://localhost/SITAAMSIntegrationService/v2/SITAAMSIntegrationService}")String wsURL)  {
		
		fact = new ObjectFactory();
		marshaller = new Jaxb2Marshaller();
		marshaller.setContextPath("au.com.quaysystems.arrivalaware.web.ws");
		ws = new WebServiceTemplate();
		ws.setDefaultUri(wsURL);
		ws.setMarshaller(marshaller);
		ws.setUnmarshaller(new StringUnmarshaller());
		
		this.token = fact.createGetFlightsSessionToken(toke);
		this.airport = fact.createGetFlightsAirport(ap);
		
		try {
			context = JAXBContext.newInstance("au.com.quaysystems.arrivalaware.web.ws");
			m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_ENCODING, "iso-8859-15");
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		} catch (PropertyException e) {
			log.error(e.getMessage());
			e.printStackTrace();
		} catch (JAXBException e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}
	private String callWebService(String service, Object payload) throws JAXBException {
		
		Object response =  ws.marshalSendAndReceive(payload, new WebServiceMessageCallback() {
			public void doWithMessage(WebServiceMessage message) {
				((SoapMessage)message).setSoapAction(service);
			}
		});
		
		return (String)response;
	}
	
	private String stripNS(String xml) {
    	return xml.replaceAll("xmlns(.*?)=(\".*?\")", "");
	}
	public String getFlights(int fromHours, int toHours) throws DatatypeConfigurationException, JAXBException {

		GetFlights request = new GetFlights();
		request.setSessionToken(this.token);
		request.setAirport(this.airport);
		request.setAirportIdentifierType(this.apType);

		DateTime dt = new DateTime();

		DateTime f = new DateTime(dt.plusMinutes(fromHours));
		DateTime t = new DateTime(dt.plusMinutes(toHours));
		XMLGregorianCalendar from = DatatypeFactory.newInstance().newXMLGregorianCalendar(f.toGregorianCalendar());
		XMLGregorianCalendar to = DatatypeFactory.newInstance().newXMLGregorianCalendar(t.toGregorianCalendar());

		request.setFrom(from);
		request.setTo(to);
		
		return stripNS(callWebService("http://www.sita.aero/ams6-xml-api-webservice/IAMSIntegrationService/GetFlights", request));
	}
	public String getFlight( String id) throws DatatypeConfigurationException, JAXBException {

	//	String id = "6E1713@2019-08-01T09:00A";
		
		FlightId fltID = new FlightId();
		
		String num = id.substring(2, id.indexOf("@"));
		fltID.setFlightNumberField(num);

		String airline  = id.substring(0,2);
		LookupCode alLookupCode = new LookupCode();
		alLookupCode.setValueField(airline);
		alLookupCode.setCodeContextField(CodeContext.IATA);
		ArrayOfLookupCode arrlc = new ArrayOfLookupCode();
		arrlc.getLookupCode().add(alLookupCode);
		fltID.setAirlineDesignatorField(arrlc);

		String sched = id.substring(id.indexOf("@")+1, id.length()-2);
		DateTime dt = new DateTime(sched);
		XMLGregorianCalendar d = DatatypeFactory.newInstance().newXMLGregorianCalendar(dt.toGregorianCalendar());
		fltID.setScheduledDateField(d);
		
		String kind = id.substring(id.length()-1);
		if (kind.equals("A")) {
			fltID.setFlightKindField(FlightKind.ARRIVAL);
		} else {
			fltID.setFlightKindField(FlightKind.DEPARTURE);
		}
		
		LookupCode apLookupCode = new LookupCode();
		apLookupCode.setValueField("OTHH");
		apLookupCode.setCodeContextField(CodeContext.ICAO);
		ArrayOfLookupCode aplc = new ArrayOfLookupCode();
		aplc.getLookupCode().add(apLookupCode);
		fltID.setAirportCodeField(aplc);
		
		GetFlight request = new GetFlight();
		request.setSessionToken(this.token);
		request.setFlightId(this.fact.createGetFlightFlightId(fltID));
		
		return stripNS(callWebService("http://www.sita.aero/ams6-xml-api-webservice/IAMSIntegrationService/GetFlight", request));
	}
}
