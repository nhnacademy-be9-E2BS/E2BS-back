package com.nhnacademy.back.account.memberrank.service;

import java.util.List;

import com.nhnacademy.back.account.memberrank.domain.dto.response.ResponseMemberRankDTO;

public interface MemberRankService {

	List<ResponseMemberRankDTO> getMemberRanks();

}
