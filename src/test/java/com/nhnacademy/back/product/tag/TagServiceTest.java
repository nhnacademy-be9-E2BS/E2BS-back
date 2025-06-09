package com.nhnacademy.back.product.tag;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.nhnacademy.back.elasticsearch.service.ProductSearchService;
import com.nhnacademy.back.product.product.domain.entity.Product;
import com.nhnacademy.back.product.publisher.domain.entity.Publisher;
import com.nhnacademy.back.product.state.domain.entity.ProductState;
import com.nhnacademy.back.product.state.domain.entity.ProductStateName;
import com.nhnacademy.back.product.tag.domain.dto.request.RequestTagDTO;
import com.nhnacademy.back.product.tag.domain.dto.response.ResponseTagDTO;
import com.nhnacademy.back.product.tag.domain.entity.ProductTag;
import com.nhnacademy.back.product.tag.domain.entity.Tag;
import com.nhnacademy.back.product.tag.exception.TagAlreadyExistsException;
import com.nhnacademy.back.product.tag.exception.TagNotFoundException;
import com.nhnacademy.back.product.tag.repository.ProductTagJpaRepository;
import com.nhnacademy.back.product.tag.repository.TagJpaRepository;
import com.nhnacademy.back.product.tag.service.impl.TagServiceImpl;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {
	@InjectMocks
	private TagServiceImpl tagService;
	@Mock
	private TagJpaRepository tagJpaRepository;
	@Mock
	private ProductTagJpaRepository productTagJpaRepository;
	@Mock
	private ProductSearchService productSearchService;

	@Test
	@DisplayName("create tag - success")
	void createTagSuccess() {
		//given
		RequestTagDTO request = new RequestTagDTO("Tag A");

		//when
		tagService.createTag(request);

		//then
		verify(tagJpaRepository, times(1)).save(any(Tag.class));

	}

	@Test
	@DisplayName("create tag - fail")
	void createTagFail() {
		//given
		RequestTagDTO request = new RequestTagDTO("Tag A");
		when(tagJpaRepository.existsByTagName("Tag A"))
			.thenReturn(false)
			.thenReturn(true);

		//when
		tagService.createTag(request);

		//then
		assertThatThrownBy(() -> tagService.createTag(request))
			.isInstanceOf(TagAlreadyExistsException.class);
	}

	@Test
	@DisplayName("get tag list")
	void getTagsTest() {
		//given
		Tag tagA = new Tag("Tag A");
		Tag tagB = new Tag("Tag B");
		List<Tag> tags = List.of(tagA, tagB);

		Pageable pageable = PageRequest.of(0, 10);
		Page<Tag> wrapperPage = new PageImpl<>(tags);

		when(tagJpaRepository.findAll(pageable)).thenReturn(wrapperPage);

		//when
		Page<ResponseTagDTO> result = tagService.getTags(pageable);

		//then
		assertThat(result.getContent()).hasSize(2);
		assertThat(result.getContent().get(0).getTagName()).isEqualTo("Tag A");
		assertThat(result.getContent().get(1).getTagName()).isEqualTo("Tag B");

	}

	@Test
	@DisplayName("update tag - success")
	void updateTagSuccessTest() {
		// given
		RequestTagDTO request = new RequestTagDTO("update after tag");
		Tag tag = new Tag("update before tag");
		String beforeName = tag.getTagName();
		Publisher publisher = new Publisher("update before publisher");
		ProductState productState = new ProductState(ProductStateName.SALE);
		Product product = new Product(1L, productState, publisher, "title", "content", "description", LocalDate.now(),
			"978-89-12345-01-1", 10000L, 8000L, true, 100, new ArrayList<>());
		ProductTag productTag = new ProductTag(product, tag);
		List<ProductTag> productTags = List.of(productTag);
		when(tagJpaRepository.findById(1L)).thenReturn(Optional.of(tag));
		when(productTagJpaRepository.findAllByTag_TagId(1L)).thenReturn(productTags);

		// when
		tagService.updateTag(1L, request);

		// then
		assertThat(tag.getTagName()).isEqualTo("update after tag");
		verify(tagJpaRepository, times(1)).save(tag);
		verify(productTagJpaRepository, times(1)).findAllByTag_TagId(1L);
		verify(productSearchService, times(1)).updateProductDocumentTag(1L, beforeName, request.getTagName());
	}

	@Test
	@DisplayName("update tag - fail1")
	void updateTagFail1Test() {
		// when & then
		assertThatThrownBy(() -> tagService.updateTag(1L, null))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	@DisplayName("update tag - fail2")
	void updateTagFail2Test() {
		// given
		RequestTagDTO request = new RequestTagDTO("update tag");
		when(tagJpaRepository.findById(anyLong())).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> tagService.updateTag(100L, request))
			.isInstanceOf(TagNotFoundException.class);
	}

	@Test
	@DisplayName("update tag - fail3")
	void updateTagFail3Test() {
		// given
		RequestTagDTO request = new RequestTagDTO("update tag");
		when(tagJpaRepository.findById(anyLong())).thenReturn(Optional.of(new Tag("update tag")));
		when(tagJpaRepository.existsByTagName(any())).thenReturn(true);

		// when & then
		assertThatThrownBy(() -> tagService.updateTag(100L, request))
			.isInstanceOf(TagAlreadyExistsException.class);
	}

}
