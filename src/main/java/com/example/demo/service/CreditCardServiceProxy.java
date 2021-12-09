package com.example.demo.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.example.demo.model.CreditCardInfo;



@FeignClient(name = "creditcard-service", url = "${service.creditcard.url}")
public interface CreditCardServiceProxy {

	
	@RequestMapping(method = RequestMethod.GET, value = "/creditcard/getdetails/{creditcardnumber}")
	public CreditCardInfo getCreditcCardInfo(@PathVariable("creditcardnumber") String creditcardnumber);

}
