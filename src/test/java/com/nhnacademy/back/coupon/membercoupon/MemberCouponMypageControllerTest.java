package com.nhnacademy.back.coupon.membercoupon;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.nhnacademy.back.coupon.membercoupon.contoller.MemberCouponMypageController;
import com.nhnacademy.back.coupon.membercoupon.domain.dto.response.ResponseMypageMemberCouponDTO;
import com.nhnacademy.back.coupon.membercoupon.service.MemberCouponService;

@WebMvcTest(MemberCouponMypageController.class)
class MemberCouponMypageControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private MemberCouponService memberCouponService;

	@Test
	@DisplayName("회원 사용 가능한 쿠폰 개수 조회 성공")
	void testGetCouponCnt() throws Exception {
		String memberId = "member123";

		ResponseMypageMemberCouponDTO responseDTO = new ResponseMypageMemberCouponDTO(memberId, 5);

		when(memberCouponService.getMemberCouponCnt(memberId)).thenReturn(responseDTO);

		mockMvc.perform(get("/api/auth/mypage/{memberId}/coupons/counts", memberId)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.couponCnt").value(5))
			.andExpect(jsonPath("$.memberId").value(memberId));
	}

}
