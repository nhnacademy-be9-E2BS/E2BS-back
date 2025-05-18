package com.nhnacademy.back.coupon.membercoupon.batchconfig;

import java.time.LocalDateTime;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.StepSynchronizationManager;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
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

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableBatchProcessing // Spring Batch를 활성화합니다.
@RequiredArgsConstructor
public class MemberCouponBatchConfig {

	private final EntityManagerFactory entityManagerFactory;
	private final PlatformTransactionManager transactionManager;
	private final CouponJpaRepository couponRepo;

	/**
	 * Batch Job 정의
	 * 하나의 Job은 여러 Step으로 구성될 수 있으나,
	 * 여기서는 단일 Step(issueStep)만 실행합니다.
	 */
	@Bean
	public Job issueAllCouponsJob(JobRepository jobRepository) {
		return new JobBuilder("issueAllCouponsJob", jobRepository) // Job 이름 지정
			.start(issueStep(jobRepository)) // 시작 Step 지정
			.build();
	}

	/**
	 * Step 정의
	 * 이 Step은 전체 Member를 조회하여 각각 MemberCoupon을 생성합니다.
	 * - Reader: Member 조회
	 * - Processor: Member → MemberCoupon 변환
	 * - Writer: MemberCoupon 저장
	 */
	@Bean
	public Step issueStep(JobRepository jobRepository) {
		return new StepBuilder("issueStep", jobRepository)
			.<Member, MemberCoupon>chunk(500, transactionManager) // 500개씩 트랜잭션 단위 처리
			.reader(memberReader()) // 데이터 읽기
			.processor(member -> {
				// 현재 실행 중인 Step의 Job 파라미터를 가져오기 위한 설정
				StepExecution stepExecution = StepSynchronizationManager.getContext().getStepExecution();
				JobParameters params = stepExecution.getJobParameters();

				// Job 실행 시 전달된 쿠폰 ID와 만료 기간 파라미터 읽기
				Long couponId = params.getLong("couponId");
				LocalDateTime period = params.getLocalDateTime("memberCouponPeriod");

				// 해당 ID의 쿠폰 엔티티 조회
				Coupon coupon = couponRepo.findById(couponId)
					.orElseThrow(() -> new CouponNotFoundException("쿠폰 없음"));

				// MemberCoupon 생성: (Member, Coupon, 발급일, 만료일)
				MemberCoupon mc = new MemberCoupon(member, coupon, LocalDateTime.now(), period);
				return mc;
			})
			.writer(memberCouponWriter()) // DB에 MemberCoupon 저장
			.build();
	}

	/**
	 * Reader 설정
	 * DB에서 Member 데이터를 페이징 방식으로 읽어옵니다.
	 */
	@Bean
	public JpaPagingItemReader<Member> memberReader() {
		return new JpaPagingItemReaderBuilder<Member>()
			.name("memberReader")
			.entityManagerFactory(entityManagerFactory)
			.queryString("SELECT m FROM Member m WHERE m.memberState.id = 1") // 상태가 ACTIVE(1) 인 회원에게만 조회
			.pageSize(500) // 한 번에 500명씩 처리
			.build();
	}

	/**
	 * Writer 설정
	 * Processor에서 생성한 MemberCoupon 객체를 DB에 저장합니다.
	 */
	@Bean
	public JpaItemWriter<MemberCoupon> memberCouponWriter() {
		return new JpaItemWriterBuilder<MemberCoupon>()
			.entityManagerFactory(entityManagerFactory)
			.build();
	}
}
