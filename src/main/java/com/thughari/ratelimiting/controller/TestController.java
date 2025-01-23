package com.thughari.ratelimiting.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;



@RestController
public class TestController {
	
	@GetMapping("/api/test") //should be accessible number of times
	public String testMethod() {
		return "Request successful";
	}
	
	@GetMapping("/api/login") //should be rate limited
	public String doLogin() {
		return "login success";
	}
	
	@GetMapping("/api/all")
	public String getMethodName() {
		return "accessable to all";
	}
	
	
	
}
