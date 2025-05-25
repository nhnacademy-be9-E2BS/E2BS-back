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
import com.nhnacademy.back.product.product.domain.dto.request.RequestMainPageProductDTO;
import com.nhnacademy.back.product.product.domain.dto.response.ResponseMainPageProductDTO;
import com.nhnacademy.back.product.product.domain.entity.Product;
import com.nhnacademy.back.product.product.exception.ProductNotFoundException;
import com.nhnacademy.back.product.product.repository.ProductJpaRepository;
import com.nhnacademy.back.product.product.service.MainPageProductService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class MainPageProductServiceImpl implements MainPageProductService {
	private final ProductJpaRepository productJpaRepository;
	private final CategoryJpaRepository categoryJpaRepository;
	private final ProductCategoryJpaRepository productCategoryJpaRepository;
	private final ProductContributorJpaRepository productContributorJpaRepository;

	@Override
	public List<ResponseMainPageProductDTO> showProducts(RequestMainPageProductDTO request) {
		return List.of();
	}

	@Transactional(readOnly = true)
	@Override
	public List<ResponseMainPageProductDTO> showProductsByCategory(String categoryName) {
		Category category = categoryJpaRepository.findCategoryByCategoryName(categoryName);
		if (category == null) {
			return List.of(); // 혹은 예외 던지기
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
					product.getProductSalePrice()
				);
			})
			.toList();
	}

	@Override
	public List<ResponseMainPageProductDTO> showBestSellerProducts() {
		return showProductsByCategory("Bestseller");
	}

	@Override
	public List<ResponseMainPageProductDTO> showBlogBestProducts() {
		return showProductsByCategory("BlogBest");
	}

	@Override
	public List<ResponseMainPageProductDTO> showNewItemsProducts() {
		return showProductsByCategory("ItemNewAll");
	}

	@Override
	public List<ResponseMainPageProductDTO> showItemNewSpecialProducts() {
		return showProductsByCategory("ItemNewSpecial");
	}

}
