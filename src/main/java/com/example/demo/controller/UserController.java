package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.JWTEncryptionModel;
import com.example.demo.model.User;
import com.example.demo.service.UserService;

@RestController
@CrossOrigin (origins = {"http://localhost:4200","http://146.148.98.149","http://146.148.98.149:80"})
@RequestMapping("/user")
public class UserController {

	@Autowired
	UserService service;

	@GetMapping("/retreivedata")
	public User getUser() {

		return service.getUserData();
	}

	@GetMapping("/getcreditcardinfo")
	public User getUserJson() {

		return service.getCreditCardDetails();
	}

	@GetMapping("/getencrypteduser/{userid}")
	public JWTEncryptionModel getEncryptedUser(@PathVariable("userid") String userid) {

		return service.encryptUser(userid);
	}

}
