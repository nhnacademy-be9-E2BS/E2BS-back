package com.nhnacademy.back.order.deliveryfee.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.back.order.deliveryfee.domain.dto.request.RequestDeliveryFeeDTO;
import com.nhnacademy.back.order.deliveryfee.domain.dto.response.ResponseDeliveryFeeDTO;
import com.nhnacademy.back.order.deliveryfee.domain.entity.DeliveryFee;
import com.nhnacademy.back.order.deliveryfee.repository.DeliveryFeeJpaRepository;
import com.nhnacademy.back.order.deliveryfee.service.DeliveryFeeService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class DeliveryFeeServiceImpl implements DeliveryFeeService {

	private final DeliveryFeeJpaRepository deliveryFeeJpaRepository;

	@Override
	public Page<ResponseDeliveryFeeDTO> getDeliveryFees(Pageable pageable) {
		return deliveryFeeJpaRepository.findAllByOrderByDeliveryFeeDateDesc(pageable)
			.map(ResponseDeliveryFeeDTO::fromEntity);
	}

	@Override
	@Transactional
	public void createDeliveryFee(RequestDeliveryFeeDTO request) {
		DeliveryFee deliveryFee = new DeliveryFee(request);
		deliveryFeeJpaRepository.save(deliveryFee);
	}

	@Override
	public ResponseDeliveryFeeDTO getCurrentDeliveryFee() {
		return ResponseDeliveryFeeDTO.fromEntity(deliveryFeeJpaRepository.findTopByOrderByDeliveryFeeDateDesc());
	}
}
