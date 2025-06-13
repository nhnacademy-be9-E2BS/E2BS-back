package com.nhnacademy.back.batch;

import static org.mockito.Mockito.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;

import com.nhnacademy.back.batch.service.AdminIssueBatchService;

@ExtendWith(MockitoExtension.class)
class AdminIssueBatchServiceTest {

	@Mock
	private JobLauncher jobLauncher;

	@Mock
	private Job adminCouponJob;

	@InjectMocks
	private AdminIssueBatchService adminIssueBatchService;

	@Test
	void issueCouponToActiveMembers_success() throws Exception {
		// Given
		Long couponId = 123L;
		LocalDateTime period = LocalDateTime.now().plusDays(7);

		when(jobLauncher.run(any(Job.class), any(JobParameters.class)))
			.thenReturn(mock(JobExecution.class));

		// When
		adminIssueBatchService.issueCouponToActiveMembers(couponId, period);

		// Then
		verify(jobLauncher, times(1)).run(eq(adminCouponJob), any(JobParameters.class));
	}
}
