package com.example.demo;


import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


@SpringBootTest
class DemoApplicationTests {
//68e109f0f40ca72a15e05cc22786f8e6
	@Test
	void contextLoads() {
		String input = "HelloWorld";
		String hashed = hashMD5(input);
		System.out.println("MD5 Hash của '" + input + "': " + hashed);
	}
	public static String hashMD5(String input) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(input.getBytes());
			byte[] digest = md.digest();

			// Chuyển kết quả thành dạng hex
			StringBuilder hexString = new StringBuilder();
			for (byte b : digest) {
				hexString.append(String.format("%02x", b));
			}
			return hexString.toString();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Lỗi thuật toán MD5", e);

		}

    }
}
