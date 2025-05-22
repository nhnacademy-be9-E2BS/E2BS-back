package com.nhnacademy.back.product.product.kim.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.back.product.category.domain.entity.Category;
import com.nhnacademy.back.product.category.domain.entity.ProductCategory;
import com.nhnacademy.back.product.category.repository.CategoryJpaRepository;
import com.nhnacademy.back.product.category.repository.ProductCategoryJpaRepository;
import com.nhnacademy.back.product.contributor.domain.entity.Contributor;
import com.nhnacademy.back.product.contributor.domain.entity.Position;
import com.nhnacademy.back.product.contributor.domain.entity.ProductContributor;
import com.nhnacademy.back.product.contributor.repository.ContributorJpaRepository;
import com.nhnacademy.back.product.contributor.repository.PositionJpaRepository;
import com.nhnacademy.back.product.contributor.repository.ProductContributorJpaRepository;
import com.nhnacademy.back.product.image.domain.entity.ProductImage;
import com.nhnacademy.back.product.image.repository.ProductImageJpaRepository;
import com.nhnacademy.back.product.product.domain.dto.request.RequestProductCreateDTO;
import com.nhnacademy.back.product.product.domain.dto.request.RequestProductGetDTO;
import com.nhnacademy.back.product.product.domain.dto.request.RequestProductSalePriceUpdateDTO;
import com.nhnacademy.back.product.product.domain.dto.request.RequestProductStockUpdateDTO;
import com.nhnacademy.back.product.product.domain.dto.request.RequestProductUpdateDTO;
import com.nhnacademy.back.product.product.domain.dto.response.ResponseProductCouponDTO;
import com.nhnacademy.back.product.product.domain.dto.response.ResponseProductReadDTO;
import com.nhnacademy.back.product.product.domain.entity.Product;
import com.nhnacademy.back.product.product.exception.ProductAlreadyExistsException;
import com.nhnacademy.back.product.product.exception.ProductNotFoundException;
import com.nhnacademy.back.product.product.exception.ProductStockDecrementException;
import com.nhnacademy.back.product.product.kim.service.ProductService;
import com.nhnacademy.back.product.product.repository.ProductJpaRepository;
import com.nhnacademy.back.product.publisher.domain.dto.request.RequestPublisherDTO;
import com.nhnacademy.back.product.publisher.domain.entity.Publisher;
import com.nhnacademy.back.product.publisher.repository.PublisherJpaRepository;
import com.nhnacademy.back.product.publisher.service.PublisherService;
import com.nhnacademy.back.product.state.domain.entity.ProductState;
import com.nhnacademy.back.product.state.domain.entity.ProductStateName;
import com.nhnacademy.back.product.state.repository.ProductStateJpaRepository;
import com.nhnacademy.back.product.tag.domain.entity.ProductTag;
import com.nhnacademy.back.product.tag.domain.entity.Tag;
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
	private final CategoryJpaRepository categoryJpaRepository;
	private final ProductCategoryJpaRepository productCategoryJpaRepository;
	private final ContributorJpaRepository contributorJpaRepository;
	private final ProductContributorJpaRepository productContributorJpaRepository;
	private final TagJpaRepository tagJpaRepository;
	private final ProductTagJpaRepository productTagJpaRepository;
	private final PublisherService publisherService;
	private final PositionJpaRepository positionJpaRepository;

	/**
	 * 도서를 DB에 저장
	 * 도서가 이미 존재하면 Exception 발생
	 */
	@Override
	@Transactional
	public void createProduct(RequestProductCreateDTO request) {
		// DB에서 없으면 생성하게 만드는게 아니라 입력받아 없으면 자동 생성하게 만듦
		if (publisherJpaRepository.findByPublisherName(request.getPublisherName()) == null) {
			RequestPublisherDTO requestPublisherDTO = new RequestPublisherDTO();
			requestPublisherDTO.setPublisherName(request.getPublisherName());
			publisherService.createPublisher(requestPublisherDTO);
		}
		Publisher publisher = publisherJpaRepository.findByPublisherName(request.getPublisherName());

		List<String> imagePaths = request.getProductImagePaths();
		List<String> tagNames = request.getTagNames();
		List<Long> categoryIds = request.getCategoryIds();
		List<String> contributorNames = request.getContributorNames();
		List<String> positionNames = request.getPositionNames();

		// 이미 존재하는지 unique인 isbn으로 DB에서 조회
		if (productJpaRepository.existsByProductIsbn(request.getProductIsbn())) {
			throw new ProductAlreadyExistsException("Product already exists");
		}

		//이미지 없이 product 객체 생성
		Product product = Product.createProductEntity(request, publisher);
		//DB에 Product저장
		productJpaRepository.save(product);

		// for문으로 ProductImage객체를 생성하여 저장을 반복
		for (String imagePath : imagePaths) {
			productImageJpaRepository.save(new ProductImage(product, imagePath));
		}

		// 태그 저장
		for (String tagName : tagNames) {
			Tag tag = tagJpaRepository.findByTagName(tagName)
				.orElseGet(() -> tagJpaRepository.save(new Tag(tagName)));
			productTagJpaRepository.save(new ProductTag(product, tag));
		}

		// 카테고리 저장
		for (Long categoryId : categoryIds) {
			Category category = categoryJpaRepository.findById(categoryId)
				.orElseThrow(() -> new IllegalArgumentException("Category not found: " + categoryId));
			productCategoryJpaRepository.save(new ProductCategory(product, category));
		}

		// 기여자 저장
		if (contributorNames != null && positionNames != null && contributorNames.size() == positionNames.size()) {
			for (int i = 0; i < contributorNames.size(); i++) {
				String contributorName = contributorNames.get(i);
				String positionName = positionNames.get(i);

				Contributor contributor = contributorJpaRepository.findByContributorName(contributorName)
					.orElseGet(() -> {
						Position position = positionJpaRepository.findByPositionName(positionName)
							.orElseGet(() -> positionJpaRepository.save(new Position(positionName)));
						return contributorJpaRepository.save(new Contributor(contributorName, position));
					});
				productContributorJpaRepository.save(new ProductContributor(product, contributor));
			}
		}
	}



	/**
	 * 도서 한 권을 DB에서 조회
	 */
	@Override
	public ResponseProductReadDTO getProduct(long productId, RequestProductGetDTO request) {
		// RequestProductGetDTO가 productId를 포함하도록 업데이트될 것이라 가정
		Product product = productJpaRepository.findById(productId)
			.orElseThrow(ProductNotFoundException::new);


		return new ResponseProductReadDTO(
			product.getProductId(),
			product.getProductState().getProductStateName().name(),
			product.getPublisher().getPublisherName(),
			product.getProductTitle(),
			product.getProductContent(),
			product.getProductDescription(),
			product.getProductPublishedAt(),
			product.getProductIsbn(),
			product.getProductRegularPrice(),
			product.getProductSalePrice(),
			product.isProductPackageable(),
			product.getProductStock(),
			findProductImagePaths(product),
			findTagNames(product),
			findCategoryIds(product),
			findContributorNames(product)
		);
	}

	/**
	 * 도서전체 목록을 페이지 단위로 조회
	 */
	@Override
	public Page<ResponseProductReadDTO> getProducts(Pageable pageable) {
		return productJpaRepository.findAll(pageable).map(product -> new ResponseProductReadDTO(
			product.getProductId(),
			product.getProductState().getProductStateName().name(),
			product.getPublisher().getPublisherName(),
			product.getProductTitle(),
			product.getProductContent(),
			product.getProductDescription(),
			product.getProductPublishedAt(),
			product.getProductIsbn(),
			product.getProductRegularPrice(),
			product.getProductSalePrice(),
			product.isProductPackageable(),
			product.getProductStock(),
			findProductImagePaths(product),
			findTagNames(product),
			findCategoryIds(product),
			findContributorNames(product)
		));
	}

	/**
	 * 특정 도서 ID 목록으로 도서 조회
	 */
	@Override
	public List<ResponseProductReadDTO> getProducts(List<Long> products) {
		return productJpaRepository.findAllById(products).stream().map(product -> new ResponseProductReadDTO(
			product.getProductId(),
			product.getProductState().getProductStateName().name(),
			product.getPublisher().getPublisherName(),
			product.getProductTitle(),
			product.getProductContent(),
			product.getProductDescription(),
			product.getProductPublishedAt(),
			product.getProductIsbn(),
			product.getProductRegularPrice(),
			product.getProductSalePrice(),
			product.isProductPackageable(),
			product.getProductStock(),
			findProductImagePaths(product),
			findTagNames(product),
			findCategoryIds(product),
			findContributorNames(product)
		)).collect(Collectors.toList());
	}

	/**
	 * 도서 정보 수정
	 */
	@Override
	@Transactional
	public void updateProduct(long productId, RequestProductUpdateDTO request) {
		Product product = productJpaRepository.findById(productId)
			.orElseThrow(() -> new IllegalArgumentException("도서를 찾을 수 없습니다."));
		if (publisherJpaRepository.findByPublisherName(request.getPublisherName()) == null) {
			RequestPublisherDTO requestPublisherDTO = new RequestPublisherDTO();
			requestPublisherDTO.setPublisherName(request.getPublisherName());
			publisherService.createPublisher(requestPublisherDTO);
		}
		Publisher publisher = publisherJpaRepository.findByPublisherName(request.getPublisherName());


		ProductStateName productStateName = ProductStateName.valueOf(request.getProductStateName());
		ProductState productState = productStateJpaRepository.findByProductStateName(productStateName);

		product.updateProduct(request, publisher, productState);

		// 기존 이미지 삭제 후 새 이미지 저장
		productImageJpaRepository.deleteByProduct_ProductId(productId);
		for (String imagePath : request.getProductImagePaths()) {
			productImageJpaRepository.save(new ProductImage(product, imagePath));
		}

		// 기존 태그 삭제 후 새 태그 저장
		productTagJpaRepository.deleteByProduct_ProductId(productId);
		for (String tagName : request.getProductTags()) {
			Tag tag = tagJpaRepository.findByTagName(tagName)
				.orElseGet(() -> tagJpaRepository.save(new Tag(tagName)));
			productTagJpaRepository.save(new ProductTag(product, tag));
		}

		// 기존 카테고리 삭제 후 새 카테고리 저장
		productCategoryJpaRepository.deleteByProduct_ProductId(productId);
		for (Long categoryId : request.getCategoryIds()) {
			Category category = categoryJpaRepository.findById(categoryId)
				.orElseThrow(() -> new IllegalArgumentException("Category not found: " + categoryId));
			productCategoryJpaRepository.save(new ProductCategory(product, category));
		}

		// 기존 기여자 삭제 후 새 기여자 저장
		productContributorJpaRepository.deleteByProduct_ProductId(productId);
		for (String contributorName : request.getContributorNames()) {
			Contributor contributor = contributorJpaRepository.findByContributorName(contributorName)
				.orElseGet(() -> contributorJpaRepository.save(new Contributor(contributorName, null)));
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
			.orElseThrow(() -> new IllegalArgumentException("도서를 찾을 수 없습니다."));
		//내부 메서드 사용
		decrementProductStock(request.getProductDecrementStock(), product);
	}

	/**
	 * 도서 판매가 수정
	 */
	@Override
	@Transactional
	public void updateProductSalePrice(long productId, RequestProductSalePriceUpdateDTO request) {
		Product product = productJpaRepository.findById(productId)
			.orElseThrow(() -> new IllegalArgumentException("도서를 찾을 수 없습니다."));

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







	/**
	 * productId로 이미지들을 찾아 List로 반환하는 내부 메서드
	 */
	private List<String> findProductImagePaths(Product product) {
		List<ProductImage> productImages = productImageJpaRepository.findByProduct_ProductId(product.getProductId());
		return productImages.stream().map(ProductImage::getProductImagePath).collect(Collectors.toList());


	}
	/**
	 * productId로 태그 이름을 찾아 List로 반환하는 내부 메서드
	 */
	private List<String> findTagNames(Product product) {
		List<ProductTag> productTags = productTagJpaRepository.findByProduct_ProductId(product.getProductId());
		return productTags.stream().map(pt -> pt.getTag().getTagName()).collect(Collectors.toList());
	}

	/**
	 * productId로 카테고리 ID를 찾아 List로 반환하는 내부 메서드
	 */
	private List<Long> findCategoryIds(Product product) {
		List<ProductCategory> productCategories = productCategoryJpaRepository.findByProduct_ProductId(product.getProductId());
		return productCategories.stream().map(pc -> pc.getCategory().getCategoryId()).collect(Collectors.toList());
	}

	/**
	 * productId로 기여자 이름을 찾아 List로 반환하는 내부 메서드
	 */
	private List<String> findContributorNames(Product product) {
		List<ProductContributor> productContributors = productContributorJpaRepository.findByProduct_ProductId(product.getProductId());
		return productContributors.stream().map(pc -> pc.getContributor().getContributorName()).collect(Collectors.toList());
	}

	/**
	 * 상품 차감 수량을 입력받아 DB에 저장하는 내부 메서드
	 */
	private void decrementProductStock(int decrementStock, Product product) {
		int productStock = product.getProductStock();
		int decrementedStock = productStock - decrementStock;

		if (decrementedStock < 0) {
			throw new ProductStockDecrementException("재고 수량 부족으로 차감 실패");
		} else {
			System.out.println("수량 차감 성공");
			product.setProduct(decrementedStock);
		}
		productJpaRepository.save(product);
	}
}
