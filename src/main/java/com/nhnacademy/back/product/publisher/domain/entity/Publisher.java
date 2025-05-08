package com.nhnacademy.back.product.publisher.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Publisher {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long publisherId;

	@Column(length = 50, nullable = false)
	private String publisherName;

	public Publisher(String publisherName) {
		this.publisherName = publisherName;
	}

	public void setPublisher(String publisherName) {
		this.publisherName = publisherName;
	}
}
