package com.sofoniaselala.file_haven_java_api.Services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class S3Service {

    private final S3Client s3Client;
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(S3Service.class);

    @Value("${aws.s3.bucket-name}") // from application.properties
    private String bucketName;

    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    /**
     * Upload a file to S3 bucket.
     *
     * @param key  The object key (path in the bucket).
     * @param file The file to upload.
     * @return Confirmation message.
     */
    public String uploadFile(String key, File file) {
        try{
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            s3Client.putObject(request, RequestBody.fromFile(file));
        } catch (AwsServiceException e) {
            // Handle service-related exceptions
            logger.error("Amazon S3 service error occurred. Message: {}", e.getMessage());
        } catch (SdkClientException e) {
            // Handle client-related exceptions (e.g., network issues)
            logger.error("SDK client error occurred. Message: {}", e.getMessage());
        } catch (Exception e) {
            // Catch any other exceptions
            logger.error("Unexpected error occurred. Message: {}", e.getMessage());
        }
        return "File uploaded successfully: " + key;
    }

    /**
     * Download a file from S3 bucket.
     *
     * @param key          The object key (path in the bucket).
     * @param downloadPath The local path to save the downloaded file.
     * @return The downloaded file.
     * @throws IOException If file writing fails.
     */
    public File downloadFile(String key, String downloadPath) throws IOException {
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        ResponseInputStream<GetObjectResponse> response = s3Client.getObject(request);

        File file = new File(downloadPath);
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            response.transferTo(outputStream);
        }
        return file;
    }

    /**
     * Delete a file from S3 bucket.
     *
     * @param key The object key (path in the bucket).
     * @return Confirmation message.
     */
    public String deleteFile(String key) {
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3Client.deleteObject(request);
        return "File deleted successfully: " + key;
    }

    /**
     * Delete a folder (and its contents) from S3 bucket.
     *
     * @param folderName The folder name (path in the bucket).
     * @return Confirmation message.
     */
    public void deleteFolder(String folderName) {
        try {
            ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
                    .bucket(bucketName)
                    .prefix(folderName + "/") // Include trailing slash to target the folder
                    .build();

            ListObjectsV2Response listResponse = s3Client.listObjectsV2(listRequest);

            listResponse.contents().forEach(s3Object -> {
                DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(s3Object.key())
                        .build();
                s3Client.deleteObject(deleteRequest);
                System.out.println("File deleted successfully: " + s3Object.key());
            });
        } catch (AwsServiceException e) {
            // Handle service-related exceptions
            logger.error("Amazon S3 service error occurred. Message: {}", e.getMessage());
        } catch (SdkClientException e) {
            // Handle client-related exceptions (e.g., network issues)
            logger.error("SDK client error occurred. Message: {}", e.getMessage());
        } catch (Exception e) {
            // Catch any other exceptions
            logger.error("Unexpected error occurred. Message: {}", e.getMessage());
        }

        System.out.println("Folder deleted successfully: " + folderName);
    }
}