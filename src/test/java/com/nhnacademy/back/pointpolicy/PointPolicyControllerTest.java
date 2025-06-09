package com.nhnacademy.back.pointpolicy;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.back.pointpolicy.controller.PointPolicyController;
import com.nhnacademy.back.pointpolicy.domain.dto.request.RequestPointPolicyRegisterDTO;
import com.nhnacademy.back.pointpolicy.domain.dto.request.RequestPointPolicyUpdateDTO;
import com.nhnacademy.back.pointpolicy.domain.dto.response.ResponsePointPolicyDTO;
import com.nhnacademy.back.pointpolicy.service.PointPolicyService;

@WebMvcTest(PointPolicyController.class)
class PointPolicyControllerTest {

	@Autowired
	MockMvc mockMvc;

	@MockitoBean
	PointPolicyService pointPolicyService;

	@Autowired
	ObjectMapper objectMapper;

	@Test
	@DisplayName("포인트 정책 등록 - 201")
	void createPointPolicy() throws Exception {
		RequestPointPolicyRegisterDTO request = new RequestPointPolicyRegisterDTO();
		// 필요한 필드가 있으면 설정

		mockMvc.perform(post("/api/admin/pointPolicies/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isCreated());

		verify(pointPolicyService).createPointPolicy(any(RequestPointPolicyRegisterDTO.class));
	}

	@Test
	@DisplayName("회원가입 포인트 정책 조회 - 200")
	void getRegisterPointPolicies() throws Exception {
		when(pointPolicyService.getRegisterPointPolicies())
			.thenReturn(List.of(new ResponsePointPolicyDTO()));

		mockMvc.perform(get("/api/admin/pointPolicies/registerPolicy"))
			.andExpect(status().isOk());

		verify(pointPolicyService).getRegisterPointPolicies();
	}

	@Test
	@DisplayName("이미지 리뷰 포인트 정책 조회 - 200")
	void getReviewImgPointPolicies() throws Exception {
		when(pointPolicyService.getReviewImgPointPolicies())
			.thenReturn(List.of(new ResponsePointPolicyDTO()));

		mockMvc.perform(get("/api/admin/pointPolicies/reviewImgPolicy"))
			.andExpect(status().isOk());

		verify(pointPolicyService).getReviewImgPointPolicies();
	}

	@Test
	@DisplayName("일반 리뷰 포인트 정책 조회 - 200")
	void getReviewPointPolicies() throws Exception {
		when(pointPolicyService.getReviewPointPolicies())
			.thenReturn(List.of(new ResponsePointPolicyDTO()));

		mockMvc.perform(get("/api/admin/pointPolicies/reviewPolicy"))
			.andExpect(status().isOk());

		verify(pointPolicyService).getReviewPointPolicies();
	}

	@Test
	@DisplayName("기본 적립률 포인트 정책 조회 - 200")
	void getBookPointPolicies() throws Exception {
		when(pointPolicyService.getBookPointPolicies())
			.thenReturn(List.of(new ResponsePointPolicyDTO()));

		mockMvc.perform(get("/api/admin/pointPolicies/bookPolicy"))
			.andExpect(status().isOk());

		verify(pointPolicyService).getBookPointPolicies();
	}

	@Test
	@DisplayName("포인트 정책 활성화 - 204")
	void activatePointPolicy() throws Exception {
		mockMvc.perform(put("/api/admin/pointPolicies/{pointPolicyId}/activate", 1L))
			.andExpect(status().isNoContent());

		verify(pointPolicyService).activatePointPolicy(1L);
	}

	@Test
	@DisplayName("포인트 정책 수정 - 204")
	void updatePointPolicy() throws Exception {
		RequestPointPolicyUpdateDTO request = new RequestPointPolicyUpdateDTO();
		// 필요한 필드 세팅

		mockMvc.perform(put("/api/admin/pointPolicies/{pointPolicyId}", 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isNoContent());

		verify(pointPolicyService).updatePointPolicy(eq(1L), any(RequestPointPolicyUpdateDTO.class));
	}
}
