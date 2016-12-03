package com.sharpic.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WebController {
	
	@RequestMapping(value={"/","home"})
	public String home(){
		return "index";
	}

	@RequestMapping(value={"/login"})
	public String login(){
		return "login";
	}

	@RequestMapping(value="/403")
	public String Error403(){
		return "403";
	}
}
