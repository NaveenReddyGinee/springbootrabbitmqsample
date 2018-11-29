package com.accenture.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.accenture.config.ApplicationConfigReader;
import com.accenture.model.ApplicationConstant;
import com.accenture.model.UserDetails;

@Service
public class MessageListener {
private static final Logger log = LoggerFactory.getLogger(MessageListener.class);
    
    @Autowired
    ApplicationConfigReader applicationConfigReader;
    
    
    org.springframework.web.client.RestTemplate restTemplate = new RestTemplate();
    
    
    
    String baseURL= "http://localhost:8585/userservice/";

    
    @RabbitListener(queues = "${app1.queue.name}")
    public void receiveMessageForApp1(final String data) {
    	log.info("Received message: {} from app1 queue.", data);
    	
    	try {
    		log.info("Making REST call to the API");
    		//TODO: Code to make REST call
    		String resp = restTemplate.getForObject(baseURL+"/consume/"+data, String.class);
    		log.info("Response : " + resp);
        	log.info("<< Exiting receiveMessageForApp1() after API call.");
    	} catch(HttpClientErrorException  ex) {
    		
    		if(ex.getStatusCode() == HttpStatus.NOT_FOUND) {
        		log.info("Delay...");
        		try {
    				Thread.sleep(ApplicationConstant.MESSAGE_RETRY_DELAY);
    			} catch (InterruptedException e) { 
    				
    			}
    			
    			log.info("Throwing exception so that message will be requed in the queue.");
    			// Note: Typically Application specific exception should be thrown below
    			throw new RuntimeException();
    		} else {
    			throw new AmqpRejectAndDontRequeueException(ex); 
    		}
    		
    	} catch(Exception e) {
    		log.error("Internal server error occurred in API call. Bypassing message requeue {}", e);
    		throw new AmqpRejectAndDontRequeueException(e); 
    	}

    }

    @RabbitListener(queues = "${app2.queue.name}")
    public void receiveMessageForApp2(final UserDetails reqObj) {
    	log.info("Received message: {} from app2 queue.", reqObj);
    	
    	try {
    		log.info("Making REST call to the API");
    		//TODO: Code to make REST call
        	log.info("<< Exiting receiveMessageCrawlCI() after API call.");
    	} catch(HttpClientErrorException  ex) {
    		
    		if(ex.getStatusCode() == HttpStatus.NOT_FOUND) {
        		log.info("Delay...");
        		try {
    				Thread.sleep(ApplicationConstant.MESSAGE_RETRY_DELAY);
    			} catch (InterruptedException e) { }
    			
    			log.info("Throwing exception so that message will be requed in the queue.");
    			// Note: Typically Application specific exception can be thrown below
    			throw new RuntimeException();
    		} else {
    			throw new AmqpRejectAndDontRequeueException(ex); 
    		}
    		
    	} catch(Exception e) {
    		log.error("Internal server error occurred in python server. Bypassing message requeue {}", e);
    		throw new AmqpRejectAndDontRequeueException(e); 
    	}

    }

}
