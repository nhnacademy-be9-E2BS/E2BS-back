package com.nhnacademy.back.coupon.couponpolicy;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.nhnacademy.back.coupon.couponpolicy.domain.entity.CouponPolicy;
import com.nhnacademy.back.coupon.couponpolicy.repository.CouponPolicyJpaRepository;

@DataJpaTest
@ActiveProfiles("test")
public class CouponPolicyJpaRepositoryTest {

	@Autowired
	private CouponPolicyJpaRepository couponPolicyJpaRepository;

	@Test
	@DisplayName("")
	void existsByCouponPolicyName() {
		// given
		couponPolicyJpaRepository.save(new CouponPolicy(
			5000L,
			500L,
			2000L,
			20,
			LocalDateTime.now(),
			"Test Coupon Policy"
		));

		// when & then
		assertTrue(couponPolicyJpaRepository.existsByCouponPolicyName("Test Coupon Policy"));
	}

}
