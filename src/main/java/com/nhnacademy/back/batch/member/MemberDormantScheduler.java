package com.nhnacademy.back.batch.member;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class MemberDormantScheduler {

	private final JobLauncher jobLauncher;
	private final Job dormantMemberJob;

	@Scheduled(cron = "0 0 4 * * *") // 매일 새벽 4시(cron = "0 0 4 * * *")
	public void runInactiveMemberJob() throws Exception {
		JobParameters jobParameters = new JobParametersBuilder()
			.addLong("time", System.currentTimeMillis()) // 매 실행마다 유니크 파라미터 필요
			.toJobParameters();

		try {
			log.info("3개월 이상 미접속 회원 휴면 전환 배치 작업 시작");
			jobLauncher.run(dormantMemberJob, jobParameters);
		} catch (JobExecutionException e) {
			log.error("3개월 이상 미접속 회원 휴면 전환 배치 작업 실패 : {}", e.getMessage());
		}
		log.info("3개월 이상 미접속 회원 휴면 전환 배치 작업 성공");
	}
}
