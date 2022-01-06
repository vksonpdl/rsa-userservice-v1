package com.example.demo.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.CreditCardInfo;
import com.example.demo.model.JWTEncryptionModel;
import com.example.demo.model.MessageDto;
import com.example.demo.model.User;
import com.example.demo.model.UserDto;
import com.example.demo.service.CreditCardServiceProxy;
import com.example.demo.service.UserService;
import com.example.demo.util.EncryptionUtil;
import com.example.demo.util.JWEUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

	@Autowired
	CreditCardServiceProxy creditCardServiceProxy;
	
	@Autowired
	JWEUtil jweUtil;

	@Autowired
	EncryptionUtil encryptionUtil;
	
	@Autowired
	ObjectMapper objectMapper;


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
	public User getCreditCardDetails() {
		
		CreditCardInfo creditCardInfo = null;
		MessageDto messagedto=null;
		
		try {
		
		UserDto userDto = UserDto.builder().name("testUser").id(200L).creditCardNumber("98761234")
				.pin(1111).build();
		
		
		String userDtoString = objectMapper.writeValueAsString(userDto);
		log.info("userDTO : {}",userDtoString);
		
		String encryptedUserDtoObject = encryptionUtil.encryptFromCloud(userDtoString);		
		messagedto=MessageDto.builder().message(encryptedUserDtoObject).build();
		
		log.info("MessageDTO : {}" ,objectMapper.writeValueAsString(messagedto) );
		
		creditCardInfo = creditCardServiceProxy.getCreditCarddetails(messagedto);
		
		} 
		
		catch (Exception exp) {
			log.error("Exception while calling creditCard service");
		}
		
		User user = User.builder().name("testUser").id(200L).creditCardNumber("98761234")
				.creditCardInfo(creditCardInfo).build();
		return user;
		
		
	}

	@Override
	public JWTEncryptionModel encryptUser(String userId) {

		JWTEncryptionModel jWTEncryptionModel = null;

		Long id = 100L;
		String creditCardNumber = "1245783265";

		try {
			id = Long.valueOf(userId);

		} catch (Exception e) {
			log.error(" Exception Occured while converting userId :{}, Exception : [{}]", userId, e.getMessage());
		}

		try {
			
			
			jweUtil.testJWT();

			UserDto userDto = UserDto.builder().id(id).creditCardNumber(creditCardNumber).name("userName").pin(1224)
					.build();

			//Calling jweUtil to do JWE based on both KMS private Key and Locally Managed Private Key
			String jwtToken = jweUtil.doJWTEncryption(encryptionUtil.getpublicKey(), userDto);
			String jwtTokenWithKMS = jweUtil.doJWTEncryption(encryptionUtil.getpublicKeyFromKMS(), userDto);

			log.info("userDto :{}", objectMapper.writeValueAsString(userDto));
			log.info("jwtToken :{}", jwtToken);
			log.info("jwtTokenWithKMS :{}", jwtTokenWithKMS);

			// Calling Creditcard Service By Passing JWT Token
			CreditCardInfo creditCardInfo = creditCardServiceProxy.getCreditCardInfoWithJWT(jwtToken);

			
			jWTEncryptionModel = JWTEncryptionModel.builder().userDto(userDto).jwtToken(jwtToken)
					.jwtTokenWithKMS(jwtTokenWithKMS).creditCardInfo(creditCardInfo).build();

		} catch (Exception e) {

			log.error("Exception : [{}]", e.getMessage());
		}

		return jWTEncryptionModel;
	}

}
