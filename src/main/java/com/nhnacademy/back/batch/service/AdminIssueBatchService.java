package com.nhnacademy.back.batch.service;

import java.time.LocalDateTime;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminIssueBatchService {

	private final JobLauncher jobLauncher;
	private final Job adminCouponJob;

	public void issueCouponToActiveMembers(Long couponId, LocalDateTime period) {
		JobParameters params = new JobParametersBuilder()
			.addLong("couponId", couponId)
			.addLocalDateTime("memberCouponPeriod", period)
			.addLong("time", System.currentTimeMillis())
			.toJobParameters();

		try {
			log.info("관리자 쿠폰 발급 배치 실행 시작");
			jobLauncher.run(adminCouponJob, params);
		} catch (JobExecutionException e) {
			log.error("관리자 쿠폰 발급 배치 실행 실패 : {}", e.getMessage());
		}
		log.info("관리자 쿠폰 발급 배치 실행 성공");
	}
}
