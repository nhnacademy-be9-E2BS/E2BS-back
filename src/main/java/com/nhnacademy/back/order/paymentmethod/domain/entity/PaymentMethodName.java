package com.nhnacademy.back.order.paymentmethod.domain.entity;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum PaymentMethodName {
	TOSS("TOSS"),
	OTHER("OTHER");

	private String provider;

	PaymentMethodName(String provider) {
		this.provider = provider;
	}

	public String getProvider() {
		return provider;
	}

	private static Map<String, PaymentMethodName> paymentMethodNameMap = Arrays.stream(PaymentMethodName.values())
		.collect(Collectors.toMap(PaymentMethodName::getProvider, Function.identity()));

	public static PaymentMethodName fromProvider(String provider) {
		PaymentMethodName paymentMethodName = OTHER;
		if(paymentMethodNameMap.get(provider.toUpperCase()) != null) {
			paymentMethodName = paymentMethodNameMap.get(provider);
		}
		return paymentMethodName;
	}

}
