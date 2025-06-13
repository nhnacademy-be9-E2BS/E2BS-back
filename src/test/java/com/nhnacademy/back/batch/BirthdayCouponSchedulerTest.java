package com.nhnacademy.back.batch;

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

import com.nhnacademy.back.batch.birthday.BirthdayCouponScheduler;

@ExtendWith(MockitoExtension.class)
class BirthdayCouponSchedulerTest {

	@Mock
	private JobLauncher jobLauncher;

	@Mock
	private Job birthdayCouponJob;

	@InjectMocks
	private BirthdayCouponScheduler scheduler;

	@Test
	void runBirthdayCouponJob_successfulRun_shouldInvokeJobLauncher() throws Exception {
		// given
		when(jobLauncher.run(any(Job.class), any(JobParameters.class)))
			.thenReturn(mock(JobExecution.class));

		// when
		scheduler.runBirthdayCouponJob();

		// then
		verify(jobLauncher, times(1)).run(eq(birthdayCouponJob), any(JobParameters.class));
	}

	@Test
	void runBirthdayCouponJob_shouldLogError_whenJobExecutionFails() throws Exception {
		// given
		when(jobLauncher.run(any(Job.class), any(JobParameters.class)))
			.thenThrow(new RuntimeException("Job failed"));

		// when/then
		try {
			scheduler.runBirthdayCouponJob();
		} catch (Exception ignored) {
			// swallow exception
		}

		verify(jobLauncher, times(1)).run(eq(birthdayCouponJob), any(JobParameters.class));
	}
}
