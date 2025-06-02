package com.nhnacademy.back.batch.admin;

import java.time.LocalDateTime;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.StepSynchronizationManager;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.nhnacademy.back.account.member.domain.entity.Member;
import com.nhnacademy.back.common.config.RabbitConfig;
import com.nhnacademy.back.coupon.coupon.domain.entity.Coupon;
import com.nhnacademy.back.coupon.coupon.exception.CouponNotFoundException;
import com.nhnacademy.back.coupon.coupon.repository.CouponJpaRepository;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableBatchProcessing // Batch 활성화
@RequiredArgsConstructor
public class AdminCouponJobConfig {

	private final EntityManagerFactory entityManagerFactory;
	private final PlatformTransactionManager transactionManager;
	private final CouponJpaRepository couponRepo;
	private final RabbitTemplate rabbitTemplate;

	/**
	 * Batch Job 정의
	 * 하나의 Job은 여러 Step으로 구성될 수 있으나,
	 * 여기서는 단일 Step(issueStep)만 실행합니다.
	 */
	@Bean
	public Job adminCouponJob(JobRepository jobRepository) {
		return new JobBuilder("adminCouponJob", jobRepository)
			.start(issueStep(jobRepository))
			.build();
	}

	@Bean
	public Step issueStep(JobRepository jobRepository) {
		return new StepBuilder("issueStep", jobRepository)
			.<Member, AdminIssueMessage>chunk(100, transactionManager)
			.reader(memberReader()) // 데이터 읽기
			.processor(member -> {
				StepExecution stepExecution = StepSynchronizationManager.getContext().getStepExecution();
				JobParameters params = stepExecution.getJobParameters();

				Long couponId = params.getLong("couponId");
				LocalDateTime period = params.getLocalDateTime("memberCouponPeriod");

				Coupon coupon = couponRepo.findById(couponId)
					.orElseThrow(() -> new CouponNotFoundException("쿠폰 없음"));

				return new AdminIssueMessage(member.getCustomerId(), coupon.getCouponId(), period);
			})
			.writer(memberCouponWriter())
			.build();
	}

	@Bean
	public JpaPagingItemReader<Member> memberReader() {
		return new JpaPagingItemReaderBuilder<Member>()
			.name("memberReader")
			.entityManagerFactory(entityManagerFactory)
			.queryString("SELECT m FROM Member m WHERE m.memberState.id = 1")
			.pageSize(100)
			.build();
	}

	@Bean
	public ItemWriter<AdminIssueMessage> memberCouponWriter() {
		return items -> {
			for (AdminIssueMessage message : items) {
				rabbitTemplate.convertAndSend(
					RabbitConfig.DIRECT_EXCHANGE,
					RabbitConfig.DIRECT_ROUTING_KEY,
					message);
			}
		};
	}
}
