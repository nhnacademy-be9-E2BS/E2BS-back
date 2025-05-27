package com.nhnacademy.back.account.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.nhnacademy.back.account.member.service.impl.MemberServiceImpl;

@SpringBootTest
@AutoConfigureMockMvc
class MemberRegisterControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private MemberServiceImpl memberService;

	// @Test
	// @DisplayName("RequestRegisterMemberDTO ValidationFailedException 테스트")
	// void requestRegisterMemberDTOValidationFailedExceptionTest() throws Exception {
	//
	// 	// Given
	// 	String requestJson = """
	// 		{
	// 		    "memberId": "nhn1",
	// 		    "customerName": "NHN",
	// 		    "customerPassword": null,
	// 		    "customerPasswordCheck": null,
	// 		    "customerEmail": "nhn@gmail.com",
	// 		    "memberBirth": "2000-01-01",
	// 		    "memberPhone": "01012345678"
	// 		}
	// 		""";
	//
	// 	// When
	//
	// 	// Then
	// 	mockMvc.perform(post("/api/register")
	// 			.contentType(MediaType.APPLICATION_JSON)
	// 			.content(requestJson))
	// 		.andExpect(status().isBadRequest());
	//
	// }
}