package uk.ac.ox.ctl.lti13.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

	@RequestMapping("/index2")
	public String index2() {
		return "index2.html";
	}
}
