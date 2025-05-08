package com.nhnacademy.back.product.publisher;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.back.product.publisher.domain.dto.request.RequestPublisherDTO;
import com.nhnacademy.back.product.publisher.domain.entity.Publisher;
import com.nhnacademy.back.product.publisher.exception.PublisherNotFoundException;
import com.nhnacademy.back.product.publisher.repository.PublisherJpaRepository;
import com.nhnacademy.back.product.publisher.service.PublisherService;

@SpringBootTest
@Transactional
public class PublisherServiceTest {
	@Autowired
	private PublisherService publisherService;
	@MockitoBean
	private PublisherJpaRepository publisherJpaRepository;

	@Test
	@DisplayName("create publisher")
	void create_publisher_success_test() {
		RequestPublisherDTO request = new RequestPublisherDTO("Publisher A");
		publisherService.createPublisher(request);

		verify(publisherJpaRepository, times(1)).save(any(Publisher.class));
	}

	@Test
	@DisplayName("get publisher list")
	void get_publishers_test() {
		Publisher publisherA = new Publisher("Publisher A");
		Publisher publisherB = new Publisher("Publisher B");
		List<Publisher> publishers = List.of(publisherA, publisherB);

		when(publisherJpaRepository.findAll()).thenReturn(publishers);
		List<Publisher> result = publisherService.getPublishers();

		assertEquals(2, result.size());
		verify(publisherJpaRepository, times(1)).findAll();
	}

	@Test
	@DisplayName("update publisher - success")
	void update_publisher_success_test() {
		RequestPublisherDTO request = new RequestPublisherDTO("update after publisher");
		Publisher publisher = new Publisher("update before publisher");
		when(publisherJpaRepository.findById(1L)).thenReturn(Optional.of(publisher));
		publisherService.updatePublisher(1L, request);

		assertThat(publisher.getPublisherName()).isEqualTo("update after publisher");
		verify(publisherJpaRepository, times(1)).save(publisher);
	}

	@Test
	@DisplayName("update publisher - fail1")
	void update_publisher_fail1_test() {
		assertThatThrownBy(() -> publisherService.updatePublisher(1L, null))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	@DisplayName("update publisher - fail2")
	void update_publisher_fail2_test() {
		RequestPublisherDTO request = new RequestPublisherDTO("update publisher");
		when(publisherJpaRepository.findById(anyLong())).thenReturn(Optional.empty());

		assertThatThrownBy(() -> publisherService.updatePublisher(100L, request))
			.isInstanceOf(PublisherNotFoundException.class);
	}
}
