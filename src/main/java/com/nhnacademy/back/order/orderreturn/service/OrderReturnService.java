package com.nhnacademy.back.order.orderreturn.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import com.nhnacademy.back.order.order.model.dto.response.ResponseOrderReturnDTO;

public interface OrderReturnService {

	ResponseEntity<Page<ResponseOrderReturnDTO>> getOrderReturnsByMemberId(String memberId, Pageable pageable);

	ResponseEntity<ResponseOrderReturnDTO> getOrderReturnByOrderCode(String orderCode);

	ResponseEntity<Page<ResponseOrderReturnDTO>> getOrderReturnsAll(Pageable pageable);
}
