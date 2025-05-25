package com.nhnacademy.back.product.product.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nhnacademy.back.product.product.domain.dto.request.RequestMainPageProductDTO;
import com.nhnacademy.back.product.product.domain.dto.response.ResponseMainPageProductDTO;

public interface MainPageProductService {
	//메인 페이지에서 책 보여주기
	Page<ResponseMainPageProductDTO> showProducts(RequestMainPageProductDTO request, Pageable pageable);


}
