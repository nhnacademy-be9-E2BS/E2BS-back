package com.nhnacademy.back.common.config;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.errors.MinioException;

@Configuration
public class MinioConfig {

	@Value("${minio.host}")
	private String host;

	@Value("${minio.access-key}")
	private String accessKey;

	@Value("${minio.secret-key}")
	private String secretKey;

	@Value("${minio.region}")
	private String region;

	/**
	 * minio API 는 AWS Signature v4 방식의 Authorization 헤더를 포함해야한다는데
	 * S3Client 는 AWS Signature Version 4 (SigV4) 인증을 자동으로 처리해준다!
	 *
	 * S3Client 는 MinIO에 요청할 때마다 다음을 자동으로 해준다.
	 * 1.Authorization: AWS4-HMAC-SHA256 ... 헤더 생성
	 * 2.X-Amz-Date, X-Amz-Content-Sha256 등 필요한 헤더 포함
	 * 3.요청 본문에 대한 서명 포함
	 * 4.AWS S3와 완벽히 호환되는 방식으로 MinIO에 요청
	 */
	// @Bean
	// public S3Client s3Client() {
	// 	return S3Client.builder()
	// 		.endpointOverride(URI.create(host))
	// 		.credentialsProvider(() -> AwsBasicCredentials.builder()
	// 									.accessKeyId(accessKey)
	// 									.secretAccessKey(secretKey)
	// 									.build())
	// 		.region(Region.of(region))
	// 		.forcePathStyle(true)
	// 		.build();
	// }

	@Bean
	public MinioClient minioClient() {
		try {
			MinioClient minioClient = MinioClient.builder()
				.endpoint(host)
				.credentials(accessKey, secretKey)
				.build();

			boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket("e2bs-reviews-image").build());
			if (!found) {
				minioClient.makeBucket(MakeBucketArgs.builder().bucket("e2bs-reviews-image").build());
			} else {
				System.out.println("Bucket 'e2bs-reviews-image' already exists.");
			}

			return minioClient;
		} catch (MinioException e) {
			System.out.println("Error occurred: " + e);
			System.out.println("HTTP trace: " + e.httpTrace());
			throw new RuntimeException(e);
		} catch (IOException | NoSuchAlgorithmException | InvalidKeyException e) {
			System.out.println("Error occurred: " + e);
			System.out.println("HTTP trace: " + Arrays.toString(e.getStackTrace()));
			throw new RuntimeException(e);
		}
	}

}
