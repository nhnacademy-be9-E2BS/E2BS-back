package com.nhnacademy.back.coupon.membercoupon;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.back.batch.service.AdminIssueBatchService;
import com.nhnacademy.back.coupon.membercoupon.contoller.MemberCouponController;
import com.nhnacademy.back.coupon.membercoupon.domain.dto.request.RequestAllMemberCouponDTO;
import com.nhnacademy.back.coupon.membercoupon.domain.dto.response.ResponseMemberCouponDTO;
import com.nhnacademy.back.coupon.membercoupon.service.MemberCouponService;

@WebMvcTest(MemberCouponController.class)
class MemberCouponControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private MemberCouponService memberCouponService;

	@MockitoBean
	private AdminIssueBatchService adminIssueBatchService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@DisplayName("관리자가 전체 회원에게 쿠폰 발급 요청")
	void testIssueCouponsToAllMembers() throws Exception {
		LocalDateTime period = LocalDateTime.now();
		RequestAllMemberCouponDTO request = new RequestAllMemberCouponDTO(1L, period);

		mockMvc.perform(post("/api/admin/memberCoupons/issue")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk());

		Mockito.verify(adminIssueBatchService).issueCouponToActiveMembers(1L, period);
	}

	@Test
	@DisplayName("회원 전체 쿠폰 조회")
	void testGetMemberCouponsByMemberId() throws Exception {
		String memberId = "user";
		Pageable pageable = PageRequest.of(0, 10);

		ResponseMemberCouponDTO dto = new ResponseMemberCouponDTO(
			1L,
			"할인쿠폰",
			"정책A",
			10L,
			"카테고리A",
			100L,
			"상품제목A",
			LocalDateTime.now().minusDays(2),
			LocalDateTime.now().plusDays(5),
			true
		);

		Page<ResponseMemberCouponDTO> page = new PageImpl<>(List.of(dto), pageable, 1);

		Mockito.when(memberCouponService.getMemberCouponsByMemberId(eq(memberId), any(Pageable.class)))
			.thenReturn(page);

		mockMvc.perform(get("/api/auth/mypage/{memberId}/coupons", memberId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content[0].couponName").value("할인쿠폰"));
	}

	@Test
	@DisplayName("회원 사용 가능한 쿠폰 조회")
	void testGetUsableMemberCoupons() throws Exception {
		String memberId = "user";
		Pageable pageable = PageRequest.of(0, 10);

		ResponseMemberCouponDTO dto = new ResponseMemberCouponDTO(
			2L,
			"사용가능쿠폰",
			"정책B",
			20L,
			"카테고리B",
			200L,
			"상품제목B",
			LocalDateTime.now().minusDays(1),
			LocalDateTime.now().plusDays(3),
			false
		);

		Page<ResponseMemberCouponDTO> page = new PageImpl<>(List.of(dto), pageable, 1);

		Mockito.when(memberCouponService.getUsableMemberCouponsByMemberId(eq(memberId), any(Pageable.class)))
			.thenReturn(page);

		mockMvc.perform(get("/api/auth/mypage/{memberId}/couponsUsable", memberId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content[0].couponName").value("사용가능쿠폰"));
	}

	@Test
	@DisplayName("회원 사용 불가능한 쿠폰 조회")
	void testGetUnusableMemberCoupons() throws Exception {
		String memberId = "user";
		Pageable pageable = PageRequest.of(0, 10);

		ResponseMemberCouponDTO dto = new ResponseMemberCouponDTO(
			3L,
			"만료된쿠폰",
			"정책C",
			30L,
			"카테고리C",
			300L,
			"상품제목C",
			LocalDateTime.now().minusDays(10),
			LocalDateTime.now().minusDays(1),
			true
		);

		Page<ResponseMemberCouponDTO> page = new PageImpl<>(List.of(dto), pageable, 1);

		Mockito.when(memberCouponService.getUnusableMemberCouponsByMemberId(eq(memberId), any(Pageable.class)))
			.thenReturn(page);

		mockMvc.perform(get("/api/auth/mypage/{memberId}/couponsUnusable", memberId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content[0].couponName").value("만료된쿠폰"));
	}
}
