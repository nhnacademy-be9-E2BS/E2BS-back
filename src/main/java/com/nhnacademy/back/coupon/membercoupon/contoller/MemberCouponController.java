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

	// JobLauncher는 Spring Batch의 Job 실행을 담당하는 컴포넌트입니다.
	private final JobLauncher jobLauncher;

	// 실행할 Job (즉, 회원 전체에게 쿠폰을 발급하는 작업)을 주입받습니다.
	private final Job issueAllCouponsJob;

	/**
	 * 모든 회원에게 쿠폰을 발급하는 배치 Job을 실행하는 엔드포인트
	 * POST /api/admin/member-coupons/issue
	 */
	@PostMapping("/issue")
	public ResponseEntity<Void> issueCouponsToAllMembers(@RequestBody RequestAllMemberCouponDTO request) {
		try {
			// Job 파라미터 생성: 실행 시 필수로 전달되어야 하는 데이터들입니다.
			JobParameters params = new JobParametersBuilder()
				.addLong("couponId", request.getCouponId()) // 어떤 쿠폰을 발급할 것인지
				.addLocalDateTime("memberCouponPeriod", request.getMemberCouponPeriod()) // 쿠폰 만료일
				.addLong("timestamp", System.currentTimeMillis()) // 동일 Job의 중복 실행을 방지
				.toJobParameters();

			// Job 실행 - issueAllCouponsJob을 위에서 만든 파라미터와 함께 실행
			jobLauncher.run(issueAllCouponsJob, params);

			// 정상적으로 실행되면 200 OK 반환
			return ResponseEntity.status(HttpStatus.OK).build();
		} catch (Exception e) {
			// 예외가 발생하면 500 INTERNAL SERVER ERROR 반환
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

}
