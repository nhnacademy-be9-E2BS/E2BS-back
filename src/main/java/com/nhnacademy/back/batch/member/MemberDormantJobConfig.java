package com.nhnacademy.back.batch.member;

import java.time.LocalDate;
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
import com.nhnacademy.back.account.memberrole.domain.entity.MemberRoleName;
import com.nhnacademy.back.account.memberrole.repository.MemberRoleJpaRepository;
import com.nhnacademy.back.account.memberstate.domain.entity.MemberState;
import com.nhnacademy.back.account.memberstate.domain.entity.MemberStateName;
import com.nhnacademy.back.account.memberstate.repository.MemberStateJpaRepository;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class MemberDormantJobConfig {

	private final EntityManagerFactory entityManagerFactory;
	private final PlatformTransactionManager transactionManager;
	private final MemberStateJpaRepository stateRepository;
	private final MemberRoleJpaRepository roleRepository;

	@Bean
	public Job dormantMemberJob(JobRepository jobRepository) {
		return new JobBuilder("dormantMemberJob", jobRepository)
			.start(dormantMemberStep(jobRepository))
			.build();
	}

	@Bean
	public Step dormantMemberStep(JobRepository jobRepository) {
		return new StepBuilder("dormantMemberStep", jobRepository)
			.<Member, Member>chunk(100, transactionManager)
			.reader(dormantMemberReader())
			.processor(dormantMemberProcessor())
			.writer(dormantMemberWriter())
			.build();
	}

	@Bean
	public JpaPagingItemReader<Member> dormantMemberReader() {
		// 마지막 로그인 (3개월 전), 상태(활성), 역할(회원) 인 member -> 휴면으로 전환
		LocalDate threeMonthsAgo = LocalDate.now().minusMonths(3);
		Long memberStateId = stateRepository.findMemberStateByMemberStateName(MemberStateName.ACTIVE).getMemberStateId();
		Long memberRoleId = roleRepository.findMemberRoleByMemberRoleName(MemberRoleName.MEMBER).getMemberRoleId();

		return new JpaPagingItemReaderBuilder<Member>()
			.name("dormantMemberReader")
			.entityManagerFactory(entityManagerFactory)
			.parameterValues(Map.of(
				"threshold", threeMonthsAgo,
				"memberStateId", memberStateId,
				"memberRoleId", memberRoleId
			))
			.queryString("SELECT m FROM Member m WHERE m.memberState.id = :memberStateId AND m.memberRole.id = :memberRoleId AND m.memberLoginLatest < :threshold")
			.pageSize(100)
			.build();
	}

	@Bean
	public ItemProcessor<Member, Member> dormantMemberProcessor() {
		return member -> {
			MemberState dormant = stateRepository.findMemberStateByMemberStateName(MemberStateName.DORMANT);
			member.updateStateToDormant(dormant);
			return member;
		};
	}

	@Bean
	public JpaItemWriter<Member> dormantMemberWriter() {
		return new JpaItemWriterBuilder<Member>()
			.entityManagerFactory(entityManagerFactory)
			.build();
	}
}
