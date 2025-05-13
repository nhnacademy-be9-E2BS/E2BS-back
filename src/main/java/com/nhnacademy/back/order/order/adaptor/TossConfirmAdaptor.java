package com.nhnacademy.back.order.order.adaptor;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.nhnacademy.back.order.order.domain.dto.request.RequestTossConfirmDTO;

@FeignClient(name = "tossPaymentClient", url = "${order.url.toss}")
public interface TossConfirmAdaptor {
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Void> confirmOrder(@RequestBody RequestTossConfirmDTO requestTossConfirmDTO,
		@RequestHeader("Authorization") String authHeader);
}
