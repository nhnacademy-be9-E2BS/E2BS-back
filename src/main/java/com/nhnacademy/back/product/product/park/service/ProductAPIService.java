package com.nhnacademy.back.product.product.park.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nhnacademy.back.product.product.domain.dto.request.RequestProductApiGetDTO;
import com.nhnacademy.back.product.product.domain.dto.request.RequestProductCreateDTO;
import com.nhnacademy.back.product.product.domain.dto.response.ResponseProductsApiGetDTO;

public interface ProductAPIService {
	Page<ResponseProductsApiGetDTO> searchProducts(RequestProductApiGetDTO request, Pageable pageable); //api에서 책 리스트 가져오기
	void createProduct(RequestProductCreateDTO request);





}