package com.nhnacademy.back.coupon.membercoupon.contoller;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.back.common.annotation.Admin;
import com.nhnacademy.back.common.annotation.Member;
import com.nhnacademy.back.coupon.membercoupon.domain.dto.request.RequestAllMemberCouponDTO;
import com.nhnacademy.back.coupon.membercoupon.domain.dto.response.ResponseMemberCouponDTO;
import com.nhnacademy.back.coupon.membercoupon.service.MemberCouponService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MemberCouponController {

	private final MemberCouponService memberCouponService;

	// JobLauncher는 Spring Batch의 Job 실행을 담당하는 컴포넌트입니다.
	private final JobLauncher jobLauncher;

	// 실행할 Job (즉, 회원 전체에게 쿠폰을 발급하는 작업)을 주입받습니다.
	private final Job issueAllCouponsJob;

	/**
	 * 모든 회원에게 쿠폰을 발급하는 배치 Job을 실행하는 엔드포인트
	 * POST /api/admin/member-coupons/issue
	 */
	@Admin
	@PostMapping("/api/admin/member-coupons/issue")
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

	/**
	 * 쿠폰함 : 회원 ID로 회원쿠폰 테이블에서 쿠폰 조회
	 */
	@Member
	@GetMapping("/api/member-coupons")
	public ResponseEntity<Page<ResponseMemberCouponDTO>> getMemberCouponsByMemberId(@RequestParam Long memberId, Pageable pageable) {
		Page<ResponseMemberCouponDTO> response = memberCouponService.getMemberCouponsByMemberId(memberId, pageable);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/**
	 * !기능확인용 API -> orderController 의 서비스 구현으로 옭기면 삭제 예정
	 * 사용자 쿠폰 사용 시 사용여부 업데이트 (미사용 -> 사용완료)
	 */
	@PutMapping("api/member-coupons")
	public ResponseEntity<Void> updateMemberCouponById(@RequestParam Long memberCouponId) {
		memberCouponService.updateMemberCouponById(memberCouponId);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	/**
	 * !기능확인용 API -> orderController 의 서비스 구현으로 옮기면 삭제 예정
	 * 주문취소 시 단일 회원에게 같은 내용의 쿠폰 재발급
	 */
	@PostMapping("/api/admin/member-coupons/re-issue")
	public ResponseEntity<Void> reIssueCoupon(@RequestParam Long memberCouponId) {
		memberCouponService.reIssueCouponById(memberCouponId);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
}
