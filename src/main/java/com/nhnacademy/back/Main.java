package com.nhnacademy.back;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;

@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
@EnableCaching
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "10m")
public class Main {

	public static void main(String[] args) {
		SpringApplication.run(Main.class, args);
	}

}
