package com.example.demo.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.CreditCardInfo;
import com.example.demo.model.User;
import com.example.demo.service.CreditCardServiceProxy;
import com.example.demo.service.UserService;
import com.example.demo.util.EncryptionUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

	@Autowired
	CreditCardServiceProxy creditCardServiceProxy;

	@Autowired
	EncryptionUtil encryptionUtil;

	@Override
	public User getUserData() {

		String creditCardNumber = "789456123";
		CreditCardInfo creditCardInfo = null;
		
		encryptionUtil.encryptFromCloud();

		try {
			log.info("creditCardNumber :" + creditCardNumber);
			String encryptedCreditCardNumber = encryptionUtil.encrypt(creditCardNumber);
			creditCardInfo = creditCardServiceProxy.getCreditcCardInfo(encryptedCreditCardNumber);
			log.info("encryptedCreditCardNumber :" + encryptedCreditCardNumber);
		} catch (Exception ex) {
			log.error("Exception while calling creditCard service", ex);
		}

		User user = User.builder().name("testUser").id(200L).creditCardNumber(creditCardNumber)
				.creditCardInfo(creditCardInfo).build();

		return user;
	}

}
