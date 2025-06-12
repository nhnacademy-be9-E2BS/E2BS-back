package com.nhnacademy.back.account.member.domain.dto.response;

import java.time.LocalDate;

import com.nhnacademy.back.account.customer.domain.entity.Customer;
import com.nhnacademy.back.account.memberrank.domain.entity.MemberRank;
import com.nhnacademy.back.account.memberrole.domain.entity.MemberRole;
import com.nhnacademy.back.account.memberstate.domain.entity.MemberState;
import com.nhnacademy.back.account.socialauth.domain.entity.SocialAuth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseMemberInfoDTO {

	private Customer customer;
	private String memberId;
	private LocalDate memberBirth;
	private String memberPhone;
	private LocalDate memberCreatedAt;
	private LocalDate memberLoginLatest;
	private MemberRank memberRank;
	private MemberState memberState;
	private MemberRole memberRole;
	private SocialAuth socialAuth;

}