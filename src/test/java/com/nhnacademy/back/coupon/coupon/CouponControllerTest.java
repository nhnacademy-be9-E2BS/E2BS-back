package com.nhnacademy.back.coupon.coupon;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.nhnacademy.back.coupon.coupon.controller.CouponController;
import com.nhnacademy.back.coupon.coupon.service.CouponService;

@WebMvcTest(controllers = CouponController.class)
public class CouponControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private CouponService couponService;


}
