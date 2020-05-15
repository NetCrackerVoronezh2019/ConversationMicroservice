package ru.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import ru.kafka.Microservices;


@RestController
public class ConfigControler {

	@Autowired Microservices m;
	
	
	
	@GetMapping("getAllInfo")
	public Microservices allmicro()
	{
		return m;
	}
}
