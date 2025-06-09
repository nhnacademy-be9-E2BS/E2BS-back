package com.nhnacademy.back.batch.member;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.nhnacademy.back.account.member.domain.entity.Member;
import com.nhnacademy.back.account.memberrank.domain.entity.MemberRank;
import com.nhnacademy.back.account.memberrank.domain.entity.RankName;
import com.nhnacademy.back.account.memberrank.repository.MemberRankJpaRepository;
import com.nhnacademy.back.account.memberrole.domain.entity.MemberRoleName;
import com.nhnacademy.back.account.memberrole.repository.MemberRoleJpaRepository;
import com.nhnacademy.back.order.order.repository.OrderJpaRepository;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class MemberRankJobConfig {

	private final EntityManagerFactory entityManagerFactory;
	private final PlatformTransactionManager transactionManager;
	private final MemberRoleJpaRepository roleRepository;
	private final OrderJpaRepository orderRepository;
	private final MemberRankJpaRepository rankRepository;

	@Bean
	public Job rankMemberJob(JobRepository jobRepository) {
		return new JobBuilder("rankMemberJob", jobRepository)
			.start(rankMemberStep(jobRepository))
			.build();
	}

	@Bean
	public Step rankMemberStep(JobRepository jobRepository) {
		return new StepBuilder("rankMemberStep", jobRepository)
			.<Member, Member>chunk(100, transactionManager)
			.reader(rankMemberReader())
			.processor(rankMemberProcessor())
			.writer(rankMemberWriter())
			.build();
	}

	@Bean
	public JpaPagingItemReader<Member> rankMemberReader() {
		// 역할(회원) 인 member
		Long memberRoleId = roleRepository.findMemberRoleByMemberRoleName(MemberRoleName.MEMBER).getMemberRoleId();

		return new JpaPagingItemReaderBuilder<Member>()
			.name("rankMemberReader")
			.entityManagerFactory(entityManagerFactory)
			.parameterValues(Map.of(
				"memberRoleId", memberRoleId
			))
			.queryString("SELECT m FROM Member m WHERE m.memberRole.id = :memberRoleId")
			.pageSize(100)
			.build();
	}

	@Bean
	public ItemProcessor<Member, Member> rankMemberProcessor() {
		return member -> {
			LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(3);

			// 최근 3개월 COMPLETE 주문 순수금액 합계
			Long totalAmount = orderRepository.sumOrderPureAmount(member.getCustomerId(), threeMonthsAgo);
			if (totalAmount == null) {
				totalAmount = 0L;
			}

			RankName newRankName;

			if (totalAmount < 100_000) {
				newRankName = RankName.NORMAL;
			} else if (totalAmount < 200_000) {
				newRankName = RankName.ROYAL;
			} else if (totalAmount < 300_000) {
				newRankName = RankName.GOLD;
			} else {
				newRankName = RankName.PLATINUM;
			}

			// 랭크가 다른 경우에만 업데이트, 같으면 writer 로 넘기지 않음 (스킵)
			if(!member.getMemberRank().getMemberRankName().equals(newRankName)) {
				MemberRank newRank = rankRepository.getMemberRankByMemberRankName(newRankName);
				member.updateMemberRank(newRank);
				return member;
			}
			return null;
		};
	}

	@Bean
	public JpaItemWriter<Member> rankMemberWriter() {
		return new JpaItemWriterBuilder<Member>()
			.entityManagerFactory(entityManagerFactory)
			.build();
	}
}
