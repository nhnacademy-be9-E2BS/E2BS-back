package com.nhnacademy.back.pointpolicy.service;

import java.util.List;

import com.nhnacademy.back.pointpolicy.domain.dto.request.RequestPointPolicyRegisterDTO;
import com.nhnacademy.back.pointpolicy.domain.dto.request.RequestPointPolicyUpdateDTO;
import com.nhnacademy.back.pointpolicy.domain.dto.response.ResponsePointPolicyDTO;

public interface PointPolicyService {

	void createPointPolicy(RequestPointPolicyRegisterDTO request);

	List<ResponsePointPolicyDTO> getRegisterPointPolicies();

	List<ResponsePointPolicyDTO> getReviewImgPointPolicies();

	List<ResponsePointPolicyDTO> getReviewPointPolicies();

	List<ResponsePointPolicyDTO> getBookPointPolicies();

	void activatePointPolicy(Long pointPolicyId);

	void updatePointPolicy(Long pointPolicyId, RequestPointPolicyUpdateDTO request);
}
