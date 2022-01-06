package com.example.demo.util;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.kms.v1.PublicKey;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.crypto.RSAEncrypter;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;

import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JWEUtil {

	@Autowired
	ObjectMapper mapper;

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

	public void testJWT() throws NoSuchAlgorithmException {
		KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance("RSA");
		keyGenerator.initialize(1024);

		KeyPair kp = keyGenerator.genKeyPair();
		java.security.PublicKey publicKey = (java.security.PublicKey) kp.getPublic();
		PrivateKey privateKey = (PrivateKey) kp.getPrivate();

		String encodedPublicKey = Base64.getEncoder().encodeToString(publicKey.getEncoded());
		String encodedPrivateKey = Base64.getEncoder().encodeToString(privateKey.getEncoded());

		log.info("encodedPublicKey :{}", encodedPublicKey);
		log.info("encodedPrivateKey :{}", encodedPrivateKey);

		String token = Jwts.builder().setSubject("userName")
				.setExpiration(new Date((new Date().getTime() + (1000 * 60 * 24)))).setIssuer("test@test.com")
				.claim("groups", new String[] { "user", "admin" }).setIssuedAt(new Date())
				// RS256 with privateKey
				.signWith(SignatureAlgorithm.RS256, privateKey).compact();

		log.info("token :{}", token);

		try {

			Jwt jwt = Jwts.parser().setSigningKey(publicKey).parse(token);

			log.info("jwt.getBody()  :{}", jwt.getBody());
			log.info("jwt.getHeader()  :{}", jwt.getHeader());
		} catch (Exception e) {
			log.error("Exception :{}", e.getMessage());
		}
	}

}
