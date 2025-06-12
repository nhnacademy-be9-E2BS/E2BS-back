package com.nhnacademy.back.order.order;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.nhnacademy.back.order.order.adaptor.PaymentAdaptor;
import com.nhnacademy.back.order.order.adaptor.PaymentAdaptorFactory;

class PaymentAdaptorFactoryTest {
	PaymentAdaptor tossAdaptor;
	PaymentAdaptor kakaopayAdaptor;
	PaymentAdaptorFactory factory;

	@BeforeEach
	void setUp() {
		tossAdaptor = mock(PaymentAdaptor.class);
		kakaopayAdaptor = mock(PaymentAdaptor.class);

		when(tossAdaptor.getName()).thenReturn("TOSS");
		when(kakaopayAdaptor.getName()).thenReturn("KAKAOPAY");

		factory = new PaymentAdaptorFactory(List.of(tossAdaptor, kakaopayAdaptor));
	}

	@Test
	@DisplayName("결제 어댑터 정상 반환 확인")
	void testGetAdapter_returnsCorrectAdaptor() {
		PaymentAdaptor result = factory.getAdapter("TOSS");

		assertEquals(tossAdaptor, result);
	}

	@Test
	@DisplayName("지원하지 않는 어댑터에 대한 요청")
	void testGetAdapter_withUnsupportedProvider_throwsException() {
		assertThatThrownBy(() -> factory.getAdapter("PAYCO"))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("지원하지 않는 결제사");
	}
}
