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
public class BatchService {

	private final JobLauncher jobLauncher;
	private final Job adminCouponJob;

	public void issueCouponToActiveMembers(Long couponId, LocalDateTime period) {
		JobParameters params = new JobParametersBuilder()
			.addLong("couponId", couponId)
			.addLocalDateTime("memberCouponPeriod", period)
			.addLong("time", System.currentTimeMillis())
			.toJobParameters();

		try {
			jobLauncher.run(adminCouponJob, params);
			log.info("관리자 쿠폰 배치 실행 성공");
		} catch (JobExecutionException e) {
			log.error("관리자 쿠폰 배치 실행 실패 : {}", e.getMessage());
		}
	}
}
