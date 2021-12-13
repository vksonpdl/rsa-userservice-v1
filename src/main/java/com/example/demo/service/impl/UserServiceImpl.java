package com.example.demo.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.CreditCardInfo;
import com.example.demo.model.User;
import com.example.demo.model.UserDto;
import com.example.demo.service.CreditCardServiceProxy;
import com.example.demo.service.SendUserJsonToCreditCard;
import com.example.demo.service.UserService;
import com.example.demo.util.EncryptionUtil;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

	@Autowired
	CreditCardServiceProxy creditCardServiceProxy;
	
	@Autowired
	SendUserJsonToCreditCard sendUserJsonToCreditCard;

	@Autowired
	EncryptionUtil encryptionUtil;

	@Override
	public User getUserData() {

		String creditCardNumber = "789456123";
		CreditCardInfo creditCardInfo = null;
		

		try {		
			
			log.info("creditCardNumber :" + creditCardNumber);
			String encryptedCreditCardNumber = encryptionUtil.encryptFromCloud(creditCardNumber);
			log.info("encryptedCreditCardNumber :" + encryptedCreditCardNumber);
			creditCardInfo = creditCardServiceProxy.getCreditcCardInfo(encryptedCreditCardNumber);	
			
		} catch (Exception ex) {
			log.error("Exception while calling creditCard service", ex);
		}

		User user = User.builder().name("testUser").id(200L).creditCardNumber(creditCardNumber)
				.creditCardInfo(creditCardInfo).build();

		return user;
	}

	@Override
	public String getJsonUser() {
		String status=null;
		try {
		
		UserDto userDto = UserDto.builder().name("testUser").id(200L).creditCardNumber("98761234")
				.pin(1111).build();
		String userJson = new Gson().toJson(userDto);
		 status = sendUserJsonToCreditCard.getStatus(userJson);	
		}
		catch (Exception exp) {
			log.error("Exception while calling creditCard service", ex);
		}
		
		
		return status;
	}

}
