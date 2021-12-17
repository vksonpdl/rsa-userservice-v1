package com.example.demo.util;



import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.crypto.RSAEncrypter;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JWEUtil {

	public String doJWTEncryption(RSAPublicKey rsaKey, Object object) {

		try {

			

			Date d = new Date();

			JWTClaimsSet.Builder claimSet = new JWTClaimsSet.Builder();

			claimSet.issuer("test-issuer");
			claimSet.subject("subject");

			claimSet.claim("appId", "APP-1212_USER_SERVICE");
			claimSet.claim("userId", "test-user");
			claimSet.claim("body", object);

			claimSet.expirationTime(new Date(d.getTime() + (1000 * 60 * 2)));
			claimSet.notBeforeTime(d);
			claimSet.jwtID(UUID.randomUUID().toString());

			JWEHeader header = new JWEHeader(JWEAlgorithm.RSA_OAEP_256, EncryptionMethod.A128GCM);

			EncryptedJWT jwt = new EncryptedJWT(header, claimSet.build());

			RSAEncrypter encrypter = new RSAEncrypter(rsaKey);

			jwt.encrypt(encrypter);

			return jwt.serialize();

		} catch (Exception e) {
			log.error("Exception Occured", e);

			return "Exception";
		}
	}

}
