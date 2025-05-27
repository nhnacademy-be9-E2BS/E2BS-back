package com.nhnacademy.back.account.admin.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseAdminSettingsDTO {

	private int totalTodayLoginMembersCnt;
	private int totalMembersCnt;
	private long totalOrdersCnt;
	private long totalSales;
	private long totalMonthlySales;
	private long totalDailySales;

}
