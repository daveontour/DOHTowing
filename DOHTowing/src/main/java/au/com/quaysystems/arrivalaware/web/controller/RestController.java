package au.com.quaysystems.arrivalaware.web.controller;
import org.springframework.web.bind.annotation.GetMapping;

@org.springframework.web.bind.annotation.RestController
public class RestController {

	@GetMapping(value = "/")
	public String getRoot() {
		return "Tow Running";
	}
}