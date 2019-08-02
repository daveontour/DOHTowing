package au.com.quaysystems.arrivalaware.web.listeners;


import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.basex.core.Context;
import org.basex.query.QueryException;
import org.basex.query.QueryProcessor;
import org.basex.query.iter.Iter;
import org.basex.query.value.item.Item;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.ibm.mq.MQException;
import com.ibm.mq.constants.MQConstants;

import au.com.quaysystems.arrivalaware.web.mq.MReceiver;
import au.com.quaysystems.arrivalaware.web.mq.MSender;
import au.com.quaysystems.arrivalaware.web.services.AMSServices;
import au.com.quaysystems.arrivalaware.web.services.XSLTransformService;



@WebListener
public class RequestListener implements ServletContextListener {


	private static final Logger log = (Logger)LoggerFactory.getLogger(RequestListener.class);

	private Thread t;
	
	@Autowired
	public AMSServices ams;

	
	@Value("${fromMin:-1440}")
	private int fromMin;
	
	@Value("${toMin:1440}")
	private int toMin;
	
	@Value("${token}")
	private String token;

	@Value("${mq.ibmoutqueue}")
	private String ibmoutqueue;

	@Value("${mq.ibminqueue}")
	private String ibminqueue;

	@Value("${msg.recv.timeout:5000}")
	private int msgRecvTimeout;

	@Value("${monitor.period:60000}")
	private long monitorPeriod;

	@Value("${ibmmq.retries:0}")
	private int retriesIBMMQ;
	
	@Value("${towrequest.url:http://localhost:80/api/v1/DOH/Towings/%s/%s}")
	private String towRequestURL;
	
	@Value("${log.level:INFO}")
	private String logLevel;
	
	@Value("${daily.refresh.time:05:00}")
	private String refreshTimeStr;
	
	@Value("${mq.host}")
	private String host;
	@Value("${mq.qmgr}")
	private String qm;
	@Value("${mq.channel}")
	private String channel;
	@Value("${mq.port}")
	private int port;
	@Value("${mq.user}")
	private String user;
	@Value("${mq.pass}")
	private String pass;
	
	private String template = "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\r\n" + 
			" <soap:Header correlationID=\"%s\"></soap:Header>\r\n" + 
			" <soap:Body>\r\n" + 
			"  <ArrayOfTowing xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\r\n" + 
			"   %s\r\n"+
			"  </ArrayOfTowing>\r\n" + 
			" </soap:Body>\r\n" + 
			"</soap:Envelope>";

	private String syncTemplate = "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\r\n" + 
			" <soap:Header correlationID=\"-\"><Sync>true</Sync></soap:Header>\r\n" + 
			" <soap:Body>\r\n" + 
			"  <ArrayOfTowing xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\r\n" + 
			"   %s\r\n"+
			"  </ArrayOfTowing>\r\n" + 
			" </soap:Body>\r\n" + 
			"</soap:Envelope>";
	
	String queryBody = 
			"declare variable $var1 as xs:string external;\n"+
			"for $x in fn:parse-xml($var1)//Towing\r\n" + 
			"return $x";
	private final DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		
		// Set the configured logging level
		this.setLogLevel();
		
		// Schedule the daily sync
		this.dailyRefresh();
		
		// Initialize the output queue by sending the current set of tows
		log.info("===> Start Of Initial Population");
		try {
			this.getAndSendTows();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.info("<=== End Of Initial Population");
		
		// Start the listener for incoming requests
		this.startListener();

	}
	
	public void startListener() {
		log.info("===> Start Request Listner Loop");
		t = new RequestListenerLoop();
		t.setName("Request Process");
		t.start();
		log.info("<=== Request Listner Loop Started");

		monitor(this.monitorPeriod);
	}
	
	public void setLogLevel() {
		
		switch(logLevel) {
		case "ERROR" :
			log.setLevel(Level.ERROR);
			break;
		case "WARN" :
			log.setLevel(Level.WARN);
			break;
		case "INFO" :
			log.setLevel(Level.INFO);
			break;
		case "DEBUG" :
			log.setLevel(Level.DEBUG);
			break;
		case "OFF" :
			log.setLevel(Level.OFF);
			break;
		}		
	}
	
