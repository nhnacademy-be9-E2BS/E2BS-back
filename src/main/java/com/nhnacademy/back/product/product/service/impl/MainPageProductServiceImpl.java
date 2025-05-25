package com.nhnacademy.back.product.product.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.back.product.category.domain.entity.Category;
import com.nhnacademy.back.product.category.domain.entity.ProductCategory;
import com.nhnacademy.back.product.category.repository.CategoryJpaRepository;
import com.nhnacademy.back.product.category.repository.ProductCategoryJpaRepository;
import com.nhnacademy.back.product.contributor.domain.dto.response.ResponseContributorDTO;
import com.nhnacademy.back.product.contributor.repository.ContributorJpaRepository;
import com.nhnacademy.back.product.contributor.repository.ProductContributorJpaRepository;
import com.nhnacademy.back.product.image.domain.entity.ProductImage;
import com.nhnacademy.back.product.image.repository.ProductImageJpaRepository;
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
	private final ContributorJpaRepository contributorJpaRepository;
	private final ProductContributorJpaRepository productContributorJpaRepository;
	private final ProductImageJpaRepository productImageJpaRepository;
	@Transactional(readOnly = true)
	@Override
	public Page<ResponseMainPageProductDTO> showProducts(RequestMainPageProductDTO request, Pageable pageable) {
		String categoryName = request.getCategoryName();
		Category category = categoryJpaRepository.findCategoryByCategoryName(categoryName);

		List<ProductCategory> products = productCategoryJpaRepository.findProductIdByCategory(category);
		List<Long> productIds = products.stream().map(productCategory -> productCategory.getProduct().getProductId()).toList();

		int start = (int) pageable.getOffset();
		int end = Math.min(start + pageable.getPageSize(), productIds.size());
		List<Long> pagedIds = productIds.subList(start, end);

		List<ResponseMainPageProductDTO> responseList = pagedIds.stream().map(productId -> {
			Product product = productJpaRepository.findById(productId)
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
		}).toList();


		return new PageImpl<>(responseList, pageable, productIds.size());
	}
}

