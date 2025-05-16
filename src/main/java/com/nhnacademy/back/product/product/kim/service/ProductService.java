package com.nhnacademy.back.product.product.kim.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import com.nhnacademy.back.product.product.domain.dto.request.RequestProductCreateDTO;
import com.nhnacademy.back.product.product.domain.dto.request.RequestProductGetDTO;
import com.nhnacademy.back.product.product.domain.dto.request.RequestProductSalePriceUpdateDTO;
import com.nhnacademy.back.product.product.domain.dto.request.RequestProductStockUpdateDTO;
import com.nhnacademy.back.product.product.domain.dto.request.RequestProductUpdateDTO;
import com.nhnacademy.back.product.product.domain.dto.response.ResponseProductCouponDTO;
import com.nhnacademy.back.product.product.domain.dto.response.ResponseProductReadDTO;

public interface ProductService {
	void createProduct(RequestProductCreateDTO request);

	ResponseProductReadDTO getProduct(RequestProductGetDTO request);

	Page<ResponseProductReadDTO> getProducts(Pageable pageable);

	ResponseEntity<List<ResponseProductReadDTO>> getProducts(List<Long> products);

	void updateProduct(RequestProductUpdateDTO request);

	ResponseEntity<Void> updateProductStock(RequestProductStockUpdateDTO request);

	void updateProductSalePrice(RequestProductSalePriceUpdateDTO request);

	Page<ResponseProductCouponDTO> getProductsToCoupon(Pageable pageable);



}
