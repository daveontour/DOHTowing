package au.com.quaysystems.arrivalaware.web.mq;

import java.io.IOException;

import com.ibm.mq.MQEnvironment;
import com.ibm.mq.MQException;
import com.ibm.mq.MQGetMessageOptions;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.constants.MQConstants;

public class MReceiver extends MBase{

	private MQMessage theMessage;
	private String content;
	public String queueName;

	public MReceiver(String q, String host, String qm, String channel, int port, String user, String pass) throws MQException {
		
//		MQEnvironment.hostname = host;
//		MQEnvironment.channel = channel;
//		MQEnvironment.port = port;
//		MQEnvironment.userID = user;
//		MQEnvironment.password = pass;
		
		queueName = q;
		qMgr = new MQQueueManager(qm);
		int openOptions =  MQConstants.MQOO_INQUIRE | MQConstants.MQOO_INPUT_AS_Q_DEF;
		queue = qMgr.accessQueue(q, openOptions, null, null, null);
		
		
	}


	public String getMessageContent() {
		if (content == null) {
			try {
				content = theMessage.readStringOfByteLength(theMessage.getDataLength());
			} catch (IOException e) {
				e.printStackTrace();
				return ("Error");
			}
		}
		return content;
	}

	public String mGet(int waitTime) {		
		try {
			return mGet(waitTime, false);
		} catch (MQException e) {
			return null;
		}
	}	


	public String mGet(int waitTime, boolean failOnTimeout) throws MQException{

		try {

			theMessage = new MQMessage();
			MQGetMessageOptions gmo = new MQGetMessageOptions();

			//Define the time to wait for messages

			gmo.options = MQConstants.MQGMO_WAIT;
			gmo.waitInterval = waitTime;
			queue.get(theMessage,gmo); 

			try {
				return theMessage.readStringOfByteLength(theMessage.getDataLength());
			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (MQException ex) {
			if ( !failOnTimeout) {
				return null;
			} else {
				throw ex;
			}
		} 

		return null;
	}
}
