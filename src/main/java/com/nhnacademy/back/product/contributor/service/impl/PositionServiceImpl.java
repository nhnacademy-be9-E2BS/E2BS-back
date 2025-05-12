package com.nhnacademy.back.product.contributor.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.nhnacademy.back.product.contributor.domain.dto.request.RequestPositionDTO;
import com.nhnacademy.back.product.contributor.domain.dto.response.ResponsePositionDTO;
import com.nhnacademy.back.product.contributor.domain.entity.Position;
import com.nhnacademy.back.product.contributor.exception.PositionAlreadyExistsException;
import com.nhnacademy.back.product.contributor.exception.PositionNotFoundException;
import com.nhnacademy.back.product.contributor.repository.PositionJpaRepository;
import com.nhnacademy.back.product.contributor.service.PositionService;
import com.nhnacademy.back.product.publisher.domain.dto.request.RequestPublisherDTO;
import com.nhnacademy.back.product.publisher.exception.PublisherNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PositionServiceImpl implements PositionService {

	private final PositionJpaRepository positionJpaRepository;

	@Override
	public void createPosition(RequestPositionDTO request) {
		String positionName = request.getPositionName();
		if (positionJpaRepository.existsByPositionName(request.getPositionName())) {
			throw new PositionAlreadyExistsException("PositionName already exists : %s".formatted(request.getPositionName()));
		}
		Position position = new Position(positionName);
		positionJpaRepository.save(position);
	}

	@Override
	public ResponsePositionDTO getPosition(Long positionId) {
		Position position = positionJpaRepository.findById(positionId).orElseThrow(
			() -> new PositionNotFoundException("Position not found : %s".formatted(positionId)));

		return new ResponsePositionDTO(position.getPositionName());
	}


	@Override
	public Page<ResponsePositionDTO> getPositions(Pageable pageable) {
		return positionJpaRepository.findAll(pageable)
			.map(position -> new ResponsePositionDTO(position.getPositionName()));
	}

	@Override
	public ResponsePositionDTO updatePosition(Long positionId, RequestPositionDTO request) {
		Position position = positionJpaRepository.findById(positionId).orElseThrow(
			() -> new PositionNotFoundException("Position Not Found : %s".formatted(positionId)));
		position.setPositionName(request.getPositionName());
		positionJpaRepository.save(position);
		return new ResponsePositionDTO(position.getPositionName());
	}


}
