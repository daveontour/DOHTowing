package au.com.quaysystems.arrivalaware.web.listeners;

import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.ibm.mq.MQException;
import com.ibm.mq.constants.MQConstants;

import au.com.quaysystems.arrivalaware.web.mq.MReceiver;
import au.com.quaysystems.arrivalaware.web.mq.MSender;
import au.com.quaysystems.arrivalaware.web.services.XSLTransformService;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;


@WebListener
public class BridgeContextListener implements ServletContextListener {


	private static final Logger log = (Logger)LoggerFactory.getLogger(BridgeContextListener.class);
	
	
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
	}
}