package com.example.demo.constants;

public final class AppConstants {

	public static final String ENCRYPTION_MODE_RSA = "RSA";
	public static final String CHAR_FORWARDSLASH = "/";
	public static final String CHAR_ASTERISK = "\\*";
	
	public static final String CIPHER_TRANSFORMATION = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";
	
	public static final String KEY_BEGIN = "-----BEGIN";
	public static final String KEY_END = "-----END";
	
	public static final String MDNAME_SHA = "SHA-256";
	public static final String MFGNAME_MGF1 = "MGF1";
	
	
	public static final Integer KEY_RINGNAME = 5;
	public static final Integer KEY_KEYNAME = 7;
	public static final Integer KEY_KEYVERSION = 9;
	
	public static final String KEY_RING_DEFAULT = "projects/gothic-scheme-334505/locations/us-central1/keyRings/rsa_key_ring";

}
