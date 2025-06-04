package com.nhnacademy.back.batch.birthday;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.nhnacademy.back.account.member.domain.entity.Member;
import com.nhnacademy.back.common.config.RabbitConfig;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class BirthdayCouponJobConfig {

	private final EntityManagerFactory entityManagerFactory;
	private final PlatformTransactionManager transactionManager;
	private final RabbitTemplate rabbitTemplate;

	/**
	 * 생일 쿠폰 발급을 위한 Spring Batch Job 설정
	 * - 매월 1일 실행되어 생일이 속한 달의 회원들에게 쿠폰 발급 메시지 전송
	 * - 단일 Step(sendBirthdayCouponStep)을 실행함
	 */
	@Bean
	public Job birthdayCouponJob(JobRepository jobRepository) {
		return new JobBuilder("birthdayCouponJob", jobRepository)
			.start(sendBirthdayCouponStep(jobRepository))
			.build();
	}

	/**
	 * 생일 쿠폰 발급 Step 설정
	 * - Reader: 현재 달에 생일이 있는 회원을 조회
	 * - Writer: 해당 회원의 customerId를 MQ로 전송
	 */
	@Bean
	public Step sendBirthdayCouponStep(JobRepository jobRepository) {
		return new StepBuilder("sendBirthdayCouponStep", jobRepository)
			.<Member, Member>chunk(100, transactionManager)
			.reader(birthdayMemberReader())
			.writer(birthdayMemberWriter())
			.build();
	}

	/**
	 * 현재 월에 생일이 있는 회원을 조회하는 Reader 설정
	 */
	@Bean
	public JpaPagingItemReader<Member> birthdayMemberReader() {
		return new JpaPagingItemReaderBuilder<Member>()
			.name("birthdayMemberReader")
			.entityManagerFactory(entityManagerFactory)
			.queryString(
				"SELECT m FROM Member m WHERE FUNCTION('MONTH', m.memberBirth) = FUNCTION('MONTH', CURRENT_DATE)")
			.pageSize(100)
			.build();
	}

	/**
	 * MQ 메시지 발행 Writer 설정
	 * - 각 회원의 customerId를 MQ에 전송
	 * - 전달되는 메시지는 exchange + Key 를 사용해 바인딩된 큐로 전송
	 */
	@Bean
	public ItemWriter<Member> birthdayMemberWriter() {
		return items -> {
			for (Member member : items) {
				rabbitTemplate.convertAndSend(
					RabbitConfig.BIRTHDAY_EXCHANGE,
					RabbitConfig.BIRTHDAY_ROUTING_KEY,
					member.getCustomerId());
			}
		};
	}

}
