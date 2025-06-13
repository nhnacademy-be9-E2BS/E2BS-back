package com.nhnacademy.back.elasticsearch;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.ApplicationArguments;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.document.Document;

import com.nhnacademy.back.elasticsearch.domain.document.ProductDocument;
import com.nhnacademy.back.elasticsearch.lifecycle.ElasticsearchDataInitializer;
import com.nhnacademy.back.elasticsearch.repository.ProductSearchRepository;

class ElasticsearchDataInitializerTest {
	private ElasticsearchOperations elasticsearchOperations;
	private ProductSearchRepository productSearchRepository;

	@BeforeEach
	void setUp() {
		elasticsearchOperations = mock(ElasticsearchOperations.class);
		productSearchRepository = mock(ProductSearchRepository.class);
		IndexOperations indexOperations = mock(IndexOperations.class);

		when(elasticsearchOperations.indexOps(ProductDocument.class)).thenReturn(indexOperations);
	}

	@Test
	@DisplayName("dev - Elasticsearch 인덱스 초기화 및 샘플 데이터 저장 테스트")
	void testRun() {
		// given
		IndexOperations indexOperations = mock(IndexOperations.class);
		when(elasticsearchOperations.indexOps(ProductDocument.class)).thenReturn(indexOperations);
		when(indexOperations.exists()).thenReturn(true);
		when(indexOperations.createMapping()).thenReturn(mock(Document.class));

		ElasticsearchDataInitializer initializer = new ElasticsearchDataInitializer(elasticsearchOperations,
			productSearchRepository);

		// when
		initializer.run(mock(ApplicationArguments.class));

		// then
		verify(indexOperations).delete();
		verify(indexOperations).create();
		verify(indexOperations).putMapping(any(Document.class));
		verify(productSearchRepository).saveAll(anyList());
	}
}
