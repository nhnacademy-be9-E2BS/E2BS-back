package com.nhnacademy.back.product.publisher;

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
import com.nhnacademy.back.product.publisher.domain.dto.response.ResponsePublisherDTO;
import com.nhnacademy.back.product.publisher.domain.entity.Publisher;
import com.nhnacademy.back.product.publisher.exception.PublisherAlreadyExistsException;
import com.nhnacademy.back.product.publisher.exception.PublisherNotFoundException;
import com.nhnacademy.back.product.publisher.repository.PublisherJpaRepository;
import com.nhnacademy.back.product.publisher.service.impl.PublisherServiceImpl;

@ExtendWith(MockitoExtension.class)
public class PublisherServiceTest {
	@InjectMocks
	private PublisherServiceImpl publisherService;
	@Mock
	private PublisherJpaRepository publisherJpaRepository;

	@Test
	@DisplayName("create publisher - success")
	void create_publisher_success_test() {
		// given
		RequestPublisherDTO request = new RequestPublisherDTO("Publisher A");

		// when
		publisherService.createPublisher(request);

		// then
		verify(publisherJpaRepository, times(1)).save(any(Publisher.class));
	}

	@Test
	@DisplayName("create publisher - fail")
	void create_publisher_fail_test() {
		// given
		RequestPublisherDTO request = new RequestPublisherDTO("Publisher A");
		when(publisherJpaRepository.existsByPublisherName("Publisher A"))
			.thenReturn(false)    // 첫 번째 호출에는 false return
			.thenReturn(true);    // 두 번째 호출에는 true return

		// when
		publisherService.createPublisher(request);

		// then
		assertThatThrownBy(() -> publisherService.createPublisher(request))
			.isInstanceOf(PublisherAlreadyExistsException.class);
	}

	@Test
	@DisplayName("get publisher list")
	void get_publishers_test() {
		// given
		Publisher publisherA = new Publisher("Publisher A");
		Publisher publisherB = new Publisher("Publisher B");
		List<Publisher> publishers = List.of(publisherA, publisherB);

		Pageable pageable = PageRequest.of(0, 10);
		Page<Publisher> wrapperPage = new PageImpl<>(publishers);

		when(publisherJpaRepository.findAll(pageable)).thenReturn(wrapperPage);

		// when
		Page<ResponsePublisherDTO> result = publisherService.getPublishers(pageable);

		// then
		assertThat(result.getContent()).hasSize(2);
		assertThat(result.getContent().get(0).getPublisherName()).isEqualTo("Publisher A");
		assertThat(result.getContent().get(1).getPublisherName()).isEqualTo("Publisher B");
	}

	@Test
	@DisplayName("update publisher - success")
	void update_publisher_success_test() {
		// given
		RequestPublisherDTO request = new RequestPublisherDTO("update after publisher");
		Publisher publisher = new Publisher("update before publisher");
		when(publisherJpaRepository.findById(1L)).thenReturn(Optional.of(publisher));

		// when
		publisherService.updatePublisher(1L, request);

		// then
		assertThat(publisher.getPublisherName()).isEqualTo("update after publisher");
		verify(publisherJpaRepository, times(1)).save(publisher);
	}

	@Test
	@DisplayName("update publisher - fail1")
	void update_publisher_fail1_test() {
		// when & then
		assertThatThrownBy(() -> publisherService.updatePublisher(1L, null))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	@DisplayName("update publisher - fail2")
	void update_publisher_fail2_test() {
		// given
		RequestPublisherDTO request = new RequestPublisherDTO("update publisher");
		when(publisherJpaRepository.findById(anyLong())).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> publisherService.updatePublisher(100L, request))
			.isInstanceOf(PublisherNotFoundException.class);
	}

	@Test
	@DisplayName("update publisher - fail3")
	void update_publisher_fail3_test() {
		// given
		RequestPublisherDTO request = new RequestPublisherDTO("update publisher");
		when(publisherJpaRepository.findById(anyLong())).thenReturn(Optional.of(new Publisher("update publisher")));
		when(publisherJpaRepository.existsByPublisherName(any())).thenReturn(true);

		// when & then
		assertThatThrownBy(() -> publisherService.updatePublisher(100L, request))
			.isInstanceOf(PublisherAlreadyExistsException.class);
	}
}
