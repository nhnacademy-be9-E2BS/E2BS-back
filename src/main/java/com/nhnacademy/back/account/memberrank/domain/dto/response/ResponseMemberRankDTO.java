package com.nhnacademy.back.account.memberrank.domain.dto.response;

import com.nhnacademy.back.account.memberrank.domain.entity.RankName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseMemberRankDTO {

	private RankName rankName;
	private int memberRankTierBonusRate;
	private long memberRankRequireAmount;

}
