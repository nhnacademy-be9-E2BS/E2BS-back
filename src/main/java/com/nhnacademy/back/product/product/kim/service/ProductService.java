package com.nhnacademy.back.product.product.kim.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nhnacademy.back.product.product.domain.dto.request.RequestProductCreateDTO;
import com.nhnacademy.back.product.product.domain.dto.request.RequestProductSalePriceUpdateDTO;
import com.nhnacademy.back.product.product.domain.dto.request.RequestProductStockUpdateDTO;
import com.nhnacademy.back.product.product.domain.dto.request.RequestProductUpdateDTO;
import com.nhnacademy.back.product.product.domain.dto.response.ResponseProductCouponDTO;
import com.nhnacademy.back.product.product.domain.dto.response.ResponseProductReadDTO;

public interface ProductService {
	//관리자 - 도서 DB에 저장
	void createProduct(RequestProductCreateDTO request);

	//공통 - 도서 한권 상세 조회
	ResponseProductReadDTO getProduct(long productId);

	//공통 - 도서 여러권 페이지로 조회
	Page<ResponseProductReadDTO> getProducts(Pageable pageable);

	//Order전용 - 도서 여러권 리스트로 조회
	List<ResponseProductReadDTO> getProducts(List<Long> products);

	//관리자 - 도서 수정
	void updateProduct(long productId, RequestProductUpdateDTO request);

	//관리자 - 재고 수동 수정
	void updateProductStock(long productId, RequestProductStockUpdateDTO request);

	//관리자 - 판매가 수정
	void updateProductSalePrice(long productId, RequestProductSalePriceUpdateDTO request);

	//Coupon전용 - Sale중인 전체 도서 페이지로 조회
	Page<ResponseProductCouponDTO> getProductsToCoupon(Pageable pageable);

}
