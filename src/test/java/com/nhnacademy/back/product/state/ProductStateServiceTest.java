package com.nhnacademy.back.product.state;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.nhnacademy.back.product.state.domain.entity.ProductState;
import com.nhnacademy.back.product.state.domain.entity.ProductStateName;
import com.nhnacademy.back.product.state.repository.ProductStateJpaRepository;
import com.nhnacademy.back.product.state.service.impl.ProductStateServiceImpl;

@ExtendWith(MockitoExtension.class)
class ProductStateServiceTest {

	@Mock
	ProductStateJpaRepository productStateJpaRepository;

	@InjectMocks
	ProductStateServiceImpl productStateService;

	@Test
	@DisplayName("전체 ProductState 조회 성공")
	void getProductStates_success() {
		// given
		ProductState state = new ProductState(1L, ProductStateName.SALE);
		given(productStateJpaRepository.findAll()).willReturn(List.of(state));

		// when
		List<ProductState> result = productStateService.getProductStates();

		// then
		assertThat(result).hasSize(1);
		assertThat(result.getFirst().getProductStateName()).isEqualTo(ProductStateName.SALE);
	}

	@Test
	@DisplayName("전체 ProductState 조회 - 데이터 없음")
	void getProductStates_empty() {
		// given
		given(productStateJpaRepository.findAll()).willReturn(Collections.emptyList());

		// when
		List<ProductState> result = productStateService.getProductStates();

		// then
		assertThat(result).isEmpty();
	}
}
