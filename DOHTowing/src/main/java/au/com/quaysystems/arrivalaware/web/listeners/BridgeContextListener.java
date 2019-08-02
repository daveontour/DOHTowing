package au.com.quaysystems.arrivalaware.web.listeners;

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

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.ibm.mq.MQException;
import com.ibm.mq.constants.MQConstants;

import au.com.quaysystems.arrivalaware.web.mq.MReceiver;
import au.com.quaysystems.arrivalaware.web.mq.MSender;
import au.com.quaysystems.arrivalaware.web.services.AMSServices;
import au.com.quaysystems.arrivalaware.web.services.XSLTransformService;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;


@WebListener
public class BridgeContextListener implements ServletContextListener {


	private static final Logger log = (Logger)LoggerFactory.getLogger(BridgeContextListener.class);
	
	@Autowired
	public AMSServices ams;

	private Thread t;

	@Value("${mq.msmqbridgequeue}")
	private String msmqbridge;
	
	@Value("${mq.ibmoutqueue}")
	private String ibmoutqueue;

	@Value("${msg.recv.timeout:5000}")
	private int msgRecvTimeout;

	@Value("${monitor.period:60000}")
	private long monitorPeriod;

	@Value("${ibmmq.retries:0}")
	private int retriesIBMMQ;
	
	@Value("${log.level:INFO}")
	private String logLevel;

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

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		
		// Set the configured logging level
		this.setLogLevel();
		
		// Start the listener for incoming notification
		this.startListener();

		return;

	}
	
	public void startListener() {
		
		log.info("---> Starting Notification Listener");
		t = new NotifcationBridgeListener();
		t.setName("Notif. Process");
		t.start();
		log.info("<--- Started Notification Listener");
		
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

	public void monitor(long period){

		log.info("---> Start Notification Listner Monitor");
		TimerTask repeatedTask = new TimerTask() {
			public void run() {
				if (t.isAlive()) {
					log.info("Tow Notification Queue Processor thread active");
				} else {
					log.error("Tow Notification Queue Processor thread is not active");
				}
			}
		};
		Timer timer = new Timer("Monitor");
		long delay  = 5000L;
		if (period > 0) {
			timer.scheduleAtFixedRate(repeatedTask, delay, period);
		}
		log.info("<--- Started Notification Listner Monitor");

	}

	public class NotifcationBridgeListener extends Thread {

		public void run() {

			do {
				boolean connectionOK = false;
				int tries = 0;
				MReceiver recv = null;

				//Try connection to the IBMMQ Queue until number of retries exceeded
				do {
					try {
						tries = tries + 1;
						recv = new MReceiver(msmqbridge, host, qm, channel,  port,  user,  pass);
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

				log.info(String.format("Conected to queue %s", msmqbridge));
				boolean continueOK = true;

				
				XSLTransformService xft = new XSLTransformService("notifications.xsl");

				do {
					try {

						String message = null;
						try {
							message = recv.mGet(msgRecvTimeout, true);
						} catch (MQException ex) {
							if ( ex.completionCode == 2 && ex.reasonCode == MQConstants.MQRC_NO_MSG_AVAILABLE) {
								log.info("No Notification Messages");
								continue;
							}
						}
						log.info("Message Received");
						message = message.substring(message.indexOf("<")).replace("xsi:type", "type");
						String notification = xft.transform(message);
						notification = notification.replace("xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"", "");
						String rego = "<Registration>"+this.getRegistration(notification)+"</Registration>";
						notification = notification.replaceAll("</FlightIdentifier>", rego+"\n</FlightIdentifier>");						
						log.info("Message Processed");

						try {
							MSender send = new MSender(ibmoutqueue, host, qm, channel,  port,  user,  pass);
							send.mqPut(notification);
							log.info("Message Sent");
						} catch (Exception e) {
							log.error("Message Send Error");
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
	}
}