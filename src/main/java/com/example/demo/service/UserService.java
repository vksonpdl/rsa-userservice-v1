package com.example.demo.service;

import com.example.demo.model.JWTEncryptionModel;
import com.example.demo.model.User;

public interface UserService {
	
	public User getUserData();

	public User getCreditCardDetails();
	
	public JWTEncryptionModel encryptUser(String userId);

}
