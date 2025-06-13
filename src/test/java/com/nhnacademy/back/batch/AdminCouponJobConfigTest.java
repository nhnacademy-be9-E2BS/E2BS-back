package com.nhnacademy.back.batch;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.nhnacademy.back.batch.admin.AdminCouponJobConfig;
import com.nhnacademy.back.coupon.coupon.repository.CouponJpaRepository;

@SpringBatchTest
@ActiveProfiles("dev")
@SpringBootTest(classes = { AdminCouponJobConfig.class, com.nhnacademy.back.Main.class })
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AdminCouponJobConfigTest {

	@Autowired
	private JobLauncherTestUtils jobLauncherTestUtils;

	@Autowired
	private Job adminCouponJob;

	@Autowired
	private CouponJpaRepository couponRepo;

	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Test
	void testAdminCouponJobCompletesSuccessfully() throws Exception {
		JobParameters jobParameters = new JobParametersBuilder()
			.addLong("couponId", 1L)
			.addLocalDateTime("memberCouponPeriod", LocalDateTime.now().plusDays(30))
			.addLong("time", System.currentTimeMillis()) // 잡 중복 방지용
			.toJobParameters();

		JobExecution execution = jobLauncherTestUtils.getJobLauncher()
			.run(adminCouponJob, jobParameters);

		assertThat(execution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
	}
}
