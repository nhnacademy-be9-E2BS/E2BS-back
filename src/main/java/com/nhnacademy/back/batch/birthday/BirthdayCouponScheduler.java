package com.nhnacademy.back.batch.birthday;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class BirthdayCouponScheduler {
	private final JobLauncher jobLauncher;
	private final Job birthdayCouponJob;

	@Scheduled(cron = "0 0 0 1 * *") // 실서비스 : 매월 1일 00시 (cron = "0 0 0 1 * *")
	@SchedulerLock(name = "birthdayCouponJob", lockAtMostFor = "10m", lockAtLeastFor = "1m")
	public void runBirthdayCouponJob() throws Exception {
		JobParameters params = new JobParametersBuilder()
			.addLong("time", System.currentTimeMillis()) // 중복 실행 방지
			.toJobParameters();

		try {
			log.info("생일 쿠폰 발급 배치 작업 시작");
			jobLauncher.run(birthdayCouponJob, params);
		} catch (JobExecutionException e) {
			log.error("생일 쿠폰 발급 배치 작업 실패 : {}", e.getMessage());
		}
		log.info("생일 쿠폰 발급 배치 작업 성공");
	}
}

