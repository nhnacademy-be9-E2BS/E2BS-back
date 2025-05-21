package com.nhnacademy.back.batch.birthday;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BirthdayCouponScheduler {
	private final JobLauncher jobLauncher;
	private final Job birthdayCouponJob;

	@Scheduled(cron = "0 0 * * * *") // 테스트용: 매분 -> 실서비스 : 매월 1일 00시 (cron = "0 0 0 1 * *")
	public void runBirthdayCouponJob() throws Exception {
		JobParameters params = new JobParametersBuilder()
			.addLong("time", System.currentTimeMillis()) // 중복 실행 방지
			.toJobParameters();
		jobLauncher.run(birthdayCouponJob, params);
	}
}

