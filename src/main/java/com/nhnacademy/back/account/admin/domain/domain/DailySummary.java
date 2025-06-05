package com.nhnacademy.back.account.admin.domain.domain;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DailySummary {

	private LocalDate date;
	private int orderCount;
	private Long sales;
	private int signupCount;

}