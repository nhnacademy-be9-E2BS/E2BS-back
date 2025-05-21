package com.nhnacademy.back.product.product.park.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.nhnacademy.back.product.image.domain.entity.ProductImage;
import com.nhnacademy.back.product.image.repository.ProductImageJpaRepository;
import com.nhnacademy.back.product.product.domain.dto.request.RequestProductApiGetDTO;
import com.nhnacademy.back.product.product.domain.dto.request.RequestProductCreateApiDTO;
import com.nhnacademy.back.product.product.domain.dto.request.RequestProductCreateDTO;
import com.nhnacademy.back.product.product.domain.dto.response.ResponseProductsApiGetDTO;
import com.nhnacademy.back.product.product.domain.entity.Product;
import com.nhnacademy.back.product.product.exception.ProductAlreadyExistsException;
import com.nhnacademy.back.product.product.exception.SearchBookException;
import com.nhnacademy.back.product.product.park.API.AladdinOpenAPI;
import com.nhnacademy.back.product.product.park.API.Item;
import com.nhnacademy.back.product.product.park.service.ProductService;
import com.nhnacademy.back.product.product.repository.ProductJpaRepository;
import com.nhnacademy.back.product.publisher.domain.dto.request.RequestPublisherDTO;
import com.nhnacademy.back.product.publisher.domain.entity.Publisher;
import com.nhnacademy.back.product.publisher.repository.PublisherJpaRepository;
import com.nhnacademy.back.product.publisher.service.PublisherService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

	private final ProductJpaRepository productJpaRepository;
	private final PublisherJpaRepository publisherJpaRepository;
	private final ProductImageJpaRepository productImageJpaRepository;
	private final PublisherService publisherService;


	/**
	 * 검색결과에 맞는 책 목록들 가져오기
	 */
	@Override
	public List<ResponseProductsApiGetDTO> getProducts(RequestProductApiGetDTO request) {
		AladdinOpenAPI api = new AladdinOpenAPI(request.getQuery(), request.getQueryType()); //검색어, 검색대상 입력해서 해당하는 책들 가져오기
		List<Item> items;

		try {
			items = api.searchBooks(); //알라딘 api는 결과를 item으로 넘겨줌
		} catch (Exception e) {
			throw new SearchBookException("Search book failed");
		}

		List<Product> products = new ArrayList<>(); //items들을 product 리스트에 담음
		for (Item item : items) {
			RequestProductCreateApiDTO createApiDTO = new RequestProductCreateApiDTO();
			createApiDTO.setPublisherName(item.publisher);
			createApiDTO.setProductTitle(item.Title);
			createApiDTO.setProductDescription(item.description);
			createApiDTO.setProductIsbn(item.isbn13);
			createApiDTO.setProductRegularPrice(item.priceStandard);
			createApiDTO.setProductSalePrice(item.priceSales);
			createApiDTO.setProductImage(item.Link);

			Publisher publisher = publisherJpaRepository.findByPublisherName(createApiDTO.getPublisherName());

			if (publisher == null) {
				publisher = new Publisher(item.publisher);
			}
			Product product = Product.createProductApiEntity(createApiDTO, publisher);

			products.add(product);
		}

		List<ResponseProductsApiGetDTO> responseList = products.stream()
			.map(ResponseProductsApiGetDTO::from)
			.collect(Collectors.toList());

		return responseList;
	}


	//검색결과에 해당하는 책 목록들 중에서 선택후 등록버튼 누르면 RequestProdcutCreateDTO에 현재 리스트에 해당하는 product의 정보기져와서 등록하기
	@Override
	public void createProduct(RequestProductCreateDTO request) {
		Publisher publisher = publisherJpaRepository.findByPublisherName(request.getPublisherName());

		if (publisher == null) {
			RequestPublisherDTO requestPublisherDTO = new RequestPublisherDTO(request.getPublisherName());
			publisherService.createPublisher(requestPublisherDTO);
		}

		List<String> imagePaths = request.getProductImagePaths();

		if (productJpaRepository.existsByProductIsbn(request.getProductIsbn())) {
			throw new ProductAlreadyExistsException("Product already exists: %s".formatted(request.getProductIsbn()));
		}

		Product product = Product.createProductEntity(request, publisher);

		productJpaRepository.save(product);


		for (String imagePath : imagePaths) {
			productImageJpaRepository.save(new ProductImage(product, imagePath));
		}
	}
}