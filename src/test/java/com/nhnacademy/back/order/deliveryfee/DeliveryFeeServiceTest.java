package com.nhnacademy.back.order.deliveryfee;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.nhnacademy.back.order.deliveryfee.domain.dto.request.RequestDeliveryFeeDTO;
import com.nhnacademy.back.order.deliveryfee.domain.dto.response.ResponseDeliveryFeeDTO;
import com.nhnacademy.back.order.deliveryfee.domain.entity.DeliveryFee;
import com.nhnacademy.back.order.deliveryfee.repository.DeliveryFeeJpaRepository;
import com.nhnacademy.back.order.deliveryfee.service.impl.DeliveryFeeServiceImpl;

@ExtendWith(MockitoExtension.class)
class DeliveryFeeServiceTest {
	@Mock
	private DeliveryFeeJpaRepository deliveryFeeJpaRepository;

	@InjectMocks
	private DeliveryFeeServiceImpl deliveryFeeService;

	@Test
	@DisplayName("배송비 정책 페이지 조회 - 최신순")
	void testGetDeliveryFees() {
		DeliveryFee fee = new DeliveryFee(new RequestDeliveryFeeDTO(3000, 50000));
		Page<DeliveryFee> page = new PageImpl<>(List.of(fee));
		Pageable pageable = PageRequest.of(0, 10);

		when(deliveryFeeJpaRepository.findAllByOrderByDeliveryFeeDateDesc(pageable)).thenReturn(page);

		Page<ResponseDeliveryFeeDTO> result = deliveryFeeService.getDeliveryFees(pageable);

		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent().get(0).getDeliveryFeeAmount()).isEqualTo(3000);
	}

	@Test
	@DisplayName("배송비 정책 생성")
	void testCreateDeliveryFee() {
		RequestDeliveryFeeDTO request = new RequestDeliveryFeeDTO(2500, 40000);

		deliveryFeeService.createDeliveryFee(request);

		verify(deliveryFeeJpaRepository, times(1)).save(any(DeliveryFee.class));
	}

	@Test
	@DisplayName("가장 최근 배송비 정책 조회")
	void testGetCurrentDeliveryFee() {
		DeliveryFee latest = new DeliveryFee(new RequestDeliveryFeeDTO(2000, 45000));
		when(deliveryFeeJpaRepository.findTopByOrderByDeliveryFeeDateDesc()).thenReturn(latest);

		ResponseDeliveryFeeDTO result = deliveryFeeService.getCurrentDeliveryFee();

		assertThat(result.getDeliveryFeeFreeAmount()).isEqualTo(45000);
		assertThat(result.getDeliveryFeeAmount()).isEqualTo(2000);
	}
}
