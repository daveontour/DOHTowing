package au.com.quaysystems.arrivalaware.web.listeners;

import javax.servlet.ServletContextEvent;
import javax.servlet.annotation.WebListener;

import org.slf4j.LoggerFactory;

import com.ibm.mq.MQException;
import com.ibm.mq.constants.MQConstants;

import au.com.quaysystems.arrivalaware.web.mq.MReceiver;
import au.com.quaysystems.arrivalaware.web.mq.MSender;
import au.com.quaysystems.arrivalaware.web.services.XSLTransformService;
import ch.qos.logback.classic.Logger;

/*
 * 
 * 1. Listen for incoming request on the bridging queue, adds aircraft registration if available
 * and puts it on the output queue
 * 
 * The handling of construction of XML documents is inelegant. Regex is used to extract values rather 
 * than a parser. Elements are added by String substitution. This was deliberate to try keep it as light-weight
 * as possible. 
 * 
 * An XSL transformation is used for transforming the incoming message to the required output. It could be done 
 * with a template and string substitution but I've done it for my own amusement
 * 
 * Uses method in the base class to extract flight descriptor for towing message and get registration for AMS
 */
@WebListener
public class BridgeContextListener extends TowContextListenerBase {


	private static final Logger log = (Logger)LoggerFactory.getLogger(BridgeContextListener.class);
	
	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		
		// Set the configured logging level
		this.setLogLevel();
		
		// Start the listener for incoming notification
		this.startListener();
	}
	
	public void startListener() {
		
		log.info("---> Starting Notification Listener");
		t = new NotifcationBridgeListener();
		t.setName("Notif. Process");
		t.start();
		log.info("<--- Started Notification Listener");
		
	}
	
	public class NotifcationBridgeListener extends Thread {

		public void run() {

			do {
				
				MReceiver recv = connectToMQ(msmqbridge);
				if (recv == null) {
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
								log.trace("No Notification Messages");
								continue;
							}
						}
						log.trace("Message Received");
						message = message.substring(message.indexOf("<")).replace("xsi:type", "type");
						String notification = xft.transform(message);
						notification = notification.replace("xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"", "");
						String rego = "<Registration>"+getRegistration(notification)+"</Registration>";
						notification = notification.replaceAll("</FlightIdentifier>", rego+"\n</FlightIdentifier>");						
						log.trace("Message Processed");

						try {
							MSender send = new MSender(ibmoutqueue, host, qm, channel,  port,  user,  pass);
							send.mqPut(notification);
							log.trace("Message Sent");
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
	}
}