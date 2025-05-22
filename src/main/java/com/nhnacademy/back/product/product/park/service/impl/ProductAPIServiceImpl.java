package com.nhnacademy.back.product.product.park.service.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.nhnacademy.back.product.contributor.domain.entity.Contributor;
import com.nhnacademy.back.product.contributor.domain.entity.Position;
import com.nhnacademy.back.product.contributor.repository.ContributorJpaRepository;
import com.nhnacademy.back.product.contributor.repository.PositionJpaRepository;
import com.nhnacademy.back.product.image.repository.ProductImageJpaRepository;
import com.nhnacademy.back.product.product.domain.dto.request.RequestProductApiCreateDTO;
import com.nhnacademy.back.product.product.domain.dto.request.RequestProductApiSearchDTO;
import com.nhnacademy.back.product.product.domain.dto.response.ResponseProductsApiSearchDTO;
import com.nhnacademy.back.product.product.domain.entity.Product;
import com.nhnacademy.back.product.product.exception.ProductAlreadyExistsException;
import com.nhnacademy.back.product.product.exception.SearchBookException;
import com.nhnacademy.back.product.product.park.API.AladdinOpenAPI;
import com.nhnacademy.back.product.product.park.API.Item;
import com.nhnacademy.back.product.product.park.service.ProductAPIService;
import com.nhnacademy.back.product.product.repository.ProductJpaRepository;
import com.nhnacademy.back.product.publisher.domain.dto.request.RequestPublisherDTO;
import com.nhnacademy.back.product.publisher.domain.entity.Publisher;
import com.nhnacademy.back.product.publisher.repository.PublisherJpaRepository;
import com.nhnacademy.back.product.publisher.service.PublisherService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductAPIServiceImpl implements ProductAPIService {

	private final ProductJpaRepository productJpaRepository;
	private final PublisherJpaRepository publisherJpaRepository;
	private final ProductImageJpaRepository productImageJpaRepository;
	private final ContributorJpaRepository contributorJpaRepository;
	private final PositionJpaRepository positionJpaRepository;
	private final PublisherService publisherService;


	/**
	 * 검색결과에 맞는 책 목록들 가져오기
	 */
	public Page<ResponseProductsApiSearchDTO> searchProducts(RequestProductApiSearchDTO request, Pageable pageable) {
		AladdinOpenAPI api = new AladdinOpenAPI(request.getQuery(), request.getQueryType());
		List<Item> items;

		try {
			items = api.searchBooks();
		} catch (Exception e) {
			throw new SearchBookException("Search book failed");
		}

		List<ResponseProductsApiSearchDTO> responseList = new ArrayList<>();


		for (Item item : items) {
			ResponseProductsApiSearchDTO responseProductsApiSearchDTO = new ResponseProductsApiSearchDTO();
			responseProductsApiSearchDTO.setPublisherName(item.publisher);
			responseProductsApiSearchDTO.setProductTitle(item.Title);
			responseProductsApiSearchDTO.setProductDescription(item.description);
			responseProductsApiSearchDTO.setProductIsbn(item.isbn13);
			responseProductsApiSearchDTO.setProductRegularPrice(item.priceStandard);
			responseProductsApiSearchDTO.setProductSalePrice(item.priceSales);
			responseProductsApiSearchDTO.setProductImage(item.cover);
			responseProductsApiSearchDTO.setContributors(item.author);

			responseList.add(responseProductsApiSearchDTO);
		}

		int start = (int) pageable.getOffset();
		int end = Math.min(start + pageable.getPageSize(), responseList.size());

		if (start > end) {
			start = end = 0;
		}

		List<ResponseProductsApiSearchDTO> pagedList = responseList.subList(start, end);

		return new PageImpl<>(pagedList, pageable, responseList.size());
	}


	@Override
	public void createProduct(RequestProductApiCreateDTO request) {
		Publisher publisher = publisherJpaRepository.findByPublisherName(request.getPublisherName());


		if (publisher == null) {
			RequestPublisherDTO requestPublisherDTO = new RequestPublisherDTO(request.getPublisherName());
			publisherService.createPublisher(requestPublisherDTO);
			publisher = publisherJpaRepository.findByPublisherName(request.getPublisherName());
		}

		if (productJpaRepository.existsByProductIsbn(request.getProductIsbn())) {
			throw new ProductAlreadyExistsException("Product already exists: %s".formatted(request.getProductIsbn()));
		}


		Map<String, String> map = parse(request.getContributors());
		for (Map.Entry<String, String> entry : map.entrySet()) {
			String contributorName = entry.getKey();
			String positionName = entry.getValue();

			if (!positionJpaRepository.existsByPositionName(positionName)) {
				positionJpaRepository.save(new Position(positionName));
			}
			Position position = positionJpaRepository.findPositionByPositionName(positionName);
			contributorJpaRepository.save(new Contributor(contributorName, position));
		}
		Product product = Product.createProductApiEntity(request, publisher);

		productJpaRepository.save(product);


	}


	private Map<String, String> parse(String contributors) {
		String[] contributorArr = contributors.split(",");
		String contributorName = "";
		String positionName = "";
		Map<String, String> result = new LinkedHashMap<>();



		for (String contributor : contributorArr) {
			contributor = contributor.trim();
			if (contributor.contains("(") && contributor.contains(")")) {
				contributorName = contributor.substring(0, contributor.indexOf("("));
				positionName = contributor.substring(contributor.indexOf("(") + 1, contributor.indexOf(")"));
				result.put(contributorName, positionName);
			} else {
				contributorName = contributor;
				positionName = "";
				result.put(contributorName, positionName);

			}
		}
		return result;
	}
}