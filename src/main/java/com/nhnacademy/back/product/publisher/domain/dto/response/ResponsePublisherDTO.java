package com.nhnacademy.back.product.publisher.domain.dto.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ResponsePublisherDTO {
	@NotNull
	private long publisherId;
	@NotNull
	private String publisherName;
}
