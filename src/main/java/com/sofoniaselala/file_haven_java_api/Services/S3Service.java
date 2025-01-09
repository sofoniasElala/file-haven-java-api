package com.sofoniaselala.file_haven_java_api.Services;

import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
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
     * @param name  The object key (path in the bucket).
     * @param userID  User id.
     * @param file The file to upload.
     * @return Confirmation message.
     */
    public String uploadFile(String name, Integer userId, File file) {
        try{
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key("users/" + userId + "/" + name)
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
        return "File uploaded successfully: " + name;
    }

    // Rename a file in the bucket
    public void renameFile(String oldName, Integer userId, String newName) {
        try {
            // Copy the file to the new key (destination)
            CopyObjectRequest copyObjRequest =  CopyObjectRequest.builder()
                .sourceBucket(bucketName)
                .sourceKey("users/" + userId + "/" + oldName)
                .destinationBucket(bucketName)
                .destinationKey("users/" + userId + "/" + newName)
                .build();

            s3Client.copyObject(copyObjRequest);

            // Delete the original file
            DeleteObjectRequest deleteObjRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key("users/" + userId + "/" + oldName)
                .build();
            s3Client.deleteObject(deleteObjRequest);

            System.out.println("File renamed from " + oldName + " to " + newName);
        } catch (Exception e) {
            System.err.println("Error renaming file: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Download a file from S3 bucket.
     *
     * @param name  The object name/key (path in the bucket).
     * @param userID  User id.
     * @return The downloaded file in base64 string.
     * @throws IOException If file writing fails.
     */
    public Map<String, Object> downloadFile(String name, Integer userId) throws IOException {
        Map<String, Object> fileData =  new HashMap<>();
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucketName)
                .key("users/" + userId + "/" + name)
                .build();
        
        HeadObjectRequest metaDataRequest = HeadObjectRequest.builder()
                .bucket(bucketName)
                .key("users/" + userId + "/" + name)
                .build();

        ResponseInputStream<GetObjectResponse> response = s3Client.getObject(request);
        HeadObjectResponse headObjectResponse = s3Client.headObject(metaDataRequest);

        String type = headObjectResponse.contentType();
        String base64String = Base64.getEncoder().encodeToString(response.readAllBytes());

        fileData.put("base64", base64String);
        fileData.put("type", type);

        return fileData;
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