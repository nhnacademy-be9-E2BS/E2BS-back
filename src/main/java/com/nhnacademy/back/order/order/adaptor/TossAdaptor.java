package com.nhnacademy.back.order.order.adaptor;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.nhnacademy.back.order.order.model.dto.request.RequestCancelDTO;
import com.nhnacademy.back.order.order.model.dto.request.RequestTossConfirmDTO;
import com.nhnacademy.back.order.order.model.dto.response.ResponseTossPaymentConfirmDTO;

@FeignClient(name = "tossPaymentClient", url = "${order.url.toss}")
public interface TossAdaptor {
	@PostMapping(path = "/confirm", consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<ResponseTossPaymentConfirmDTO> confirmOrder(@RequestBody RequestTossConfirmDTO requestTossConfirmDTO,
		@RequestHeader("Authorization") String authHeader);

	@PostMapping("/{paymentKey}/cancel")
	ResponseEntity<ResponseTossPaymentConfirmDTO> cancelOrder(@PathVariable String paymentKey,
		@RequestBody RequestCancelDTO requestCancelDTO,
		@RequestHeader("Authorization") String authHeader);
}
