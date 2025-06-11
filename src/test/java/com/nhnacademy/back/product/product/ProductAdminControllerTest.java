package com.nhnacademy.back.product.product;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.back.product.category.domain.dto.response.ResponseCategoryDTO;
import com.nhnacademy.back.product.product.controller.ProductAdminController;
import com.nhnacademy.back.product.product.domain.dto.request.RequestProductMetaDTO;
import com.nhnacademy.back.product.product.domain.dto.request.RequestProductSalePriceUpdateDTO;
import com.nhnacademy.back.product.product.domain.dto.response.ResponseProductReadDTO;
import com.nhnacademy.back.product.product.service.ProductAPIService;
import com.nhnacademy.back.product.product.service.ProductService;
import com.nhnacademy.back.product.publisher.domain.dto.response.ResponsePublisherDTO;
import com.nhnacademy.back.product.state.domain.dto.response.ResponseProductStateDTO;
import com.nhnacademy.back.product.state.domain.entity.ProductStateName;

@WebMvcTest(controllers = ProductAdminController.class)
class ProductAdminControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@MockitoBean
	private ProductService productService;
	@MockitoBean
	private ProductAPIService productAPIService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@DisplayName("도서 저장")
	void create_product_test() throws Exception {
		// given
		RequestProductMetaDTO request = new RequestProductMetaDTO(
			1L, 1L, "title", "content", "description",
			LocalDate.now(), "978-89-12345-01-1", 10000L, 8000L, true, 100,
			List.of(1L), List.of(1L), List.of(1L));
		String jsonRequest = objectMapper.writeValueAsString(request);

		MockMultipartFile jsonPart = new MockMultipartFile(
			"requestMeta",
			"requestMeta.json",
			"application/json",
			jsonRequest.getBytes(StandardCharsets.UTF_8)
		);

		MockMultipartFile mockFile1 = new MockMultipartFile("productImage", "test-image1.jpg", "image/jpeg",
			"dummy image content".getBytes());
		MockMultipartFile mockFile2 = new MockMultipartFile("productImage", "test-image2.jpg", "image/jpeg",
			"dummy image content".getBytes());

		// when & then
		mockMvc.perform(multipart("/api/auth/admin/books")
				.file(jsonPart)
				.file(mockFile1)
				.file(mockFile2)
				.contentType(MediaType.MULTIPART_FORM_DATA))
			.andExpect(status().isCreated());
	}

	@Test
	@DisplayName("전체 도서 페이징 조회")
	void get_products_test() throws Exception {
		// given
		ResponseProductStateDTO productStateDTO = new ResponseProductStateDTO(1L, ProductStateName.SALE.name());
		ResponsePublisherDTO publisherDTO = new ResponsePublisherDTO(1L, "publisher");
		ResponseCategoryDTO categoryADTO = new ResponseCategoryDTO(1L, "category A", null);
		ResponseCategoryDTO categoryBDTO = new ResponseCategoryDTO(2L, "category B", null);
		ResponseProductReadDTO responseA = new ResponseProductReadDTO(1L, productStateDTO, publisherDTO, "title A",
			"content A", "description A",
			LocalDate.now(), "978-89-12345-01-1", 10000, 8000, true, 1000, new ArrayList<>(), new ArrayList<>(),
			List.of(categoryADTO), new ArrayList<>(), 3.0, 2, false, 10);
		ResponseProductReadDTO responseB = new ResponseProductReadDTO(2L, productStateDTO, publisherDTO, "title B",
			"content B", "description B",
			LocalDate.now(), "978-89-12345-01-2", 9000, 7000, false, 500, new ArrayList<>(), new ArrayList<>(),
			List.of(categoryADTO), new ArrayList<>(), 4.3, 7, false, 10);
		ResponseProductReadDTO responseC = new ResponseProductReadDTO(3L, productStateDTO, publisherDTO, "title C",
			"content C", "description C",
			LocalDate.now(), "978-89-12345-01-3", 6000, 5000, true, 700, new ArrayList<>(), new ArrayList<>(),
			List.of(categoryBDTO), new ArrayList<>(), 4.0, 3, false, 6);
		List<ResponseProductReadDTO> dtos = List.of(responseA, responseB, responseC);

		Pageable pageable = PageRequest.of(0, 10);
		Page<ResponseProductReadDTO> products = new PageImpl<>(dtos, pageable, dtos.size());

		when(productService.getProducts(pageable, 0)).thenReturn(products);

		// when & then
		mockMvc.perform(get("/api/auth/admin/books")
				.param("page", "0")
				.param("size", "10")
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").isArray())
			.andExpect(jsonPath("$.content[0].productTitle").value("title A"))
			.andExpect(jsonPath("$.content[1].productTitle").value("title B"));
	}

	@Test
	@DisplayName("도서 수정")
	void update_product_test() throws Exception {
		// given
		Long bookId = 1L;
		RequestProductMetaDTO requestMeta = new RequestProductMetaDTO(
			1L, 1L, "title", "content", "description", LocalDate.now(),
			"978-89-12345-01-1", 10000L, 8000L, true, 200,
			List.of(1L), List.of(1L), List.of(1L));
		String jsonRequest = objectMapper.writeValueAsString(requestMeta);
		MockMultipartFile jsonPart = new MockMultipartFile("product", "product.json", "application/json",
			jsonRequest.getBytes(StandardCharsets.UTF_8));

		MockMultipartFile mockFile1 = new MockMultipartFile("productImage", "test-image1.jpg", "image/jpeg",
			"dummy image content".getBytes());
		MockMultipartFile mockFile2 = new MockMultipartFile("productImage", "test-image2.jpg", "image/jpeg",
			"dummy image content".getBytes());

		// when & then
		mockMvc.perform(multipart("/api/auth/admin/books/{bookId}", bookId)
				.file(jsonPart)
				.file(mockFile1)
				.file(mockFile2)
				.with(request -> {
					request.setMethod("PUT");
					return request;
				})
				.contentType(MediaType.MULTIPART_FORM_DATA))
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("도서 판매가 수정")
	void update_productSalePrice_test() throws Exception {
		// given
		RequestProductSalePriceUpdateDTO request = new RequestProductSalePriceUpdateDTO(3000L);
		String jsonRequest = objectMapper.writeValueAsString(request);

		// when & then
		mockMvc.perform(put("/api/auth/admin/books/1/salePrice")
				.content(jsonRequest)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
	}

}
