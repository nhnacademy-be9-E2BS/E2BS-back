package com.nhnacademy.back.order.order.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nhnacademy.back.order.order.domain.dto.response.ResponseOrderDTO;

public interface OrderAdminService {
	Page<ResponseOrderDTO> getOrders(Pageable pageable);
}
