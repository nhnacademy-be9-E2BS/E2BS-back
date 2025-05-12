package com.nhnacademy.back.account.member.domain.dto.response;

import com.nhnacademy.back.account.memberrole.domain.entity.MemberRoleName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseLoginMemberDTO {

	private String memberId;
	private String customerPassword;
	private MemberRoleName memberRoleName;

}
