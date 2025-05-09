package com.nhnacademy.back.product.publisher.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nhnacademy.back.product.publisher.domain.dto.request.RequestPublisherDTO;
import com.nhnacademy.back.product.publisher.domain.dto.response.ResponsePublisherDTO;

public interface PublisherService {
	void createPublisher(RequestPublisherDTO request);

	Page<ResponsePublisherDTO> getPublishers(Pageable pageable);

	void updatePublisher(long publisherId, RequestPublisherDTO request);
}
