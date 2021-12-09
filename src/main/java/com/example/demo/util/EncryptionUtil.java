package com.example.demo.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.stream.Collectors;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.example.demo.constants.AppConstants;
import com.google.cloud.kms.v1.CryptoKeyVersionName;
import com.google.cloud.kms.v1.KeyManagementServiceClient;


import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class EncryptionUtil {

	@Value("${key.public}")
	String publicKeyString;

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

	public String encryptFromCloud(String plainText)
			throws IOException, InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException,
			IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
		

		if (StringUtils.hasText(plainText)) {
			KeyManagementServiceClient client = KeyManagementServiceClient.create();

			CryptoKeyVersionName keyVersionName = CryptoKeyVersionName.of(projectId, projectZone, keyRingName, keyName,
					keyVersion);

			// Get the public key.
			com.google.cloud.kms.v1.PublicKey publicKey = client.getPublicKey(keyVersionName);

			BufferedReader bufferedReader = new BufferedReader(new StringReader(publicKey.getPem()));
			String encoded = bufferedReader.lines()
					.filter(line -> !line.startsWith(AppConstants.KEY_BEGIN) && !line.startsWith(AppConstants.KEY_END))
					.collect(Collectors.joining());
			byte[] derKey = Base64.getDecoder().decode(encoded);

			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(derKey);
			java.security.PublicKey rsaKey = KeyFactory.getInstance(AppConstants.ENCRYPTION_MODE_RSA).generatePublic(keySpec);

			Cipher cipher = Cipher.getInstance(AppConstants.CIPHER_TRANSFORMATION);
			OAEPParameterSpec oaepParams = new OAEPParameterSpec(AppConstants.MDNAME_SHA, AppConstants.MFGNAME_MGF1, MGF1ParameterSpec.SHA256,
					PSource.PSpecified.DEFAULT);
			cipher.init(Cipher.ENCRYPT_MODE, rsaKey, oaepParams);
			byte[] ciphertext = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

			String encryptedString = Base64.getEncoder().encodeToString(ciphertext);
			
			log.info("encryptedString : {}",encryptedString);

			return encryptedString.replaceAll(AppConstants.CHAR_FORWARDSLASH, AppConstants.CHAR_ASTERISK);

		} else {
			return plainText;
		}

	}

}
