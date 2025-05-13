package com.nhnacademy.back.product.contributor.service.impl;

import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.nhnacademy.back.product.contributor.domain.dto.request.RequestContributorDTO;
import com.nhnacademy.back.product.contributor.domain.dto.response.ResponseContributorDTO;
import com.nhnacademy.back.product.contributor.domain.entity.Contributor;
import com.nhnacademy.back.product.contributor.domain.entity.Position;
import com.nhnacademy.back.product.contributor.exception.ContributorNotFoundException;
import com.nhnacademy.back.product.contributor.exception.PositionNotFoundException;
import com.nhnacademy.back.product.contributor.repository.ContributorJpaRepository;
import com.nhnacademy.back.product.contributor.repository.PositionJpaRepository;
import com.nhnacademy.back.product.contributor.service.ContributorService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContributorServiceImpl implements ContributorService {

	private final ContributorJpaRepository contributorJpaRepository;
	private final PositionJpaRepository positionJpaRepository;

	@Override
	public void createContributor(RequestContributorDTO request) {
		Position position = positionJpaRepository.findById(request.getPositionId())
			.orElseThrow(() -> new PositionNotFoundException("position not found : %s".formatted(request.getPositionId())));

		Contributor contributor = new Contributor(request.getContributorName(), position);
		contributorJpaRepository.save(contributor);
	}

	@Override
	public ResponseContributorDTO getContributor(long contributorId) {
		Contributor contributor = contributorJpaRepository.findById(contributorId)
			.orElseThrow(() -> new ContributorNotFoundException("contributor id not found : %s".formatted(contributorId)));

		return new ResponseContributorDTO(
			contributor.getContributorId(),
			contributor.getContributorName(),
			contributor.getPosition().getPositionId(),
			contributor.getPosition().getPositionName()
		);
	}

	@Override
	public Page<ResponseContributorDTO> getContributors(Pageable pageable) {
		return contributorJpaRepository.findAll(pageable)
			.map(contributor -> new ResponseContributorDTO(
				contributor.getContributorId(),
				contributor.getContributorName(),
				contributor.getPosition().getPositionId(),
				contributor.getPosition().getPositionName()
			));
	}

	@Override
	public ResponseContributorDTO updateContributor(long contributorId, RequestContributorDTO request) {
		Contributor contributor = contributorJpaRepository.findById(contributorId)
			.orElseThrow(() -> new ContributorNotFoundException("contributor id not found : %s".formatted(contributorId)));

		contributor.setContributorName(request.getContributorName());
		contributor.setPosition(positionJpaRepository.findById(request.getPositionId())
			.orElseThrow(() -> new PositionNotFoundException("position not found : %s".formatted(request.getPositionId()))));
		contributorJpaRepository.save(contributor);

		return new ResponseContributorDTO(
			contributor.getContributorId(),
			contributor.getContributorName(),
			contributor.getPosition().getPositionId(),
			contributor.getPosition().getPositionName()
		);
	}
}