	public void dailyRefresh() {
		
		log.info("===> Scheduling daily resync");
		
		TimerTask dailyTask = new TimerTask() {
			public void run() {
				log.info("DAILY REFRESH TASK - START");
				try {
					getAndSendTows();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				log.info("DAILY REFRESH TASK - COMPLETE");
			}
		};
		
		// Set the time that the daily sync runs
		DateTime sched = new DateTime()
				.withHourOfDay(Integer.parseInt(refreshTimeStr.split(":")[0]))
				.withMinuteOfHour(Integer.parseInt(refreshTimeStr.split(":")[1]))
				.withMinuteOfHour(0)
				.withSecondOfMinute(0)
				.withMillisOfSecond(0);
		
		// Change time to tomorrow if the scheduled time for today has already passed
		if (sched.isBeforeNow()) {
			sched = sched.plusDays(1);
		}
			

		Timer timer = new Timer("Daily Refresh");
		timer.schedule(
		  dailyTask,
		  sched.toDate().getTime(),
		  1000 * 60 * 60 * 24
		);

		log.info("<=== Daily Sync Scheduled at: "+sched.toDate().toString());

		
	}
	
	public boolean getAndSendTows() throws Exception{
		
		DateTime dt = new DateTime();
		DateTime fromTime = new DateTime(dt.plusMinutes(fromMin));
		DateTime toTime = new DateTime(dt.plusMinutes(toMin));
		
		String from = dtf.print(fromTime);
		String to = dtf.print(toTime);
		
		XSLTransformService xft = new XSLTransformService("sync.xsl");
		String response = null;
		
		try {
			response = getTows(from,to);
			response = xft.transform(response);
		} catch (Exception e1) {
			e1.printStackTrace();
			return false;
		} 
		
		String towings = "";
		Context context = new Context();
		try (QueryProcessor proc = new QueryProcessor(queryBody, context)) {
			proc.bind("var1", response);
			Iter iter = proc.iter();
			for (Item item; (item = iter.next()) != null;) {
				String tow = item.serialize().toString();
				String rego = "<Registration>"+getRegistration(tow)+"</Registration>";
				tow = tow.replaceAll("</FlightIdentifier>", rego+"\n</FlightIdentifier>");						
				towings = towings.concat(tow).concat("\r\n");
			}
		}
		
		String msg = String.format(syncTemplate, towings);

		try {
			MSender send = new MSender(ibmoutqueue, host, qm, channel,  port,  user,  pass);
			send.mqPut(msg);
			log.info("Sync Sent");
			return true;
		} catch (Exception e) {
			log.error("Sync Send Error");
			log.error(e.getMessage());
			return false;
		}
	}

	public void monitor(long period){

		log.info("===> Start Request Listner Monitor");
		TimerTask repeatedTask = new TimerTask() {
			public void run() {
				if (t.isAlive()) {
					log.info("Tow Request Processor thread active");
				} else {
					log.error("Tow Request Processor thread is not active");
				}
			}
		};
		Timer timer = new Timer("Requst Monitor");
		long delay  = 5000L;
		if (period > 0) {
			timer.scheduleAtFixedRate(repeatedTask, delay, period);
		}
		log.info("<=== Request Listner Monitor Starteted");

	}
	
	public String getTows(String from, String to) throws ClientProtocolException, IOException {
		
		String URI = String.format(towRequestURL, from, to);
		
		HttpClient client = HttpClientBuilder.create().build();
		HttpUriRequest request = RequestBuilder.get()
				.setUri(URI)
				.setHeader("Authorization", token)
				.build();
		
		HttpResponse response = client.execute(request);
		int statusCode = response.getStatusLine().getStatusCode();

		if (statusCode == HttpStatus.SC_OK) {
			log.info("GET OK");
			return EntityUtils.toString(response.getEntity());
		} else {
			log.info("GET FAILURE");
			return "<Status>Failed</Failed>";
		}				    
	}

	public class RequestListenerLoop extends Thread {

		public void run() {
			
			do {
				boolean connectionOK = false;
				int tries = 0;
				MReceiver recv = null;

				//Try connection to the IBMMQ Queue until number of retries exceeded
				do {
					try {
						tries = tries + 1;
						recv = new MReceiver(ibminqueue, host, qm, channel,  port,  user,  pass);
						connectionOK = true;
						tries = 0;
					} catch (Exception ex) {
						log.error(String.format("Error connection to source queue: Error Message %s ",ex.getMessage()));
						connectionOK = false;
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				} while (!connectionOK  && (tries < retriesIBMMQ || retriesIBMMQ == 0));

				// Number of retries exceeded
				if (!connectionOK) {
					log.error(String.format("Exceeded IBM MQ connect retry limit {%s}. Exiting", retriesIBMMQ));
					return;
				}

				log.info(String.format("Conected to queue %s", ibminqueue));


				boolean continueOK = true;
//				XSLTransformService xft = new XSLTransformService("request.xsl");

				do {
					try {

						String message = null;
						try {
							message = recv.mGet(msgRecvTimeout, true);
						} catch (MQException ex) {
							if ( ex.completionCode == 2 && ex.reasonCode == MQConstants.MQRC_NO_MSG_AVAILABLE) {
								log.info("No Request Messages");
								continue;
							}
						}
						log.info("Request Message Received");
						message = message.substring(message.indexOf("<"));
						
						Pattern p = Pattern.compile("CorrelationID>([a-zA-Z0-9]*)<");
					    Matcher m = p.matcher(message);
					    
					    String correlationID = "-";
					    if (m.find()) {
					    	correlationID = m.group(1).replaceAll(" ", "");
					    }
					    
					    
						DateTime dt = new DateTime();
						DateTime fromTime = new DateTime(dt.plusMinutes(fromMin));
						DateTime toTime = new DateTime(dt.plusMinutes(toMin));
						
						String from = dtf.print(fromTime);
						String to = dtf.print(toTime);
					    
					    
					    p = Pattern.compile("RangeFrom>(.*)<");
					    m = p.matcher(message);
					    
					    if (m.find()) {
					    	from = m.group(1).replaceAll(" ", "");
					    }

					    p = Pattern.compile("RangeTo>(.*)<");
					    m = p.matcher(message);
					    
					    if (m.find()) {
					    	to = m.group(1).replaceAll(" ", "");
					    }
					    
					    log.info(correlationID+"  "+from+" "+to);

						String response = getTows(from,to);

						
						String towings = "";
						Context context = new Context();
						try (QueryProcessor proc = new QueryProcessor(queryBody, context)) {
							proc.bind("var1", response);
							Iter iter = proc.iter();
							for (Item item; (item = iter.next()) != null;) {
								String tow = item.serialize().toString();
								String rego = "<Registration>"+getRegistration(tow)+"</Registration>";
								tow = tow.replaceAll("</FlightIdentifier>", rego+"\n</FlightIdentifier>");						
								towings = towings.concat(tow).concat("\r\n");
							}
						}
						
						String msg = String.format(template, correlationID, towings);
						System.out.println(prettyFormat(msg,2));

						try {
							MSender send = new MSender(ibmoutqueue, host, qm, channel,  port,  user,  pass);
							send.mqPut(msg);
							log.info("Request Response Sent");
						} catch (Exception e) {
							log.error("Request Response Send Error");
							log.error(e.getMessage());
						}
					} catch (Exception e) {
						log.error("Unhandled Exception "+e.getMessage());
						e.printStackTrace();
						recv.disconnect();
						continueOK = false;
					}
				} while (continueOK);
			} while (true);
		}
	}
	
	private String getRegistration(String notif) {
		
	    Pattern pDescriptor = Pattern.compile("<FlightDescriptor>(.*)</FlightDescriptor>");
	    Pattern pReg = Pattern.compile("<Registration>([a-zA-Z0-9]*)</Registration>");
	    String reg = "Not Available";


	    //Extract the flight descriptor
	    Matcher m = pDescriptor.matcher(notif);
	    String flightID = null;
	    if (m.find()) {
	    	flightID = m.group(1);
	    }
	    
	    if (flightID == null) {
	    	return reg;
	    }
	    
	    try {
	    	// Use the AMS Web Services to get the flight using the flight descriptor
			String flt = ams.getFlight(flightID);
			if (flt == null) {
				return reg;
			} 
			
			// Extract the registration from the flight record returned from AMS
		    Matcher mReg = pReg.matcher(flt);
		    if (mReg.find()) {
		    	reg = mReg.group(1);
		    }
		    return reg;
			
		} catch (Exception e) {
			e.printStackTrace();
			return reg;
		}

	}

	
	public static String prettyFormat(String input, int indent) {
	    try {
	        Source xmlInput = new StreamSource(new StringReader(input));
	        StringWriter stringWriter = new StringWriter();
	        StreamResult xmlOutput = new StreamResult(stringWriter);
	        TransformerFactory transformerFactory = TransformerFactory.newInstance();
	        Transformer transformer = transformerFactory.newTransformer(); 
	        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	        transformer.transform(xmlInput, xmlOutput);
	        return xmlOutput.getWriter().toString();
	    } catch (Exception e) {
	        throw new RuntimeException(e); // simple exception handling, please review it
	    }
	}

}