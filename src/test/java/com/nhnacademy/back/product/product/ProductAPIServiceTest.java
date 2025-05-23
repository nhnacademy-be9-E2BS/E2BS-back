//
// package com.nhnacademy.back.product.product;
//
// import static org.assertj.core.api.Assertions.*;
// import static org.mockito.Mockito.*;
//
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
//
// import com.nhnacademy.back.product.contributor.domain.entity.Position;
// import com.nhnacademy.back.product.contributor.domain.entity.ProductContributor;
// import com.nhnacademy.back.product.contributor.repository.ContributorJpaRepository;
// import com.nhnacademy.back.product.contributor.repository.PositionJpaRepository;
// import com.nhnacademy.back.product.contributor.repository.ProductContributorJpaRepository;
// import com.nhnacademy.back.product.image.repository.ProductImageJpaRepository;
// import com.nhnacademy.back.product.product.domain.dto.request.RequestProductApiCreateDTO;
// import com.nhnacademy.back.product.product.domain.entity.Product;
// import com.nhnacademy.back.product.product.exception.ProductAlreadyExistsException;
// import com.nhnacademy.back.product.product.park.service.impl.ProductAPIServiceImpl;
// import com.nhnacademy.back.product.product.repository.ProductJpaRepository;
// import com.nhnacademy.back.product.publisher.domain.entity.Publisher;
// import com.nhnacademy.back.product.publisher.repository.PublisherJpaRepository;
// import com.nhnacademy.back.product.publisher.service.PublisherService;
// import com.nhnacademy.back.product.state.domain.entity.ProductState;
// import com.nhnacademy.back.product.state.domain.entity.ProductStateName;
// import com.nhnacademy.back.product.state.repository.ProductStateJpaRepository;
//
// @ExtendWith(MockitoExtension.class)
// class ProductAPIServiceImplTest {
//
// 	@InjectMocks
// 	private ProductAPIServiceImpl productAPIService;
//
// 	@Mock
// 	private ProductJpaRepository productJpaRepository;
//
// 	@Mock
// 	private PublisherJpaRepository publisherJpaRepository;
//
// 	@Mock
// 	private ProductImageJpaRepository productImageJpaRepository;
//
// 	@Mock
// 	private ContributorJpaRepository contributorJpaRepository;
//
// 	@Mock
// 	private PositionJpaRepository positionJpaRepository;
//
// 	@Mock
// 	private PublisherService publisherService;
//
// 	@Mock
// 	private ProductContributorJpaRepository productContributorJpaRepository;
//
// 	@Mock
// 	private ProductStateJpaRepository productStateJpaRepository;
//
// 	@Test
// 	@DisplayName("도서 API 생성 - 성공")
// 	void createProduct_success() {
// 		RequestProductApiCreateDTO request = new RequestProductApiCreateDTO(
// 			"Test Publisher",
// 			"Test Book",
// 			"1234567890123",
// 			"http://image.com/image.jpg",
// 			"Test description",
// 			20000L,
// 			18000L,
// 			"홍길동(지은이), 김철수(감수)",
// 			"목차 예시",
// 			false,
// 			10
// 		);
//
// 		Publisher publisher = new Publisher("Test Publisher");
// 		ProductState state = new ProductState(ProductStateName.SALE);
//
// 		when(publisherJpaRepository.findByPublisherName("Test Publisher")).thenReturn(publisher);
// 		when(productJpaRepository.existsByProductIsbn("1234567890123")).thenReturn(false);
// 		when(productStateJpaRepository.findByProductStateName(ProductStateName.SALE)).thenReturn(state);
// 		when(positionJpaRepository.existsByPositionName(any())).thenReturn(false);
// 		when(positionJpaRepository.findPositionByPositionName(any())).thenReturn(new Position("지은이"));
//
//
// 		productAPIService.createProduct(request);
// 		verify(productJpaRepository, times(1)).save(any(Product.class));
// 		verify(productContributorJpaRepository, atLeastOnce()).save(any(ProductContributor.class));
// 	}
//
// 	@Test
// 	@DisplayName("도서 API 생성 - 실패 (이미 존재)")
// 	void createProduct_failAlreadyExists() {
// 		RequestProductApiCreateDTO request = new RequestProductApiCreateDTO(
// 			"Test Publisher",
// 			"Test Book",
// 			"1234567890123",
// 			"http://image.com/image.jpg",
// 			"Test description",
// 			20000L,
// 			18000L,
// 			"홍길동(지은이)",
// 			"목차 예시",
// 			false,
// 			10
// 		);
//
// 		when(productJpaRepository.existsByProductIsbn("1234567890123")).thenReturn(true);
//
//
// 		assertThatThrownBy(() -> productAPIService.createProduct(request))
// 			.isInstanceOf(ProductAlreadyExistsException.class);
// 	}
// }
