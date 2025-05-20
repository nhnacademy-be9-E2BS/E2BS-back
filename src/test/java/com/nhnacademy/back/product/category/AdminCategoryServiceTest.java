package com.nhnacademy.back.product.category;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.nhnacademy.back.product.category.domain.dto.request.RequestCategoryDTO;
import com.nhnacademy.back.product.category.domain.dto.response.ResponseCategoryDTO;
import com.nhnacademy.back.product.category.domain.entity.Category;
import com.nhnacademy.back.product.category.exception.CategoryAlreadyExistsException;
import com.nhnacademy.back.product.category.exception.CategoryDeleteNotAllowedException;
import com.nhnacademy.back.product.category.exception.CategoryNotFoundException;
import com.nhnacademy.back.product.category.repository.CategoryJpaRepository;
import com.nhnacademy.back.product.category.service.impl.AdminCategoryServiceImpl;

@ExtendWith(MockitoExtension.class)
public class AdminCategoryServiceTest {
	@InjectMocks
	private AdminCategoryServiceImpl categoryService;
	@Mock
	private CategoryJpaRepository categoryJpaRepository;

	@Test
	@DisplayName("create category(하위 카테고리 추가) - success")
	void create_child_category_success_test() {
		// given
		RequestCategoryDTO request = new RequestCategoryDTO("child category");
		Category parentCategory = new Category("parent category", null);
		when(categoryJpaRepository.findById(anyLong())).thenReturn(Optional.of(parentCategory));
		when(categoryJpaRepository.existsByParentCategoryIdAndCategoryName(anyLong(), anyString())).thenReturn(false);

		// when
		categoryService.createChildCategory(1L, request);

		// then
		verify(categoryJpaRepository, times(1)).save(any(Category.class));
	}

