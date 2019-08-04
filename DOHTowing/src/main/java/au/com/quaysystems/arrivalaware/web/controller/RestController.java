package au.com.quaysystems.arrivalaware.web.controller;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.springframework.web.bind.annotation.GetMapping;

@org.springframework.web.bind.annotation.RestController
public class RestController {

	@GetMapping(value = "/")
	public String getRoot() {
		
		// Load all the properties used by the sub classes
		try {
			Properties props = getProperties();
			return "Towing Interface Properties <br/>"+props.toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Tow Running - Unable to Display Properties";
		}
	}
	
	public Properties getProperties() throws IOException {
		 
		InputStream inputStream = null;
		Properties props = new Properties();

		try {
			String propFileName = "application.properties";
 
			inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
 
			if (inputStream != null) {
				props.load(inputStream);
			} else {
				throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
			}
 
 		} catch (Exception e) {
			System.out.println("Exception: " + e);
		} finally {
			inputStream.close();
		}
		return props;
	}
}