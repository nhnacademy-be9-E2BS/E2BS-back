package com.nhnacademy.back.product.contributor.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.back.product.contributor.domain.dto.request.RequestPositionDTO;
import com.nhnacademy.back.product.contributor.domain.dto.response.ResponsePositionDTO;
import com.nhnacademy.back.product.contributor.domain.entity.Position;
import com.nhnacademy.back.product.contributor.exception.PositionAlreadyExistsException;
import com.nhnacademy.back.product.contributor.exception.PositionNotFoundException;
import com.nhnacademy.back.product.contributor.repository.PositionJpaRepository;
import com.nhnacademy.back.product.contributor.service.PositionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PositionServiceImpl implements PositionService {

	private final PositionJpaRepository positionJpaRepository;

	@Override
	@Transactional
	public ResponsePositionDTO createPosition(RequestPositionDTO request) {
		String positionName = request.getPositionName();
		if (positionJpaRepository.existsByPositionName(request.getPositionName())) {
			throw new PositionAlreadyExistsException("PositionName already exists : %s".formatted(request.getPositionName()));
		}
		Position position = new Position(positionName);
		Position saved = positionJpaRepository.save(position);
		return new ResponsePositionDTO(saved.getPositionId(), saved.getPositionName());
	}

	@Override
	public ResponsePositionDTO getPosition(Long positionId) {
		Position position = positionJpaRepository.findById(positionId).orElseThrow(
			() -> new PositionNotFoundException("Position not found : %s".formatted(positionId)));

		return new ResponsePositionDTO(position.getPositionId(), position.getPositionName());
	}


	@Override
	public Page<ResponsePositionDTO> getPositions(Pageable pageable) {
		return positionJpaRepository.findAll(pageable)
			.map(position -> new ResponsePositionDTO(position.getPositionId(), position.getPositionName()));
	}



	@Override
	@Transactional
	public ResponsePositionDTO updatePosition(Long positionId, RequestPositionDTO request) {
		Position position = positionJpaRepository.findById(positionId).orElseThrow(
			() -> new PositionNotFoundException("Position Not Found : %s".formatted(positionId)));
		position.setPositionName(request.getPositionName());
		positionJpaRepository.save(position);
		return new ResponsePositionDTO(position.getPositionId(), position.getPositionName());
	}


}
