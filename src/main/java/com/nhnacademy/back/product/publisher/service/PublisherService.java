package com.nhnacademy.back.product.publisher.service;

import java.util.List;

import com.nhnacademy.back.product.publisher.domain.dto.request.RequestPublisherDTO;
import com.nhnacademy.back.product.publisher.domain.entity.Publisher;

public interface PublisherService {
	void createPublisher(RequestPublisherDTO request);

	List<Publisher> getPublishers();

	void updatePublisher(long publisherId, RequestPublisherDTO request);
}
