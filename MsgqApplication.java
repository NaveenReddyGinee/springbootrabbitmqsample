package com.accenture;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MsgqApplication extends SpringBootServletInitializer  {
	
	public static void main(String[] args) {
		SpringApplication.run(MsgqApplication.class, args);
	}
	
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(MsgqApplication.class);
	}
	
	


}
