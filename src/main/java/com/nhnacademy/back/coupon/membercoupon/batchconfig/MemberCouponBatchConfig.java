package com.nhnacademy.back.coupon.membercoupon.batchconfig;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.persistence.EntityManagerFactory;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.StepSynchronizationManager;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.nhnacademy.back.account.member.domain.entity.Member;
import com.nhnacademy.back.coupon.coupon.domain.entity.Coupon;
import com.nhnacademy.back.coupon.coupon.exception.CouponNotFoundException;
import com.nhnacademy.back.coupon.coupon.repository.CouponJpaRepository;
import com.nhnacademy.back.coupon.membercoupon.domain.entity.MemberCoupon;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class MemberCouponBatchConfig {

	private final EntityManagerFactory entityManagerFactory;
	private final PlatformTransactionManager transactionManager;
	private final CouponJpaRepository couponRepo;

	@Bean
	public Job issueAllCouponsJob(JobRepository jobRepository) {
		return new JobBuilder("issueAllCouponsJob", jobRepository)
			.start(issueStep(jobRepository))
			.build();
	}

	@Bean
	public Step issueStep(JobRepository jobRepository) {
		return new StepBuilder("issueStep", jobRepository)
			.<Member, MemberCoupon>chunk(500, transactionManager)
			.reader(memberReader())
			.processor(member -> {
				StepExecution stepExecution = StepSynchronizationManager.getContext().getStepExecution();
				JobParameters params = stepExecution.getJobParameters();

				Long couponId = params.getLong("couponId");
				LocalDateTime period = params.getLocalDateTime("memberCouponPeriod");

				Coupon coupon = couponRepo.findById(couponId)
					.orElseThrow(() -> new CouponNotFoundException("쿠폰 없음"));

				MemberCoupon mc = new MemberCoupon(member, coupon, LocalDateTime.now(), period);
				return mc;
			})
			.writer(memberCouponWriter())
			.build();
	}

	@Bean
	public JpaPagingItemReader<Member> memberReader() {
		return new JpaPagingItemReaderBuilder<Member>()
			.name("memberReader")
			.entityManagerFactory(entityManagerFactory)
			.queryString("SELECT m FROM Member m")
			.pageSize(500)
			.build();
	}

	@Bean
	public JpaItemWriter<MemberCoupon> memberCouponWriter() {
		return new JpaItemWriterBuilder<MemberCoupon>()
			.entityManagerFactory(entityManagerFactory)
			.build();
	}
}
