package com.nhnacademy.back.product.publisher.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.back.product.publisher.domain.entity.Publisher;

public interface PublisherJpaRepository extends JpaRepository<Publisher, Long> {
	boolean existsByPublisherName(String publisherName);

	String findByPublisherName(String publisherName);

	Publisher findByPublisherId(long publisherId);
}