	@Test
	@DisplayName("create category(하위 카테고리 추가) - fail1")
	void create_child_category_fail1_test() {
		// given
		RequestCategoryDTO request = new RequestCategoryDTO("child category");
		when(categoryJpaRepository.findById(anyLong())).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> categoryService.createChildCategory(1L, request))
			.isInstanceOf(CategoryNotFoundException.class);
	}

	@Test
	@DisplayName("create category(하위 카테고리 추가) - fail2")
	void create_child_category_fail2_test() {
		// given
		RequestCategoryDTO request = new RequestCategoryDTO("child category");
		Category parentCategory = new Category("parent category", null);
		when(categoryJpaRepository.findById(anyLong())).thenReturn(Optional.of(parentCategory));
		when(categoryJpaRepository.existsByParentCategoryIdAndCategoryName(anyLong(), anyString())).thenReturn(true);

		// when & then
		assertThatThrownBy(() -> categoryService.createChildCategory(1L, request))
			.isInstanceOf(CategoryAlreadyExistsException.class);
	}

	@Test
	@DisplayName("create category(최상위 + 하위 카테고리 추가) - success")
	void create_category_tree_success_test() {
		// given
		RequestCategoryDTO requestParent = new RequestCategoryDTO("parent category");
		RequestCategoryDTO requestChild = new RequestCategoryDTO("child category");
		List<RequestCategoryDTO> requests = List.of(requestParent, requestChild);
		when(categoryJpaRepository.existsByParentIsNullAndCategoryName(anyString())).thenReturn(false);

		// when
		categoryService.createCategoryTree(requests);

		// then
		verify(categoryJpaRepository, times(2)).save(any(Category.class));
	}

	@Test
	@DisplayName("create category(최상위 + 하위 카테고리 추가) - fail1")
	void create_category_tree_fail1_test() {
		// given
		RequestCategoryDTO requestParent = new RequestCategoryDTO("parent category");
		List<RequestCategoryDTO> requests = List.of(requestParent);

		// when & then
		assertThatThrownBy(() -> categoryService.createCategoryTree(requests))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	@DisplayName("create category(최상위 + 하위 카테고리 추가) - fail2")
	void create_category_tree_fail2_test() {
		// given
		RequestCategoryDTO requestParent = new RequestCategoryDTO("parent category");
		RequestCategoryDTO requestChild = new RequestCategoryDTO("child category");
		List<RequestCategoryDTO> requests = List.of(requestParent, requestChild);
		when(categoryJpaRepository.existsByParentIsNullAndCategoryName(anyString())).thenReturn(true);

		// when & then
		assertThatThrownBy(() -> categoryService.createCategoryTree(requests))
			.isInstanceOf(CategoryAlreadyExistsException.class);
	}

	@Test
	@DisplayName("get categories(ALL)")
	void get_categories_test() {
		// given
		Category parentCategory = new Category("Parent", null);
		ReflectionTestUtils.setField(parentCategory, "categoryId", 1L);

		Category childCategory1 = new Category("Child 1", parentCategory);
		ReflectionTestUtils.setField(childCategory1, "categoryId", 2L);

		Category childCategory2 = new Category("Child 2", parentCategory);
		ReflectionTestUtils.setField(childCategory2, "categoryId", 3L);

		List<Category> allCategories = List.of(parentCategory, childCategory1, childCategory2);

		when(categoryJpaRepository.findAll()).thenReturn(allCategories);

		// when
		List<ResponseCategoryDTO> result = categoryService.getCategories();

		// then
		assertThat(result).hasSize(1); // only root (parent)
		ResponseCategoryDTO parentDTO = result.get(0);
		assertThat(parentDTO.getCategoryName()).isEqualTo("Parent");
		assertThat(parentDTO.getChildren()).hasSize(2);

		List<String> childNames = parentDTO.getChildren().stream()
			.map(ResponseCategoryDTO::getCategoryName)
			.collect(Collectors.toList());

		assertThat(childNames).containsExactlyInAnyOrder("Child 1", "Child 2");

		verify(categoryJpaRepository, times(1)).findAll();
	}

	@Test
	@DisplayName("update category - success")
	void update_category_success_test() {
		// given
		RequestCategoryDTO request = new RequestCategoryDTO("new name category");
		Category parentCategory = mock(Category.class);
		when(parentCategory.getCategoryId()).thenReturn(1L);

		Category category = new Category("category", parentCategory);
		when(categoryJpaRepository.findById(anyLong())).thenReturn(Optional.of(category));
		when(categoryJpaRepository.existsByParentCategoryIdAndCategoryName(anyLong(), anyString())).thenReturn(false);

		// when
		categoryService.updateCategory(2L, request);

		// then
		verify(categoryJpaRepository, times(1)).save(any(Category.class));
	}

	@Test
	@DisplayName("update category - fail1")
	void update_category_fail1_test() {
		// given
		RequestCategoryDTO request = new RequestCategoryDTO("new name category");
		when(categoryJpaRepository.findById(anyLong())).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> categoryService.updateCategory(1L, request))
			.isInstanceOf(CategoryNotFoundException.class);
	}

	@Test
	@DisplayName("update category - fail2")
	void update_category_fail2_test() {
		// given
		RequestCategoryDTO request = new RequestCategoryDTO("new name category");
		Category parentCategory = mock(Category.class);
		when(parentCategory.getCategoryId()).thenReturn(1L);

		Category category = new Category("category", parentCategory);
		when(categoryJpaRepository.findById(anyLong())).thenReturn(Optional.of(category));
		when(categoryJpaRepository.existsByParentCategoryIdAndCategoryName(anyLong(), anyString())).thenReturn(true);

		// when & then
		assertThatThrownBy(() -> categoryService.updateCategory(2L, request))
			.isInstanceOf(CategoryAlreadyExistsException.class);
	}

	@Test
	@DisplayName("delete category - success1")
	void delete_category_success1_test() {
		// given
		Category firstCategory = new Category("first category", null);
		Category secondCategory = new Category("second category", firstCategory);
		Category thirdCategory = new Category("third category", secondCategory);
		when(categoryJpaRepository.findById(anyLong())).thenReturn(Optional.of(thirdCategory));

		// when
		categoryService.deleteCategory(3L);

		// then
		verify(categoryJpaRepository, times(1)).save(secondCategory);
		verify(categoryJpaRepository, times(1)).deleteById(3L);
	}

	@Test
	@DisplayName("delete category - success2")
	void delete_category_success2_test() {
		// given
		Category parentCategory = new Category("parent category", null);
		Category childACategory = new Category("child A category", parentCategory);
		Category childBCategory = new Category("child B category", parentCategory);
		parentCategory.getChildren().add(childACategory);
		parentCategory.getChildren().add(childBCategory);

		when(categoryJpaRepository.findById(anyLong())).thenReturn(Optional.of(childACategory));

		// when
		categoryService.deleteCategory(3L);

		// then
		verify(categoryJpaRepository, times(1)).save(parentCategory);
		verify(categoryJpaRepository, times(1)).deleteById(3L);
	}

	@Test
	@DisplayName("delete category - fail1")
	void delete_category_fail1_test() {
		// given
		Category category = new Category("category", null);
		when(categoryJpaRepository.findById(anyLong())).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> categoryService.deleteCategory(1L))
			.isInstanceOf(CategoryNotFoundException.class);
	}

	@Test
	@DisplayName("delete category - fail2")
	void delete_category_fail2_test() {
		// given
		Category parentCategory = new Category("parent category", null);
		Category childACategory = new Category("child A category", parentCategory);
		parentCategory.getChildren().add(childACategory);

		when(categoryJpaRepository.findById(anyLong())).thenReturn(Optional.of(parentCategory));

		// when & then
		assertThatThrownBy(() -> categoryService.deleteCategory(1L))
			.isInstanceOf(CategoryDeleteNotAllowedException.class);
	}

	@Test
	@DisplayName("delete category - fail3")
	void delete_category_fail3_test() {
		// given
		Category parentCategory = new Category("parent category", null);
		Category childACategory = new Category("child A category", parentCategory);
		parentCategory.getChildren().add(childACategory);

		when(categoryJpaRepository.findById(anyLong())).thenReturn(Optional.of(childACategory));

		// when & then
		assertThatThrownBy(() -> categoryService.deleteCategory(2L))
			.isInstanceOf(CategoryDeleteNotAllowedException.class);
	}

}
