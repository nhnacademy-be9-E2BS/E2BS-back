package com.nhnacademy.back.batch;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;

import com.nhnacademy.back.batch.member.MemberRankScheduler;

@ExtendWith(MockitoExtension.class)
class MemberRankSchedulerTest {

	@Mock
	private JobLauncher jobLauncher;

	@Mock
	private Job rankMemberJob;

	@InjectMocks
	private MemberRankScheduler memberRankScheduler;

	@Test
	void runInactiveMemberJob_success() throws Exception {
		// Given
		when(jobLauncher.run(eq(rankMemberJob), any(JobParameters.class)))
			.thenReturn(mock(JobExecution.class));

		// When
		memberRankScheduler.runInactiveMemberJob();

		// Then
		verify(jobLauncher, times(1)).run(eq(rankMemberJob), any(JobParameters.class));
	}

	@Test
	void runInactiveMemberJob_jobExecutionException() throws Exception {
		// Given
		when(jobLauncher.run(eq(rankMemberJob), any(JobParameters.class)))
			.thenThrow(new RuntimeException("실패 테스트"));

		// When & Then
		assertDoesNotThrow(() -> memberRankScheduler.runInactiveMemberJob());
		verify(jobLauncher, times(1)).run(eq(rankMemberJob), any(JobParameters.class));
	}


}
