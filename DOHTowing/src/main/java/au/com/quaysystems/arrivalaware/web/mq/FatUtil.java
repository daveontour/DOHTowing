package au.com.quaysystems.arrivalaware.web.mq;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ibm.mq.MQEnvironment;


@Service
public class FatUtil {

	private static FatUtil single;
	
	public Properties props;
	
	@Value("${mq.qmgr}")
	public String qmgr;
	public String channel;
	public String hostname;
	public String userID;
	public String password;
	
	@Value("${mq.port}")
	public int port;
	public boolean use_client_connection = true;

	private FatUtil() {
			if (use_client_connection) {
				 setClientConnection();
			}
	}

	public static FatUtil getFatUtil() {
		if (single == null) {
			single = new FatUtil();
		}
		return single;
	}
	
	public void  setClientConnection() {
		
//		System.out.println(port);
//		System.out.println(qmgr);
//		MQEnvironment.hostname = "localhost";
//		MQEnvironment.channel = "AMS.SVRCONN";
//		MQEnvironment.port = 1415;
//		MQEnvironment.userID = userID;
//		MQEnvironment.password = password;
	}
}
