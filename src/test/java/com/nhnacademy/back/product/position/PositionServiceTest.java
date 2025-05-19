package com.nhnacademy.back.product.position;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
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

import com.nhnacademy.back.product.contributor.domain.dto.request.RequestPositionDTO;
import com.nhnacademy.back.product.contributor.domain.dto.response.ResponsePositionDTO;
import com.nhnacademy.back.product.contributor.domain.entity.Position;
import com.nhnacademy.back.product.contributor.exception.PositionAlreadyExistsException;
import com.nhnacademy.back.product.contributor.exception.PositionNotFoundException;
import com.nhnacademy.back.product.contributor.repository.PositionJpaRepository;
import com.nhnacademy.back.product.contributor.service.impl.PositionServiceImpl;

@ExtendWith(MockitoExtension.class)
class PositionServiceTest {
	@Mock
	private PositionJpaRepository positionJpaRepository;

	@InjectMocks
	private PositionServiceImpl positionService;

	@Test
	@DisplayName("관리자 역할 생성 성공")
	void createPositionSuccess() {
		RequestPositionDTO requestPositionDTO = new RequestPositionDTO("position");
		when(positionJpaRepository.existsByPositionName(anyString())).thenReturn(false);

		Position mockPosition = new Position("position");
		when(positionJpaRepository.save(any())).thenReturn(mockPosition);

		positionService.createPosition(requestPositionDTO);

		verify(positionJpaRepository, times(1)).existsByPositionName(anyString());
		verify(positionJpaRepository, times(1)).save(any());
	}

	@Test
	@DisplayName("관리자 역할 생성 실패")
	void createPositionFail() {
		RequestPositionDTO requestPositionDTO = new RequestPositionDTO("position");
		when(positionJpaRepository.existsByPositionName(anyString())).thenReturn(true);

		assertThatThrownBy(() -> positionService.createPosition(requestPositionDTO)).isInstanceOf(
			PositionAlreadyExistsException.class);
	}

	@Test
	@DisplayName("관리자 역할 수정 성공")
	void updatePositionSuccess() {
		Long positionId = 1L;
		RequestPositionDTO requestDTO = new RequestPositionDTO("newPosition");

		Position existing = new Position("oldPosition");
		when(positionJpaRepository.findById(positionId)).thenReturn(Optional.of(existing));
		when(positionJpaRepository.save(any(Position.class))).thenAnswer(invocation -> invocation.getArgument(0));

		ResponsePositionDTO result = positionService.updatePosition(positionId, requestDTO);

		assertNotNull(result);
		assertEquals("newPosition", result.getPositionName());

		verify(positionJpaRepository).findById(positionId);
		verify(positionJpaRepository).save(existing);
	}

	@Test
	@DisplayName("관리자 역할 수정 실퍄")
	void updatePositionFail() {
		Long positionId = 999L;
		RequestPositionDTO requestDTO = new RequestPositionDTO("newPosition");

		when(positionJpaRepository.findById(positionId))
			.thenReturn(Optional.empty());

		assertThrows(PositionNotFoundException.class, () -> {
			positionService.updatePosition(positionId, requestDTO);
		});

		verify(positionJpaRepository).findById(positionId);
		verify(positionJpaRepository, never()).save(any());
	}

	@Test
	@DisplayName("관리자 역할 아이디로 역할 단건 조회 성공 ")
	void getPositionByIdSuccess() {
		Position position = new Position("position1");
		when(positionJpaRepository.findById(anyLong())).thenReturn(Optional.of(position));

		ResponsePositionDTO responsePositionDTO = positionService.getPosition(1L);

		assertThat(responsePositionDTO).isNotNull();
		assertThat(responsePositionDTO.getPositionName()).isEqualTo("position1");
	}

	@Test
	@DisplayName("관리자 역할 아이디로 역할 단건 조회 실패 ")
	void getPositionByIdFail() {
		when(positionJpaRepository.findById(anyLong())).thenReturn(Optional.empty());
		assertThatThrownBy(() -> positionService.getPosition(1L)).isInstanceOf(PositionNotFoundException.class);
	}

	@Test
	@DisplayName("관리자 역할 전체 조회 - 페이징")
	void getPositionsWithPaging() {
		Position position1 = new Position("position1");
		Position position2 = new Position("position2");

		List<Position> positions = Arrays.asList(position1, position2);
		Pageable pageable = PageRequest.of(0, 10);
		Page<Position> page = new PageImpl<>(positions, pageable, positions.size());

		when(positionJpaRepository.findAll(any(Pageable.class))).thenReturn(page);

		Page<ResponsePositionDTO> result = positionService.getPositions(pageable);

		verify(positionJpaRepository, times(1)).findAll(pageable);
		assertThat(result).isNotNull();
		assertEquals(2, result.getContent().size());
		assertThat(result.getContent().get(0).getPositionName()).isEqualTo("position1");
	}
}
