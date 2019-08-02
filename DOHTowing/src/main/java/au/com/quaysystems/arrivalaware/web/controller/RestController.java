package au.com.quaysystems.arrivalaware.web.controller;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;

@org.springframework.web.bind.annotation.RestController
public class RestController {

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
	
	@GetMapping(value = "/info")
	public String getRaw() {
		// Just returns all the active configuration parameters
		String info = String.format("Default From Time Offset (min): %s<br/> Default To Time Offset (min): %s<br/>", fromMin, toMin);	
		return info;
	}
}