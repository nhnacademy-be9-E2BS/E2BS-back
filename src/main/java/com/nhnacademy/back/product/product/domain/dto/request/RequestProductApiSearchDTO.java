package com.nhnacademy.back.product.product.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
<<<<<<<< HEAD:src/main/java/com/nhnacademy/back/product/product/domain/dto/request/RequestProductApiSearchDTO.java
public class RequestProductApiSearchDTO {
	String query;
	String queryType;
========
public class RequestProductGetDTO {
	/**
	 * 도서 단건 조회를 위해 필요한 정보
	 */
	@NotNull
	String productIsbn;


>>>>>>>> dev:src/main/java/com/nhnacademy/back/product/product/domain/dto/request/RequestProductGetDTO.java
}

