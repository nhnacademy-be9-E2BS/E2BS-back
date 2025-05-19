package com.nhnacademy.back.product.product.kim.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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
import com.nhnacademy.back.product.publisher.domain.entity.Publisher;
import com.nhnacademy.back.product.publisher.repository.PublisherJpaRepository;
import com.nhnacademy.back.product.state.domain.entity.ProductState;
import com.nhnacademy.back.product.state.domain.entity.ProductStateName;
import com.nhnacademy.back.product.state.repository.ProductStateJpaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
	private final ProductJpaRepository productJpaRepository;
	private final ProductImageJpaRepository productImageJpaRepository;
	private final PublisherJpaRepository publisherJpaRepository;
	private final ProductStateJpaRepository productStateJpaRepository;

	/**
	 * 도서를 DB에 저장
	 * 도서가 이미 존재하면 Exception 발생
	 */
	@Override
	public void createProduct(RequestProductCreateDTO request) {
		// (현규) front에서 출판사 리스트를 선택하게 해서 없으면 생성하게 만들게 할 것
		// 인스턴스 빼서 변수 선언
		Publisher publisher = publisherJpaRepository.findByPublisherName(request.getPublisherName());
		List<String> imagePaths = request.getProductImage();

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

	}

	/**
	 * 도서 한 권을 DB에서 조회
	 */
	@Override
	public ResponseProductReadDTO getProduct(RequestProductGetDTO request) {
		// RequestProductGetDTO가 productId를 포함하도록 업데이트될 것이라 가정
		Product product = productJpaRepository.findById(request.getProductId())
			.orElseThrow(ProductNotFoundException::new);


		return new ResponseProductReadDTO(
			product.getProductId(),
			product.getProductState().getProductStateId(),
			product.getPublisher().getPublisherId(),
			product.getProductTitle(),
			product.getProductContent(),
			product.getProductDescription(),
			product.getProductPublishedAt(),
			product.getProductIsbn(),
			product.getProductRegularPrice(),
			product.isProductPackageable(),
			product.getProductStock(),
			//내부 메서드 사용
			findProductImagePaths(product)
		);
	}

	/**
	 * 도서전체 목록을 페이지 단위로 조회
	 */
	@Override
	public Page<ResponseProductReadDTO> getProducts(Pageable pageable) {
		return productJpaRepository.findAll(pageable).map(product -> new ResponseProductReadDTO(
			product.getProductId(),
			product.getProductState().getProductStateId(),
			product.getPublisher().getPublisherId(),
			product.getProductTitle(),
			product.getProductContent(),
			product.getProductDescription(),
			product.getProductPublishedAt(),
			product.getProductIsbn(),
			product.getProductRegularPrice(),
			product.isProductPackageable(),
			product.getProductStock(),
			//내부 메서드 사용
			findProductImagePaths(product)
		));
	}

	/**
	 * 특정 도서 ID 목록으로 도서 조회
	 */
	@Override
	public ResponseEntity<List<ResponseProductReadDTO>> getProducts(List<Long> products) {
		List<ResponseProductReadDTO> list = productJpaRepository.findAllById(products).stream().map(product -> new ResponseProductReadDTO(
			product.getProductId(),
			product.getProductState().getProductStateId(),
			product.getPublisher().getPublisherId(),
			product.getProductTitle(),
			product.getProductContent(),
			product.getProductDescription(),
			product.getProductPublishedAt(),
			product.getProductIsbn(),
			product.getProductRegularPrice(),
			product.isProductPackageable(),
			product.getProductStock(),
			//내부 메서드 사용
			findProductImagePaths(product)
		)).collect(Collectors.toList());
		return ResponseEntity.ok(list);
	}

	/**
	 * 도서 정보 수정
	 */
	@Override
	public void updateProduct(RequestProductUpdateDTO request) {
		Product product = productJpaRepository.findById(request.getProductId())
			.orElseThrow(() -> new IllegalArgumentException("도서를 찾을 수 없습니다."));
		Publisher publisher = publisherJpaRepository.findById(request.getPublisherId())
			.orElseThrow(() -> new IllegalArgumentException("출판사를 찾을 수 없습니다."));
		ProductState productState = productStateJpaRepository.findById(request.getProductStateId())
			.orElseThrow(() -> new IllegalArgumentException("도서 상태를 찾을 수 없습니다."));


		product.updateProduct(request, publisher, productState);


		List<String> imagePaths = request.getProductImagePaths();
		// (현규) 사진 수정 시 전부 삭제하고 다시 저장, 뷰에서 가진 이미지 전부 보여주고 삭제 여부 넣기
		for (String imagePath : imagePaths) {
			productImageJpaRepository.save(new ProductImage(product, imagePath));
		}
		productJpaRepository.save(product);
	}

	/**
	 * 도서 재고 수정
	 */
	@Override
	public ResponseEntity<Void> updateProductStock(RequestProductStockUpdateDTO request) {
		Product product = productJpaRepository.findById(request.getProductId())
			.orElseThrow(() -> new IllegalArgumentException("도서를 찾을 수 없습니다."));
		//내부 메서드 사용
		decrementProductStock(request.getProductDecrementStock(), product);
		return ResponseEntity.status(HttpStatus.OK).build();

	}

	/**
	 * 도서 판매가 수정
	 */
	@Override
	public void updateProductSalePrice(RequestProductSalePriceUpdateDTO request) {
		Product product = productJpaRepository.findById(request.getProductId())
			.orElseThrow(() -> new IllegalArgumentException("도서를 찾을 수 없습니다."));

		product.setProduct(request.getProductSalePrice());
		productJpaRepository.save(product);
	}


	/**
	 * 쿠폰 적용 가능한 도서 목록 조회 (재고 있고 판매 상태인 도서)
	 */
	@Override
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

		List<String> productImagePaths = new ArrayList<>();

		for (ProductImage productImage : productImages) {
			productImagePaths.add(productImage.getProductImagePath());
		}
		return productImagePaths;


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
