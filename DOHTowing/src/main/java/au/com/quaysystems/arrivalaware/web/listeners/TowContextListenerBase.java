package au.com.quaysystems.arrivalaware.web.listeners;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import au.com.quaysystems.arrivalaware.web.services.AMSServices;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;


public class TowContextListenerBase implements ServletContextListener {


	protected static final Logger log = (Logger)LoggerFactory.getLogger(TowContextListenerBase.class);
	
	@Autowired
	public AMSServices ams;

	protected Thread t;

	@Value("${mq.msmqbridgequeue}")
	protected String msmqbridge;
	
	@Value("${mq.ibmoutqueue}")
	protected String ibmoutqueue;

	@Value("${msg.recv.timeout:5000}")
	protected int msgRecvTimeout;

	@Value("${monitor.period:60000}")
	protected long monitorPeriod;

	@Value("${ibmmq.retries:0}")
	protected int retriesIBMMQ;
	
	@Value("${log.level:INFO}")
	protected String logLevel;

	@Value("${mq.host}")
	protected String host;
	@Value("${mq.qmgr}")
	protected String qm;
	@Value("${mq.channel}")
	protected String channel;
	@Value("${mq.port}")
	protected int port;
	@Value("${mq.user}")
	protected String user;
	@Value("${mq.pass}")
	protected String pass;

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		return;
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


	public String getRegistration(String notif) {
		
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