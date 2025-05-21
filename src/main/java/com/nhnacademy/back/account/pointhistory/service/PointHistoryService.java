package com.nhnacademy.back.account.pointhistory.service;

import com.nhnacademy.back.account.pointhistory.domain.dto.response.ResponseMemberPointDTO;

public interface PointHistoryService {

	ResponseMemberPointDTO getMemberPoints(String memberId);

}
