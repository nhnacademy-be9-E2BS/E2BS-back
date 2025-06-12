package com.nhnacademy.back.jwt.parser;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

class JwtMemberIdParserTest {

	private final ObjectMapper objectMapper = new ObjectMapper();

	private String createJwt(String memberId) throws Exception {
		String header = Base64.getUrlEncoder().encodeToString("{\"alg\":\"HS256\",\"typ\":\"JWT\"}".getBytes());

		String payloadJson = objectMapper.writeValueAsString(Map.of("MemberId", memberId));
		String payload = Base64.getUrlEncoder()
			.withoutPadding()
			.encodeToString(payloadJson.getBytes(StandardCharsets.UTF_8));

		String signature = "signature";

		return header + "." + payload + "." + signature;
	}

	@Test
	@DisplayName("JwtMemberIdParser 클래스 getMemberId 메서드 테스트")
	void JwtMemberIdParserGetMemberIdMethodTest() throws Exception {

		// Given

		// When
		String token = createJwt("user");

		// Then
		String result = JwtMemberIdParser.getMemberId(token);

		Assertions.assertThat(result).isEqualTo("user");

	}

	@Test
	@DisplayName("JwtMemberIdParser 클래스 getMemberId 메서드 Null 테스트")
	void JwtMemberIdParserGetMemberIdNullMethodTest() {

		// Given
		String invalidToken = "hello";

		// When
		String result = JwtMemberIdParser.getMemberId(invalidToken);

		// Then
		Assertions.assertThat(result).isNull();

	}

	@Test
	@DisplayName("JwtMemberIdParser 클래스 getMemberId 메서드 Exception 테스트")
	void JwtMemberIdParserGetMemberIdMethodExceptionTest() throws Exception {

		// Given
		String header = Base64.getUrlEncoder().encodeToString("{\"alg\":\"HS256\",\"typ\":\"JWT\"}".getBytes());

		// When
		String payloadJson = objectMapper.writeValueAsString(Map.of("username", "other"));
		String payload = Base64.getUrlEncoder().withoutPadding().encodeToString(payloadJson.getBytes());
		String token = header + "." + payload + ".sig";

		String result = JwtMemberIdParser.getMemberId(token);

		// Then
		Assertions.assertThat(result).isNull();
	}

}