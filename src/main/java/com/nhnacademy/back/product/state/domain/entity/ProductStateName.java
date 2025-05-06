package com.nhnacademy.back.product.state.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum ProductStateName {
	SALE, OUT, DELETE, END
}
