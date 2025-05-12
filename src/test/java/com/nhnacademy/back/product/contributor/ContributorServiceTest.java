package com.nhnacademy.back.product.contributor;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;

import com.nhnacademy.back.product.contributor.domain.dto.request.RequestContributorDTO;
import com.nhnacademy.back.product.contributor.domain.dto.response.ResponseContributorDTO;
import com.nhnacademy.back.product.contributor.domain.entity.Contributor;
import com.nhnacademy.back.product.contributor.domain.entity.Position;
import com.nhnacademy.back.product.contributor.exception.ContributorAlreadyExistsException;
import com.nhnacademy.back.product.contributor.exception.ContributorNotFoundException;
import com.nhnacademy.back.product.contributor.exception.PositionNotFoundException;
import com.nhnacademy.back.product.contributor.repository.ContributorJpaRepository;
import com.nhnacademy.back.product.contributor.repository.PositionJpaRepository;
import com.nhnacademy.back.product.contributor.service.ContributorService;
import com.nhnacademy.back.product.contributor.service.impl.ContributorServiceImpl;

public class ContributorServiceTest {
	@Mock
	private ContributorJpaRepository contributorJpaRepository;

	@Mock
	private PositionJpaRepository positionJpaRepository;

	@InjectMocks
	private ContributorServiceImpl contributorService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	@DisplayName("기여자 생성 성공")
	public void createContributorSuccess() {
		Position position = new Position("작가");
		when(positionJpaRepository.findById(anyLong())).thenReturn(Optional.of(position));

		RequestContributorDTO requestContributorDTO = new RequestContributorDTO("기여자이름", position.getPositionId());

		contributorService.createContributor(requestContributorDTO);

		when(contributorJpaRepository.existsByContributorName(anyString())).thenReturn(false);


		verify(contributorJpaRepository, times(1)).save(any());
	}


	@Test
	@DisplayName("기여자 생성 실패")
	public void createContributorFail() {
		Position position = new Position("작가");
		RequestContributorDTO requestContributorDTO = new RequestContributorDTO("기여자이름", position.getPositionId());

		when(contributorJpaRepository.existsByContributorName(anyString())).thenReturn(true);

		assertThatThrownBy(() -> contributorService.createContributor(requestContributorDTO)).isInstanceOf(
			PositionNotFoundException.class);
	}


	@Test
	@DisplayName("기여자 수정 성공")
	public void updateContributorSuccess() {
		Long contributorId = 1L;
		Position newPosition = new Position("새로운역할");

		RequestContributorDTO request = new RequestContributorDTO("새로운기여자", newPosition.getPositionId());


		Contributor existingContributor = new Contributor("기존기여자", new Position("기존역할"));

		when(contributorJpaRepository.findById(contributorId)).thenReturn(Optional.of(existingContributor));
		when(positionJpaRepository.findById(newPosition.getPositionId())).thenReturn(Optional.of(newPosition));

		ResponseContributorDTO response = contributorService.updateContributor(contributorId, request);

		assertNotNull(response);
		assertEquals("새로운기여자", response.getContributorName());
		assertEquals("새로운역할", response.getPositionName());
		assertEquals(newPosition.getPositionId(), response.getPositionId());

		verify(contributorJpaRepository).save(any(Contributor.class));
	}

	@Test
	@DisplayName("기여자 수정 실패")
	public void updateContributorFail() {

	}

	@Test
	@DisplayName("기여자 아이디로 단건 조회 성공")
	public void getContributorSuccess() {
		Position position = new Position( "position1");
		Contributor existingContributor = new Contributor("기여자", position);
		when(contributorJpaRepository.findById(0L)).thenReturn(Optional.of(existingContributor));

		ResponseContributorDTO responseContributorDTO = contributorService.getContributor(0L);

		assertThat(responseContributorDTO).isNotNull();
		assertThat(responseContributorDTO.getContributorName()).isEqualTo("기여자");

	}

	@Test
	@DisplayName("기여자 아이디로 단건 조회 실패")
	public void getContributorFail() {
		when(contributorJpaRepository.findById(anyLong())).thenReturn(Optional.empty());
		assertThatThrownBy(() -> contributorService.getContributor(0L)).isInstanceOf(ContributorNotFoundException.class);
	}

	@Test
	@DisplayName("기여자 전체 조회 - 페이징")
	public void getContributors() {
		Position position = new Position("position");
		Contributor contributor1 = new Contributor("기여자1", position);
		Contributor contributor2 = new Contributor("기여자2", position);

		List<Contributor> contributors = Arrays.asList(contributor1, contributor2);
		Pageable pageable = PageRequest.of(0, 10);
		Page<Contributor> page = new PageImpl<>(contributors, pageable, contributors.size());

		when(contributorJpaRepository.findAll(any(Pageable.class))).thenReturn(page);

		Page<ResponseContributorDTO> result = contributorService.getContributors(pageable);

		verify(contributorJpaRepository, times(1)).findAll(any(Pageable.class));
		assertThat(result).isNotNull();
		assertThat(result.getTotalElements()).isEqualTo(2);
		assertThat(result.getTotalPages()).isEqualTo(1);
		assertThat(result.getContent().get(0).getContributorName()).isEqualTo("기여자1");
		assertThat(result.getContent().get(0).getPositionName()).isEqualTo("position");
	}
}
