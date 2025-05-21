package com.nhnacademy.back.product.category;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.nhnacademy.back.product.category.domain.entity.Category;
import com.nhnacademy.back.product.category.repository.CategoryJpaRepository;

@DataJpaTest
@ActiveProfiles("test")
public class CategoryJpaRepositoryTest {
	@Autowired
	private CategoryJpaRepository categoryJpaRepository;

	@Test
	@DisplayName("findAllByParentIsNull 메소드 테스트")
	public void find_all_by_parent_is_null_test() {
		// given
		Category rootCategory = new Category("Root", null);
		categoryJpaRepository.save(rootCategory);

		// when
		List<Category> roots = categoryJpaRepository.findAllByParentIsNull();

		// then
		assertThat(roots).isNotEmpty();
		assertThat(roots).extracting("categoryName").contains("Root");
		assertThat(roots.get(0).getParent()).isNull();
	}

	@Test
	@DisplayName("existsByParentIsNullAndCategoryName 메소드 테스트")
	public void exists_by_parent_is_null_and_category_name_test() {
		// given
		Category rootCategory = new Category("Root", null);
		categoryJpaRepository.save(rootCategory);

		// when
		boolean exists = categoryJpaRepository.existsByParentIsNullAndCategoryName("Root");
		boolean notExists = categoryJpaRepository.existsByParentIsNullAndCategoryName("NonExisting");

		// then
		assertThat(exists).isTrue();
		assertThat(notExists).isFalse();
	}

	@Test
	@DisplayName("existsByParentCategoryIdAndCategoryName 메소드 테스트")
	public void exists_by_parent_category_id_and_category_name_test() {
		// given
		Category root = new Category("Root", null);
		categoryJpaRepository.save(root);
		Category child = new Category("Child", root);
		categoryJpaRepository.save(child);

		// when
		boolean exists = categoryJpaRepository.existsByParentCategoryIdAndCategoryName(root.getCategoryId(), "Child");
		boolean notExists = categoryJpaRepository.existsByParentCategoryIdAndCategoryName(root.getCategoryId(),
			"NonExisting");

		// then
		assertThat(exists).isTrue();
		assertThat(notExists).isFalse();
	}
}
