package com.nhnacademy.back.product.category;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.nhnacademy.back.product.category.domain.dto.response.ResponseCategoryDTO;
import com.nhnacademy.back.product.category.domain.entity.Category;
import com.nhnacademy.back.product.category.repository.CategoryJpaRepository;
import com.nhnacademy.back.product.category.service.AdminCategoryService;
import com.nhnacademy.back.product.category.service.impl.UserCategoryServiceImpl;

@ExtendWith(MockitoExtension.class)
public class UserCategoryServiceTest {
	@InjectMocks
	private UserCategoryServiceImpl userCategoryService;
	@Mock
	private CategoryJpaRepository categoryJpaRepository;
	@Mock
	private AdminCategoryService adminCategoryService;

	@Test
	@DisplayName("get categories to depth 3")
	void get_categories_to_depth_3_test() {
		// given
		Category root = new Category("Root", null);
		ReflectionTestUtils.setField(root, "categoryId", 1L);

		Category child1 = new Category("Child 1", root);
		ReflectionTestUtils.setField(child1, "categoryId", 2L);

		Category child2 = new Category("Child 2", root);
		ReflectionTestUtils.setField(child2, "categoryId", 3L);

		Category grandChild = new Category("Grandchild", child1);
		ReflectionTestUtils.setField(grandChild, "categoryId", 4L);

		root.getChildren().addAll(List.of(child1, child2));
		child1.getChildren().add(grandChild);

		when(categoryJpaRepository.findAllByParentIsNull()).thenReturn(List.of(root));

		// when
		List<ResponseCategoryDTO> result = userCategoryService.getCategoriesToDepth3();

		// then
		assertThat(result).hasSize(1);
		ResponseCategoryDTO rootDto = result.get(0);
		assertThat(rootDto.getCategoryName()).isEqualTo("Root");
		assertThat(rootDto.getChildren()).hasSize(2);

		ResponseCategoryDTO child1Dto = rootDto.getChildren().stream()
			.filter(c -> c.getCategoryName().equals("Child 1"))
			.findFirst().orElseThrow();

		assertThat(child1Dto.getChildren()).hasSize(1);
		assertThat(child1Dto.getChildren().get(0).getCategoryName()).isEqualTo("Grandchild");

		ResponseCategoryDTO grandchildDto = child1Dto.getChildren().get(0);
		assertThat(grandchildDto.getChildren()).isEmpty();

		verify(categoryJpaRepository, times(1)).findAllByParentIsNull();
	}

	@Test
	@DisplayName("get categories by id")
	void get_categories_by_id_test() {
		// given
		ResponseCategoryDTO grandChild = new ResponseCategoryDTO(4L, "Grandchild", new ArrayList<>());
		ResponseCategoryDTO child1 = new ResponseCategoryDTO(2L, "Child 1", List.of(grandChild));
		ResponseCategoryDTO child2 = new ResponseCategoryDTO(3L, "Child 2", new ArrayList<>());
		ResponseCategoryDTO root = new ResponseCategoryDTO(1L, "Root", List.of(child1, child2));

		when(adminCategoryService.getCategories()).thenReturn(List.of(root));

		// when
		List<ResponseCategoryDTO> result = userCategoryService.getCategoriesById(1L);

		// then
		assertThat(result).hasSize(2); // child1, child2

		ResponseCategoryDTO child1Dto = result.stream()
			.filter(dto -> dto.getCategoryName().equals("Child 1"))
			.findFirst().orElseThrow();

		assertThat(child1Dto.getChildren()).hasSize(1);
		assertThat(child1Dto.getChildren().get(0).getCategoryName()).isEqualTo("Grandchild");

		ResponseCategoryDTO child2Dto = result.stream()
			.filter(dto -> dto.getCategoryName().equals("Child 2"))
			.findFirst().orElseThrow();

		assertThat(child2Dto.getChildren()).isEmpty();

		verify(categoryJpaRepository, never()).findById(any());
	}

}
