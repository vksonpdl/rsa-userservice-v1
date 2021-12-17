package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JWTEncryptionModel {

	private String jwtToken;
	private String jwtTokenWithKMS;
	private CreditCardInfo creditCardInfo;
	private UserDto userDto;

}
