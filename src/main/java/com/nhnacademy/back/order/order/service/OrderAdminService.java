package com.nhnacademy.back.order.order.service;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import com.nhnacademy.back.order.order.model.dto.response.ResponseOrderDTO;

public interface OrderAdminService {
	Page<ResponseOrderDTO> getOrders(Pageable pageable, String stateName, LocalDate startDate, LocalDate endDate,
		String orderCode, String memberId);

	ResponseEntity<Void> startDelivery(String orderCode);
}
