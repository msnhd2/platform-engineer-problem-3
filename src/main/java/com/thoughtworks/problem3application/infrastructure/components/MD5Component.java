package com.thoughtworks.problem3application.infrastructure.components;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.stereotype.Component;

@Component
public class MD5Component {
	private static final String MD5_ALGORITHM = "MD5";

	public String encrypt(String input) {
		if (input == null || input.isEmpty()) {
			return null;
		}

		try {
			MessageDigest messageDigest = MessageDigest.getInstance(MD5_ALGORITHM);
			messageDigest.update(input.getBytes());
			byte[] digest = messageDigest.digest();
			BigInteger bigInt = new BigInteger(1, digest);
			String md5Hash = bigInt.toString(16);

			// Add leading zeros to ensure the string has 32 characters
			while (md5Hash.length() < 32) {
				md5Hash = "0" + md5Hash;
			}

			return md5Hash;
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Error while hashing input string", e);
		}
	}
}
