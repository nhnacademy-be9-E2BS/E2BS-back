package com.nhnacademy.back.product.product.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.back.product.category.domain.entity.Category;
import com.nhnacademy.back.product.category.repository.CategoryJpaRepository;
import com.nhnacademy.back.product.category.repository.ProductCategoryJpaRepository;
import com.nhnacademy.back.product.contributor.domain.dto.response.ResponseContributorDTO;
import com.nhnacademy.back.product.contributor.repository.ProductContributorJpaRepository;
import com.nhnacademy.back.product.image.domain.entity.ProductImage;
import com.nhnacademy.back.product.product.domain.dto.response.ResponseMainPageProductDTO;
import com.nhnacademy.back.product.product.domain.entity.Product;
import com.nhnacademy.back.product.product.exception.ProductNotFoundException;
import com.nhnacademy.back.product.product.repository.ProductJpaRepository;
import com.nhnacademy.back.product.product.service.MainPageProductService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class MainPageProductServiceImpl implements MainPageProductService {
	private final ProductJpaRepository productJpaRepository;
	private final CategoryJpaRepository categoryJpaRepository;
	private final ProductCategoryJpaRepository productCategoryJpaRepository;
	private final ProductContributorJpaRepository productContributorJpaRepository;



	@Override
	public List<ResponseMainPageProductDTO> getProductsByCategory(String categoryName) {
		Category category = categoryJpaRepository.findCategoryByCategoryName(categoryName);
		if (category == null) {
			return List.of();
		}

		List<Long> productIds = productCategoryJpaRepository.findProductIdByCategory(category)
			.stream()
			.map(pc -> pc.getProduct().getProductId())
			.toList();

		return productIds.stream()
			.map(productId -> {
				Product product = productJpaRepository.findByIdWithImages(productId)
					.orElseThrow(ProductNotFoundException::new);

				String contributorName = productContributorJpaRepository
					.findContributorDTOsByProductId(productId)
					.stream()
					.findFirst()
					.map(ResponseContributorDTO::getContributorName)
					.orElse("미상");

				String imagePath = product.getProductImage()
					.stream()
					.findFirst()
					.map(ProductImage::getProductImagePath)
					.orElse(null);

				return new ResponseMainPageProductDTO(
					product.getProductId(),
					product.getProductTitle(),
					contributorName,
					imagePath,
					product.getProductSalePrice(),
					product.getProductDescription(),
					product.getPublisher().getPublisherName()
				);
			})
			.toList();
	}

	@Override
	public List<ResponseMainPageProductDTO> getBestSellerProducts() {
		return getProductsByCategory("베스트셀러");
	}
	@Override
	public List<ResponseMainPageProductDTO> getBlogBestProducts() {
		return getProductsByCategory("블로그베스트");
	}

	@Override
	public List<ResponseMainPageProductDTO> getNewItemsProducts() {
		return getProductsByCategory("신간");
	}

	@Override
	public List<ResponseMainPageProductDTO> getItemNewSpecialProducts() {
		return getProductsByCategory("신간스페셜");
	}

	@Override
	public List<ResponseMainPageProductDTO> getItemEditorChoiceProducts() {
		return getProductsByCategory("ItemEditorChoice");

	}

}