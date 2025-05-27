package com.nhnacademy.back.home.model.dto.response;

import com.nhnacademy.back.account.memberrole.domain.entity.MemberRole;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseHomeMemberNameDTO {

	private String memberId;
	private String memberName;
	private MemberRole memberRole;

}
