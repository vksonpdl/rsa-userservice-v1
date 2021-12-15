package com.example.demo.util;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.demo.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.crypto.RSADecrypter;
import com.nimbusds.jose.crypto.RSAEncrypter;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JWTUtil {

	@Value("${gcp.project-id}")
	String projectId;

	@Value("${gcp.key-zone}")
	String projectZone;

	@Value("${gcp.key-ring}")
	String keyRingName;

	@Value("${gcp.key-name-rsa}")
	String keyName;

	@Value("${gcp.key-version-rsa}")
	String keyVersion;

	@Autowired
	ObjectMapper mapper;

	private KeyPair getKeyPair() throws NoSuchAlgorithmException {
		KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
		keyPairGen.initialize(2048);
		return keyPairGen.generateKeyPair();

	}

	public void doJWTEncryption() {

		try {

			KeyPair keyPair = getKeyPair();

			Date now = new Date();
			User user = User.builder().creditCardNumber("12455678").name("userName").id(200L).build();

			JWTClaimsSet jwtClaims = new JWTClaimsSet.Builder().issuer("https://openid.net").subject("alice")
					.claim("request",user)
					.audience(Arrays.asList("https://app-one.com", "https://app-two.com"))
					.expirationTime(new Date(now.getTime() + 1000 * 60 * 10)) // expires in 10 minutes
					.notBeforeTime(now).issueTime(now).jwtID(UUID.randomUUID().toString())
					.build();
			
			
			

			JWEHeader header = new JWEHeader(JWEAlgorithm.RSA_OAEP_256, EncryptionMethod.A128GCM);

			EncryptedJWT jwt = new EncryptedJWT(header, jwtClaims);
		

					RSAEncrypter encrypter = new RSAEncrypter((RSAPublicKey) keyPair.getPublic());

			jwt.encrypt(encrypter);

			// Serialise to JWT compact form
			String jwtString = jwt.serialize();

			log.info("jwtString :{}", jwtString);

			// Parse
			EncryptedJWT jwt2 = EncryptedJWT.parse(jwtString);

			// Create a decrypter with the specified private RSA key
			RSADecrypter decrypter = new RSADecrypter(keyPair.getPrivate());
			
			try {
				log.info("jwt2.getPayload().toString() 2 :{}", jwt2.getPayload().toString());
				
				
			}catch (Exception e) {
				log.error("Exception occured",e);
			}

			jwt2.decrypt(decrypter);

			log.info("jwt2.getPayload().toString() 2 :{}", jwt2.getPayload().toString());
			
			
			
			

			//////////////// NEW

			/*
			 * Date d = new Date();
			 * 
			 * JWTClaimsSet.Builder claimSet = new JWTClaimsSet.Builder();
			 * 
			 * claimSet.issuer("test-issuer"); claimSet.subject("subject");
			 * 
			 * claimSet.claim("appId", "APP-1212"); claimSet.claim("userId", "test-user");
			 * 
			 * claimSet.expirationTime(new Date(d.getTime() + (1000 * 60 * 2)));
			 * claimSet.notBeforeTime(d); claimSet.jwtID(UUID.randomUUID().toString());
			 * 
			 * JWEHeader header = new JWEHeader(JWEAlgorithm.RSA_OAEP_256,
			 * EncryptionMethod.A128GCM);
			 * 
			 * EncryptedJWT jwt = new EncryptedJWT(header, claimSet.build());
			 * 
			 * RSAPublicKey rsaKey = (RSAPublicKey) keyPair.getPublic();
			 * 
			 * RSAEncrypter encrypter = new RSAEncrypter(rsaKey);
			 * 
			 * log.info("jwt.getState().toString() :{}", jwt.getState().toString());
			 * jwt.encrypt(encrypter); log.info("jwt.getState().toString() :{}",
			 * jwt.getState().toString());
			 * 
			 * String encryptedText = jwt.serialize();
			 * 
			 * log.info("encryptedText :{}", encryptedText);
			 * 
			 * log.info("jwt.getPayload().toString() :{}", jwt.getPayload().toString());
			 * 
			 * RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
			 * 
			 * EncryptedJWT jwtNew = EncryptedJWT.parse(encryptedText);
			 * 
			 * RSADecrypter decrypter = new RSADecrypter(privateKey);
			 * 
			 * log.info("jwtNew.getState().toString() :{}", jwtNew.getState().toString());
			 * 
			 * jwtNew.decrypt(decrypter);
			 * 
			 * log.info("jwtNew.getState().toString() :{}", jwtNew.getState().toString());
			 * 
			 * Payload payload = jwtNew.getPayload();
			 * 
			 * log.info("payload :{}", payload.toString());
			 * 
			 * log.info("jwtNew.getCipherText() :{}", jwtNew.getCipherText());
			 * log.info("decrypted Text :{}", jwtNew.serialize());
			 */

		} catch (Exception e) {
			log.error("Exception Occured", e);
		}
	}
}
