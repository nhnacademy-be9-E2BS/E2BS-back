package com.nhnacademy.back.order.wrapper;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import com.nhnacademy.back.order.wrapper.domain.entity.Wrapper;
import com.nhnacademy.back.order.wrapper.repository.WrapperJpaRepository;

@DataJpaTest
@ActiveProfiles("test")
class WrapperJpaRepositoryTest {
	@Autowired
	private WrapperJpaRepository wrapperJpaRepository;

	@Test
	@DisplayName("findAllByWrapperSaleable 메소드 테스트")
	public void find_all_by_wrapper_saleable_test() {
		// given
		Wrapper wrapper1 = new Wrapper(1000, "wrapper A", "a.jpg", true);
		Wrapper wrapper2 = new Wrapper(1300, "wrapper B", "b.jpg", false);
		Wrapper wrapper3 = new Wrapper(1800, "wrapper C", "c.jpg", true);

		wrapperJpaRepository.saveAll(List.of(wrapper1, wrapper2, wrapper3));

		Pageable pageable = PageRequest.of(0, 2);
		Page<Wrapper> result = wrapperJpaRepository.findAllByWrapperSaleable(true, pageable);

		// when & then
		assertThat(result.getContent()).hasSize(2);
		assertThat(result.getTotalElements()).isEqualTo(2);
		assertThat(result.getContent())
			.extracting(Wrapper::getWrapperName)
			.containsExactlyInAnyOrder("wrapper A", "wrapper C");
	}
}
