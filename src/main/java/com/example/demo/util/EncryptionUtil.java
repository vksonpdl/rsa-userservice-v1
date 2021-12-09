package com.example.demo.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.example.demo.constants.AppConstants;
import com.google.cloud.kms.v1.KeyManagementServiceClient;
import com.google.cloud.kms.v1.KeyManagementServiceClient.ListKeyRingsPagedResponse;
import com.google.cloud.kms.v1.KeyRing;
import com.google.cloud.kms.v1.LocationName;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class EncryptionUtil {

	@Value("${key.public}")
	String publicKeyString;
	
	@Value("${gcp.project-id}")
	String projectId;
	
	@Value("${gcp.project-zone}")
	String projectZone;

	public String encrypt(String plainText) throws InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidKeySpecException, IllegalBlockSizeException, BadPaddingException {

		if (StringUtils.hasText(plainText)) {
			byte[] publicKeyBytes = publicKeyString.getBytes(StandardCharsets.UTF_8);
			publicKeyBytes = Base64.getDecoder().decode(publicKeyBytes);
			KeyFactory keyFactory = KeyFactory.getInstance(AppConstants.ENCRYPTION_MODE_RSA);
			EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
			RSAPublicKey publicKey = (RSAPublicKey) keyFactory.generatePublic(publicKeySpec);

			Cipher encryptCipher = Cipher.getInstance(AppConstants.ENCRYPTION_MODE_RSA);
			encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);

			byte[] secretMessageBytes = plainText.getBytes(StandardCharsets.UTF_8);
			byte[] encryptedMessageBytes = encryptCipher.doFinal(secretMessageBytes);

			String encryptedString = Base64.getEncoder().encodeToString(encryptedMessageBytes);

			return encryptedString.replaceAll(AppConstants.CHAR_FORWARDSLASH, AppConstants.CHAR_ASTERISK);

		} else {

			return plainText;

		}
	}
	
	
	public void encryptFromCloud() {
		try (KeyManagementServiceClient client = KeyManagementServiceClient.create()) {
			LocationName parent = LocationName.of(projectId, projectZone);
			
			log.info("projectId :{},projectId");
			log.info("projectZone :{},projectZone");

			// Call the API.
			ListKeyRingsPagedResponse response = client.listKeyRings(parent);

			for (KeyRing keyRing : response.iterateAll()) {

				log.info("keyName :{}", keyRing.getName());
			}

		} catch (IOException e) {
			log.error("exception ", e);
		}
	}

}
