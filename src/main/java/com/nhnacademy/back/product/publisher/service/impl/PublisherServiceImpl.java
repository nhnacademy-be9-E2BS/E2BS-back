package com.nhnacademy.back.product.publisher.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.nhnacademy.back.product.publisher.domain.dto.request.RequestPublisherDTO;
import com.nhnacademy.back.product.publisher.domain.entity.Publisher;
import com.nhnacademy.back.product.publisher.exception.PublisherNotFoundException;
import com.nhnacademy.back.product.publisher.repository.PublisherJpaRepository;
import com.nhnacademy.back.product.publisher.service.PublisherService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PublisherServiceImpl implements PublisherService {
	private final PublisherJpaRepository publisherJpaRepository;

	/**
	 * Publisher을 DB에 저장하는 로직
	 */
	@Override
	public void createPublisher(RequestPublisherDTO request) {
		Publisher publisher = new Publisher(request.getPublisherName());
		publisherJpaRepository.save(publisher);
	}

	/**
	 * DB에 저장 되어 있는 모든 Publisher을 조회하여 List로 return 하는 로직
	 */
	@Override
	public List<Publisher> getPublishers() {
		return publisherJpaRepository.findAll();
	}

	/**
	 * DB에 저장 되어 있는 Publisher의 값을 수정하는 로직
	 * 수정 가능한 값 : publisher_name
	 * publisher_id가 없는 경우에는 Exception 발생
	 */
	@Override
	public void updatePublisher(long publisherId, RequestPublisherDTO request) {
		if (Objects.isNull(request)) {
			throw new IllegalArgumentException();
		}
		Optional<Publisher> publisher = publisherJpaRepository.findById(publisherId);
		if (publisher.isEmpty()) {
			throw new PublisherNotFoundException();
		}

		publisher.get().setPublisher(request.getPublisherName());
		publisherJpaRepository.save(publisher.get());
	}
}
