package com.nhnacademy.back.product.product.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nhnacademy.back.product.product.domain.dto.request.RequestProductDTO;
import com.nhnacademy.back.product.product.domain.dto.request.RequestProductSalePriceUpdateDTO;
import com.nhnacademy.back.product.product.domain.dto.request.RequestProductStockUpdateDTO;
import com.nhnacademy.back.product.product.domain.dto.response.ResponseMainPageProductDTO;
import com.nhnacademy.back.product.product.domain.dto.response.ResponseProductReadDTO;

public interface ProductService {
	//관리자 - 도서 DB에 저장
	void createProduct(RequestProductDTO request);

	//공통 - 도서 한권 상세 조회
	ResponseProductReadDTO getProduct(long productId);

	//공통 - 도서 여러권 페이지로 조회
	Page<ResponseProductReadDTO> getProducts(Pageable pageable, long categoryId);

	//Order전용 - 도서 여러권 리스트로 조회
	List<ResponseProductReadDTO> getProducts(List<Long> productIds);

	//관리자 - 도서 수정
	void updateProduct(long productId, RequestProductDTO request);

	//관리자 - 재고 수동 수정
	void updateProductStock(long productId, RequestProductStockUpdateDTO request);

	//관리자 - 판매가 수정
	void updateProductSalePrice(long productId, RequestProductSalePriceUpdateDTO request);

	//검색 전용 - 페이징 처리 된 id들을 받아서 도서 페이지 조회
	Page<ResponseProductReadDTO> getProductsToElasticSearch(Page<Long> productIds);

	//메인페이지 전용 - id 리스트를 받아서 도서 조회
	List<ResponseMainPageProductDTO> getProductsToMain(List<Long> productIds);
}
