package com.nhnacademy.back.account.admin.domain.dto.response;

import java.util.List;

import com.nhnacademy.back.account.admin.domain.domain.DailySummary;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseAdminSettingsDailySummaryDTO {

	private List<DailySummary> dailySummaries;

}
