package com.nhnacademy.back.product.product.kim.service.impl;

import org.springframework.stereotype.Service;

import com.nhnacademy.back.product.product.domain.dto.request.RequestProductCreateDTO;
import com.nhnacademy.back.product.product.domain.dto.request.RequestProductStockUpdateDTO;
import com.nhnacademy.back.product.product.domain.dto.request.RequestProductUpdateDTO;
import com.nhnacademy.back.product.product.domain.dto.response.ResponseProductReadDTO;
import com.nhnacademy.back.product.product.domain.entity.Product;
import com.nhnacademy.back.product.product.exception.ProductAlreadyExistsException;
import com.nhnacademy.back.product.product.kim.service.ProductService;
import com.nhnacademy.back.product.product.repository.ProductJpaRepository;
import com.nhnacademy.back.product.publisher.domain.entity.Publisher;
import com.nhnacademy.back.product.publisher.repository.PublisherJpaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
	private final ProductJpaRepository productJpaRepository;
	private final PublisherJpaRepository publisherJpaRepository;

	/**
	 * 도서를 DB에 저장
	 * 도서가 이미 존재하면 Exception 발생
	 */
	@Override
	public void createProduct(RequestProductCreateDTO request) {
		Publisher publisher = publisherJpaRepository.findById(request.getPublisherId()).get();
		String productTitle = request.getProductTitle();
		String productContent = request.getProductContent();
		String productDescription = request.getProductDescription();
		String productIsbn = request.getProductIsbn();
		long productRegularPrice = request.getProductRegularPrice();
		long productSalePrice = request.getProductSalePrice();
		boolean productPackageable = request.isProductPackageable();
		int productStock = request.getProductStock();

		if (productJpaRepository.existsByProductIsbn(productIsbn)) {
			throw new ProductAlreadyExistsException("Product already exists");
		}
		Product product = new Product(
			publisher,
			productTitle,
			productContent,
			productDescription,
			productIsbn,
			productRegularPrice,
			productSalePrice,
			productPackageable,
			productStock
		);
		productJpaRepository.save(product);


	}

	@Override
	public ResponseProductReadDTO readProductDetail(String productTitle) {
		return null;
	}

	@Override
	public void updateProduct(RequestProductUpdateDTO request) {

	}

	@Override
	public void updateProductStock(RequestProductStockUpdateDTO request) {

	}

}
