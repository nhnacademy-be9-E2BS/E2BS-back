package com.nhnacademy.back.order.wrapper;

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

import com.nhnacademy.back.order.wrapper.domain.dto.request.RequestModifyWrapperDTO;
import com.nhnacademy.back.order.wrapper.domain.dto.request.RequestRegisterWrapperDTO;
import com.nhnacademy.back.order.wrapper.domain.dto.response.ResponseWrapperDTO;
import com.nhnacademy.back.order.wrapper.domain.entity.Wrapper;
import com.nhnacademy.back.order.wrapper.exception.WrapperNotFoundException;
import com.nhnacademy.back.order.wrapper.repository.WrapperJpaRepository;
import com.nhnacademy.back.order.wrapper.service.impl.WrapperServiceImpl;

@ExtendWith(MockitoExtension.class)
class WrapperServiceTest {
	@InjectMocks
	private WrapperServiceImpl wrapperService;
	@Mock
	private WrapperJpaRepository wrapperJpaRepository;

	@Test
	@DisplayName("create wrapper")
	void create_wrapper_test() {
		// given
		RequestRegisterWrapperDTO request = new RequestRegisterWrapperDTO(1000L, "Wrapper A", "a.jpg", true);

		// when
		wrapperService.createWrapper(request);

		// then
		verify(wrapperJpaRepository, times(1)).save(any(Wrapper.class));
	}

	@Test
	@DisplayName("get wrappers_all")
	void get_wrappers_test() {
		// given
		Wrapper wrapperA = new Wrapper(700L, "Wrapper A", "a.jpg", true);
		Wrapper wrapperB = new Wrapper(1000L, "Wrapper B", "b.jpg", false);
		List<Wrapper> wrappers = List.of(wrapperA, wrapperB);

		Pageable pageable = PageRequest.of(0, 10);
		Page<Wrapper> wrapperPage = new PageImpl<>(wrappers);

		when(wrapperJpaRepository.findAll(pageable)).thenReturn(wrapperPage);

		// when
		Page<ResponseWrapperDTO> result = wrapperService.getWrappers(pageable);

		// then
		assertThat(result.getContent()).hasSize(2);
		ResponseWrapperDTO dto = result.getContent().get(0);
		assertThat(dto.getWrapperPrice()).isEqualTo(700L);
		assertThat(dto.getWrapperName()).isEqualTo("Wrapper A");
		assertThat(dto.getWrapperImage()).isEqualTo("a.jpg");
		assertThat(dto.isWrapperSaleable()).isTrue();
	}

	@Test
	@DisplayName("get wrappers by saleable")
	void get_wrappers_by_saleable_test() {
		// given
		Wrapper wrapperA = new Wrapper(700L, "Wrapper A", "a.jpg", true);
		Wrapper wrapperC = new Wrapper(900L, "Wrapper C", "c.jpg", true);
		List<Wrapper> wrappers = List.of(wrapperA, wrapperC);

		Pageable pageable = PageRequest.of(0, 10);
		Page<Wrapper> wrapperPage = new PageImpl<>(wrappers);

		when(wrapperJpaRepository.findAllByWrapperSaleable(true, pageable)).thenReturn(wrapperPage);

		// when
		Page<ResponseWrapperDTO> result = wrapperService.getWrappersBySaleable(true, pageable);

		// then
		assertThat(result.getContent()).hasSize(2);
		ResponseWrapperDTO dto = result.getContent().get(1);
		assertThat(dto.getWrapperPrice()).isEqualTo(900L);
		assertThat(dto.getWrapperName()).isEqualTo("Wrapper C");
		assertThat(dto.getWrapperImage()).isEqualTo("c.jpg");
		assertThat(dto.isWrapperSaleable()).isTrue();
	}

	@Test
	@DisplayName("update wrapper - success")
	void update_wrapper_success_test() {
		// given
		RequestModifyWrapperDTO request = new RequestModifyWrapperDTO(false);
		Wrapper wrapper = new Wrapper(800L, "update Wrapper A", "a.jpg", true);
		when(wrapperJpaRepository.findById(1L)).thenReturn(Optional.of(wrapper));

		// when
		wrapperService.updateWrapper(1L, request);

		// then
		assertThat(wrapper.isWrapperSaleable()).isFalse();
		verify(wrapperJpaRepository, times(1)).save(wrapper);
	}

	@Test
	@DisplayName("update wrapper - fail1")
	void update_wrapper_fail1_test() {
		// when & then
		assertThatThrownBy(() -> wrapperService.updateWrapper(1L, null))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	@DisplayName("update wrapper - fail2")
	void update_wrapper_fail2_test() {
		// given
		RequestModifyWrapperDTO request = new RequestModifyWrapperDTO(true);
		when(wrapperJpaRepository.findById(anyLong())).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> wrapperService.updateWrapper(100L, request))
			.isInstanceOf(WrapperNotFoundException.class);
	}
}
