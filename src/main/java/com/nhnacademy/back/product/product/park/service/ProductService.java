package com.nhnacademy.back.product.product.park.service;

import java.util.List;

import com.nhnacademy.back.product.product.domain.dto.request.RequestProductApiGetDTO;
import com.nhnacademy.back.product.product.domain.dto.request.RequestProductCreateDTO;
import com.nhnacademy.back.product.product.domain.dto.response.ResponseProductsApiGetDTO;

public interface ProductService {
	List<ResponseProductsApiGetDTO> getProducts(RequestProductApiGetDTO request); //api에서 책 리스트 가져오기
	void createProduct(RequestProductCreateDTO request);





}