package com.nhnacademy.back.product.product.kim.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.back.product.category.domain.dto.response.ResponseCategoryDTO;
import com.nhnacademy.back.product.category.domain.entity.Category;
import com.nhnacademy.back.product.category.repository.ProductCategoryJpaRepository;
import com.nhnacademy.back.product.contributor.domain.dto.response.ResponseContributorDTO;
import com.nhnacademy.back.product.contributor.domain.entity.Contributor;
import com.nhnacademy.back.product.contributor.domain.entity.ProductContributor;
import com.nhnacademy.back.product.contributor.exception.ContributorNotFoundException;
import com.nhnacademy.back.product.contributor.repository.ContributorJpaRepository;
import com.nhnacademy.back.product.contributor.repository.ProductContributorJpaRepository;
import com.nhnacademy.back.product.image.domain.dto.response.ResponseProductImageDTO;
import com.nhnacademy.back.product.image.domain.entity.ProductImage;
import com.nhnacademy.back.product.image.repository.ProductImageJpaRepository;
import com.nhnacademy.back.product.product.domain.dto.request.RequestProductDTO;
import com.nhnacademy.back.product.product.domain.dto.request.RequestProductSalePriceUpdateDTO;
import com.nhnacademy.back.product.product.domain.dto.request.RequestProductStockUpdateDTO;
import com.nhnacademy.back.product.product.domain.dto.response.ResponseProductCouponDTO;
import com.nhnacademy.back.product.product.domain.dto.response.ResponseProductReadDTO;
import com.nhnacademy.back.product.product.domain.entity.Product;
import com.nhnacademy.back.product.product.exception.ProductAlreadyExistsException;
import com.nhnacademy.back.product.product.exception.ProductNotFoundException;
import com.nhnacademy.back.product.product.exception.ProductStockDecrementException;
import com.nhnacademy.back.product.product.kim.service.ProductService;
import com.nhnacademy.back.product.product.repository.ProductJpaRepository;
import com.nhnacademy.back.product.publisher.domain.dto.response.ResponsePublisherDTO;
import com.nhnacademy.back.product.publisher.domain.entity.Publisher;
import com.nhnacademy.back.product.publisher.exception.PublisherNotFoundException;
import com.nhnacademy.back.product.publisher.repository.PublisherJpaRepository;
import com.nhnacademy.back.product.state.domain.dto.response.ResponseProductStateDTO;
import com.nhnacademy.back.product.state.domain.entity.ProductState;
import com.nhnacademy.back.product.state.domain.entity.ProductStateName;
import com.nhnacademy.back.product.state.exception.ProductStateNotFoundException;
import com.nhnacademy.back.product.state.repository.ProductStateJpaRepository;
import com.nhnacademy.back.product.tag.domain.dto.response.ResponseTagDTO;
import com.nhnacademy.back.product.tag.domain.entity.ProductTag;
import com.nhnacademy.back.product.tag.domain.entity.Tag;
import com.nhnacademy.back.product.tag.exception.TagNotFoundException;
import com.nhnacademy.back.product.tag.repository.ProductTagJpaRepository;
import com.nhnacademy.back.product.tag.repository.TagJpaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {
	private final ProductJpaRepository productJpaRepository;
	private final ProductImageJpaRepository productImageJpaRepository;
	private final PublisherJpaRepository publisherJpaRepository;
	private final ProductStateJpaRepository productStateJpaRepository;
	private final ProductCategoryJpaRepository productCategoryJpaRepository;
	private final ContributorJpaRepository contributorJpaRepository;
	private final ProductContributorJpaRepository productContributorJpaRepository;
	private final TagJpaRepository tagJpaRepository;
	private final ProductTagJpaRepository productTagJpaRepository;

	/**
	 * 도서를 DB에 저장
	 * 도서가 이미 존재하면 Exception 발생
	 */
	@Override
	@Transactional
	public Long createProduct(RequestProductDTO request) {
		// 이미 존재하는지 unique인 isbn으로 DB에서 조회
		if (productJpaRepository.existsByProductIsbn(request.getProductIsbn())) {
			throw new ProductAlreadyExistsException("Product already exists");
		}

		ProductState productState = productStateJpaRepository.findById(request.getProductStateId())
			.orElseThrow(() -> new ProductStateNotFoundException("상품상태 조회 실패"));

		Publisher publisher = publisherJpaRepository.findById(request.getPublisherId())
			.orElseThrow(() -> new PublisherNotFoundException("출판사 조회 실패"));

		List<String> imagePaths = request.getProductImagePaths();
		List<Long> tagIds = request.getTagIds();
		List<Long> contributorIds = request.getContributorIds();

		// product 객체 생성
		Product product = Product.createProductEntity(request, productState, publisher);

		// DB에 Product저장
		productJpaRepository.save(product);

		// 이미지 저장
		for (String imagePath : imagePaths) {
			productImageJpaRepository.save(new ProductImage(product, imagePath));
		}

		// 태그 저장
		for (Long tagId : tagIds) {
			Tag tag = tagJpaRepository.findById(tagId)
				.orElseThrow(() -> new TagNotFoundException("태그 조회 실패"));
			productTagJpaRepository.save(new ProductTag(product, tag));
		}

		// 기여자 저장
		for (Long contributorId : contributorIds) {
			Contributor contributor = contributorJpaRepository.findById(contributorId)
				.orElseThrow(() -> new ContributorNotFoundException("기여자 조회 실패"));
			productContributorJpaRepository.save(new ProductContributor(product, contributor));
		}

		return product.getProductId();
	}

	/**
	 * 도서 한 권을 DB에서 조회
	 */
	@Override
	public ResponseProductReadDTO getProduct(long productId) {
		Product product = productJpaRepository.findById(productId)
			.orElseThrow(ProductNotFoundException::new);

		return new ResponseProductReadDTO(
			product.getProductId(),
			new ResponseProductStateDTO(product.getProductState().getProductStateId(),
				product.getProductState().getProductStateName().name()),
			new ResponsePublisherDTO(product.getPublisher().getPublisherId(),
				product.getPublisher().getPublisherName()),
			product.getProductTitle(),
			product.getProductContent(),
			product.getProductDescription(),
			product.getProductPublishedAt(),
			product.getProductIsbn(),
			product.getProductRegularPrice(),
			product.getProductSalePrice(),
			product.isProductPackageable(),
			product.getProductStock(),
			productImageJpaRepository.findImageDTOsByProductId(productId),
			productTagJpaRepository.findTagDTOsByProductId(productId),
			productCategoryJpaRepository.findCategoryDTOsByProductId(productId),
			productContributorJpaRepository.findContributorDTOsByProductId(productId)
		);
	}

	/**
	 * 도서 목록을 페이지 단위로 조회
	 * categoryId = 0이면 전체 조회, 0이 아니면 해당 카테고리에서 조회
	 */
	@Override
	public Page<ResponseProductReadDTO> getProducts(Pageable pageable, long categoryId) {
		Page<Product> productPage;
		if (categoryId == 0) {
			productPage = productJpaRepository.findAll(pageable);
		} else {
			productPage = productJpaRepository.findAllByCategoryId(categoryId, pageable);
		}
		List<Long> productIds = productPage.stream().map(Product::getProductId).toList();

		Map<Long, List<ProductImage>> imageMap = productImageJpaRepository.findAllByProductIdsGrouped(productIds);
		Map<Long, List<Tag>> tagMap = productTagJpaRepository.findTagsGroupedByProductIds(productIds);
		Map<Long, List<Category>> categoryMap = productCategoryJpaRepository.findCategoriesGroupedByProductIds(
			productIds);
		Map<Long, List<Contributor>> contributorMap = productContributorJpaRepository.findContributorsGroupedByProductIds(
			productIds);

		Page<ResponseProductReadDTO> result = productPage.map(product -> {
			Long id = product.getProductId();
			return new ResponseProductReadDTO(
				id,
				new ResponseProductStateDTO(product.getProductState().getProductStateId(),
					product.getProductState().getProductStateName().name()),
				new ResponsePublisherDTO(product.getPublisher().getPublisherId(),
					product.getPublisher().getPublisherName()),
				product.getProductTitle(),
				product.getProductContent(),
				product.getProductDescription(),
				product.getProductPublishedAt(),
				product.getProductIsbn(),
				product.getProductRegularPrice(),
				product.getProductSalePrice(),
				product.isProductPackageable(),
				product.getProductStock(),
				imageMap.getOrDefault(id, List.of())
					.stream()
					.map(image -> new ResponseProductImageDTO(image.getProductImageId(), image.getProductImagePath()))
					.toList(),
				tagMap.getOrDefault(id, List.of()).stream()
					.map(tag -> new ResponseTagDTO(tag.getTagId(), tag.getTagName())).toList(),
				categoryMap.getOrDefault(id, List.of())
					.stream()
					.map(
						category -> new ResponseCategoryDTO(category.getCategoryId(), category.getCategoryName(), null))
					.toList(),
				contributorMap.getOrDefault(id, List.of())
					.stream()
					.map(contributor -> new ResponseContributorDTO(contributor.getContributorId(),
						contributor.getContributorName(),
						contributor.getPosition().getPositionId(), contributor.getPosition().getPositionName()))
					.toList()
			);
		});

		return result;
	}

	/**
	 * 특정 도서 ID 목록으로 도서 조회
	 */
	@Override
	public List<ResponseProductReadDTO> getProducts(List<Long> productIds) {
		List<Product> products = productJpaRepository.findAllById(productIds);

		// 한권이라도 존재하지 않는다면 예외 발생
		if (products.size() != productIds.size()) {
			throw new ProductNotFoundException();
		}

		return products.stream()
			.map(product -> new ResponseProductReadDTO(
				product.getProductId(),
				new ResponseProductStateDTO(product.getProductState().getProductStateId(),
					product.getProductState().getProductStateName().name()),
				new ResponsePublisherDTO(product.getPublisher().getPublisherId(),
					product.getPublisher().getPublisherName()),
				product.getProductTitle(),
				product.getProductContent(),
				product.getProductDescription(),
				product.getProductPublishedAt(),
				product.getProductIsbn(),
				product.getProductRegularPrice(),
				product.getProductSalePrice(),
				product.isProductPackageable(),
				product.getProductStock(),
				productImageJpaRepository.findImageDTOsByProductId(product.getProductId()),
				productTagJpaRepository.findTagDTOsByProductId(product.getProductId()),
				productCategoryJpaRepository.findCategoryDTOsByProductId(product.getProductId()),
				productContributorJpaRepository.findContributorDTOsByProductId(product.getProductId())
			)).toList();
	}

	/**
	 * 도서 정보 수정
	 */
	@Override
	@Transactional
	public void updateProduct(long productId, RequestProductDTO request) {
		Product product = productJpaRepository.findById(productId)
			.orElseThrow(ProductNotFoundException::new);

		ProductState productState = productStateJpaRepository.findById(request.getProductStateId())
			.orElseThrow(() -> new ProductStateNotFoundException("상품상태 조회 실패"));

		Publisher publisher = publisherJpaRepository.findById(request.getPublisherId())
			.orElseThrow(() -> new PublisherNotFoundException("출판사 조회 실패"));

		long stock = product.getProductStock() + request.getProductStock();
		if (stock < 0) {
			throw new ProductStockDecrementException("재고 수량 변경 불가");
		}

		// 상품 정보 업데이트 (수정)
		product.updateProduct(request, productState, publisher);

		List<String> imagePaths = request.getProductImagePaths();
		List<Long> tagIds = request.getTagIds();
		List<Long> contributorIds = request.getContributorIds();

		// 이미지 삭제 후 저장
		productImageJpaRepository.deleteByProduct_ProductId(productId);
		for (String imagePath : imagePaths) {
			productImageJpaRepository.save(new ProductImage(product, imagePath));
		}

		// 태그 삭제 후 저장
		productTagJpaRepository.deleteByProduct_ProductId(productId);
		for (Long tagId : tagIds) {
			Tag tag = tagJpaRepository.findById(tagId)
				.orElseThrow(() -> new TagNotFoundException("태그 조회 실패"));
			productTagJpaRepository.save(new ProductTag(product, tag));
		}

		// 기여자 삭제 후 저장
		productContributorJpaRepository.deleteByProduct_ProductId(productId);
		for (Long contributorId : contributorIds) {
			Contributor contributor = contributorJpaRepository.findById(contributorId)
				.orElseThrow(() -> new ContributorNotFoundException("기여자 조회 실패"));
			productContributorJpaRepository.save(new ProductContributor(product, contributor));
		}

		productJpaRepository.save(product);
	}

	/**
	 * 도서 재고 수정
	 */
	@Override
	@Transactional
	public void updateProductStock(long productId, RequestProductStockUpdateDTO request) {
		Product product = productJpaRepository.findById(productId)
			.orElseThrow(ProductNotFoundException::new);

		int stock = product.getProductStock() + request.getProductStock();
		if (stock < 0) {
			throw new ProductStockDecrementException("재고 수량 변경 불가");
		}

		product.setProductSale(stock);
		productJpaRepository.save(product);
	}

	/**
	 * 도서 판매가 수정
	 */
	@Override
	@Transactional
	public void updateProductSalePrice(long productId, RequestProductSalePriceUpdateDTO request) {
		Product product = productJpaRepository.findById(productId)
			.orElseThrow(ProductNotFoundException::new);

		product.setProduct(request.getProductSalePrice());
		productJpaRepository.save(product);
	}

	/**
	 * 쿠폰 적용 가능한 도서 목록 조회 (재고 있고 판매 상태인 도서)
	 */
	@Override
	@Transactional
	public Page<ResponseProductCouponDTO> getProductsToCoupon(Pageable pageable) {
		Page<Product> saleProducts = productJpaRepository.findAllByProductStateName(ProductStateName.SALE, pageable);

		return saleProducts.map(product ->
			new ResponseProductCouponDTO(
				product.getProductId(),
				product.getProductTitle(),
				product.getPublisher().getPublisherName()
			));
	}

}
