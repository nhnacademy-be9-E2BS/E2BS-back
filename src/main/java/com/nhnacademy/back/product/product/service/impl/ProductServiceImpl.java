package com.nhnacademy.back.product.product.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import com.nhnacademy.back.common.util.MinioUtils;
import com.nhnacademy.back.elasticsearch.domain.dto.request.RequestProductDocumentDTO;
import com.nhnacademy.back.elasticsearch.service.ProductSearchService;
import com.nhnacademy.back.product.category.domain.entity.Category;
import com.nhnacademy.back.product.category.domain.entity.ProductCategory;
import com.nhnacademy.back.product.category.exception.CategoryNotFoundException;
import com.nhnacademy.back.product.category.exception.ProductCategoryCreateNotAllowException;
import com.nhnacademy.back.product.category.repository.CategoryJpaRepository;
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
import com.nhnacademy.back.product.product.domain.dto.response.ResponseMainPageProductDTO;
import com.nhnacademy.back.product.product.domain.dto.response.ResponseProductReadDTO;
import com.nhnacademy.back.product.product.domain.entity.Product;
import com.nhnacademy.back.product.product.exception.ProductAlreadyExistsException;
import com.nhnacademy.back.product.product.exception.ProductNotFoundException;
import com.nhnacademy.back.product.product.exception.ProductStockDecrementException;
import com.nhnacademy.back.product.product.repository.ProductJpaRepository;
import com.nhnacademy.back.product.product.service.ProductService;
import com.nhnacademy.back.product.publisher.domain.dto.response.ResponsePublisherDTO;
import com.nhnacademy.back.product.publisher.domain.entity.Publisher;
import com.nhnacademy.back.product.publisher.exception.PublisherNotFoundException;
import com.nhnacademy.back.product.publisher.repository.PublisherJpaRepository;
import com.nhnacademy.back.product.state.domain.dto.response.ResponseProductStateDTO;
import com.nhnacademy.back.product.state.domain.entity.ProductState;
import com.nhnacademy.back.product.state.domain.entity.ProductStateName;
import com.nhnacademy.back.product.state.exception.ProductStateNotFoundException;
import com.nhnacademy.back.product.state.repository.ProductStateJpaRepository;
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
	private final CategoryJpaRepository categoryJpaRepository;

	private final ProductSearchService productSearchService;

	private final MinioUtils minioUtils;
	private final String BUCKET_NAME = "e2bs-products-image";

	/**
	 * 도서를 DB에 저장
	 * 도서가 이미 존재하면 Exception 발생
	 */
	@Override
	@Transactional
	public void createProduct(RequestProductDTO request) {
		// 이미 존재하는지 unique인 isbn으로 DB에서 조회
		if (productJpaRepository.existsByProductIsbn(request.getProductIsbn())) {
			throw new ProductAlreadyExistsException("Product already exists");
		}

		//상태 저장
		ProductState productState = productStateJpaRepository.findByProductStateId(request.getProductStateId());
		Integer productStock = request.getProductStock();
		ProductState stateSale = productStateJpaRepository.findByProductStateName(ProductStateName.SALE);

		if (productStock == 0 && productState == stateSale) {
			productState = productStateJpaRepository.findByProductStateName(ProductStateName.OUT);
		}

		Publisher publisher = publisherJpaRepository.findById(request.getPublisherId())
			.orElseThrow(() -> new PublisherNotFoundException("출판사 조회 실패"));

		List<MultipartFile> productImageFiles = request.getProductImage();
		List<Long> tagIds = request.getTagIds();
		List<Long> contributorIds = request.getContributorIds();

		// product 객체 생성 및 DB에 Product 저장
		Product product = productJpaRepository.save(Product.createProductEntity(request, productState, publisher));

		List<String> tagNames = new ArrayList<>();
		List<String> contributorNames = new ArrayList<>();

		// 이미지 저장
		// 자식 추가 (ProductImage)
		for (MultipartFile productImageFile : productImageFiles) {
			String imagePath = "";
			if (Objects.nonNull(productImageFile) && !productImageFile.isEmpty()) {
				imagePath = uploadFile(productImageFile);
			}
			ProductImage productImage = new ProductImage(product, imagePath);
			product.getProductImage().add(productImage);
			productImageJpaRepository.save(productImage);
		}

		// 태그 저장
		if (!Objects.isNull(request.getTagIds())) {
			for (Long tagId : tagIds) {
				Tag tag = tagJpaRepository.findById(tagId)
					.orElseThrow(() -> new TagNotFoundException("태그 조회 실패"));
				tagNames.add(tag.getTagName());
				productTagJpaRepository.save(new ProductTag(product, tag));
			}
		}

		// 기여자 저장
		for (Long contributorId : contributorIds) {
			Contributor contributor = contributorJpaRepository.findById(contributorId)
				.orElseThrow(() -> new ContributorNotFoundException("기여자 조회 실패"));
			contributorNames.add(contributor.getContributorName());
			productContributorJpaRepository.save(new ProductContributor(product, contributor));
		}

		// 카테고리 저장
		createProductCategory(product.getProductId(), request.getCategoryIds(), false);

		List<Long> categoryIds = productCategoryJpaRepository.findCategoryIdsByProductId(product.getProductId());

		// 엘라스틱 서치에 저장
		productSearchService.createProductDocument(new RequestProductDocumentDTO(
			product.getProductId(), product.getProductTitle(), product.getProductDescription(),
			product.getPublisher().getPublisherName(), product.getProductPublishedAt(), product.getProductSalePrice(),
			tagNames, contributorNames, categoryIds));
	}

	/**
	 * 도서 한 권을 DB에서 조회
	 */
	@Override
	public ResponseProductReadDTO getProduct(long productId) {
		Product product = productJpaRepository.findById(productId)
			.orElseThrow(ProductNotFoundException::new);

		// 엘라스틱 서치에서 조회수 update
		productSearchService.updateProductDocumentHits(productId);

		return getProductByChangedImagePath(product);
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

		return productPage.map(this::getProductByChangedImagePath);
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
			.map(this::getProductByChangedImagePath).toList();
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

		List<MultipartFile> productImageFiles = request.getProductImage();
		List<Long> tagIds = request.getTagIds();
		List<Long> contributorIds = request.getContributorIds();

		List<String> tagNames = new ArrayList<>();
		List<String> contributorNames = new ArrayList<>();

		// 이미지 삭제 후 저장
		// 자식 추가 (ProductImage)
		// - 이미지가 들어왔다면
		if (Objects.nonNull(productImageFiles) && !Objects.requireNonNull(
			productImageFiles.getFirst().getOriginalFilename()).isBlank()) {
			// - 기존 리스트 조회
			List<ProductImage> productImages = productImageJpaRepository.getAllByProduct_ProductId(productId);

			// - miniO 기존 파일 삭제
			if (!product.getProductImage().getFirst().getProductImagePath().startsWith("http")) {
				for (ProductImage productImage : product.getProductImage()) {
					minioUtils.deleteObject(BUCKET_NAME, productImage.getProductImagePath());
				}
			}

			// - DB에서 삭제
			productImageJpaRepository.deleteAll(productImages);

			List<ProductImage> newProductImages = new ArrayList<>();
			for (MultipartFile productImageFile : productImageFiles) {
				String originalFilename = productImageFile.getOriginalFilename();
				String objectName = UUID.randomUUID() + "_" + originalFilename;

				// MinIO에 파일 업로드
				minioUtils.uploadObject(BUCKET_NAME, objectName, productImageFile);

				// 새로운 ProductImage 엔티티 생성
				ProductImage productImage = new ProductImage(product, objectName);
				newProductImages.add(productImage);
			}
			// Product의 productImage 리스트 갱신
			product.getProductImage().clear(); // 기존 리스트 비우기
			product.getProductImage().addAll(newProductImages);
			productImageJpaRepository.saveAll(newProductImages); // 새로운 이미지 저장
		}

		// 상품 정보 업데이트 (수정)
		product.updateProduct(request, productState, publisher);

		// 태그 삭제 후 저장
		if (!Objects.isNull(request.getTagIds())) {
			productTagJpaRepository.deleteByProduct_ProductId(productId);
			for (Long tagId : tagIds) {
				Tag tag = tagJpaRepository.findById(tagId)
					.orElseThrow(() -> new TagNotFoundException("태그 조회 실패"));
				tagNames.add(tag.getTagName());
				productTagJpaRepository.save(new ProductTag(product, tag));
			}
		}

		// 기여자 삭제 후 저장
		productContributorJpaRepository.deleteByProduct_ProductId(productId);
		for (Long contributorId : contributorIds) {
			Contributor contributor = contributorJpaRepository.findById(contributorId)
				.orElseThrow(() -> new ContributorNotFoundException("기여자 조회 실패"));
			contributorNames.add(contributor.getContributorName());
			productContributorJpaRepository.save(new ProductContributor(product, contributor));
		}

		// 카테고리 삭제 후 저장
		createProductCategory(productId, request.getCategoryIds(), true);

		List<Long> categoryIds = productCategoryJpaRepository.findCategoryIdsByProductId(product.getProductId());

		// 엘라스틱 서치에서 수정
		productSearchService.updateProductDocument(new RequestProductDocumentDTO(
			productId, product.getProductTitle(), product.getProductDescription(),
			product.getPublisher().getPublisherName(), product.getProductPublishedAt(),
			product.getProductSalePrice(), tagNames, contributorNames, categoryIds));

		productJpaRepository.save(product);
	}

	/**
	 * 도서 재고 수정
	 */
	@Override
	@Transactional
	public synchronized void updateProductStock(long productId, RequestProductStockUpdateDTO request) {
		Product product = productJpaRepository.findById(productId)
			.orElseThrow(ProductNotFoundException::new);

		int stock = product.getProductStock() + request.getProductStock();
		if (stock < 0) {
			throw new ProductStockDecrementException("재고 수량 변경 불가");
		}

		if (stock == 0) {
			product.setState(productStateJpaRepository.findByProductStateName(ProductStateName.OUT));
		}

		if (stock > 0) {
			product.setState(productStateJpaRepository.findByProductStateName(ProductStateName.SALE));
		}

		product.setStock(stock);
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

		product.setSalePrice(request.getProductSalePrice());
		productJpaRepository.save(product);

		// 엘라스틱 서치에서 수정
		productSearchService.updateProductSalePrice(productId, request.getProductSalePrice());
	}

	/**
	 * 엘라스틱 서치의 결과로 나온 Product ID 리스트로 도서 조회
	 */
	@Override
	public Page<ResponseProductReadDTO> getProductsToElasticSearch(Page<Long> productIds) {
		List<Long> idOrder = productIds.getContent();
		List<Product> unorderedProducts = productJpaRepository.findAllById(idOrder);
		List<ResponseProductReadDTO> responseProductReadDTOS = new ArrayList<>();
		// 한 권이라도 존재하지 않으면 예외 발생
		if (unorderedProducts.size() != idOrder.size()) {
			throw new ProductNotFoundException();
		}

		for (Product result : unorderedProducts) {
			getProductByChangedImagePath(result);
			responseProductReadDTOS.add(getProductByChangedImagePath(result));
		}

		return new PageImpl<>(responseProductReadDTOS, productIds.getPageable(), productIds.getTotalElements());
	}

	/**
	 * 엘라스틱 서치 - 메인 페이지 도서 조회 (베스트, 신상)
	 */
	@Override
	public List<ResponseMainPageProductDTO> getProductsToMain(List<Long> productIds) {
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

				String imagePath = product.getProductImage().getFirst().getProductImagePath();
				if (!imagePath.startsWith("http")) {
					imagePath = minioUtils.getPresignedUrl(BUCKET_NAME, imagePath);
				}

				return new ResponseMainPageProductDTO(
					product.getProductId(),
					product.getProductTitle(),
					contributorName,
					imagePath,
					product.getProductRegularPrice(),
					product.getProductSalePrice(),
					product.getProductDescription(),
					product.getPublisher().getPublisherName()
				);
			})
			.toList();
	}

	/**
	 * 카테고리 - 도서 관계 테이블 저장 로직
	 */
	private void createProductCategory(long productId, List<Long> categoryIds, boolean isUpdate) {
		// 저장하려는 카테고리의 개수가 10개 초과 또는 0개 이하인 경우 예외 발생
		if (categoryIds.size() > 10 || categoryIds.isEmpty()) {
			throw new ProductCategoryCreateNotAllowException();
		}

		if (isUpdate) {
			productCategoryJpaRepository.deleteAllByProductId(productId);
		}

		Set<Long> uniqueCategoryIds = new HashSet<>();

		for (Long categoryId : categoryIds) {
			Category category = categoryJpaRepository.findById(categoryId)
				.orElseThrow(CategoryNotFoundException::new);

			while (category != null) {
				uniqueCategoryIds.add(category.getCategoryId());
				category = category.getParent();
			}
		}

		Product product = productJpaRepository.findById(productId)
			.orElseThrow(ProductNotFoundException::new);

		for (Long id : uniqueCategoryIds) {
			Category category = categoryJpaRepository.findById(id)
				.orElseThrow(CategoryNotFoundException::new);
			productCategoryJpaRepository.save(new ProductCategory(product, category));
		}
	}

	/**
	 * 파일 업로드 메소드
	 */
	private String uploadFile(MultipartFile productImageFile) {
		String originalFilename = productImageFile.getOriginalFilename();
		UUID uuid = UUID.randomUUID();
		String objectName = uuid + "_" + originalFilename;
		minioUtils.uploadObject(BUCKET_NAME, objectName, productImageFile);
		return objectName;
	}

	/**
	 * 이미지 경로 가공 메소드
	 */
	private ResponseProductReadDTO getProductByChangedImagePath(Product product) {
		String productImagePath = "";
		List<String> productImagePaths = productImageJpaRepository.findAllByProduct_ProductId(product.getProductId());
		List<ResponseProductImageDTO> changedResponseProductImageDTOs = new ArrayList<>();

		// 알라딘 api 이미지인 경우
		ProductImage firstProductImage = product.getProductImage().getFirst();
		if (firstProductImage.getProductImagePath().startsWith("http")) {
			changedResponseProductImageDTOs.add(new ResponseProductImageDTO(firstProductImage.getProductImageId(),
				firstProductImage.getProductImagePath()));
		} else {
			for (String productImage : productImagePaths) {
				if (!StringUtils.isEmpty(productImage)) {
					productImagePath = minioUtils.getPresignedUrl(BUCKET_NAME, productImage);
					ResponseProductImageDTO responseProductImageDTO = new ResponseProductImageDTO(
						productImageJpaRepository.findByProductImagePath(productImage), productImagePath);
					changedResponseProductImageDTOs.add(responseProductImageDTO);
				}
			}
		}

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
			changedResponseProductImageDTOs,
			productTagJpaRepository.findTagDTOsByProductId(product.getProductId()),
			productCategoryJpaRepository.findCategoryDTOsByProductId(product.getProductId()),
			productContributorJpaRepository.findContributorDTOsByProductId(product.getProductId()
			)
		);
	}

}
