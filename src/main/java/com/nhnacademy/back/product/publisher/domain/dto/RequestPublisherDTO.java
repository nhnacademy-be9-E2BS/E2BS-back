package com.nhnacademy.back.product.publisher.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RequestPublisherDTO {
	private long publisherId;
	private String publisherName;
}
