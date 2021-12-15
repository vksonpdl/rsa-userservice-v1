package com.example.demo.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.CreditCardInfo;
import com.example.demo.model.ServiceCommunicationModel;
import com.example.demo.model.User;
import com.example.demo.service.CreditCardServiceProxy;
import com.example.demo.service.UserService;
import com.example.demo.util.EncryptionUtil;
import com.example.demo.util.JWTUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

	@Autowired
	CreditCardServiceProxy creditCardServiceProxy;

	@Autowired
	EncryptionUtil encryptionUtil;
	
	@Autowired
	ObjectMapper mapper;
	
	@Autowired
	JWTUtil jwtUtil;

	@Override
	public User getUserData() {

		String creditCardNumber = "789456123";
		CreditCardInfo creditCardInfo = null;
		

		try {		
			
			log.info("creditCardNumber :" + creditCardNumber);
			jwtUtil.doJWTEncryption();
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
	public User getUserDataFromPost() {

		CreditCardInfo creditCardInfo = null;

		String creditCardNumber = "789456123";
		User user = User.builder().name("testUser").id(200L).creditCardNumber(creditCardNumber).build();

		try {

			log.info("user :" + mapper.writeValueAsString(user));
			
			
			String encryptedCreditCardNumber = encryptionUtil.encryptFromCloud(mapper.writeValueAsString(user));
			ServiceCommunicationModel model = ServiceCommunicationModel.builder().content(encryptedCreditCardNumber)
					.build();

			log.info("model :" + mapper.writeValueAsString(model));
			creditCardInfo = creditCardServiceProxy.getCreditcCardInfo(model);
			user.setCreditCardInfo(creditCardInfo);
		} catch (Exception ex) {
			log.error("Exception while calling creditCard service", ex);
		}

		return user;
	}

}
