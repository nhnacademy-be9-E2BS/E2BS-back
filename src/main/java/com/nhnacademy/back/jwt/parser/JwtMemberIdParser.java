package com.nhnacademy.back.jwt.parser;

import java.util.Base64;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JwtMemberIdParser {
	private JwtMemberIdParser() {
	}

	private static final ObjectMapper objectMapper = new ObjectMapper();

	public static String getMemberId(String accessToken) {

		try {
			String[] parts = accessToken.split("\\.");
			if (parts.length != 3) {
				return null;
			}

			String payload = parts[1];
			byte[] decodeBytes = Base64.getUrlDecoder().decode(payload);
			String payloadJson = new String(decodeBytes);

			Map<String, String> claims = objectMapper.readValue(payloadJson, Map.class);

			return claims.get("MemberId");

		} catch (Exception e) {
			return null;
		}

	}

}

