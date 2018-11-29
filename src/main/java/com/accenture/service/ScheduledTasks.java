package com.accenture.service;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.accenture.config.ApplicationConfigReader;
import com.accenture.controller.UserService;
import com.accenture.model.ApplicationConstant;

@Component
public class ScheduledTasks {
	
	private static final Logger log = LoggerFactory.getLogger(UserService.class);
	private SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	
	private final RabbitTemplate rabbitTemplate;
	private  ApplicationConfigReader applicationConfig;
	private  MessageSender messageSender;
	
	public ApplicationConfigReader getApplicationConfig() {
		return applicationConfig;
	}

	@Autowired
	public void setApplicationConfig(ApplicationConfigReader applicationConfig) {
		this.applicationConfig = applicationConfig;
	}

	@Autowired
	public ScheduledTasks(final RabbitTemplate rabbitTemplate) {
		this.rabbitTemplate = rabbitTemplate;
	}

	public MessageSender getMessageSender() {
		return messageSender;
	}

	@Autowired
	public void setMessageSender(MessageSender messageSender) {
		this.messageSender = messageSender;
	}
	
	@Scheduled(fixedRate= 10000)
	public String performTask() {
		System.out.println("Regular task performed at "
				+ dateFormat.format(new Date()));
//		String exchange = getApplicationConfig().getApp1Exchange();
		String exchange = getApplicationConfig().getApp2Exchange();
		String routingKey = getApplicationConfig().getApp1RoutingKey();

		try {
			messageSender.sendMessage(rabbitTemplate, exchange, routingKey, "Hello , Naveen Your success fully sent the Rabit MQ Message");
			return ApplicationConstant.IN_QUEUE+ HttpStatus.OK;
			
		} catch (Exception ex) {
			log.error("Exception occurred while sending message to the queue. Exception= {}", ex);
			return ApplicationConstant.MESSAGE_QUEUE_SEND_ERROR+
					HttpStatus.INTERNAL_SERVER_ERROR;
		}
	}
	@Scheduled(initialDelay=1000,fixedRate=10000)
	public void performDelayedTask() {
		/*System.out.println("Delayed Regular task performed at "
				+ dateFormat.format(new Date()));*/

	}
	
	
	@Scheduled(cron = "*/5 * * * * *")
	public void performTaskUsingCron() {

	/*	System.out.println("Regular task performed using Cron at "
				+ dateFormat.format(new Date()));*/

	}

}
