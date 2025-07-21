package com.nhnacademy.back.product.like.domain.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseLikeDTO {

	private long likeId;
	private long customerId;
	private long productId;
	private LocalDateTime likeCreatedAt;

}

