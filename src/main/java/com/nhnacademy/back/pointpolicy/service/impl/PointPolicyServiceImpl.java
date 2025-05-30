package com.nhnacademy.back.pointpolicy.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.back.pointpolicy.domain.dto.request.RequestPointPolicyRegisterDTO;
import com.nhnacademy.back.pointpolicy.domain.dto.request.RequestPointPolicyUpdateDTO;
import com.nhnacademy.back.pointpolicy.domain.dto.response.ResponsePointPolicyDTO;
import com.nhnacademy.back.pointpolicy.domain.entity.PointPolicy;
import com.nhnacademy.back.pointpolicy.domain.entity.PointPolicyType;
import com.nhnacademy.back.pointpolicy.exception.PointPolicyAlreadyExistsException;
import com.nhnacademy.back.pointpolicy.exception.PointPolicyNotFoundException;
import com.nhnacademy.back.pointpolicy.repository.PointPolicyJpaRepository;
import com.nhnacademy.back.pointpolicy.service.PointPolicyService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PointPolicyServiceImpl implements PointPolicyService {

	private final PointPolicyJpaRepository pointPolicyJpaRepository;

	@Override
	@Transactional
	public void createPointPolicy(RequestPointPolicyRegisterDTO request) {
		String pointPolicyName = request.getPointPolicyName();
		if(pointPolicyJpaRepository.existsByPointPolicyName(pointPolicyName)) {
			throw new PointPolicyAlreadyExistsException("Point Policy Already Exists");
		}

		PointPolicy pointPolicy = new PointPolicy(
			request.getPointPolicyType(),
			request.getPointPolicyName(),
			request.getPointPolicyFigure(),
			LocalDateTime.now()
		);

		PointPolicyType type = request.getPointPolicyType();

		List<PointPolicy> sameTypePolicies = pointPolicyJpaRepository.findByPointPolicyTypeOrderByPointPolicyIsActiveDescPointPolicyCreatedAtDesc(type);
		for (PointPolicy policy : sameTypePolicies) {
			policy.updateIsActive(false);
		}

		pointPolicyJpaRepository.save(pointPolicy);
	}

	@Override
	public List<ResponsePointPolicyDTO> getRegisterPointPolicies() {
		return pointPolicyJpaRepository.findByPointPolicyTypeOrderByPointPolicyIsActiveDescPointPolicyCreatedAtDesc(PointPolicyType.REGISTER).stream()
			.map(pointPolicy -> new ResponsePointPolicyDTO(
				pointPolicy.getPointPolicyId(),
				pointPolicy.getPointPolicyType(),
				pointPolicy.getPointPolicyName(),
				pointPolicy.getPointPolicyFigure(),
				pointPolicy.getPointPolicyCreatedAt(),
				pointPolicy.getPointPolicyIsActive()
			))
			.toList();
	}

	@Override
	public List<ResponsePointPolicyDTO> getReviewImgPointPolicies() {
		return pointPolicyJpaRepository.findByPointPolicyTypeOrderByPointPolicyIsActiveDescPointPolicyCreatedAtDesc(PointPolicyType.REVIEW_IMG).stream()
			.map(pointPolicy -> new ResponsePointPolicyDTO(
				pointPolicy.getPointPolicyId(),
				pointPolicy.getPointPolicyType(),
				pointPolicy.getPointPolicyName(),
				pointPolicy.getPointPolicyFigure(),
				pointPolicy.getPointPolicyCreatedAt(),
				pointPolicy.getPointPolicyIsActive()
			))
			.toList();
	}

	@Override
	public List<ResponsePointPolicyDTO> getReviewPointPolicies() {
		return pointPolicyJpaRepository.findByPointPolicyTypeOrderByPointPolicyIsActiveDescPointPolicyCreatedAtDesc(PointPolicyType.REVIEW).stream()
			.map(pointPolicy -> new ResponsePointPolicyDTO(
				pointPolicy.getPointPolicyId(),
				pointPolicy.getPointPolicyType(),
				pointPolicy.getPointPolicyName(),
				pointPolicy.getPointPolicyFigure(),
				pointPolicy.getPointPolicyCreatedAt(),
				pointPolicy.getPointPolicyIsActive()
			))
			.toList();
	}

	@Override
	public List<ResponsePointPolicyDTO> getBookPointPolicies() {
		return pointPolicyJpaRepository.findByPointPolicyTypeOrderByPointPolicyIsActiveDescPointPolicyCreatedAtDesc(PointPolicyType.BOOK).stream()
			.map(pointPolicy -> new ResponsePointPolicyDTO(
				pointPolicy.getPointPolicyId(),
				pointPolicy.getPointPolicyType(),
				pointPolicy.getPointPolicyName(),
				pointPolicy.getPointPolicyFigure(),
				pointPolicy.getPointPolicyCreatedAt(),
				pointPolicy.getPointPolicyIsActive()
			))
			.toList();
	}

	@Override
	@Transactional
	public void activatePointPolicy(Long pointPolicyId) {
		PointPolicy pointPolicy = pointPolicyJpaRepository.findById(pointPolicyId)
			.orElseThrow(() -> new PointPolicyNotFoundException("Point Policy Not Found"));

		PointPolicyType type = pointPolicy.getPointPolicyType();

		List<PointPolicy> sameTypePolicies = pointPolicyJpaRepository.findByPointPolicyTypeOrderByPointPolicyIsActiveDescPointPolicyCreatedAtDesc(type);
		for (PointPolicy policy : sameTypePolicies) {
			if (policy.getPointPolicyId() != pointPolicyId) {
				policy.updateIsActive(false);
			}
		}

		pointPolicy.updateIsActive(true);
		// (영속성 컨텍스트에 의해 flush), 하지만 명시적이면 안전함
		pointPolicyJpaRepository.save(pointPolicy);
	}

	@Override
	@Transactional
	public void updatePointPolicy(Long pointPolicyId, RequestPointPolicyUpdateDTO request) {
		PointPolicy pointPolicy = pointPolicyJpaRepository.findById(pointPolicyId)
			.orElseThrow(() -> new PointPolicyNotFoundException("Point Policy Not Found"));

		pointPolicy.updateFigure(request.getPointPolicyFigure());
		pointPolicyJpaRepository.save(pointPolicy);
	}
}
