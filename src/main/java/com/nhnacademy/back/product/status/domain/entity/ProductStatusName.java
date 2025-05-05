package com.nhnacademy.back.product.status.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum ProductStatusName {
	SALE, OUT, DELETE, END
}
