package com.nhnacademy.back.coupon.couponpolicy;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.back.coupon.couponpolicy.controller.CouponPolicyController;
import com.nhnacademy.back.coupon.couponpolicy.domain.dto.RequestCouponPolicyDTO;
import com.nhnacademy.back.coupon.couponpolicy.domain.dto.ResponseCouponPolicyDTO;
import com.nhnacademy.back.coupon.couponpolicy.service.CouponPolicyService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(CouponPolicyController.class)
class CouponPolicyControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private CouponPolicyService couponPolicyService;

	@Test
	@DisplayName("관리자 모든 쿠폰 정책 조회 (페이징 처리)")
	void getCouponPolicies_withPaging() throws Exception {
		List<ResponseCouponPolicyDTO> mockList = List.of(
			new ResponseCouponPolicyDTO(1L, 5000L, 10000L, 1000L, 10, LocalDateTime.now(), "정책A"),
			new ResponseCouponPolicyDTO(2L, 3000L, 8000L, 1500L, 15, LocalDateTime.now(), "정책B")
		);

		Page<ResponseCouponPolicyDTO> mockPage = new PageImpl<>(mockList);
		Pageable pageable = PageRequest.of(0, 10);

		when(couponPolicyService.getCouponPolicies(pageable)).thenReturn(mockPage);

		mockMvc.perform(MockMvcRequestBuilders.get("/api/admin/couponPolicies")
				.param("page", "0")
				.param("size", "10"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content.length()").value(2))
			.andExpect(jsonPath("$.content[0].couponPolicyName").value("정책A"));
	}


	@Test
	@DisplayName("관리자 단일 쿠폰 정책 조회")
	void getCouponPolicyById() throws Exception {
		ResponseCouponPolicyDTO dto = new ResponseCouponPolicyDTO(1L, 5000L, 10000L, 1000L, 10, LocalDateTime.now(), "정책A");
		when(couponPolicyService.getCouponPolicyById(1L)).thenReturn(dto);

		mockMvc.perform(MockMvcRequestBuilders.get("/api/admin/couponPolicies/1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.couponPolicyName").value("정책A"));
	}

	@Test
	@DisplayName("관리자 쿠폰 정책 생성")
	void createCouponPolicy() throws Exception {
		RequestCouponPolicyDTO request = new RequestCouponPolicyDTO();
		request.setCouponPolicySalePrice(5000L);
		request.setCouponPolicyMinimum(1000L);
		request.setCouponPolicyMaximumAmount(10000L);
		request.setCouponPolicyDiscountRate(20);
		request.setCouponPolicyCreatedAt(LocalDateTime.now());
		request.setCouponPolicyName("테스트 정책");

		doNothing().when(couponPolicyService).createCouponPolicy(request);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/admin/couponPolicies")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isCreated());
	}
}
