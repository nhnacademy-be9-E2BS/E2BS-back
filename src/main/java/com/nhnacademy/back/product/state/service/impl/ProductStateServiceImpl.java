package com.nhnacademy.back.product.state.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.back.product.state.domain.entity.ProductState;
import com.nhnacademy.back.product.state.repository.ProductStateJpaRepository;
import com.nhnacademy.back.product.state.service.ProductStateService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductStateServiceImpl implements ProductStateService {

	private final ProductStateJpaRepository productStateJpaRepository;


	@Override
	public List<ProductState> getProductStates() {
		return productStateJpaRepository.findAll();
	}
}
