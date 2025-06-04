package com.nhnacademy.back.account.admin.domain.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonthlySummary {

	private int orderCount;
	private Long sales;
	private int signupCount;

}