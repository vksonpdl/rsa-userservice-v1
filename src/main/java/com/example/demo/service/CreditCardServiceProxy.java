package com.example.demo.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.example.demo.model.CreditCardInfo;
import com.example.demo.model.MessageDto;



@FeignClient(name = "creditcard-service", url = "${service.creditcard.url}")
public interface CreditCardServiceProxy {
	
	@RequestMapping(method = RequestMethod.POST, value = "/creditcard/getcreditcard")
	public CreditCardInfo getCreditCarddetails(MessageDto messageDto);
	
	@RequestMapping(method = RequestMethod.GET, value = "/creditcard/getdetails/{creditcardnumber}")
	public CreditCardInfo getCreditcCardInfo(@PathVariable("creditcardnumber") String creditcardnumber);
	
	@RequestMapping(method = RequestMethod.GET, value = "/creditcard/getdetails/jwt/{jwtstring}")
	public CreditCardInfo getCreditCardInfoWithJWT(@PathVariable("jwtstring") String jwtstring);

}


