package com.nhnacademy.back.coupon.coupon;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.back.coupon.coupon.controller.CouponController;
import com.nhnacademy.back.coupon.coupon.domain.dto.request.RequestCouponDTO;
import com.nhnacademy.back.coupon.coupon.domain.dto.response.ResponseCouponDTO;
import com.nhnacademy.back.coupon.coupon.service.CouponService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CouponController.class)
class CouponControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private CouponService couponService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@DisplayName("쿠폰 생성 성공")
	void createCoupon_success() throws Exception {
		RequestCouponDTO request = new RequestCouponDTO();
		request.setCouponPolicyId(1L);
		request.setCouponName("테스트 쿠폰");

		doNothing().when(couponService).createCoupon(any());

		mockMvc.perform(post("/api/admin/coupons")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isCreated());
	}

	@Test
	@DisplayName("쿠폰 전체 조회")
	void getCoupons_success() throws Exception {
		Pageable pageable = PageRequest.of(0, 10);
		ResponseCouponDTO dto = new ResponseCouponDTO(1L, 1L, "테스트 쿠폰", null, null, null, null, null, true);
		Page<ResponseCouponDTO> page = new PageImpl<>(Collections.singletonList(dto), pageable, 1);

		when(couponService.getCoupons(any(Pageable.class))).thenReturn(page);

		mockMvc.perform(get("/api/admin/coupons"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content[0].couponId").value(1));
	}

	@Test
	@DisplayName("쿠폰 단건 조회")
	void getCoupon_success() throws Exception {
		ResponseCouponDTO dto = new ResponseCouponDTO(1L, 1L, "테스트 정책", "테스트 쿠폰", null, null, null, null, true);

		when(couponService.getCoupon(1L)).thenReturn(dto);

		mockMvc.perform(get("/api/admin/coupons/1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.couponName").value("테스트 쿠폰"));
	}

	@Test
	@DisplayName("쿠폰 생성 요청 실패 - 유효성 검증 실패")
	void createCoupon_validationFail() throws Exception {
		RequestCouponDTO request = new RequestCouponDTO();

		mockMvc.perform(post("/api/admin/coupons")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("쿠폰 활성화 여부 수정 성공")
	void updateCoupon_success() throws Exception {
		doNothing().when(couponService).updateCouponIsActive(1L);

		mockMvc.perform(put("/api/admin/coupons/1"))
			.andExpect(status().isNoContent());
	}

}
