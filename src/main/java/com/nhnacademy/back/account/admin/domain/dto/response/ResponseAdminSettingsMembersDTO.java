package com.nhnacademy.back.account.admin.domain.dto.response;

import com.nhnacademy.back.account.memberstate.domain.entity.MemberState;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseAdminSettingsMembersDTO {

	private String memberId;
	private String customerName;
	private String customerEmail;
	private String memberRankName;
	private MemberState memberState;
	private String memberRole;

}