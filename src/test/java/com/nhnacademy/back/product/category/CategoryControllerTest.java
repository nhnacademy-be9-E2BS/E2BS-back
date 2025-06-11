package com.nhnacademy.back.product.category;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.back.product.category.controller.CategoryController;
import com.nhnacademy.back.product.category.domain.dto.request.RequestCategoryDTO;
import com.nhnacademy.back.product.category.domain.dto.response.ResponseCategoryDTO;
import com.nhnacademy.back.product.category.domain.dto.response.ResponseCategoryIdsDTO;
import com.nhnacademy.back.product.category.service.CategoryService;
import com.nhnacademy.back.product.category.service.ProductCategoryService;

@WebMvcTest(controllers = CategoryController.class)
class CategoryControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@MockitoBean
	private CategoryService categoryService;
	@MockitoBean
	private ProductCategoryService productCategoryService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@DisplayName("User - header category 리스트 조회")
	void get_categories_to_depth_3_test() throws Exception {
		// given
		List<ResponseCategoryDTO> dummyResponse = List.of(
			new ResponseCategoryDTO(1L, "Root", List.of(
				new ResponseCategoryDTO(2L, "Child", List.of())
			))
		);

		when(categoryService.getCategoriesToDepth3()).thenReturn(dummyResponse);

		// when & then
		mockMvc.perform(get("/api/categories"))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$[0].categoryId").value(1))
			.andExpect(jsonPath("$[0].categoryName").value("Root"))
			.andExpect(jsonPath("$[0].children[0].categoryName").value("Child"));

		verify(categoryService, times(1)).getCategoriesToDepth3();
	}

	@Test
	@DisplayName("모든 category 리스트 조회")
	void get_categories_test() throws Exception {
		// given
		List<ResponseCategoryDTO> dummyResponse = List.of(
			new ResponseCategoryDTO(1L, "Admin Root", List.of())
		);

		when(categoryService.getCategories()).thenReturn(dummyResponse);

		// when & then
		mockMvc.perform(get("/api/categories/all"))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$[0].categoryName").value("Admin Root"));

		verify(categoryService, times(1)).getCategories();
	}

	@Test
	@DisplayName("product id 리스트로 각 product의 category id 리스트 조회")
	void get_categories_by_product_id_test() throws Exception {
		// given
		List<ResponseCategoryIdsDTO> response = List.of(
			new ResponseCategoryIdsDTO(1L, List.of(1L, 2L)),
			new ResponseCategoryIdsDTO(2L, List.of(3L, 4L))
		);
		when(productCategoryService.getCategoriesByProductId(List.of(1L, 2L))).thenReturn(response);

		// when & then
		mockMvc.perform(get("/api/categories/productIds")
				.param("productIds", "1", "2")
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$[0].productId").value(1))
			.andExpect(jsonPath("$[0].categoryIds[0]").value(1))
			.andExpect(jsonPath("$[0].categoryIds[1]").value(2))
			.andExpect(jsonPath("$[1].productId").value(2))
			.andExpect(jsonPath("$[1].categoryIds[0]").value(3))
			.andExpect(jsonPath("$[1].categoryIds[1]").value(4));
	}

	@Test
	@DisplayName("Admin - 최상위 + 하위 카테고리 저장")
	void create_category_tree_test() throws Exception {
		// given
		RequestCategoryDTO requestParent = new RequestCategoryDTO("parent category");
		RequestCategoryDTO requestChild = new RequestCategoryDTO("child category");
		List<RequestCategoryDTO> requests = List.of(requestParent, requestChild);
		String jsonRequest = objectMapper.writeValueAsString(requests);

		// when & then
		mockMvc.perform(post("/api/auth/admin/categories")
				.content(jsonRequest)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated());
	}

	@Test
	@DisplayName("Admin - 자식 카테고리만 저장")
	void create_child_category_test() throws Exception {
		// given
		RequestCategoryDTO request = new RequestCategoryDTO("child category");
		String jsonRequest = objectMapper.writeValueAsString(request);

		// when & then
		mockMvc.perform(post("/api/auth/admin/categories/1")
				.content(jsonRequest)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated());
	}

	@Test
	@DisplayName("Admin - 카테고리 수정")
	void update_category_test() throws Exception {
		// given
		RequestCategoryDTO request = new RequestCategoryDTO("new name category");
		String jsonRequest = objectMapper.writeValueAsString(request);

		// when & then
		mockMvc.perform(put("/api/auth/admin/categories/1")
				.content(jsonRequest)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("Admin - 카테고리 삭제")
	void delete_category_test() throws Exception {
		// when & then
		mockMvc.perform(delete("/api/auth/admin/categories/1"))
			.andExpect(status().isOk());
	}
}
