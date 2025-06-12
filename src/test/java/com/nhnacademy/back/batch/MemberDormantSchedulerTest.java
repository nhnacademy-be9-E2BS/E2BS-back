package com.nhnacademy.back.batch;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.eq;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;

import com.nhnacademy.back.batch.member.MemberDormantScheduler;

@ExtendWith(MockitoExtension.class)
public class MemberDormantSchedulerTest {

	@Mock
	private JobLauncher jobLauncher;

	@Mock
	private Job dormantMemberJob;

	@InjectMocks
	private MemberDormantScheduler scheduler;

	@Test
	void runInactiveMemberJob_success_shouldInvokeJobLauncher() throws Exception {
		// Given
		when(jobLauncher.run(eq(dormantMemberJob), any(JobParameters.class)))
			.thenReturn(mock(JobExecution.class));

		// When
		scheduler.runInactiveMemberJob();

		// Then
		verify(jobLauncher, times(1)).run(eq(dormantMemberJob), any(JobParameters.class));
	}

	@Test
	void runInactiveMemberJob_failure_shouldCatchException() throws Exception {
		// Given
		when(jobLauncher.run(eq(dormantMemberJob), any(JobParameters.class)))
			.thenThrow(new RuntimeException("Batch failed"));

		// When
		try {
			scheduler.runInactiveMemberJob();
		} catch (Exception ignored) {
		}

		// Then
		verify(jobLauncher, times(1)).run(eq(dormantMemberJob), any(JobParameters.class));
	}
}
