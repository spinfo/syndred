package de.uk.spinfo.syndred.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FrontendController {

	@GetMapping("/")
	public String root() {
		return "redirect:/syndred.html";
	}

}