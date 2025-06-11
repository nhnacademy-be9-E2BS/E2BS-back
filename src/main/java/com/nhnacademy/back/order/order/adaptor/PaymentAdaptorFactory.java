package com.nhnacademy.back.order.order.adaptor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class PaymentAdaptorFactory {
	private final Map<String, PaymentAdaptor> adapterMap = new HashMap<>();

	public PaymentAdaptorFactory(List<PaymentAdaptor> adapters) {
		for (PaymentAdaptor adapter : adapters) {
			adapterMap.put(adapter.getName(), adapter);
		}
	}

	public PaymentAdaptor getAdapter(String provider) {
		if (!adapterMap.containsKey(provider)) {
			throw new IllegalArgumentException("지원하지 않는 결제사 : " + provider);
		}
		return adapterMap.get(provider);
	}
}
