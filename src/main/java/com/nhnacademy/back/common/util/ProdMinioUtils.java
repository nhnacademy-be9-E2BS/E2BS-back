package com.nhnacademy.back.common.util;

import java.io.IOException;
import java.time.Duration;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.nhnacademy.back.common.exception.FileStreamOpenException;
import com.nhnacademy.back.common.exception.InvalidImageFormatException;
import com.nhnacademy.back.common.exception.MinioDeleteException;
import com.nhnacademy.back.common.exception.MinioUploadException;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Component
@Profile("prod")
@RequiredArgsConstructor
public class ProdMinioUtils implements MinioUtils {

	@Value("${minio.host}")
	private String host;

	private final S3Client s3Client;
	private final S3Presigner presigner;


	/**
	 * MINIO 가상 저장소 서버에 파일 업로드 메소드
	 */
	public void uploadObject(String bucketName, String objectName, MultipartFile file) {
		// png, jpeg, gif, bmp 같은 이미지 파일인지 검증
		String contentType = file.getContentType();
		if (Objects.isNull(contentType) || !contentType.startsWith("image/")) {
			throw new InvalidImageFormatException(contentType);
		}

		try {
			PutObjectRequest putObjectRequest = PutObjectRequest.builder()
				.bucket(bucketName)
				.key(objectName)
				.contentType(file.getContentType())
				.build();

			s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
		}  catch (IOException e) {
			throw new FileStreamOpenException();
		} catch (S3Exception e) {
			throw new MinioUploadException();
		}
	}

	/**
	 * MINIO 가상 저장소 서버에서 오브젝트(파일)에 대해 파일 삭제 메소드
	 */
	public void deleteObject(String bucketName, String objectName) {
		try {
			DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
				.bucket(bucketName)
				.key(objectName)
				.build();

			s3Client.deleteObject(deleteObjectRequest);
		} catch (S3Exception e) {
			throw new MinioDeleteException();
		}
	}

	/**
	 * MINIO 가상 저장소 서버에서 오브젝트(파일)에 대해 제한된 시간 동안 접근 가능한 URL 가공 메소드
	 */
	public String getPresignedUrl(String bucketName, String objectKey) {
		// object 파일을 가져오고
		// 해당 파일에 대한 접근 url 생성
		GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
			.signatureDuration(Duration.ofDays(7)) // 최대 유지 가능 시간
			.getObjectRequest(b -> b
				.bucket(bucketName)
				.key(objectKey))
			.build();
		String imageUrl = presigner.presignGetObject(presignRequest).url().toString();

		return imageUrl.replace(host, "/storage");
	}

}
