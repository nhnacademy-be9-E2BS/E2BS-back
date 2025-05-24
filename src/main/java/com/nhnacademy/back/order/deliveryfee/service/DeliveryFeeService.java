package com.nhnacademy.back.order.deliveryfee.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nhnacademy.back.order.deliveryfee.domain.dto.request.RequestDeliveryFeeDTO;
import com.nhnacademy.back.order.deliveryfee.domain.dto.response.ResponseDeliveryFeeDTO;

public interface DeliveryFeeService {
	Page<ResponseDeliveryFeeDTO> getDeliveryFees(Pageable pageable);
	void createDeliveryFee(RequestDeliveryFeeDTO request);
	ResponseDeliveryFeeDTO getCurrentDeliveryFee();
}
