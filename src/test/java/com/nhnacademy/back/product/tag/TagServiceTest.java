package com.nhnacademy.back.product.tag;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

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

import com.nhnacademy.back.product.publisher.domain.dto.request.RequestPublisherDTO;
import com.nhnacademy.back.product.publisher.domain.entity.Publisher;
import com.nhnacademy.back.product.publisher.exception.PublisherAlreadyExistsException;
import com.nhnacademy.back.product.publisher.exception.PublisherNotFoundException;
import com.nhnacademy.back.product.tag.domain.dto.request.RequestTagDTO;
import com.nhnacademy.back.product.tag.domain.dto.response.ResponseTagDTO;
import com.nhnacademy.back.product.tag.domain.entity.Tag;
import com.nhnacademy.back.product.tag.exception.TagAlreadyExistsException;
import com.nhnacademy.back.product.tag.exception.TagNotFoundException;
import com.nhnacademy.back.product.tag.repository.TagJpaRepository;
import com.nhnacademy.back.product.tag.service.TagService;
import com.nhnacademy.back.product.tag.service.impl.TagServiceImpl;

@ExtendWith(MockitoExtension.class)
public class TagServiceTest {
	@InjectMocks
	private TagServiceImpl tagService;
	@Mock
	private TagJpaRepository tagJpaRepository;

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
		when(tagJpaRepository.findById(1L)).thenReturn(Optional.of(tag));

		// when
		tagService.updateTag(1L, request);

		// then
		assertThat(tag.getTagName()).isEqualTo("update after tag");
		verify(tagJpaRepository, times(1)).save(tag);
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
