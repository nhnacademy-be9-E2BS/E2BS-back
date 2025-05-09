package com.nhnacademy.back.product.publisher.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ResponsePublisherDTO {
	private long publisherId;
	private String publisherName;
}
