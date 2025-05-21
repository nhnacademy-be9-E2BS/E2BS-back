package com.nhnacademy.back.account.pointhistory.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseMemberPointDTO {

	private String memberId;
	private long pointAmount;

}
