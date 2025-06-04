package com.nhnacademy.back.account.admin.domain.dto.response;

import com.nhnacademy.back.account.admin.domain.domain.MonthlySummary;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseAdminSettingsMonthlySummaryDTO {

	private MonthlySummary monthlySummary;

}
