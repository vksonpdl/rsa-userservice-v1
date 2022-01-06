package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.RsaKeyService;

@RestController
@RequestMapping("/rsa")
public class RsaKeyController {

	@Autowired
	RsaKeyService service;

	@GetMapping("/getchunkids")
	public List<String> getChunkIds() {
		return service.getKeyChunkIds();

	}

}
