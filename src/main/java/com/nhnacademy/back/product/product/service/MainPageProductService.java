package com.nhnacademy.back.product.product.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.back.product.product.domain.dto.request.RequestMainPageProductDTO;
import com.nhnacademy.back.product.product.domain.dto.response.ResponseMainPageProductDTO;

public interface MainPageProductService {
	//메인 페이지에서 책 보여주기
	List<ResponseMainPageProductDTO> showProducts(RequestMainPageProductDTO request);

	@Transactional(readOnly = true)
	List<ResponseMainPageProductDTO> showProductsByCategory(String categoryName);

	List<ResponseMainPageProductDTO> showBestSellerProducts();

	List<ResponseMainPageProductDTO> showBlogBestProducts();

	List<ResponseMainPageProductDTO> showNewItemsProducts();

	List<ResponseMainPageProductDTO> showItemNewSpecialProducts();
}
