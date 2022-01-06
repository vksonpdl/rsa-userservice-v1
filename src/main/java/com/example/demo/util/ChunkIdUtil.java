package com.example.demo.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.demo.constants.AppConstants;
import com.example.demo.model.KeyInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.kms.v1.CryptoKey;
import com.google.cloud.kms.v1.CryptoKeyVersion;
import com.google.cloud.kms.v1.KeyManagementServiceClient;
import com.google.cloud.kms.v1.KeyManagementServiceClient.ListCryptoKeyVersionsPagedResponse;
import com.google.cloud.kms.v1.KeyManagementServiceClient.ListCryptoKeysPagedResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ChunkIdUtil {

	@Autowired
	private ObjectMapper mapper;

	@Value("${key.aes}")
	private String secretKeyString;

	public List<String> getChunkIds() {

		List<String> keyChunkIds = new ArrayList<>();

		try {
			KeyManagementServiceClient client = KeyManagementServiceClient.create();
			// Iterate over each key ring and print its name.

			ListCryptoKeysPagedResponse keysPagedResponse = client.listCryptoKeys(AppConstants.KEY_RING_DEFAULT);

			for (CryptoKey cryptoKey : keysPagedResponse.iterateAll()) {

				ListCryptoKeyVersionsPagedResponse responseKeys = client.listCryptoKeyVersions(cryptoKey.getName());

				for (CryptoKeyVersion cryptoKeyVersion : responseKeys.iterateAll()) {

					List<String> keyComponentList = Arrays.asList(cryptoKeyVersion.getName().split("/"));

					KeyInfo keyInfo = KeyInfo.builder().keyName(keyComponentList.get(AppConstants.KEY_KEYNAME))
							.version(keyComponentList.get(AppConstants.KEY_KEYVERSION)).build();

					keyChunkIds.add(encrypt(mapper.writeValueAsString(keyInfo)));

				}

			}

		} catch (Exception e) {
			log.error("Exception :[{}]", e.getMessage());
		}

		return keyChunkIds;

	}

	private String encrypt(String keyInfoString) throws InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {

		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, getSecretKeySpec());
		return Base64.getEncoder().encodeToString(cipher.doFinal(keyInfoString.getBytes("UTF-8")));

	}

	// TODO : Update
	private String decrypt(String keyInfoString) throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException {

		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
		cipher.init(Cipher.DECRYPT_MODE, getSecretKeySpec());
		return new String(cipher.doFinal(Base64.getDecoder().decode(keyInfoString)));

	}

	private SecretKeySpec getSecretKeySpec() throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest sha = MessageDigest.getInstance("SHA-1");
		byte[] key = secretKeyString.getBytes("UTF-8");
		key = sha.digest(key);
		key = Arrays.copyOf(key, 16);
		return new SecretKeySpec(key, "AES");
	}

}
