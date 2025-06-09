package com.nhnacademy.back.coupon.membercoupon;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.nhnacademy.back.coupon.membercoupon.contoller.MemberCouponOrderController;
import com.nhnacademy.back.coupon.membercoupon.domain.dto.response.ResponseOrderCouponDTO;
import com.nhnacademy.back.coupon.membercoupon.service.MemberCouponService;

@WebMvcTest(MemberCouponOrderController.class)
class MemberCouponOrderControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private MemberCouponService memberCouponService;

	@Test
	@DisplayName("주문서 적용 가능 쿠폰 조회 성공")
	void testGetCouponsInOrder() throws Exception {
		String memberId = "member123";
		List<Long> productIds = List.of(100L, 200L);

		LocalDateTime now = LocalDateTime.now();

		List<ResponseOrderCouponDTO> coupons = List.of(
			new ResponseOrderCouponDTO(
				1L,
				"할인쿠폰A",
				1000L,
				10000L,
				500L,
				10,
				"정기 할인",
				now.minusDays(5),
				now.plusDays(5),
				10L,
				"카테고리A",
				100L,
				"상품A"
			),
			new ResponseOrderCouponDTO(
				2L,
				"할인쿠폰B",
				2000L,
				20000L,
				1000L,
				20,
				"특별 할인",
				now.minusDays(10),
				now.plusDays(10),
				20L,
				"카테고리B",
				200L,
				"상품B"
			)
		);

		when(memberCouponService.getCouponsInOrderByMemberIdAndProductIds(eq(memberId), eq(productIds)))
			.thenReturn(coupons);

		mockMvc.perform(get("/api/order/{memberId}/coupons", memberId)
				.param("request", "100", "200")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(coupons.size()))
			.andExpect(jsonPath("$[0].memberCouponId").value(1L))
			.andExpect(jsonPath("$[0].couponName").value("할인쿠폰A"))
			.andExpect(jsonPath("$[0].couponPolicyMinimum").value(1000))
			.andExpect(jsonPath("$[0].couponPolicyMaximumAmount").value(10000))
			.andExpect(jsonPath("$[0].couponPolicySalePrice").value(500))
			.andExpect(jsonPath("$[0].couponPolicyDiscountRate").value(10))
			.andExpect(jsonPath("$[0].couponPolicyName").value("정기 할인"))
			.andExpect(jsonPath("$[0].categoryId").value(10))
			.andExpect(jsonPath("$[0].categoryName").value("카테고리A"))
			.andExpect(jsonPath("$[0].productId").value(100))
			.andExpect(jsonPath("$[0].productTitle").value("상품A"))

			.andExpect(jsonPath("$[1].memberCouponId").value(2L))
			.andExpect(jsonPath("$[1].couponName").value("할인쿠폰B"))
			.andExpect(jsonPath("$[1].couponPolicyMinimum").value(2000))
			.andExpect(jsonPath("$[1].couponPolicyMaximumAmount").value(20000))
			.andExpect(jsonPath("$[1].couponPolicySalePrice").value(1000))
			.andExpect(jsonPath("$[1].couponPolicyDiscountRate").value(20))
			.andExpect(jsonPath("$[1].couponPolicyName").value("특별 할인"))
			.andExpect(jsonPath("$[1].categoryId").value(20))
			.andExpect(jsonPath("$[1].categoryName").value("카테고리B"))
			.andExpect(jsonPath("$[1].productId").value(200))
			.andExpect(jsonPath("$[1].productTitle").value("상품B"));
	}
}
