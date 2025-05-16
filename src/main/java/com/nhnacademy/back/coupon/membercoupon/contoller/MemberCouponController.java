package com.nhnacademy.back.coupon.membercoupon.contoller;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.back.coupon.membercoupon.domain.dto.RequestAllMemberCouponDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/member-coupons")
public class MemberCouponController {

	private final JobLauncher jobLauncher;
	private final Job issueAllCouponsJob;

	@PostMapping("/issue")
	public ResponseEntity<Void> issueCouponsToAllMembers(@RequestBody RequestAllMemberCouponDTO request) {
		try {
			JobParameters params = new JobParametersBuilder()
				.addLong("couponId", request.getCouponId())
				.addLocalDateTime("memberCouponPeriod", request.getMemberCouponPeriod())
				.addLong("timestamp", System.currentTimeMillis()) // 중복 실행 방지
				.toJobParameters();

			jobLauncher.run(issueAllCouponsJob, params);
			return ResponseEntity.status(HttpStatus.OK).build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

}
