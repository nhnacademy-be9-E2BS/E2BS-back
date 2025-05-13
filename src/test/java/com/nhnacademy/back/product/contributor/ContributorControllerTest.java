package com.nhnacademy.back.product.contributor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.back.product.contributor.controller.ContributorController;
import com.nhnacademy.back.product.contributor.domain.dto.request.RequestContributorDTO;
import com.nhnacademy.back.product.contributor.domain.dto.response.ResponseContributorDTO;
import com.nhnacademy.back.product.contributor.domain.entity.Contributor;
import com.nhnacademy.back.product.contributor.domain.entity.Position;
import com.nhnacademy.back.product.contributor.repository.ContributorJpaRepository;
import com.nhnacademy.back.product.contributor.repository.PositionJpaRepository;
import com.nhnacademy.back.product.contributor.service.ContributorService;

@WebMvcTest(ContributorController.class)
public class ContributorControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private ContributorService contributorService;

	@MockitoBean
	private ContributorJpaRepository contributorJpaRepository;

	@MockitoBean
	private PositionJpaRepository positionJpaRepository;

	@Test
	@DisplayName("contributor 생성")
	void createContributor() throws Exception {
		Position position = new Position("작가");
		RequestContributorDTO requestContributorDTO = new RequestContributorDTO("기여자이름", position.getPositionId());

		mockMvc.perform(post("/api/admin/contributors")
			.content(objectMapper.writeValueAsString(requestContributorDTO))
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated());
	}

	@Test
	@DisplayName("contributor 목록 전체 조회 - 페이징")
	void getContributors() throws Exception {
		Position position1 = new Position("작가");
		Position position2 = new Position("<UNK>");

		List<ResponseContributorDTO> mockList = List.of(
			new ResponseContributorDTO(position1.getPositionName(), "이름1"),
			new ResponseContributorDTO(position1.getPositionName(), "이름2"),
			new ResponseContributorDTO(position2.getPositionName(), "이름3")
		);

		Page<ResponseContributorDTO> mockPage = new PageImpl<>(mockList);
		Pageable pageable = PageRequest.of(0, 10);

		when(contributorService.getContributors(pageable)).thenReturn(mockPage);

		mockMvc.perform(MockMvcRequestBuilders.get("/api/admin/contributors")
			.param("page", "0")
			.param("size", "10"))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.content.length()").value(3))
			.andExpect(jsonPath("$.content[0].positionName").value("작가"))
			.andExpect(jsonPath("$.content[0].contributorName").value("이름1"));
	}

	@Test
	@DisplayName("contributorId로 contributor 조회")
	void getContributorById() throws Exception {
		Position position1 = new Position("작가");

		ResponseContributorDTO responseContributorDTO = new ResponseContributorDTO(1L, "이름1", 1L, position1.getPositionName());
		when(contributorService.getContributor(1L)).thenReturn(responseContributorDTO);

		mockMvc.perform(MockMvcRequestBuilders.get("/api/admin/contributors/1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.positionName").value("작가"))
			.andExpect(jsonPath("$.contributorName").value("이름1"));
	}

	@Test
	@DisplayName("contributor 수정")
	void updateContributor() throws Exception {
		Position newPosition = new Position("수정역할");

		RequestContributorDTO request = new RequestContributorDTO("수정된이름", newPosition.getPositionId());

		ResponseContributorDTO mockResponse = new ResponseContributorDTO(
			1L, "수정된이름", newPosition.getPositionId(), "수정역할"
		);

		when(contributorService.updateContributor(eq(1L), any(RequestContributorDTO.class)))
			.thenReturn(mockResponse);

		mockMvc.perform(put("/api/admin/contributors/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.contributorName").value("수정된이름"))
			.andExpect(jsonPath("$.positionName").value("수정역할"));
	}

}
